package eu.csgroup.coprs.ps2.pw.l0c.model;

import eu.csgroup.coprs.ps2.core.catalog.model.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cAuxFolders;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuxFile {

    // TODO

    AUX_UT1UTC(AuxProductType.AUX_UT1UTC, 1, L0cAuxFolders.IERS, ""),
    GIP_ATMIMA(AuxProductType.GIP_ATMIMA, 1, L0cAuxFolders.GIPP, ""),
    GIP_ATMSAD(AuxProductType.GIP_ATMSAD, 1, L0cAuxFolders.GIPP, ""),
    GIP_BLINDP(AuxProductType.GIP_BLINDP, 1, L0cAuxFolders.GIPP, ""),
    GIP_CLOINV(AuxProductType.GIP_CLOINV, 1, L0cAuxFolders.GIPP, ""),
    GIP_DATATI(AuxProductType.GIP_DATATI, 1, L0cAuxFolders.GIPP, ""),
    GIP_INVLOC(AuxProductType.GIP_INVLOC, 1, L0cAuxFolders.GIPP, ""),
    GIP_JP2KPA(AuxProductType.GIP_JP2KPA, 1, L0cAuxFolders.GIPP, ""),
    GIP_LREXTR(AuxProductType.GIP_LREXTR, 1, L0cAuxFolders.GIPP, ""),
    GIP_OLQCPA(AuxProductType.GIP_OLQCPA, 1, L0cAuxFolders.GIPP, ""),
    GIP_PROBAS(AuxProductType.GIP_PROBAS, 1, L0cAuxFolders.GIPP, ""),
    GIP_R2ABCA(AuxProductType.GIP_R2ABCA, 1, L0cAuxFolders.GIPP, ""),
    GIP_SPAMOD(AuxProductType.GIP_SPAMOD, 1, L0cAuxFolders.GIPP, ""),
    GIP_VIEDIR(AuxProductType.GIP_VIEDIR, 13, L0cAuxFolders.GIPP, "");

    private final AuxProductType auxProductType;
    private final int bandCount;
    private final L0cAuxFolders folder;
    private final String placeHolder;

}
