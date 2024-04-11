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

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class Md5UtilsTest {

    private static final Path rootPath = Paths.get("src/test/resources/md5UtilsTest").toAbsolutePath();
    private static final Path filePath = rootPath.resolve("foo4");

    @Test
    void getFolderMd5() {
        final Map<String, String> md5 = Md5Utils.getMd5(rootPath);
        assertEquals(4, md5.size());
        assertEquals(4, md5.values().stream().distinct().count());
    }

    @Test
    void getSingleFile() {
        final Map<String, String> md5 = Md5Utils.getMd5(filePath);
        assertEquals(1, md5.size());
        assertEquals(1, md5.values().stream().distinct().count());
    }

    @Test
    void getFileMd5() {
        final String foo4 = Md5Utils.getFileMd5(rootPath.resolve("foo4"));
        assertNotNull(foo4);
    }

}
