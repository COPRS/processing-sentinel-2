package eu.csgroup.coprs.ps2.pw.l1s.model;

import eu.csgroup.coprs.ps2.core.common.model.aux.AuxFile;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxFolder;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum L1sAuxFile implements AuxFile {

    AUX_CAMSFO(AuxProductType.AUX_CAMSFO, AuxFolder.S2IPF_CAMS),
    AUX_ECMWFD(AuxProductType.AUX_ECMWFD, AuxFolder.S2IPF_ECMWF),
    AUX_UT1UTC(AuxProductType.AUX_UT1UTC, AuxFolder.S2IPF_IERS),
    GIP_BLINDP(AuxProductType.GIP_BLINDP, AuxFolder.S2IPF_GIPP),
    GIP_CLOINV(AuxProductType.GIP_CLOINV, AuxFolder.S2IPF_GIPP),
    GIP_CLOPAR(AuxProductType.GIP_CLOPAR, AuxFolder.S2IPF_GIPP),
    GIP_CONVER(AuxProductType.GIP_CONVER, AuxFolder.S2IPF_GIPP),
    GIP_DATATI(AuxProductType.GIP_DATATI, AuxFolder.S2IPF_GIPP),
    GIP_DECOMP(AuxProductType.GIP_DECOMP, AuxFolder.S2IPF_GIPP),
    GIP_EARMOD(AuxProductType.GIP_EARMOD, AuxFolder.S2IPF_GIPP),
    GIP_ECMWFP(AuxProductType.GIP_ECMWFP, AuxFolder.S2IPF_GIPP),
    GIP_G2PARA(AuxProductType.GIP_G2PARA, AuxFolder.S2IPF_GIPP),
    GIP_G2PARE(AuxProductType.GIP_G2PARE, AuxFolder.S2IPF_GIPP),
    GIP_GEOPAR(AuxProductType.GIP_GEOPAR, AuxFolder.S2IPF_GIPP),
    GIP_HRTPAR(AuxProductType.GIP_HRTPAR, AuxFolder.S2IPF_GIPP),
    GIP_INTDET(AuxProductType.GIP_INTDET, AuxFolder.S2IPF_GIPP),
    GIP_INVLOC(AuxProductType.GIP_INVLOC, AuxFolder.S2IPF_GIPP),
    GIP_JP2KPA(AuxProductType.GIP_JP2KPA, AuxFolder.S2IPF_GIPP),
    GIP_LREXTR(AuxProductType.GIP_LREXTR, AuxFolder.S2IPF_GIPP),
    GIP_MASPAR(AuxProductType.GIP_MASPAR, AuxFolder.S2IPF_GIPP),
    GIP_OLQCPA(AuxProductType.GIP_OLQCPA, AuxFolder.S2IPF_GIPP),
    GIP_PRDLOC(AuxProductType.GIP_PRDLOC, AuxFolder.S2IPF_GIPP),
    GIP_PROBAS(AuxProductType.GIP_PROBAS, AuxFolder.S2IPF_GIPP),
    GIP_R2ABCA(AuxProductType.GIP_R2ABCA, AuxFolder.S2IPF_GIPP),
    GIP_R2BINN(AuxProductType.GIP_R2BINN, AuxFolder.S2IPF_GIPP),
    GIP_R2CRCO(AuxProductType.GIP_R2CRCO, AuxFolder.S2IPF_GIPP),
    GIP_R2DECT(AuxProductType.GIP_R2DECT, AuxFolder.S2IPF_GIPP),
    GIP_R2DEFI(AuxProductType.GIP_R2DEFI, AuxFolder.S2IPF_GIPP),
    GIP_R2DENT(AuxProductType.GIP_R2DENT, AuxFolder.S2IPF_GIPP),
    GIP_R2DEPI(AuxProductType.GIP_R2DEPI, AuxFolder.S2IPF_GIPP),
    GIP_R2EOB2(AuxProductType.GIP_R2EOB2, AuxFolder.S2IPF_GIPP),
    GIP_R2EQOG(AuxProductType.GIP_R2EQOG, AuxFolder.S2IPF_GIPP),
    GIP_R2L2NC(AuxProductType.GIP_R2L2NC, AuxFolder.S2IPF_GIPP),
    GIP_R2MACO(AuxProductType.GIP_R2MACO, AuxFolder.S2IPF_GIPP),
    GIP_R2NOMO(AuxProductType.GIP_R2NOMO, AuxFolder.S2IPF_GIPP),
    GIP_R2PARA(AuxProductType.GIP_R2PARA, AuxFolder.S2IPF_GIPP),
    GIP_R2SWIR(AuxProductType.GIP_R2SWIR, AuxFolder.S2IPF_GIPP),
    GIP_R2WAFI(AuxProductType.GIP_R2WAFI, AuxFolder.S2IPF_GIPP),
    GIP_RESPAR(AuxProductType.GIP_RESPAR, AuxFolder.S2IPF_GIPP),
    GIP_SPAMOD(AuxProductType.GIP_SPAMOD, AuxFolder.S2IPF_GIPP),
    GIP_TILPAR(AuxProductType.GIP_TILPAR, AuxFolder.S2IPF_GIPP),
    GIP_VIEDIR(AuxProductType.GIP_VIEDIR, AuxFolder.S2IPF_GIPP);

    private final AuxProductType auxProductType;
    private final AuxFolder folder;

}
