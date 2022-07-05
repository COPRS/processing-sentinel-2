package eu.csgroup.coprs.ps2.core.common.config;

import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;

import java.time.format.DateTimeFormatterBuilder;

public class CustomInstantSerializer extends InstantSerializer { //NOSONAR

    public CustomInstantSerializer() {
        super(InstantSerializer.INSTANCE, false, true, new DateTimeFormatterBuilder().appendInstant(6).toFormatter());
    }

}
