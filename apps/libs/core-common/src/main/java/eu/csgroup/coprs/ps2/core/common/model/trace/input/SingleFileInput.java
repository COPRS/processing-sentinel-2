package eu.csgroup.coprs.ps2.core.common.model.trace.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SingleFileInput implements TaskInput {

    private String filenameString;

}
