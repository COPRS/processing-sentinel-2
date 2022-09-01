package eu.csgroup.coprs.ps2.core.common.model.trace.output;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JobGeneratorOutput implements TaskOutput {

    private List<String> jobOrderIdStrings;

}
