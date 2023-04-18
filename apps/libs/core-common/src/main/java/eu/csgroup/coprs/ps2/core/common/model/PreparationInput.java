package eu.csgroup.coprs.ps2.core.common.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public abstract class PreparationInput extends CommonInput {

    private String session;

}
