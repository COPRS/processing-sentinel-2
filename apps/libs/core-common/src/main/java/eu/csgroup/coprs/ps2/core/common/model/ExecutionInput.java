package eu.csgroup.coprs.ps2.core.common.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public abstract class ExecutionInput extends CommonInput {

    private Set<FileInfo> files;

}
