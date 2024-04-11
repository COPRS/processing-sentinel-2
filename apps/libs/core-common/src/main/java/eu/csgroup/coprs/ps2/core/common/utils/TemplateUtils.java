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
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TemplateUtils {


    public static Map<String, String> fillTemplates(String templateResourceFolder, Map<String, String> values) {

        try {

            Resource resource = new ClassPathResource(templateResourceFolder);
            return fillTemplates(resource.getFile().toPath(), values);

        } catch (IOException e) {
            throw new FileOperationException("Unable to access folder " + templateResourceFolder, e);
        }
    }

    public static Map<String, String> fillTemplates(Path templateFolderPath, Map<String, String> values) {

        try (final Stream<Path> templates = Files.list(templateFolderPath)) {

            return templates.map(path -> fill(path, values)).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        } catch (IOException e) {
            throw new FileOperationException("Unable to access folder " + templateFolderPath, e);
        }
    }

    private static Pair<String, String> fill(Path templatePath, Map<String, String> values) {

        try {

            String jobOrder = Files.readString(templatePath);
            String jobOrderName = templatePath.getFileName().toString();
            return Pair.of(jobOrderName, FileContentUtils.replaceAll(jobOrder, values));

        } catch (IOException e) {
            throw new FileOperationException("Unable to access file " + templatePath, e);
        }
    }

    private TemplateUtils() {

    }

}
