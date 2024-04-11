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
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateUtilsTest {

    private static final String dir = "templateUtilsTest";
    private static final Path dirPath = Paths.get(new ClassPathResource(dir).getPath());
    private static final Path template1Path = dirPath.resolve("JobOrder_QL_GEO_01.xml");
    private static final Path template2Path = dirPath.resolve("JobOrder_QL_GEO_02.xml");

    private static final String key1 = "@acquisitionStation@";
    private static final String key2 = "@processingStation@";
    private static final String value1 = "foobar01";
    private static final String value2 = "foobar02";

    private static final Map<String, String> values = Map.of(key1, value1, key2, value2);

    @Test
    void fillTemplates_str() {
        // When
        final Map<String, String> templatesByName = TemplateUtils.fillTemplates(dir, values);
        // Then
        assertEquals(2, templatesByName.size());
        assertTrue(templatesByName.containsKey(template1Path.getFileName().toString()));
        assertTrue(templatesByName.containsKey(template2Path.getFileName().toString()));
        assertTrue(templatesByName.values().stream().allMatch(s -> s.contains(value1)));
        assertTrue(templatesByName.values().stream().allMatch(s -> s.contains(value2)));
    }

    @Test
    void fillTemplates_path() {
        // When
        final Map<String, String> templatesByName = TemplateUtils.fillTemplates(dir, values);
        // Then
        assertEquals(2, templatesByName.size());
        assertTrue(templatesByName.containsKey(template1Path.getFileName().toString()));
        assertTrue(templatesByName.containsKey(template2Path.getFileName().toString()));
        assertTrue(templatesByName.values().stream().allMatch(s -> s.contains(value1)));
        assertTrue(templatesByName.values().stream().allMatch(s -> s.contains(value2)));
    }

    @Test
    void fillTemplates_str_nofile() {
        // When Then
        assertThrows(FileOperationException.class, ()-> TemplateUtils.fillTemplates("foo69", values));
    }

    @Test
    void fillTemplates_path_nofile() {
        // When
        final Path path = Paths.get("foo69");
        // Then
        assertThrows(FileOperationException.class, ()-> TemplateUtils.fillTemplates(path, values));
    }

}
