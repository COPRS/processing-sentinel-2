package eu.csgroup.coprs.ps2.stub.rmg;


import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

@ActiveProfiles("dev")
@SpringBootTest
class S3Tests extends AbstractTest {

    private final String BUCKET = "pfabre-dev";

    @Autowired
    private ObsService obsService;

    @Override
    void cleanup() {
        try {
            Files.deleteIfExists(Path.of("/tmp/single_file"));
            Files.deleteIfExists(Path.of("src/test/resources/big_file"));
            FileSystemUtils.deleteRecursively(Path.of("/tmp/many"));
            FileSystemUtils.deleteRecursively(Path.of("/tmp/many_batch"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    @Test
    void download() {
        final String file = "/tmp/single_file";
        final String key = "nohup.out";
        obsService.download(BUCKET, key, file);
        Assertions.assertTrue(Files.exists(Path.of(file)));
    }

    //    @Test
    void downloadBigFile() {
        final String file = "src/test/resources/big_file";
        final String key = "big_file_1648825736179";
        obsService.download(BUCKET, key, file);
        Assertions.assertTrue(Files.exists(Path.of(file)));
    }

    //        @Test
    void downloadDirectory() {
        final String dir = "/tmp/many";
        final String key = "many_files_1648826273885";
        obsService.downloadDirectory(BUCKET, key, dir);
        Assertions.assertTrue(Files.exists(Path.of(dir + "/file_0")));
        Assertions.assertTrue(Files.exists(Path.of(dir + "/file_495")));
    }

    //            @Test
    void downloadBatch() throws IOException {
        final List<String> keyList = IntStream.range(0, 1000)
                .mapToObj(i -> "many_files_1648826273885/file_" + i)
                .toList();
        final String destRoot = "/tmp/many_batch";
        Files.createDirectories(Paths.get(destRoot));
        obsService.downloadBatch(BUCKET, keyList, destRoot);
        Assertions.assertTrue(Files.exists(Path.of(destRoot + "/file_69")));
    }

    //        @Test
    void uploadSmallFile() {
        final String key = "small_file_" + Instant.now().toEpochMilli();
        obsService.upload(BUCKET, "src/test/resources/many_files/file_0", key);
        Assertions.assertTrue(obsService.exists(BUCKET, key));
    }

    //        @Test
    void uploadBigFile() {
        final String key = "big_file_" + Instant.now().toEpochMilli();
        obsService.upload(BUCKET, "src/test/resources/large_file", key);
        Assertions.assertTrue(obsService.exists(BUCKET, key));
    }

    //        @Test
    void uploadDirectory() {
        final String key = "many_files_" + Instant.now().toEpochMilli();
        obsService.uploadDirectory(BUCKET, "src/test/resources/many_files", key);
        Assertions.assertTrue(obsService.exists(BUCKET, key + "/file_0"));
    }

    //        @Test
    void uploadBatch() {
        final String key = "many_files_" + Instant.now().toEpochMilli();
        final List<Path> fileList = IntStream.range(0, 1000)
                .mapToObj(i -> Paths.get("src/test/resources/many_files/file_" + i))
                .toList();
        obsService.uploadBatch(BUCKET, fileList, key);
        Assertions.assertTrue(obsService.exists(BUCKET, key + "/file_69"));
    }

    //    @Test
    void exists() {
        boolean exists = obsService.exists(BUCKET, "nohup.out");
        Assertions.assertTrue(exists);
    }

}
