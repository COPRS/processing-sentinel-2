package eu.csgroup.coprs.ps2.core.obs.service;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.obs.config.ObsProperties;
import eu.csgroup.coprs.ps2.core.obs.exception.ObsException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ObsServiceTest extends AbstractTest {

    @Mock
    private S3TransferManager transferManager;
    @Mock
    private S3Client s3Client;
    @Mock
    private ObsProperties obsProperties;

    @InjectMocks
    private ObsService obsService;

    private static final String BUCKET = "bucket";
    private static final String FOLDER_1_KEY = "folder1";
    private static final String FOLDER_2_KEY = "folder2";
    private static final String FILE_KEY = "root/file";
    private static final List<String> KEY_LIST = List.of(FOLDER_1_KEY, FILE_KEY);
    private static final String FOLDER_1_PATH = "src/test/resources/folder1";
    private static final String FOLDER_2_PATH = "src/test/resources/folder2";
    private static final String FILE_PATH = "src/test/resources/folder1/foo";
    private static final List<Path> PATH_LIST = List.of(Paths.get(FOLDER_1_PATH), Paths.get(FILE_PATH));
    private static final FileInfo FILE_INFO_FOLDER_1 = new FileInfo().setBucket(BUCKET).setKey(FOLDER_1_KEY).setFullLocalPath(FOLDER_1_PATH);
    private static final FileInfo FILE_INFO_FOLDER_2 = new FileInfo().setBucket(BUCKET).setKey(FOLDER_2_KEY).setFullLocalPath(FOLDER_2_PATH);
    private static final FileInfo FILE_INFO_FILE = new FileInfo().setBucket(BUCKET).setKey(FILE_KEY).setFullLocalPath(FILE_PATH);
    private static final Set<FileInfo> FILE_INFO_MIXED_SET = Set.of(FILE_INFO_FOLDER_1, FILE_INFO_FILE);
    private static final Set<FileInfo> FILE_INFO_FOLDER_SET = Set.of(FILE_INFO_FOLDER_1, FILE_INFO_FOLDER_2);

    @Override
    public void setup() {
        obsService = new ObsService(transferManager, s3Client, obsProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
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
        assertThrows(ObsException.class, () -> obsService.exists(BUCKET, FOLDER_1_KEY));
    }

    @Test
    void exists_all() {
        // Given
        final ListObjectsV2Response response1 = ListObjectsV2Response.builder()
                .isTruncated(true)
                .nextContinuationToken("token")
                .contents(Set.of(S3Object.builder().key(FOLDER_1_KEY).build()))
                .build();
        final ListObjectsV2Response response2 = ListObjectsV2Response.builder()
                .isTruncated(false)
                .continuationToken("token")
                .contents(Set.of(S3Object.builder().key(FOLDER_2_KEY).build()))
                .build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(response1)
                .thenReturn(response2);
        // When
        final Map<String, Boolean> existsByKey = obsService.exists(BUCKET, Set.of(FOLDER_1_KEY, FOLDER_2_KEY));
        // Then
        verify(s3Client, times(2)).listObjectsV2(any(ListObjectsV2Request.class));
        assertEquals(2, existsByKey.size());
        assertTrue(existsByKey.values().stream().allMatch(Boolean::booleanValue));
    }

    @Test
    void exists_all_fails() {
        final Set<String> keySet = Set.of(FOLDER_1_KEY);
        assertThrows(ObsException.class, () -> obsService.exists(BUCKET, keySet));
    }

    @Test
    void getETags() {
        mockListResponseEtags();
        final Map<String, String> eTags = obsService.getETags(BUCKET, "root");
        assertEquals(2, eTags.size());
    }

    @Test
    void download() {
        when(obsProperties.getDownloadTimeout()).thenReturn(30);
        mockDirDownloadSuccess();
        obsService.download(FILE_INFO_MIXED_SET);
        assertTrue(true);
    }

    @Test
    void download_failure() {
        when(obsProperties.getDownloadTimeout()).thenReturn(30);
        mockDirDownloadFailure();
        assertThrows(ObsException.class, () -> obsService.download(FILE_INFO_MIXED_SET));
    }

    @Test
    void upload() {
        when(obsProperties.getUploadTimeout()).thenReturn(30);
        mockDirUploadSuccess();
        mockFileUploadSuccess();
        obsService.upload(FILE_INFO_MIXED_SET);
        assertTrue(true);
    }

    @Test
    void upload_failure() {
        when(obsProperties.getUploadTimeout()).thenReturn(30);
        mockDirUploadFailure();
        mockFileUploadSuccess();
        assertThrows(ObsException.class, () -> obsService.upload(FILE_INFO_MIXED_SET));
    }

    @Test
    void uploadWithMd5() {
        when(obsProperties.getUploadTimeout()).thenReturn(30);
        mockDirUploadSuccess();
        mockFileUploadSuccess();
        mockListResponseEtagsMd5();
        obsService.uploadWithMd5(FILE_INFO_FOLDER_SET, null);
        assertTrue(true);
    }

    // ------------------------------------------------------------------------------------------------------------------------------------------------

    private void mockFileDownloadSuccess() {
        FileDownload fileDownloadSuccess = Mockito.mock(FileDownload.class);
        when(fileDownloadSuccess.completionFuture()).thenReturn(CompletableFuture.supplyAsync(() -> Mockito.mock(CompletedFileDownload.class)));
        when(transferManager.downloadFile(any(DownloadFileRequest.class))).thenReturn(fileDownloadSuccess);
    }

    private void mockFileDownloadFailure() {
        FileDownload fileDownloadFailure = Mockito.mock(FileDownload.class);
        when(fileDownloadFailure.completionFuture()).thenReturn(CompletableFuture.supplyAsync(() -> {
            throw SdkServiceException.builder().message("Nope").build();
        }));
        when(transferManager.downloadFile(any(DownloadFileRequest.class))).thenReturn(fileDownloadFailure);
    }

    private void mockDirDownloadSuccess() {
        DirectoryDownload directoryDownloadSuccess = Mockito.mock(DirectoryDownload.class);
        when(directoryDownloadSuccess.completionFuture()).thenReturn(CompletableFuture.supplyAsync(() -> Mockito.mock(CompletedDirectoryDownload.class)));
        when(transferManager.downloadDirectory(any(DownloadDirectoryRequest.class))).thenReturn(directoryDownloadSuccess);
    }

    private void mockDirDownloadFailure() {
        DirectoryDownload directoryDownloadFailure = Mockito.mock(DirectoryDownload.class);
        when(directoryDownloadFailure.completionFuture()).thenReturn(CompletableFuture.supplyAsync(() -> {
            throw SdkClientException.create("Nope", null);
        }));
        when(transferManager.downloadDirectory(any(DownloadDirectoryRequest.class))).thenReturn(directoryDownloadFailure);
    }

    private void mockFileUploadSuccess() {
        FileUpload fileUploadSuccess = Mockito.mock(FileUpload.class);
        when(fileUploadSuccess.completionFuture()).thenReturn(CompletableFuture.supplyAsync(() -> Mockito.mock(CompletedFileUpload.class)));
        when(transferManager.uploadFile(any(UploadFileRequest.class))).thenReturn(fileUploadSuccess);
    }

    private void mockFileUploadFailure() {
        FileUpload fileUploadFailure = Mockito.mock(FileUpload.class);
        when(fileUploadFailure.completionFuture()).thenReturn(CompletableFuture.supplyAsync(() -> {
            throw SdkServiceException.builder().message("Nope").build();
        }));
        when(transferManager.uploadFile(any(UploadFileRequest.class))).thenReturn(fileUploadFailure);
    }

    private void mockDirUploadSuccess() {
        DirectoryUpload directoryUploadSuccess = Mockito.mock(DirectoryUpload.class);
        when(directoryUploadSuccess.completionFuture()).thenReturn(CompletableFuture.supplyAsync(() -> Mockito.mock(CompletedDirectoryUpload.class)));
        when(transferManager.uploadDirectory(any(UploadDirectoryRequest.class))).thenReturn(directoryUploadSuccess);
    }

    private void mockDirUploadFailure() {
        DirectoryUpload directoryUploadFailure = Mockito.mock(DirectoryUpload.class);
        when(directoryUploadFailure.completionFuture()).thenReturn(CompletableFuture.supplyAsync(() -> {
            throw SdkClientException.create("Nope", null);
        }));
        when(transferManager.uploadDirectory(any(UploadDirectoryRequest.class))).thenReturn(directoryUploadFailure);
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
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(emptyResponse);
    }

    /**
     * Used for exists and isFolder:
     * - exists will return true
     * - isFolder will return true
     */
    private void mockListResponse() {
        final ListObjectsV2Response response = ListObjectsV2Response.builder().contents(Set.of(S3Object.builder().build())).build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(response);
    }

    /**
     * Used for exists and isFolder:
     * - exists will return true then false
     * - isFolder will return true then false
     */
    private void mockListResponseFullThenEmpty() {
        final ListObjectsV2Response emptyResponse = ListObjectsV2Response.builder().build();
        final ListObjectsV2Response response = ListObjectsV2Response.builder().contents(Set.of(S3Object.builder().build())).build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(response).thenReturn(emptyResponse);
    }

    private void mockListResponseEtags() {
        final ListObjectsV2Response response = ListObjectsV2Response.builder().contents(
                        Set.of(
                                S3Object.builder().key(FOLDER_1_KEY).eTag("eTag1").build(),
                                S3Object.builder().key(FOLDER_2_KEY).eTag("eTag2").build()
                        ))
                .build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(response);
    }

    private void mockListResponseEtagsMd5() {
        final ListObjectsV2Response response1 = ListObjectsV2Response.builder().contents(
                        Set.of(
                                S3Object.builder().key(FOLDER_1_KEY + "/foo1").eTag("eTag11").build(),
                                S3Object.builder().key(FOLDER_1_KEY + "/subfolder1/bar1").eTag("eTag12").build()
                        ))
                .build();
        final ListObjectsV2Response response2 = ListObjectsV2Response.builder().contents(
                        Set.of(
                                S3Object.builder().key(FOLDER_2_KEY + "/foo2").eTag("eTag21").build(),
                                S3Object.builder().key(FOLDER_2_KEY + "/subfolder2/bar2").eTag("eTag22").build()
                        ))
                .build();
        when(s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(BUCKET).prefix(FOLDER_1_KEY).build()))
                .thenReturn(response1);
        when(s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(BUCKET).prefix(FOLDER_2_KEY).build()))
                .thenReturn(response2);
    }

}
