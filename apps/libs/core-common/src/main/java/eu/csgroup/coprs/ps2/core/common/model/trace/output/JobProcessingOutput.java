package eu.csgroup.coprs.ps2.core.common.model.trace.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class JobProcessingOutput implements TaskOutput {

    private Set<String> filenameStrings;
    private Instant t0PdgsDate;

}
