package eu.csgroup.coprs.ps2.core.common.model.aux;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public enum AuxProductType {

    AUX_UT1UTC(".txt", null),
    DEM_GLOBEF(null, null),
    GIP_ATMIMA(".xml", null),
    GIP_ATMSAD(".xml", null),
    GIP_BLINDP(".xml", null),
    GIP_CLOINV(".xml", null),
    GIP_DATATI(".xml", null),
    GIP_INVLOC(".xml", null),
    GIP_JP2KPA(".xml", null),
    GIP_LREXTR(".xml", null),
    GIP_OLQCPA(".zip", null),
    GIP_PROBAS(".xml", null),
    GIP_R2ABCA(".xml", null),
    GIP_SPAMOD(".xml", null),
    GIP_VIEDIR(".xml", List.of("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "8A"));

    private final String extension;
    private final List<String> bandList;

}
