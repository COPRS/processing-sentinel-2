package eu.csgroup.coprs.ps2.core.common.settings;

public final class FileParameters {

    // TODO move and rename

    public static final String SAD_REGEX = "^.*AUX_SADATA.*";
    public static final String HKTM_REGEX = "^.*PRD_HKTM.*";
    public static final String DT_REGEX = "^DT.*";
    public static final String DS_REGEX = "^S2[A|B]_OPER_.*_N00\\.00$";
    public static final String DS_SUFFIX = "_N00.00";
    public static final String DEM_REGEX = "^.*DEM_GLOBEF.*";
    public static final String AUX_FILE_EXTENSION = ".DBL";

    private FileParameters() {
    }

}
