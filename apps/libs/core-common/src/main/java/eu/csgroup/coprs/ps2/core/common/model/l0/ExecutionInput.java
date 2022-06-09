package eu.csgroup.coprs.ps2.core.common.model.l0;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class ExecutionInput {

    private String satellite;
    private String station;

    private Set<FileInfo> files;

}
