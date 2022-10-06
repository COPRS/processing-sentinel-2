package eu.csgroup.coprs.ps2.pw.l0c.model;

import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum L0cAuxFile {

    AUX_UT1UTC(AuxProductType.AUX_UT1UTC, L0cAuxFolder.IERS, null),
    GIP_ATMIMA(AuxProductType.GIP_ATMIMA, L0cAuxFolder.GIPP, "gipp_atmima"),
    GIP_ATMSAD(AuxProductType.GIP_ATMSAD, L0cAuxFolder.GIPP, "gipp_atmsad"),
    GIP_BLINDP(AuxProductType.GIP_BLINDP, L0cAuxFolder.GIPP, "gipp_blindp"),
    GIP_CLOINV(AuxProductType.GIP_CLOINV, L0cAuxFolder.GIPP, "gipp_cloinv"),
    GIP_DATATI(AuxProductType.GIP_DATATI, L0cAuxFolder.GIPP, "gipp_datati"),
    GIP_INVLOC(AuxProductType.GIP_INVLOC, L0cAuxFolder.GIPP, "gipp_invloc"),
    GIP_JP2KPA(AuxProductType.GIP_JP2KPA, L0cAuxFolder.GIPP, "gipp_jp2kpa"),
    GIP_LREXTR(AuxProductType.GIP_LREXTR, L0cAuxFolder.GIPP, "gipp_lrextr"),
    GIP_OLQCPA(AuxProductType.GIP_OLQCPA, L0cAuxFolder.GIPP, "gipp_olqcpa"),
    GIP_PROBAS(AuxProductType.GIP_PROBAS, L0cAuxFolder.GIPP, "gipp_probas"),
    GIP_R2ABCA(AuxProductType.GIP_R2ABCA, L0cAuxFolder.GIPP, "gipp_r2abca"),
    GIP_SPAMOD(AuxProductType.GIP_SPAMOD, L0cAuxFolder.GIPP, "gipp_spamod"),
    GIP_VIEDIR(AuxProductType.GIP_VIEDIR, L0cAuxFolder.GIPP, "gipp_viedir");

    private final AuxProductType auxProductType;
    private final L0cAuxFolder folder;
    private final String placeHolder;

}
