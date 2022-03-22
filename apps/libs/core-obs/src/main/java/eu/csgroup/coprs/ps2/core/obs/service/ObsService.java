package eu.csgroup.coprs.ps2.core.obs.service;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.obs.exception.ObsException;
import eu.csgroup.coprs.ps2.core.obs.settings.ObsProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;


@Slf4j
@Service
public class ObsService {

    public static final String DELIMITER = "/";

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

    /**
     * Download a file from the specified key
     *
     * @param bucket   Source bucket
     * @param key      Key to download from
     * @param destFile Path to the destination file
     */
    public void download(String bucket, String key, String destFile) {
        doFileDownload(key, bucket, Paths.get(destFile))
                .block();
    }

    /**
     * Download the content of an obs folder to a local directory
     *
     * @param bucket    Source bucket
     * @param keyPrefix Name of the s3 folder content to download
     * @param destRoot  Path to the local directory under which to store the downloaded files
     */
    public void downloadDirectory(String bucket, String keyPrefix, String destRoot) {
        doDirDownload(keyPrefix, bucket, Paths.get(destRoot))
                .block();
    }

    /**
     * Download a list of files to the specified directory
     *
     * @param bucket   Source bucket
     * @param keyList  List of keys to download
     * @param destRoot Path to the local directory under which to store the downloaded files
     */
    public void downloadBatch(String bucket, List<String> keyList, String destRoot) {
        Mono.when(keyList.stream()
                        .map(key -> doFileDownload(key, bucket, Paths.get(destRoot, filename(key))))
                        .toList())
                .block();
    }

    /**
     * Download a list of directories to the specified directory root
     *
     * @param bucket   Source bucket
     * @param keyList  List of keys to download
     * @param destRoot Path to the local directory under which to store the downloaded directories
     */
    public void downloadDirBatch(String bucket, List<String> keyList, String destRoot) {
        Mono.when(keyList.stream()
                        .map(key -> doDirDownload(key, bucket, Paths.get(destRoot, filename(key))))
                        .toList())
                .block();
    }

    /**
     * Download a set of files or directories, given their FileInfo
     *
     * @param fileInfoList List of FileInfo objects, containing source and destination info for each file
     */
    public void downloadAll(Set<FileInfo> fileInfoList) {
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
     * Upload a single file to the specified key
     *
     * @param bucket     Destination bucket
     * @param sourceFile Path to the source file
     * @param key        Key to upload to
     */
    public void upload(String bucket, String sourceFile, String key) {
        doFileUpload(Paths.get(sourceFile), bucket, key)
                .block();
    }

    /**
     * Upload a whole directory, including subdirectories, to the specified folder key
     *
     * @param bucket    Destination bucket
     * @param sourceDir Path to the source directory
     * @param key       Key of the destination folder
     */
    public void uploadDirectory(String bucket, String sourceDir, String key) {
        doDirUpload(Paths.get(sourceDir), bucket, key)
                .block();
    }

    /**
     * Upload a list of files to the specified folder key
     *
     * @param bucket   Destination bucket
     * @param pathList List of Path to files to upload
     * @param rootKey  Root key under which to upload files
     */
    public void uploadBatch(String bucket, List<Path> pathList, String rootKey) {
        Mono.when(pathList.stream()
                        .map(path -> doFileUpload(path, bucket, destinationPath(rootKey,  path)))
                        .toList())
                .block();
    }

    /**
     * Upload a list of directories to the specified folder key
     *
     * @param bucket   Destination bucket
     * @param pathList List of Path to directories to upload
     * @param rootKey  Root key under which to upload directories
     */
    public void uploadDirBatch(String bucket, List<Path> pathList, String rootKey) {
        Mono.when(pathList.stream()
                        .map(path -> doDirUpload(path, bucket, destinationPath(rootKey,  path)))
                        .toList())
                .block();
    }

    /**
     * Upload a set of files or folders, given their FileInfo
     *
     * @param fileInfoList List of FileInfo objects, containing source and destination info for each file
     */
    public void uploadAll(Set<FileInfo> fileInfoList) {
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
        return s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucket).prefix(key).maxKeys(1).build())
                .contents()
                .stream()
                .anyMatch(s3Object -> s3Object.key().equals(key + DELIMITER));
    }

    private List<String> listFiles(String bucket, String key) {
        return s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucket).prefix(key).build())
                .contents()
                .stream()
                .map(S3Object::key)
                .filter(s -> !s.equals(key + DELIMITER))
                .toList();
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
