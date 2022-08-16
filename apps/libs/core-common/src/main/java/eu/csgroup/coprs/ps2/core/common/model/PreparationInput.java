package eu.csgroup.coprs.ps2.core.common.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.Instant;

@Getter
@Setter
@Accessors(chain = true)
public class PreparationInput {

    private String session;
    private String satellite;
    private String station;
    private Instant t0PdgsDate;

}
