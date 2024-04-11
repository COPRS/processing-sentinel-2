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

package eu.csgroup.coprs.ps2.core.common.test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public final class TestUtils {

    public static Set<Path> createFiles(String rootPath, String prefix, int count, long size) throws IOException {

        final String format = "%0" + String.valueOf(count).length() + "d";

        Set<Path> pathSet = new HashSet<>();

        String name;
        Path path;
        for (int i = 0; i < count; i++) {
            name = prefix + String.format(format, i);
            path = Path.of(rootPath, name);
            createFile(path, size);
            pathSet.add(path);
        }
        return pathSet;
    }

    private static void createFile(Path path, long size) throws IOException {
        File file = path.toFile();
        if (file.createNewFile()) {
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                raf.setLength(size);
            }
        }
    }

    private TestUtils() {
    }

}
