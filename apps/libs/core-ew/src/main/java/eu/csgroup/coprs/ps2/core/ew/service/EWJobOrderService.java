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

package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public abstract class EWJobOrderService<T extends ExecutionInput> {

    public abstract void saveJobOrders(T executionInput);

    protected void save(Map<String, String> jobOrderByName, Path folderPath) {
        jobOrderByName.forEach((name, jobOrder) -> {
            try {
                Files.writeString(folderPath.resolve(name), jobOrder);
            } catch (IOException e) {
                throw new FileOperationException("Unable to save Job Order", e);
            }
        });
    }

}
