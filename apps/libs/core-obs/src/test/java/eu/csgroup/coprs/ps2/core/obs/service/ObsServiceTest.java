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
    private static final String FOLDER_KEY = "root/folder";
    private static final String FILE_KEY = "root/file";
    private static final List<String> KEY_LIST = List.of(FOLDER_KEY, FILE_KEY);
    private static final String FOLDER_PATH = "src/test/resources/folder";
    private static final String FILE_PATH = "src/test/resources/folder/foo";
    private static final List<Path> PATH_LIST = List.of(Paths.get(FOLDER_PATH), Paths.get(FILE_PATH));
    private static final FileInfo FILE_INFO_FOLDER = new FileInfo().setBucket(BUCKET).setKey(FOLDER_KEY).setFullLocalPath(FOLDER_PATH);
    private static final FileInfo FILE_INFO_FILE = new FileInfo().setBucket(BUCKET).setKey(FILE_KEY).setFullLocalPath(FILE_PATH);
    private static final Set<FileInfo> FILE_INFO_SET = Set.of(FILE_INFO_FOLDER, FILE_INFO_FILE);

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
        mockExists();
        assertTrue(obsService.exists(BUCKET, FILE_KEY));
    }

    @Test
    void exists_not() {
        mockNotExists();
        assertFalse(obsService.exists(BUCKET, FILE_KEY));
    }

    @Test
    void exists_fails() {
        assertThrows(ObsException.class, () -> obsService.exists(BUCKET, FOLDER_KEY));
    }

    @Test
    void downloadFile() {
        mockIsFile();
        mockFileDownloadSuccess();
        obsService.download(BUCKET, FILE_KEY, FILE_PATH);
        assertTrue(true);
    }

    @Test
    void downloadFile_failure() {
        mockIsFile();
        mockFileDownloadFailure();
        assertThrows(ObsException.class, () -> obsService.download(BUCKET, FILE_KEY, FOLDER_PATH));
    }

    @Test
    void downloadDirectory() {
        mockIsFolder();
        mockDirDownloadSuccess();
        obsService.download(BUCKET, FOLDER_KEY, FOLDER_PATH);
        assertTrue(true);
    }

    @Test
    void downloadDirectory_failure() {
        mockIsFolder();
        mockDirDownloadFailure();
        assertThrows(ObsException.class, () -> obsService.download(BUCKET, FOLDER_KEY, FOLDER_PATH));
    }

    @Test
    void downloadBatch() {
        mockIsFolderThenIsFile();
        mockDirDownloadSuccess();
        mockFileDownloadSuccess();
        obsService.download(BUCKET, KEY_LIST, FOLDER_PATH);
        assertTrue(true);
    }

    @Test
    void downloadBatch_failure() {
        mockIsFolderThenIsFile();
        mockDirDownloadSuccess();
        mockFileDownloadFailure();
        assertThrows(ObsException.class, () -> obsService.download(BUCKET, KEY_LIST, FOLDER_PATH));
    }

    @Test
    void downloadAll() {
        mockIsFolderThenIsFile();
        mockDirDownloadSuccess();
        mockFileDownloadSuccess();
        obsService.download(FILE_INFO_SET);
        assertTrue(true);
    }

    @Test
    void downloadAll_failure() {
        mockIsFolderThenIsFile();
        mockDirDownloadSuccess();
        mockFileDownloadFailure();
        assertThrows(ObsException.class, () -> obsService.download(FILE_INFO_SET));
    }

    @Test
    void uploadFile() {
        mockFileUploadSuccess();
        obsService.upload(BUCKET, FILE_PATH, FILE_KEY);
        assertTrue(true);
    }

    @Test
    void uploadFile_failure() {
        mockFileUploadFailure();
        assertThrows(ObsException.class, () -> obsService.upload(BUCKET, FILE_PATH, FILE_KEY));
    }

    @Test
    void uploadDirectory() {
        mockDirUploadSuccess();
        obsService.upload(BUCKET, FOLDER_PATH, FOLDER_KEY);
        assertTrue(true);
    }

    @Test
    void uploadDirectory_failure() {
        mockDirUploadFailure();
        assertThrows(ObsException.class, () -> obsService.upload(BUCKET, FOLDER_PATH, FOLDER_KEY));
    }

    @Test
    void uploadBatch() {
        mockDirUploadSuccess();
        mockFileUploadSuccess();
        obsService.upload(BUCKET, PATH_LIST, FOLDER_KEY);
        assertTrue(true);
    }

    @Test
    void uploadBatch_failure() {
        mockDirUploadFailure();
        mockFileUploadSuccess();
        assertThrows(ObsException.class, () -> obsService.upload(BUCKET, PATH_LIST, FOLDER_KEY));
    }

    @Test
    void uploadAll() {
        mockDirUploadSuccess();
        mockFileUploadSuccess();
        obsService.upload(FILE_INFO_SET);
        assertTrue(true);
    }

    @Test
    void uploadAll_failure() {
        mockDirUploadFailure();
        mockFileUploadSuccess();
        assertThrows(ObsException.class, () -> obsService.upload(FILE_INFO_SET));
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


    private void mockExists() {
        mockListResponse();
    }

    private void mockNotExists() {
        mockListResponseEmpty();
    }

    private void mockIsFolder() {
        mockListResponse();
    }

    private void mockIsFile() {
        mockListResponseEmpty();
    }

    private void mockIsFolderThenIsFile() {
        mockListResponseFullThenEmpty();
    }

    /**
     * Used for exists and isFolder:
     * - exists will return false
     * - isFolder will return false
     */
    private void mockListResponseEmpty() {
        final ListObjectsV2Response emptyResponse = ListObjectsV2Response.builder().build();
        Mockito.when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(emptyResponse);
    }

    /**
     * Used for exists and isFolder:
     * - exists will return true
     * - isFolder will return true
     */
    private void mockListResponse() {
        final ListObjectsV2Response response = ListObjectsV2Response.builder().contents(Set.of(S3Object.builder().build())).build();
        Mockito.when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(response);
    }

    /**
     * Used for exists and isFolder:
     * - exists will return true then false
     * - isFolder will return true then false
     */
    private void mockListResponseFullThenEmpty() {
        final ListObjectsV2Response emptyResponse = ListObjectsV2Response.builder().build();
        final ListObjectsV2Response response = ListObjectsV2Response.builder().contents(Set.of(S3Object.builder().build())).build();
        Mockito.when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(response).thenReturn(emptyResponse);
    }

}
