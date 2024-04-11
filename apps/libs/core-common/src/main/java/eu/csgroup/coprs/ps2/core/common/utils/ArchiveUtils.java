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

import eu.csgroup.coprs.ps2.core.common.exception.ExtractionException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ArchiveUtils {

    /**
     * Decompress and extract a tar-gz archive, in place, optionally deleting the source file.
     * Applicable to files with extension ".tar.gz" or ".tgz".
     *
     * @param tarGzFile Name of the file to extract
     * @param delete    Whether to delete the source file
     * @throws ExtractionException If anything bad happens during extraction or decompression
     */
    public static void unTarGz(String tarGzFile, boolean delete) throws ExtractionException {
        unTar(unGzip(tarGzFile, delete), true);
    }

    /**
     * Extract a tar archive, in place, optionally deleting the source file.
     * Applicable to files with extension ".tar".
     *
     * @param tarFile Name of the file to extract
     * @param delete  Whether to delete the source file
     * @throws ExtractionException If anything bad happens during extraction
     */
    public static void unTar(String tarFile, boolean delete) throws ExtractionException {

        Path inputPath = Paths.get(tarFile);
        Path parentFolderPath = inputPath.getParent();

        try (TarArchiveInputStream archive = new TarArchiveInputStream(new BufferedInputStream(Files.newInputStream(inputPath)))) {

            TarArchiveEntry entry;
            while ((entry = archive.getNextTarEntry()) != null) {
                extractEntry(archive, entry, parentFolderPath);
            }

            if (delete) {
                Files.delete(inputPath);
            }

        } catch (IOException e) {
            throw new ExtractionException("Unable to extract TAR archive " + tarFile, e);
        }
    }

    /**
     * Extract a zip archive, in place, optionally deleting the source file.
     * Applicable to files with extension ".zip".
     *
     * @param zipFile Name of the file to extract
     * @param delete  Whether to delete the source file
     * @throws ExtractionException If anything bad happens during extraction
     */
    public static void unZip(String zipFile, boolean delete) throws ExtractionException {

        Path inputPath = Paths.get(zipFile);
        Path parentFolderPath = inputPath.getParent();

        try (ZipArchiveInputStream archive = new ZipArchiveInputStream(new BufferedInputStream(Files.newInputStream(inputPath)))) {

            ZipArchiveEntry entry;
            while ((entry = archive.getNextZipEntry()) != null) {
                extractEntry(archive, entry, parentFolderPath);
            }

            if (delete) {
                Files.delete(inputPath);
            }

        } catch (IOException e) {
            throw new ExtractionException("Unable to extract ZIP archive " + zipFile, e);
        }
    }

    /**
     * Decompress a GZip archive, in place, optionally deleting the source file.
     * Applicable to files with extension ".gz" and ".tgz".
     *
     * @param gzipFile Name of the file to decompress
     * @param delete   Whether to delete the source file
     * @return Name ot the decompressed file
     * @throws ExtractionException If anything bad happens during decompression
     */
    public static String unGzip(String gzipFile, boolean delete) throws ExtractionException {

        Path inputPath = Paths.get(gzipFile);
        String outputFile;
        if (gzipFile.endsWith(".tgz")) {
            outputFile = StringUtils.removeEnd(gzipFile, ".tgz") + ".tar";
        } else {
            outputFile = StringUtils.removeEnd(gzipFile, ".gz");
        }
        Path outputPath = Paths.get(outputFile);

        try (GzipCompressorInputStream archive = new GzipCompressorInputStream(new BufferedInputStream(Files.newInputStream(inputPath)))) {
            try (OutputStream out = Files.newOutputStream(outputPath)) {
                IOUtils.copy(archive, out);
            }
            if (delete) {
                Files.delete(inputPath);
            }
        } catch (IOException e) {
            throw new ExtractionException("Unable to decompress GZIP file " + gzipFile, e);
        }

        return outputFile;
    }

    private static void extractEntry(ArchiveInputStream archive, ArchiveEntry entry, Path parentFolderPath) throws IOException {

        String entryName = StringUtils.removeStart(entry.getName(), "./");
        Path entryPath = parentFolderPath.resolve(entryName);

        if (entry.isDirectory()) {
            Files.createDirectories(entryPath);
        } else {
            Files.createDirectories(entryPath.getParent());
            try (OutputStream outputStream = Files.newOutputStream(entryPath)) {
                IOUtils.copy(archive, outputStream);
            }
        }
    }

    private ArchiveUtils() {
    }

}
