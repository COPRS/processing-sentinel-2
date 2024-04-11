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

package eu.csgroup.coprs.ps2.pw.l0c.service.setup;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.exception.InvalidInputException;
import eu.csgroup.coprs.ps2.core.common.exception.InvalidMessageException;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cPreparationInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.common.utils.ProcessingMessageUtils;
import eu.csgroup.coprs.ps2.core.pw.service.PWInputManagementService;
import eu.csgroup.coprs.ps2.pw.l0c.service.prepare.L0cDatastripManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@Component
public class L0cPWInputManagementService implements PWInputManagementService {

    private final L0cDatastripManagementService managementService;
    private final SharedProperties sharedProperties;

    public L0cPWInputManagementService(L0cDatastripManagementService managementService, SharedProperties sharedProperties) {
        this.managementService = managementService;
        this.sharedProperties = sharedProperties;
    }

    @Override
    public UUID manageInput(ProcessingMessage processingMessage) {

        if (isPreparationInput(processingMessage)) {

            final L0cPreparationInput l0cPreparationInput = extractInput(processingMessage);

            log.info("Preparing datastrips for input folder {}", l0cPreparationInput.getInputFolder());

            final List<Path> datastrips = getDatastrips(l0cPreparationInput.getInputFolder());

            log.info("Found {} datastrips to prepare", datastrips.size());

            datastrips.forEach(datastripPath -> managementService.create(datastripPath, l0cPreparationInput.getSatellite(), l0cPreparationInput.getStation(),
                    l0cPreparationInput.getT0PdgsDate()));
        }

        return null;
    }

    private boolean isPreparationInput(ProcessingMessage processingMessage) {
        return ProcessingMessageUtils.hasAdditionalField(processingMessage, MessageParameters.PREPARATION_INPUT_FIELD);
    }

    private L0cPreparationInput extractInput(ProcessingMessage processingMessage) {

        log.info("Extracting preparation input from message: {}", processingMessage);

        final L0cPreparationInput l0cPreparationInput =
                ProcessingMessageUtils.getAdditionalField(processingMessage, MessageParameters.PREPARATION_INPUT_FIELD, L0cPreparationInput.class);

        if (!StringUtils.hasText(l0cPreparationInput.getInputFolder()) || !StringUtils.hasText(l0cPreparationInput.getSession())) {
            throw new InvalidMessageException("Invalid Preparation Data");
        }

        return l0cPreparationInput;
    }

    private List<Path> getDatastrips(String inputFolder) {

        List<Path> datastripPaths = new ArrayList<>();

        final Path l0uDumpPath = Paths.get(sharedProperties.getSharedFolderRoot(), inputFolder);

        FileOperationUtils.findFolders(l0uDumpPath, S2FileParameters.DT_REGEX)
                .forEach(path -> {
                    final List<Path> datastripFolders = FileOperationUtils.findFolders(path.resolve("DS"), S2FileParameters.L0U_DS_REGEX);
                    // We should have exactly one DS in each DT
                    if (datastripFolders.size() != 1) {
                        throw new InvalidInputException("Invalid number of DS in DT: " + path);
                    }
                    datastripPaths.add(datastripFolders.get(0));
                });

        return datastripPaths;
    }

}
