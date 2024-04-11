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

package eu.csgroup.coprs.ps2.core.ew.service.l2;

import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWInputService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class L2EWInputService implements EWInputService<L2ExecutionInput> {

    @Override
    public L2ExecutionInput extract(ProcessingMessage processingMessage) {

        log.info("Extracting execution input from message: {}", processingMessage);

        final L2ExecutionInput executionInput = ProcessingMessageUtils.getAdditionalField(
                processingMessage,
                MessageParameters.EXECUTION_INPUT_FIELD,
                L2ExecutionInput.class
        );

        log.info("Finished extracting execution input from message: {}", processingMessage);

        return executionInput;
    }

}
