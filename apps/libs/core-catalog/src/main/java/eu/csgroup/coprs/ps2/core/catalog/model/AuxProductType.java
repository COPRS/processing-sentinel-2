package eu.csgroup.coprs.ps2.core.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuxProductType {

    AUX_UT1UTC(".txt"),
    DEM_GLOBEF(null),
    GIP_ATMIMA(".xml"),
    GIP_ATMSAD(".xml"),
    GIP_BLINDP(".xml"),
    GIP_CLOINV(".xml"),
    GIP_DATATI(".xml"),
    GIP_INVLOC(".xml"),
    GIP_JP2KPA(".xml"),
    GIP_LREXTR(".xml"),
    GIP_OLQCPA(".zip"),
    GIP_PROBAS(".xml"),
    GIP_R2ABCA(".xml"),
    GIP_SPAMOD(".xml"),
    GIP_VIEDIR(".xml");

    private final String extension;

}
