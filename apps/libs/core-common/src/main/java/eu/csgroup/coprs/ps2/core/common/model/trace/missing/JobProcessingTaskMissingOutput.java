package eu.csgroup.coprs.ps2.core.common.model.trace.missing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobProcessingTaskMissingOutput implements TaskMissingOutput {

    private ProductMetadata productMetadataCustomObject;
    private boolean endToEndProductBoolean;
    private int estimatedCountInteger;

}
