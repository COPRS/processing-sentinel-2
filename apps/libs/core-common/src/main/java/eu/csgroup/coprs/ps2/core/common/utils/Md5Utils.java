package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

public final class Md5Utils {

    /**
     * Computes and returns a list of md5 checksums:
     * - if path is a directory, for each file contained inside this directory (recursively)
     * - if path is a file, for this file.
     *
     * @param path Path to folder or file
     * @return A map of md5 checksums by fileName
     */
    public static Map<String, String> getMd5(Path path) {

        final String parent = path.getParent().toString() + FileSystems.getDefault().getSeparator();

        return FileOperationUtils.findFilesInTree(path, ".*")
                .stream()
                .collect(Collectors.toMap(
                        filePath -> StringUtils.removeStart(filePath.toString(), parent),
                        Md5Utils::getFileMd5
                ));
    }

    /**
     * Computes and returns the md5 digest for a file
     *
     * @param path Path to file
     * @return The md5 digest for the file
     */
    public static String getFileMd5(Path path) {
        try (final InputStream inputStream = Files.newInputStream(path)) {
            return DigestUtils.md5Hex(inputStream);
        } catch (IOException e) {
            throw new FileOperationException("Unable to access file: " + path, e);
        }
    }

    private Md5Utils() {
    }

}
