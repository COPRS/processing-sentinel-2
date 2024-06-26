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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.csgroup.coprs.ps2.core.common.exception.InvalidMessageException;
import eu.csgroup.coprs.ps2.core.common.model.CommonInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.Mission;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public final class ProcessingMessageUtils {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ProcessingMessage create() {
        return new ProcessingMessage()
                .setUid(UUID.randomUUID().toString())
                .setCreationDate(DateUtils.toLongDate(Instant.now()))
                .setMissionId(Mission.S2.getValue())
                .setPodName(System.getenv("HOSTNAME"))
                .setRetryCounter(0);
    }

    public static boolean hasAdditionalField(ProcessingMessage processingMessage, String fieldName) {
        final Map<String, Object> additionalFields = processingMessage.getAdditionalFields();
        return additionalFields.containsKey(fieldName) && additionalFields.get(fieldName) != null;
    }

    public static <T> T getAdditionalField(ProcessingMessage processingMessage, String fieldName, Class<T> clazz) {

        if (!hasAdditionalField(processingMessage, fieldName)) {
            throw new InvalidMessageException("Message does not contain additional field '" + fieldName + "'");
        }

        return objectMapper.convertValue(processingMessage.getAdditionalFields().get(fieldName), clazz);
    }

    public static boolean hasMetadata(ProcessingMessage processingMessage, String fieldName) {
        final Map<String, Object> metadata = processingMessage.getMetadata();
        return metadata.containsKey(fieldName) && metadata.get(fieldName) != null;
    }

    public static <T> T getMetadata(ProcessingMessage processingMessage, String fieldName, Class<T> clazz) {

        if (!hasMetadata(processingMessage, fieldName)) {
            throw new InvalidMessageException("Message does not contain metadata '" + fieldName + "'");
        }

        return objectMapper.convertValue(processingMessage.getMetadata().get(fieldName), clazz);
    }

    public static Instant getT0PdgsDate(ProcessingMessage processingMessage) {
        // Looking for t0 in metadata first, where it's supposed to be. And in additionalFields after that, just in case
        Instant t0PdgsDate = Instant.EPOCH;
        if (ProcessingMessageUtils.hasMetadata(processingMessage, MessageParameters.T0_PDGS_DATE_FIELD)) {
            t0PdgsDate = ProcessingMessageUtils.getMetadata(processingMessage, MessageParameters.T0_PDGS_DATE_FIELD, Instant.class);
        } else if (ProcessingMessageUtils.hasAdditionalField(processingMessage, MessageParameters.T0_PDGS_DATE_FIELD)) {
            t0PdgsDate = ProcessingMessageUtils.getAdditionalField(processingMessage, MessageParameters.T0_PDGS_DATE_FIELD, Instant.class);
        }
        return t0PdgsDate;
    }

    public static CommonInput getCommonInput(ProcessingMessage processingMessage) {
        CommonInput commonInput = null;
        if (ProcessingMessageUtils.hasAdditionalField(processingMessage, MessageParameters.PREPARATION_INPUT_FIELD)) {
            commonInput = ProcessingMessageUtils.getAdditionalField(processingMessage, MessageParameters.PREPARATION_INPUT_FIELD, CommonInput.class);
        } else if (ProcessingMessageUtils.hasAdditionalField(processingMessage, MessageParameters.EXECUTION_INPUT_FIELD)) {
            commonInput = ProcessingMessageUtils.getAdditionalField(processingMessage, MessageParameters.EXECUTION_INPUT_FIELD, CommonInput.class);
        }
        return commonInput;
    }

    private ProcessingMessageUtils() {
    }

}
