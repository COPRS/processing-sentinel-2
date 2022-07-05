package eu.csgroup.coprs.ps2.core.common.model.trace;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.csgroup.coprs.ps2.core.common.config.CustomInstantSerializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TraceLogger {

    private static final ObjectMapper objectMapper;

    static {

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(new CustomInstantSerializer());

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(javaTimeModule);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static void log(Trace trace) {

        try {
            log.info(objectMapper.writeValueAsString(trace));
        } catch (JsonProcessingException e) {
            log.error("Unable to log trace " + trace.toString(), e);
        }

    }

    private TraceLogger() {
    }

}
