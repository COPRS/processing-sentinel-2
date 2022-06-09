package eu.csgroup.coprs.ps2.core.obs.service;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.obs.config.ObsProperties;
import eu.csgroup.coprs.ps2.core.obs.exception.ObsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.transfer.s3.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ObsServiceTest {

    @Mock
    private S3TransferManager transferManager;
    @Mock
    private S3Client s3Client;
    @Mock
    private ObsProperties obsProperties;

    @InjectMocks
    private ObsService obsService;

    private static final String BUCKET = "bucket";
    private static final String KEY_1 = "root/key1";
    private static final String KEY_2 = "root/key2";
    private static final List<String> KEY_LIST = List.of(KEY_1, KEY_2);
    private static final String PATH_1 = "src/test/resources/folder";
    private static final String PATH_2 = "src/test/resources/folder/foo";
    private static final List<Path> PATH_LIST = List.of(Paths.get(PATH_1), Paths.get(PATH_2));
    private static final FileInfo FILE_INFO_FILE = new FileInfo().setBucket(BUCKET).setKey(KEY_1).setFullLocalPath(PATH_2);
    private static final FileInfo FILE_INFO_DIR = new FileInfo().setBucket(BUCKET).setKey(KEY_2).setFullLocalPath(PATH_1);
    private static final Set<FileInfo> FILE_INFO_SET = Set.of(FILE_INFO_FILE, FILE_INFO_DIR);

    private AutoCloseable autoCloseable;

    @BeforeEach
    protected void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        obsService = new ObsService(transferManager, s3Client, obsProperties);
    }

    @AfterEach
    protected void cleanup() throws Exception {
        autoCloseable.close();
    }

    @Test
    void exists() {
        mockListResponse();
        assertTrue(obsService.exists(BUCKET, KEY_1));
    }

    @Test
    void exists_not() {
        mockListResponseEmpty();
        assertFalse(obsService.exists(BUCKET, KEY_1));
    }

    @Test
    void exists_fails() {
        assertThrows(ObsException.class, () -> obsService.exists(BUCKET, KEY_1));
    }

    @Test
    void downloadFile() {
        mockFileDownloadSuccess();
        obsService.downloadFile(BUCKET, KEY_1, PATH_1);
        assertTrue(true);
    }

    @Test
    void downloadFile_failure() {
        mockFileDownloadFailure();
        assertThrows(ObsException.class, () -> obsService.downloadFile(BUCKET, KEY_1, PATH_1));
    }

    @Test
    void downloadDirectory() {
        mockDirDownloadSuccess();
        obsService.downloadDirectory(BUCKET, KEY_1, PATH_1);
        assertTrue(true);
    }

    @Test
    void downloadDirectory_failure() {
        mockDirDownloadFailure();
        assertThrows(ObsException.class, () -> obsService.downloadDirectory(BUCKET, KEY_1, PATH_1));
    }

    @Test
    void downloadFileBatch() {
        mockFileDownloadSuccess();
        obsService.downloadFileBatch(BUCKET, KEY_LIST, PATH_1);
        assertTrue(true);
    }

    @Test
    void downloadFileBatch_failure() {
        mockFileDownloadFailure();
        assertThrows(ObsException.class, () -> obsService.downloadFileBatch(BUCKET, KEY_LIST, PATH_1));
    }

    @Test
    void downloadDirBatch() {
        mockDirDownloadSuccess();
        obsService.downloadDirBatch(BUCKET, KEY_LIST, PATH_1);
        assertTrue(true);
    }

    @Test
    void downloadDirBatch_failure() {
        mockDirDownloadFailure();
        assertThrows(ObsException.class, () -> obsService.downloadDirBatch(BUCKET, KEY_LIST, PATH_1));
    }

    @Test
    void downloadAll_withFiles() {
        mockListResponseEmpty();
        mockFileDownloadSuccess();
        obsService.downloadAll(FILE_INFO_SET);
        assertTrue(true);
    }

    @Test
    void downloadAll_withDirs() {
        mockListResponse();
        mockDirDownloadSuccess();
        obsService.downloadAll(FILE_INFO_SET);
        assertTrue(true);
    }

    @Test
    void downloadAll_failure() {
        mockListResponseEmpty();
        mockFileDownloadFailure();
        assertThrows(ObsException.class, () -> obsService.downloadAll(FILE_INFO_SET));
    }

    @Test
    void uploadFile() {
        mockFileUploadSuccess();
        obsService.uploadFile(BUCKET, PATH_1, KEY_1);
        assertTrue(true);
    }

    @Test
    void uploadFile_failure() {
        mockFileUploadFailure();
        assertThrows(ObsException.class, () -> obsService.uploadFile(BUCKET, PATH_1, KEY_1));
    }

    @Test
    void uploadDirectory() {
        mockDirUploadSuccess();
        obsService.uploadDirectory(BUCKET, PATH_1, KEY_1);
        assertTrue(true);
    }

    @Test
    void uploadDirectory_failure() {
        mockDirUploadFailure();
        assertThrows(ObsException.class, () -> obsService.uploadDirectory(BUCKET, PATH_1, KEY_1));
    }

    @Test
    void uploadFileBatch() {
        mockFileUploadSuccess();
        obsService.uploadFileBatch(BUCKET, PATH_LIST, KEY_1);
        assertTrue(true);
    }

    @Test
    void uploadFileBatch_failure() {
        mockFileUploadFailure();
        assertThrows(ObsException.class, () -> obsService.uploadFileBatch(BUCKET, PATH_LIST, KEY_1));
    }

    @Test
    void uploadDirBatch() {
        mockDirUploadSuccess();
        obsService.uploadDirBatch(BUCKET, PATH_LIST, KEY_1);
        assertTrue(true);
    }

    @Test
    void uploadDirBatch_failure() {
        mockDirUploadFailure();
        assertThrows(ObsException.class, () -> obsService.uploadDirBatch(BUCKET, PATH_LIST, KEY_1));
    }

    @Test
    void uploadAll() {
        mockDirUploadSuccess();
        mockFileUploadSuccess();
        obsService.uploadAll(FILE_INFO_SET);
        assertTrue(true);
    }

    @Test
    void uploadAll_failure() {
        mockDirUploadFailure();
        mockFileUploadFailure();
        assertThrows(ObsException.class, () -> obsService.uploadAll(FILE_INFO_SET));
    }

    // ------------------------------------------------------------------------------------------------------------------------------------------------

    private void mockFileDownloadSuccess() {
        FileDownload fileDownloadSuccess = Mockito.mock(FileDownload.class);
        Mockito.when(fileDownloadSuccess.completionFuture()).thenReturn(CompletableFuture.supplyAsync(() -> Mockito.mock(CompletedFileDownload.class)));
        Mockito.when(transferManager.downloadFile(any(DownloadFileRequest.class))).thenReturn(fileDownloadSuccess);
    }

    private void mockFileDownloadFailure() {
        FileDownload fileDownloadFailure = Mockito.mock(FileDownload.class);
        Mockito.when(fileDownloadFailure.completionFuture()).thenReturn(CompletableFuture.supplyAsync(() -> {
            throw SdkServiceException.builder().message("Nope").build();
        }));
        Mockito.when(transferManager.downloadFile(any(DownloadFileRequest.class))).thenReturn(fileDownloadFailure);
    }

    private void mockDirDownloadSuccess() {
        DirectoryDownload directoryDownloadSuccess = Mockito.mock(DirectoryDownload.class);
        Mockito.when(directoryDownloadSuccess.completionFuture()).thenReturn(CompletableFuture.supplyAsync(() -> Mockito.mock(CompletedDirectoryDownload.class)));
        Mockito.when(transferManager.downloadDirectory(any(DownloadDirectoryRequest.class))).thenReturn(directoryDownloadSuccess);
    }

    private void mockDirDownloadFailure() {
        DirectoryDownload directoryDownloadFailure = Mockito.mock(DirectoryDownload.class);
        Mockito.when(directoryDownloadFailure.completionFuture()).thenReturn(CompletableFuture.supplyAsync(() -> {
            throw SdkClientException.create("Nope", null);
        }));
        Mockito.when(transferManager.downloadDirectory(any(DownloadDirectoryRequest.class))).thenReturn(directoryDownloadFailure);
    }

    private void mockFileUploadSuccess() {
        FileUpload fileUploadSuccess = Mockito.mock(FileUpload.class);
        Mockito.when(fileUploadSuccess.completionFuture()).thenReturn(CompletableFuture.supplyAsync(() -> Mockito.mock(CompletedFileUpload.class)));
        Mockito.when(transferManager.uploadFile(any(UploadFileRequest.class))).thenReturn(fileUploadSuccess);
    }

    private void mockFileUploadFailure() {
        FileUpload fileUploadFailure = Mockito.mock(FileUpload.class);
        Mockito.when(fileUploadFailure.completionFuture()).thenReturn(CompletableFuture.supplyAsync(() -> {
            throw SdkServiceException.builder().message("Nope").build();
        }));
        Mockito.when(transferManager.uploadFile(any(UploadFileRequest.class))).thenReturn(fileUploadFailure);
    }

    private void mockDirUploadSuccess() {
        DirectoryUpload directoryUploadSuccess = Mockito.mock(DirectoryUpload.class);
        Mockito.when(directoryUploadSuccess.completionFuture()).thenReturn(CompletableFuture.supplyAsync(() -> Mockito.mock(CompletedDirectoryUpload.class)));
        Mockito.when(transferManager.uploadDirectory(any(UploadDirectoryRequest.class))).thenReturn(directoryUploadSuccess);
    }

    private void mockDirUploadFailure() {
        DirectoryUpload directoryUploadFailure = Mockito.mock(DirectoryUpload.class);
        Mockito.when(directoryUploadFailure.completionFuture()).thenReturn(CompletableFuture.supplyAsync(() -> {
            throw SdkClientException.create("Nope", null);
        }));
        Mockito.when(transferManager.uploadDirectory(any(UploadDirectoryRequest.class))).thenReturn(directoryUploadFailure);
    }

    private void mockListResponseEmpty() {
        final ListObjectsV2Response emptyResponse = ListObjectsV2Response.builder().build();
        Mockito.when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(emptyResponse);
    }

    private void mockListResponse() {
        final ListObjectsV2Response response = ListObjectsV2Response.builder().contents(Set.of(S3Object.builder().build())).build();
        Mockito.when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(response);
    }

}