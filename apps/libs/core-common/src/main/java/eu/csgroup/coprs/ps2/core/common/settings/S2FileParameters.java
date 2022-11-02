package eu.csgroup.coprs.ps2.core.common.settings;

public final class S2FileParameters {

    public static final String SAD_REGEX = "^.*AUX_SADATA.*";
    public static final String HKTM_REGEX = "^.*PRD_HKTM.*";
    public static final String DT_REGEX = "^DT.*";
    public static final String L0U_DS_REGEX = "^S2[A|B]_OPER_.*_N00\\.00$";
    public static final String AUX_FILE_EXTENSION = ".DBL";
    public static final String L0C_DS_REGEX = "^S2[A|B]_OPER_MSI_L0__DS.*";
    public static final String L0C_GR_REGEX = "^S2[A|B]_OPER_MSI_L0__GR.*";
    public static final String L0C_GR_REGEX_TEMPLATE = "^S2[A|B]_OPER_MSI_L0__GR_.*_DXX.*";

    private S2FileParameters() {
    }

}
