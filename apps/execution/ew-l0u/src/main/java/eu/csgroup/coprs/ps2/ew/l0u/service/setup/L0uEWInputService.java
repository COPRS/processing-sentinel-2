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

package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.exception.InvalidMessageException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.ew.service.EWInputService;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Component
public class L0uEWInputService implements EWInputService<L0uExecutionInput> {

    @Override
    public L0uExecutionInput extract(ProcessingMessage processingMessage) {

        log.info("Extracting execution input from message: {}", processingMessage);

        final L0uExecutionInput l0uExecutionInput = ProcessingMessageUtils.getAdditionalField(processingMessage, MessageParameters.EXECUTION_INPUT_FIELD, L0uExecutionInput.class);

        if (l0uExecutionInput.getJobOrders().size() != 1) {
            throw new InvalidMessageException("Invalid Job Order count");
        }

        log.info("Finished extracting execution input from message: {}", processingMessage);

        return l0uExecutionInput;
    }

    @Override
    public Set<String> getTaskInputs(L0uExecutionInput executionInput) {
        return executionInput.getFiles().stream().map(FileInfo::getObsName).collect(Collectors.toSet());
    }

}
