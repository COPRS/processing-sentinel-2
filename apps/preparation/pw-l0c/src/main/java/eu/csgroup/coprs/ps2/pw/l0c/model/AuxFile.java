package eu.csgroup.coprs.ps2.pw.l0c.model;

import eu.csgroup.coprs.ps2.core.catalog.model.AuxProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuxFile {

    AUX_UT1UTC(AuxProductType.AUX_UT1UTC, AuxFolders.IERS, null),
    GIP_ATMIMA(AuxProductType.GIP_ATMIMA, AuxFolders.GIPP, "gipp_atmima"),
    GIP_ATMSAD(AuxProductType.GIP_ATMSAD, AuxFolders.GIPP, "gipp_atmsad"),
    GIP_BLINDP(AuxProductType.GIP_BLINDP, AuxFolders.GIPP, "gipp_blindp"),
    GIP_CLOINV(AuxProductType.GIP_CLOINV, AuxFolders.GIPP, "gipp_cloinv"),
    GIP_DATATI(AuxProductType.GIP_DATATI, AuxFolders.GIPP, "gipp_datati"),
    GIP_INVLOC(AuxProductType.GIP_INVLOC, AuxFolders.GIPP, "gipp_invloc"),
    GIP_JP2KPA(AuxProductType.GIP_JP2KPA, AuxFolders.GIPP, "gipp_jp2kpa"),
    GIP_LREXTR(AuxProductType.GIP_LREXTR, AuxFolders.GIPP, "gipp_lrextr"),
    GIP_OLQCPA(AuxProductType.GIP_OLQCPA, AuxFolders.GIPP, "gipp_olqcpa"),
    GIP_PROBAS(AuxProductType.GIP_PROBAS, AuxFolders.GIPP, "gipp_probas"),
    GIP_R2ABCA(AuxProductType.GIP_R2ABCA, AuxFolders.GIPP, "gipp_r2abca"),
    GIP_SPAMOD(AuxProductType.GIP_SPAMOD, AuxFolders.GIPP, "gipp_spamod"),
    GIP_VIEDIR(AuxProductType.GIP_VIEDIR, AuxFolders.GIPP, "gipp_viedir");

    private final AuxProductType auxProductType;
    private final AuxFolders folder;
    private final String placeHolder;

}
