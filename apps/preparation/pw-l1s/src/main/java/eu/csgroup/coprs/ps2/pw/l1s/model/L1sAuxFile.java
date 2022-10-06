package eu.csgroup.coprs.ps2.pw.l1s.model;

import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum L1sAuxFile {

    AUX_CAMSFO(AuxProductType.AUX_CAMSFO, L1sAuxFolder.S2IPF_CAMS),
    AUX_ECMWFD(AuxProductType.AUX_ECMWFD, L1sAuxFolder.S2IPF_ECMWF),
    AUX_UT1UTC(AuxProductType.AUX_UT1UTC, L1sAuxFolder.S2IPF_IERS),
    GIP_BLINDP(AuxProductType.GIP_BLINDP, L1sAuxFolder.S2IPF_GIPP),
    GIP_CLOINV(AuxProductType.GIP_CLOINV, L1sAuxFolder.S2IPF_GIPP),
    GIP_CLOPAR(AuxProductType.GIP_CLOPAR, L1sAuxFolder.S2IPF_GIPP),
    GIP_CONVER(AuxProductType.GIP_CONVER, L1sAuxFolder.S2IPF_GIPP),
    GIP_DATATI(AuxProductType.GIP_DATATI, L1sAuxFolder.S2IPF_GIPP),
    GIP_DECOMP(AuxProductType.GIP_DECOMP, L1sAuxFolder.S2IPF_GIPP),
    GIP_EARMOD(AuxProductType.GIP_EARMOD, L1sAuxFolder.S2IPF_GIPP),
    GIP_ECMWFP(AuxProductType.GIP_ECMWFP, L1sAuxFolder.S2IPF_GIPP),
    GIP_G2PARA(AuxProductType.GIP_G2PARA, L1sAuxFolder.S2IPF_GIPP),
    GIP_G2PARE(AuxProductType.GIP_G2PARE, L1sAuxFolder.S2IPF_GIPP),
    GIP_GEOPAR(AuxProductType.GIP_GEOPAR, L1sAuxFolder.S2IPF_GIPP),
    GIP_INTDET(AuxProductType.GIP_INTDET, L1sAuxFolder.S2IPF_GIPP),
    GIP_INVLOC(AuxProductType.GIP_INVLOC, L1sAuxFolder.S2IPF_GIPP),
    GIP_JP2KPA(AuxProductType.GIP_JP2KPA, L1sAuxFolder.S2IPF_GIPP),
    GIP_LREXTR(AuxProductType.GIP_LREXTR, L1sAuxFolder.S2IPF_GIPP),
    GIP_MASPAR(AuxProductType.GIP_MASPAR, L1sAuxFolder.S2IPF_GIPP),
    GIP_OLQCPA(AuxProductType.GIP_OLQCPA, L1sAuxFolder.S2IPF_GIPP),
    GIP_PRDLOC(AuxProductType.GIP_PRDLOC, L1sAuxFolder.S2IPF_GIPP),
    GIP_PROBAS(AuxProductType.GIP_PROBAS, L1sAuxFolder.S2IPF_GIPP),
    GIP_R2ABCA(AuxProductType.GIP_R2ABCA, L1sAuxFolder.S2IPF_GIPP),
    GIP_R2BINN(AuxProductType.GIP_R2BINN, L1sAuxFolder.S2IPF_GIPP),
    GIP_R2CRCO(AuxProductType.GIP_R2CRCO, L1sAuxFolder.S2IPF_GIPP),
    GIP_R2DECT(AuxProductType.GIP_R2DECT, L1sAuxFolder.S2IPF_GIPP),
    GIP_R2DEFI(AuxProductType.GIP_R2DEFI, L1sAuxFolder.S2IPF_GIPP),
    GIP_R2DENT(AuxProductType.GIP_R2DENT, L1sAuxFolder.S2IPF_GIPP),
    GIP_R2DEPI(AuxProductType.GIP_R2DEPI, L1sAuxFolder.S2IPF_GIPP),
    GIP_R2EOB2(AuxProductType.GIP_R2EOB2, L1sAuxFolder.S2IPF_GIPP),
    GIP_R2EQOG(AuxProductType.GIP_R2EQOG, L1sAuxFolder.S2IPF_GIPP),
    GIP_R2L2NC(AuxProductType.GIP_R2L2NC, L1sAuxFolder.S2IPF_GIPP),
    GIP_R2MACO(AuxProductType.GIP_R2MACO, L1sAuxFolder.S2IPF_GIPP),
    GIP_R2NOMO(AuxProductType.GIP_R2NOMO, L1sAuxFolder.S2IPF_GIPP),
    GIP_R2PARA(AuxProductType.GIP_R2PARA, L1sAuxFolder.S2IPF_GIPP),
    GIP_R2SWIR(AuxProductType.GIP_R2SWIR, L1sAuxFolder.S2IPF_GIPP),
    GIP_R2WAFI(AuxProductType.GIP_R2WAFI, L1sAuxFolder.S2IPF_GIPP),
    GIP_RESPAR(AuxProductType.GIP_RESPAR, L1sAuxFolder.S2IPF_GIPP),
    GIP_SPAMOD(AuxProductType.GIP_SPAMOD, L1sAuxFolder.S2IPF_GIPP),
    GIP_TILPAR(AuxProductType.GIP_TILPAR, L1sAuxFolder.S2IPF_GIPP),
    GIP_VIEDIR(AuxProductType.GIP_VIEDIR, L1sAuxFolder.S2IPF_GIPP);

    private final AuxProductType auxProductType;
    private final L1sAuxFolder folder;

}
