package eu.csgroup.coprs.ps2.core.common.settings;

public final class S2FileParameters {

    public static final String STATION = "2BPS"; // Comes from PS orchestrator, see if we can modify this
    public static final String SAD_REGEX = "^.*AUX_SADATA.*";
    public static final String HKTM_REGEX = "^.*PRD_HKTM.*";
    public static final String DT_REGEX = "^DT.*";
    public static final String L0U_DS_REGEX = "^S2[A|B]_OPER_.*_N00\\.00$";
    public static final String AUX_FILE_EXTENSION = ".DBL";
    public static final String L0C_DS_REGEX = "^S2[A|B]_OPER_MSI_L0__DS.*";
    public static final String L0C_GR_REGEX = "^S2[A|B]_OPER_MSI_L0__GR.*";
    public static final String L0C_GR_REGEX_TEMPLATE = "^S2[A|B]_OPER_MSI_L0__GR_.*_DXX.*";
    public static final String L1_DS_REGEX = "^S2[A|B]_OPER_MSI_L1[A|B|C]_DS_" + STATION + ".*";
    public static final String L1_GR_REGEX = "^S2[A|B]_OPER_MSI_L1[A|B|C]_GR_" + STATION + ".*";
    public static final String L1A_DS_REGEX = "^S2[A|B]_OPER_MSI_L1A_DS_" + STATION + ".*";
    public static final String L1A_GR_REGEX = "^S2[A|B]_OPER_MSI_L1A_GR_" + STATION + ".*";
    public static final String L1B_DS_REGEX = "^S2[A|B]_OPER_MSI_L1B_DS_" + STATION + ".*";
    public static final String L1B_GR_REGEX = "^S2[A|B]_OPER_MSI_L1B_GR_" + STATION + ".*";
    public static final String L1C_DS_REGEX = "^S2[A|B]_OPER_MSI_L1C_DS_" + STATION + ".*";
    public static final String L1C_GR_REGEX = "^S2[A|B]_OPER_MSI_L1C_GR_" + STATION + ".*";

    private S2FileParameters() {
    }

}
