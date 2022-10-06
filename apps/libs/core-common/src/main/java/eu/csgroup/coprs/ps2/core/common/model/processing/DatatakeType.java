package eu.csgroup.coprs.ps2.core.common.model.processing;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static eu.csgroup.coprs.ps2.core.common.model.processing.Level.*;


@Getter
@AllArgsConstructor
public enum DatatakeType {

    NOBS(List.of(L0U, L0C, L1B, L1C, L2A)),
    VIC(List.of(L0U, L0C, L1B, L1C)),
    RAW(List.of(L0U, L0C, L1A, L1B, L1C, L2A)),
    DASC(List.of(L0U, L0C, L1A)),
    ABSR(List.of(L0U, L0C, L1A)),
    EOBS(List.of(L0U, L0C, L1A)),
    DEFAULT(List.of(L0U, L0C));

    private final List<Level> levelList;

}
