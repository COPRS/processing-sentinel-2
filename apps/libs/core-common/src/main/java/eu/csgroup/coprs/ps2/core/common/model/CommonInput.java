package eu.csgroup.coprs.ps2.core.common.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class CommonInput {

    private String satellite;
    private String station;
    private Instant startTime;
    private Instant stopTime;
    private Instant t0PdgsDate;
    private Set<String> customTaskInputs;

}
