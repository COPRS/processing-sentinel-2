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

package eu.csgroup.coprs.ps2.ew.l0c.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.EWMessageService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class L0cEWMessageService extends EWMessageService<L0cExecutionInput> {

    @Override
    protected Set<ProcessingMessage> doBuild(L0cExecutionInput l0cExecutionInput, Map<ProductFamily, Set<FileInfo>> fileInfosByFamily, String outputFolder) {
        return buildCatalogMessages(fileInfosByFamily, l0cExecutionInput);
    }

}
