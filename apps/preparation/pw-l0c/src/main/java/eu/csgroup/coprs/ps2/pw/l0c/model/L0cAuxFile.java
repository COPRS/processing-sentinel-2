package eu.csgroup.coprs.ps2.pw.l0c.model;

import eu.csgroup.coprs.ps2.core.common.model.aux.AuxFile;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxFolder;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum L0cAuxFile implements AuxFile {

    AUX_UT1UTC(AuxProductType.AUX_UT1UTC, AuxFolder.L0_IERS, null),
    GIP_ATMIMA(AuxProductType.GIP_ATMIMA, AuxFolder.L0_GIPP, "gipp_atmima"),
    GIP_ATMSAD(AuxProductType.GIP_ATMSAD, AuxFolder.L0_GIPP, "gipp_atmsad"),
    GIP_BLINDP(AuxProductType.GIP_BLINDP, AuxFolder.L0_GIPP, "gipp_blindp"),
    GIP_CLOINV(AuxProductType.GIP_CLOINV, AuxFolder.L0_GIPP, "gipp_cloinv"),
    GIP_DATATI(AuxProductType.GIP_DATATI, AuxFolder.L0_GIPP, "gipp_datati"),
    GIP_INVLOC(AuxProductType.GIP_INVLOC, AuxFolder.L0_GIPP, "gipp_invloc"),
    GIP_JP2KPA(AuxProductType.GIP_JP2KPA, AuxFolder.L0_GIPP, "gipp_jp2kpa"),
    GIP_LREXTR(AuxProductType.GIP_LREXTR, AuxFolder.L0_GIPP, "gipp_lrextr"),
    GIP_OLQCPA(AuxProductType.GIP_OLQCPA, AuxFolder.L0_GIPP, "gipp_olqcpa"),
    GIP_PROBAS(AuxProductType.GIP_PROBAS, AuxFolder.L0_GIPP, "gipp_probas"),
    GIP_R2ABCA(AuxProductType.GIP_R2ABCA, AuxFolder.L0_GIPP, "gipp_r2abca"),
    GIP_SPAMOD(AuxProductType.GIP_SPAMOD, AuxFolder.L0_GIPP, "gipp_spamod"),
    GIP_VIEDIR(AuxProductType.GIP_VIEDIR, AuxFolder.L0_GIPP, "gipp_viedir");

    private final AuxProductType auxProductType;
    private final AuxFolder folder;
    private final String placeHolder;

}
