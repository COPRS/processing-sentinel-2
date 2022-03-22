package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;


@Slf4j
public final class FileOperationUtils {

    public static void deleteFolderContent(String folder) {

        log.info("Deleting content for folder: {}", folder);

        try (final Stream<Path> list = Files.list(Paths.get(folder))) {
            list.forEach(path -> {
                try {
                    if (path.toFile().isDirectory()) {
                        FileSystemUtils.deleteRecursively(path);
                    } else {
                        Files.delete(path);
                    }
                } catch (IOException e) {
                    throw new FileOperationException("Unable to delete file: " + path, e);
                }
            });
        } catch (Exception e) {
            throw new FileOperationException("Unable to clean folder: " + folder, e);
        }
    }

    public static void deleteFiles(String folder, String pattern) {

        log.info("Deleting files in folder: '{}' matching pattern: '{}'", folder, pattern);

        try (final Stream<Path> list = Files.list(Paths.get(folder))) {
            list
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .map(File::toString)
                    .filter(s -> s.matches(pattern))
                    .map(Paths::get)
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new FileOperationException("Unable to delete file: " + path, e);
                        }
                    });
        } catch (Exception e) {
            throw new FileOperationException("Unable to clean folder: " + folder, e);
        }
    }

    public static void deleteFolders(Set<String> folderSet) {
        folderSet.forEach(folder -> {
            log.info("Deleting folder: {}", folder);
            try {
                FileSystemUtils.deleteRecursively(Paths.get(folder));
            } catch (Exception e) {
                throw new FileOperationException("Unable to delete folder: " + folder, e);
            }
        });
    }

    public static void createFolders(Set<String> folderSet) {
        folderSet.forEach(folder -> {
            log.info("Creating folder: {}", folder);
            try {
                Files.createDirectories(Paths.get(folder));
            } catch (Exception e) {
                throw new FileOperationException("Unable to create folder: " + folder, e);
            }
        });
    }

    public static List<Path> findFolders(Path root, String regex) {
        List<Path> pathList = Collections.emptyList();
        try (Stream<Path> stream = Files.list(root)) {
            pathList = stream.filter(Files::isDirectory)
                    .filter(path -> path.getFileName().toString().matches(regex))
                    .toList();
        } catch (IOException e) {
            throw new FileOperationException("Unable to list folder: " + root, e);
        }
        return pathList;
    }

    private FileOperationUtils() {
    }

}
