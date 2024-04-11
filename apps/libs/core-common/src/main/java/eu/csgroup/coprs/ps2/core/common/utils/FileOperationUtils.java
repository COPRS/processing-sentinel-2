/*
 * Copyright 2023 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
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
                    .map(File::toPath)
                    .filter(path -> path.getFileName().toString().matches(pattern))
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

    public static void deleteFolderIfEmpty(String folder) {
        log.info("Deleting folder if empty: {}", folder);
        try {
            final Path folderPath = Paths.get(folder);
            if (FileUtils.isEmptyDirectory(folderPath.toFile())) {
                FileSystemUtils.deleteRecursively(folderPath);
            }
        } catch (Exception e) {
            throw new FileOperationException("Unable to delete folder: " + folder, e);
        }
    }

    public static void deleteExpiredFolders(String rootFolder, int hours) {

        log.info("Cleaning up folders older than {} hours inside folder {}", hours, rootFolder);

        long expiredTime = Instant.now().minus(hours, ChronoUnit.HOURS).toEpochMilli();
        final List<Path> folders = findFolders(Paths.get(rootFolder), ".*");
        Set<String> expiredFolders = new HashSet<>();

        folders.forEach(path -> {
            if (path.toFile().lastModified() < expiredTime) {
                expiredFolders.add(path.toString());
            }
        });

        log.info("Found {} folders to delete", expiredFolders.size());

        try {
            deleteFolders(expiredFolders);
        } catch (Exception e) {
            log.warn("Unable to delete all expired folders");
        }

        log.info("Finished cleaning up folder {}", rootFolder);
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

    public static List<Path> findFiles(Path root, String regex) {
        try (Stream<Path> stream = Files.list(root)) {
            return stream.filter(path -> !Files.isDirectory(path))
                    .filter(path -> path.getFileName().toString().matches(regex))
                    .toList();
        } catch (IOException e) {
            throw new FileOperationException("Unable to list files in: " + root, e);
        }
    }

    public static List<Path> findFilesInTree(Path root, String regex) {
        try (Stream<Path> stream = Files.walk(root)) {
            return stream.filter(path -> !Files.isDirectory(path))
                    .filter(path -> path.getFileName().toString().matches(regex))
                    .toList();
        } catch (IOException e) {
            throw new FileOperationException("Unable to list files in: " + root, e);
        }
    }

    public static List<Path> findFolders(Path root, String regex) {
        try (Stream<Path> stream = Files.list(root)) {
            return stream.filter(Files::isDirectory)
                    .filter(path -> path.getFileName().toString().matches(regex))
                    .toList();
        } catch (IOException e) {
            throw new FileOperationException("Unable to list folder: " + root, e);
        }
    }

    public static List<Path> findFoldersInTree(Path root, String regex) {
        try (Stream<Path> stream = Files.walk(root)) {
            return stream.filter(Files::isDirectory)
                    .filter(path -> path.getFileName().toString().matches(regex))
                    .toList();
        } catch (IOException e) {
            throw new FileOperationException("Unable to list folder: " + root, e);
        }
    }

    public static long countFiles(Path folder) {
        try (Stream<Path> stream = Files.list(folder)) {
            return stream.count();
        } catch (IOException e) {
            throw new FileOperationException("Unable to access folder: " + folder, e);
        }
    }

    public static void move(Path sourcePath, Path destinationPath) {
        try {
            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileOperationException("Unable to move file", e);
        }
    }

    /**
     * Merge files contained in a given folder matching a given regex into a single file
     *
     * @param folder Path to the folder containing files to merge
     * @param target Path to the file resulting from the merge
     * @param regex  Regular expression to filter files to merge
     */
    public static void mergeFiles(Path folder, Path target, String regex) {
        try (OutputStream outputStream = Files.newOutputStream(target)) {
            for (Path path : findFilesInTree(folder, regex)) {
                Files.copy(path, outputStream);
            }
        } catch (IOException e) {
            throw new FileOperationException("Unable to merges files", e);
        }
    }

    public static long getSize(Set<String> paths) {
        long size = 0;
        try {
            for (String path : paths) {
                final File file = Path.of(path).toFile();
                if (file.isFile()) {
                    size += FileUtils.sizeOf(file);
                } else {
                    size += FileUtils.sizeOfDirectory(file);
                }
            }
        } catch (Exception e) {
            throw new FileOperationException("Unable to compute files size", e);
        }
        return size;
    }

    private FileOperationUtils() {
    }

}
