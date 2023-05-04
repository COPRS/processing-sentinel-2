package eu.csgroup.coprs.ps2.core.obs.service;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.ReportTask;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.common.utils.Md5Utils;
import eu.csgroup.coprs.ps2.core.obs.config.ObsProperties;
import eu.csgroup.coprs.ps2.core.obs.exception.ObsException;
import eu.csgroup.coprs.ps2.core.obs.utils.ObsTraceUtils;
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
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
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
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ObsService {

    private static final String DELIMITER = "/";
    private static final String MD5SUM_SUFFIX = ".md5sum";
    private static final String ERROR_MESSAGE = "Error occurred during OBS operation: ";

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

        log.debug("Checking Obs file existence for file {}", key);

        boolean exists;
        try {
            exists = s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucket).prefix(key).maxKeys(1).build()).hasContents();
        } catch (Exception e) {
            String errorMessage = ERROR_MESSAGE + e.getMessage();
            log.error(errorMessage);
            throw new ObsException(errorMessage, e);
        }
        return exists;
    }

    public Map<String, Boolean> exists(String bucket, Set<String> keySet) {

        log.debug("Checking Obs file existence for {} files", keySet.size());

        Map<String, Boolean> existsByKey = keySet.stream().collect(Collectors.toMap(Function.identity(), s -> false));

        final String commonPrefix = StringUtils.getCommonPrefix(keySet.toArray(new String[0]));

        log.debug("Found common prefix {}", commonPrefix);

        ListObjectsV2Request request;
        ListObjectsV2Response response;
        String continuationToken = null;
        boolean isTruncated = true;

        try {

            while (isTruncated && existsByKey.entrySet().stream().anyMatch(entry -> !entry.getValue())) {

                request = ListObjectsV2Request.builder()
                        .bucket(bucket)
                        .prefix(commonPrefix)
                        .continuationToken(continuationToken)
                        .build();
                response = s3Client.listObjectsV2(request);
                isTruncated = response.isTruncated();
                continuationToken = response.nextContinuationToken();

                final Set<String> foundKeySet = response.contents().stream().map(S3Object::key).collect(Collectors.toSet());

                log.debug("Found {} files", foundKeySet.size());

                existsByKey.entrySet()
                        .stream()
                        .filter(entry -> !entry.getValue())
                        .forEach(entry -> {
                            if (foundKeySet.stream().anyMatch(foundKey -> foundKey.equals(entry.getKey()) || foundKey.startsWith(entry.getKey() + "/"))) {
                                existsByKey.put(entry.getKey(), true);
                            }
                        });
            }

        } catch (Exception e) {

            String errorMessage = ERROR_MESSAGE + e.getMessage();
            log.error(errorMessage);
            throw new ObsException(errorMessage, e);
        }

        return existsByKey;
    }

    public Map<String, String> getETags(String bucket, String key) {

        log.debug("Fetching eTag for file {} in bucket {}", key, bucket);

        try {
            return s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucket).prefix(key).build())
                    .contents()
                    .stream()
                    .collect(Collectors.toMap(S3Object::key, s3Object -> StringUtils.remove(s3Object.eTag(), "\"")));
        } catch (Exception e) {
            String errorMessage = ERROR_MESSAGE + e.getMessage();
            log.error(errorMessage);
            throw new ObsException(errorMessage, e);
        }
    }

    public void download(Set<FileInfo> fileInfoSet, UUID parentUid) {
        ObsTraceUtils.traceTransfer(fileInfoSet, ReportTask.OBS_READ, parentUid, this::download);
    }

    /**
     * Download a set of files or directories, given their FileInfo.
     *
     * @param fileInfoSet List of FileInfo objects, containing source and destination info for each file
     */
    public void download(Set<FileInfo> fileInfoSet) {

        log.debug("Downloading {} folders using FileInfos", fileInfoSet.size());

        waitOnTransfers(
                fileInfoSet.stream()
                        .map(fileInfo -> {
                            final String key = fileInfo.getKey();
                            final String bucket = fileInfo.getBucket();
                            final Path destinationPath = Paths.get(fileInfo.getFullLocalPath());
                            if (fileInfo.isSimpleFile()) {
                                return doFileDownload(key, bucket, destinationPath);
                            } else {
                                return doDirDownload(key, bucket, destinationPath);
                            }
                        })
                        .toList(),
                obsProperties.getDownloadTimeout()
        );
    }

    public void upload(Set<FileInfo> fileInfoSet, UUID parentUid) {
        ObsTraceUtils.traceTransfer(fileInfoSet, ReportTask.OBS_WRITE, parentUid, this::upload);
    }

    /**
     * Upload a set of files or folders, given their FileInfo
     *
     * @param fileInfoSet List of FileInfo objects, containing source and destination info for each file
     */
    public void upload(Set<FileInfo> fileInfoSet) {

        log.debug("Uploading {} files using FileInfos", fileInfoSet.size());

        waitOnTransfers(
                fileInfoSet.stream()
                        .map(fileInfo -> {
                            final Path sourcePath = Paths.get(fileInfo.getFullLocalPath());
                            if (sourcePath.toFile().isDirectory()) {
                                return doDirUpload(sourcePath, fileInfo.getBucket(), fileInfo.getKey());
                            } else {
                                return doFileUpload(sourcePath, fileInfo.getBucket(), fileInfo.getKey());
                            }
                        })
                        .toList(),
                obsProperties.getUploadTimeout());
    }

    /**
     * Upload a set of folders, given their FileInfo, and create the matching md5sum files according to ICD.
     * Create an ObsWrite trace relative to the download.
     *
     * @param fileInfoSet List of FileInfo objects, containing source and destination info for each file
     * @param parentUid   UUID of the parent task
     */
    public void uploadWithMd5(Set<FileInfo> fileInfoSet, UUID parentUid) {

        if (!fileInfoSet.isEmpty()) {

            log.info("Uploading {} folders to OBS", fileInfoSet.size());
            upload(fileInfoSet, parentUid);

            log.info("Creating md5sum files");

            String tmpFolder = FolderParameters.TMP_DOWNLOAD_FOLDER + FileSystems.getDefault().getSeparator() + UUID.randomUUID();
            FileOperationUtils.createFolders(Set.of(tmpFolder));

            Set<FileInfo> md5FileInfos = new HashSet<>();

            fileInfoSet.forEach(fileInfo -> {

                final Path localPath = Paths.get(fileInfo.getFullLocalPath());
                final Path md5sumPath = Paths.get(tmpFolder).resolve(localPath.getFileName() + MD5SUM_SUFFIX);

                final Map<String, String> md5ByFileName = Md5Utils.getMd5(localPath);
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
            upload(md5FileInfos, parentUid);

            if (!FileUtils.deleteQuietly(new File(tmpFolder))) {
                log.warn("Unable to delete temp folder {}", tmpFolder);
            }
        }
    }

    private void waitOnTransfers(List<Mono<?>> transfers, int timeout) {
        Mono.when(transfers)
                .timeout(
                        Duration.ofMinutes(timeout),
                        Mono.fromCallable(() -> {
                            final String message = String.format("Transfer operation timed out after %s minutes", timeout);
                            log.error(message);
                            throw new ObsException(message);
                        }))
                .retryWhen(getRetrySpec())
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
        String startKey = "start";
        return Mono.deferContextual(context ->
                        Mono.fromFuture(transfer.get().completionFuture())
                                .doOnSubscribe(unused -> log.info("{} starting {}", type.getName(), info))
                                .handle((transfer1, synchronousSink) -> {
                                    if (transfer1 instanceof CompletedDirectoryTransfer completedDirectoryTransfer && !completedDirectoryTransfer.failedTransfers().isEmpty()) {
                                        String errorMessage = String.format("%s failed %s -- %s", type.getName(), info, "Some files could not be transferred");
                                        log.error(errorMessage);
                                        synchronousSink.error(new ObsException(errorMessage));
                                    }
                                })
                                .doOnSuccess(completedTransfer -> {
                                    final Duration elapsed = Duration.between(context.<Instant>get(startKey), Instant.now());
                                    log.info("{} complete after {} seconds {}", type.getName(), elapsed.getSeconds(), info);
                                })
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
                                .retryWhen(getRetrySpec()))
                .contextWrite(context -> context.put(startKey, Instant.now()));
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

    private boolean isFolder(String bucket, String key) { // NOSONAR
        return !s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucket).prefix(key + DELIMITER).maxKeys(1).build())
                .contents()
                .isEmpty();
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
