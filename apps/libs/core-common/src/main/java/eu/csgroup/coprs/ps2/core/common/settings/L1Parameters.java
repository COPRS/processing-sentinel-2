package eu.csgroup.coprs.ps2.core.common.settings;

public final class L1Parameters {

    // Local path for temporary files
    public static final String WORKING_FOLDER_ROOT = "/workspace"; // NOSONAR
    public static final String L1A_DS_ROOT = "INV_L1A_DS";
    public static final String L1A_GR_ROOT = "INV_L1A_GR";
    public static final String L1B_DS_ROOT = "INV_L1B_DS";
    public static final String L1B_GR_ROOT = "INV_L1B_GR";
    public static final String L1C_DS_ROOT = "INV_L1C_DS";
    public static final String L1C_GR_ROOT = "INV_L1C_GR";

    // Folder names inside the shared folder
    public static final String INPUT_FOLDER = "input";
    public static final String OUTPUT_FOLDER = "output";
    public static final String AUX_FOLDER = "aux";
    public static final String DS_FOLDER = "DS";
    public static final String GR_FOLDER = "GR";
    public static final String TMP_DS_SUFFIX = ".L1S";

    private L1Parameters() {
    }

}
