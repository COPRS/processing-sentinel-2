package eu.csgroup.coprs.ps2.core.common.model.preparation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class PreparationInput {

    private String session;
    private String satellite;
    private String inputFolder;

}
