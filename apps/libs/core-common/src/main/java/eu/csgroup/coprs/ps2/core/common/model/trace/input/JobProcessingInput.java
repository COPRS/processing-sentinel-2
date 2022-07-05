package eu.csgroup.coprs.ps2.core.common.model.trace.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class JobProcessingInput implements TaskInput {

    private Set<String> filenameStrings;

}
