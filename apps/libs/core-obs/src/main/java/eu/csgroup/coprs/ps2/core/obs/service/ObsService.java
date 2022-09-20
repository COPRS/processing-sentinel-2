package eu.csgroup.coprs.ps2.core.obs.service;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.settings.PreparationParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.common.utils.Md5utils;
import eu.csgroup.coprs.ps2.core.obs.config.ObsProperties;
import eu.csgroup.coprs.ps2.core.obs.exception.ObsException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.transfer.s3.*;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ObsService {

    public static final String DELIMITER = "/";
    public static final String MD5SUM_SUFFIX = ".md5sum";

    private final S3TransferManager transferManager;
    private final S3Client s3Client;
    private final ObsProperties obsProperties;

    public ObsService(S3TransferManager transferManager, S3Client s3Client, ObsProperties obsProperties) {
        this.transferManager = transferManager;
        this.s3Client = s3Client;
        this.obsProperties = obsProperties;
    }

    @PostConstruct
    public void info() {
        log.info("Obs Service ready. Endpoint: {}.", obsProperties.getEndpoint());
    }

    public boolean exists(String bucket, String key) {
        boolean exists;
        try {
            exists = s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucket).prefix(key).maxKeys(1).build()).hasContents();
        } catch (Exception e) {
            String errorMessage = "Error occurred during OBS operation: " + e.getMessage();
            log.error(errorMessage);
            throw new ObsException(errorMessage, e);
        }
        return exists;
    }

    public Map<String, String> getETags(String bucket, String key) {
        try {
            return s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucket).prefix(key).build())
                    .contents()
                    .stream()
                    .filter(s3Object -> !s3Object.key().equals(key))
                    .collect(Collectors.toMap(S3Object::key, s3Object -> StringUtils.remove(s3Object.eTag(), "\"")));
        } catch (Exception e) {
            String errorMessage = "Error occurred during OBS operation: " + e.getMessage();
            log.error(errorMessage);
            throw new ObsException(errorMessage, e);
        }
    }

    /**
     * Download a file from the specified key to a given destination OR the content of an obs folder to a local directory
     *
     * @param bucket Source bucket
     * @param key    Key to download from OR name of the s3 folder content to download
     * @param dest   Path to the destination file OR to the local directory under which to store the downloaded files
     */
    public void download(String bucket, String key, String dest) {
        final Path destPath = Paths.get(dest);
        if (isFolder(bucket, key)) {
            doDirDownload(key, bucket, destPath).block();
        } else {
            doFileDownload(key, bucket, destPath).block();
        }
    }

    /**
     * Download a list of files or directories to the specified directory root
     *
     * @param bucket   Source bucket
     * @param keyList  List of keys to download
     * @param destRoot Path to the local directory under which to store the downloaded items
     */
    public void download(String bucket, List<String> keyList, String destRoot) {
        Mono.when(keyList.stream()
                        .map(key -> {
                            if (isFolder(bucket, key)) {
                                return doDirDownload(key, bucket, Paths.get(destRoot, filename(key)));
                            } else {
                                return doFileDownload(key, bucket, Paths.get(destRoot, filename(key)));
                            }
                        })
                        .toList())
                .block();
    }

    /**
     * Download a set of files or directories, given their FileInfo
     *
     * @param fileInfoList List of FileInfo objects, containing source and destination info for each file
     */
    public void download(Set<FileInfo> fileInfoList) {
        Mono.when(fileInfoList.stream()
                        .map(fileInfo -> {
                            final String key = fileInfo.getKey();
                            final String bucket = fileInfo.getBucket();
                            final Path destinationPath = Paths.get(fileInfo.getFullLocalPath());
                            if (isFolder(bucket, key)) {
                                return doDirDownload(key, bucket, destinationPath);
                            } else {
                                return doFileDownload(key, bucket, destinationPath);
                            }
                        })
                        .toList())
                .block();
    }

    /**
     * Upload a single file or directory to the specified key
     *
     * @param bucket Destination bucket
     * @param source Path to the source item
     * @param key    Key to upload to
     */
    public void upload(String bucket, String source, String key) {
        final Path sourcePath = Paths.get(source);
        if (Files.isDirectory(sourcePath)) {
            doDirUpload(sourcePath, bucket, key).block();
        } else {
            doFileUpload(sourcePath, bucket, key).block();
        }
    }

    /**
     * Upload a list of files or directories to the specified folder key
     *
     * @param bucket   Destination bucket
     * @param pathList List of Path to items to upload
     * @param rootKey  Root key under which to upload items
     */
    public void upload(String bucket, List<Path> pathList, String rootKey) {
        Mono.when(pathList.stream()
                        .map(path -> {
                            if (Files.isDirectory(path)) {
                                return doDirUpload(path, bucket, destinationPath(rootKey, path));
                            } else {
                                return doFileUpload(path, bucket, destinationPath(rootKey, path));
                            }
                        })
                        .toList())
                .block();
    }

    /**
     * Upload a set of files or folders, given their FileInfo
     *
     * @param fileInfoList List of FileInfo objects, containing source and destination info for each file
     */
    public void upload(Set<FileInfo> fileInfoList) {
        Mono.when(fileInfoList.stream()
                        .map(fileInfo -> {
                            final Path sourcePath = Paths.get(fileInfo.getFullLocalPath());
                            if (sourcePath.toFile().isDirectory()) {
                                return doDirUpload(sourcePath, fileInfo.getBucket(), fileInfo.getKey());
                            } else {
                                return doFileUpload(sourcePath, fileInfo.getBucket(), fileInfo.getKey());
                            }
                        })
                        .toList())
                .block();
    }

    /**
     * Upload a set of folders, given their FileInfo, and create the matching md5sum files according to ICD
     *
     * @param fileInfoList List of FileInfo objects, containing source and destination info for each file
     */
    public void uploadWithMd5(Set<FileInfo> fileInfoList) {

        log.info("Uploading {} folders to OBS", fileInfoList.size());
        upload(fileInfoList);

        log.info("Creating md5sum files");

        String tmpFolder = PreparationParameters.TMP_DOWNLOAD_FOLDER + FileSystems.getDefault().getSeparator() + UUID.randomUUID();
        FileOperationUtils.createFolders(Set.of(tmpFolder));

        Set<FileInfo> md5FileInfos = new HashSet<>();

        fileInfoList.forEach(fileInfo -> {

            final Path localPath = Paths.get(fileInfo.getFullLocalPath());
            final Path md5sumPath = Paths.get(tmpFolder).resolve(localPath.getFileName() + MD5SUM_SUFFIX);

            final Map<String, String> md5ByFileName = Md5utils.getFolderMd5(localPath);
            final Map<String, String> eTagByKey = getETags(fileInfo.getBucket(), fileInfo.getKey());

            final List<String> lines = md5ByFileName.entrySet()
                    .stream()
                    .map(entry -> String.format("%s %s %s", entry.getValue(), eTagByKey.get(entry.getKey()), entry.getKey()))
                    .toList();

            try (final OutputStream outputStream = Files.newOutputStream(md5sumPath)) {
                for (String line : lines) {
                    outputStream.write(line.getBytes());
                    outputStream.write(System.lineSeparator().getBytes());
                }
            } catch (IOException e) {
                throw new FileOperationException("Unable to create temporary files in: " + tmpFolder, e);
            }

            md5FileInfos.add(
                    new FileInfo()
                            .setFullLocalPath(md5sumPath.toString())
                            .setBucket(fileInfo.getBucket())
                            .setKey(md5sumPath.getFileName().toString())
            );
        });

        log.info("Uploading md5sum files");
        upload(md5FileInfos);

        if (!FileUtils.deleteQuietly(new File(tmpFolder))) {
            log.warn("Unable to delete temp folder {}", tmpFolder);
        }
    }

    private Mono<?> doFileDownload(String key, String bucket, Path destinationPath) {
        return doTransfer(
                () -> transferManager.downloadFile(
                        DownloadFileRequest.builder()
                                .getObjectRequest(r -> r.bucket(bucket).key(key))
                                .destination(destinationPath)
//                                .overrideConfiguration(c -> c.addListener(LoggingTransferListener.create()))
                                .build()),
                transferInfo(bucket, key, destinationPath.toString()),
                TransferType.FILE_DOWNLOAD);
    }

    private Mono<?> doDirDownload(String prefix, String bucket, Path destinationDirPath) {
        return doTransfer(
                () -> transferManager.downloadDirectory(
                        DownloadDirectoryRequest.builder()
                                .bucket(bucket)
                                .prefix(prefix)
                                .destinationDirectory(destinationDirPath)
                                .build()),
                transferInfo(bucket, prefix, destinationDirPath.toString()),
                TransferType.DIR_DOWNLOAD);
    }

    private Mono<?> doFileUpload(Path sourcePath, String bucket, String key) {
        return doTransfer(
                () -> transferManager.uploadFile(
                        UploadFileRequest.builder()
                                .source(sourcePath)
                                .putObjectRequest(r -> r.bucket(bucket).key(key))
//                                .overrideConfiguration(c -> c.addListener(LoggingTransferListener.create()))
                                .build()),
                transferInfo(bucket, key, sourcePath.toString()),
                TransferType.FILE_UPLOAD);
    }

    private Mono<?> doDirUpload(Path sourcePath, String bucket, String prefix) {
        return doTransfer(
                () -> transferManager.uploadDirectory(
                        UploadDirectoryRequest.builder()
                                .sourceDirectory(sourcePath)
                                .bucket(bucket)
                                .prefix(prefix)
                                .build()),
                transferInfo(bucket, prefix, sourcePath.toString()),
                TransferType.DIR_UPLOAD);
    }

    private Mono<?> doTransfer(Supplier<Transfer> transfer, String info, TransferType type) {
        return Mono.fromFuture(transfer.get().completionFuture())
                .doFirst(() -> log.info("{} starting {}", type.getName(), info))
                .timed()
                .doOnSuccess(completedTransfer -> log.info("{} complete in {} seconds {}", type.getName(), completedTransfer.elapsed().getSeconds(), info))
                .doOnError(throwable -> {
                    String message = "Obs error occurred: ";
                    if (throwable instanceof SdkException) {
                        if (throwable instanceof SdkClientException) {
                            message = "Obs Client Exception occurred: ";
                        } else if (throwable instanceof SdkServiceException) {
                            message = "Obs Service Exception occurred: ";
                        }
                    }
                    String errorMessage = String.format("%s failed %s -- Cause: %s", type.getName(), info, message + throwable.getLocalizedMessage());
                    log.error(errorMessage);
                    throw new ObsException(errorMessage, throwable);
                })
                .retryWhen(getRetrySpec());
    }

    private String transferInfo(String bucket, String key, String file) {
        return "- Bucket: '" + bucket + "' - File: '" + file + "' - Key: '" + key + "'";
    }

    private RetryBackoffSpec getRetrySpec() {
        return Retry.backoff(obsProperties.getMaxRetries(), Duration.ofSeconds(2))
                .doBeforeRetry(retrySignal -> log.warn("Retrying OBS operation (#" + (retrySignal.totalRetries() + 1) + ")"))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        new ObsException("Obs operation failed after " + retryBackoffSpec.maxAttempts + " retries", retrySignal.failure()));
    }

    private boolean isFolder(String bucket, String key) {
        return !s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucket).prefix(key + DELIMITER).maxKeys(1).build())
                .contents()
                .isEmpty();
    }

    private String filename(String path) {
        return Paths.get(path).getFileName().toString();
    }

    private String destinationPath(String root, Path path) {
        return root + DELIMITER + path.getFileName().toString();
    }

    @Getter
    @AllArgsConstructor
    private enum TransferType {

        DIR_UPLOAD("Directory Upload"),
        DIR_DOWNLOAD("Directory Download"),
        FILE_UPLOAD("File Upload"),
        FILE_DOWNLOAD("File Download");

        private final String name;
    }

}
