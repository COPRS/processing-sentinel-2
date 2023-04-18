package eu.csgroup.coprs.ps2.ew.l0u.settings;

import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;

import java.util.Set;

public final class L0uFolderParameters {

    // -------------------------------------------------------- Workspace ---------------------------------------------------------------------------

    public static final String WORKPLANS_PATH = FolderParameters.WORKING_FOLDER_ROOT + "/workplans";
    public static final String INPUT_PATH = FolderParameters.WORKING_FOLDER_ROOT + "/INPUT";
    public static final String CH_1_PATH = INPUT_PATH + "/ch_1";
    public static final String CH_2_PATH = INPUT_PATH + "/ch_2";
    public static final String SCENARIO_PATH = WORKPLANS_PATH + "/scenario";
    public static final String APP_DATA_PATH = SCENARIO_PATH + "/app_data";
    public static final String WP_DATA_PATH = APP_DATA_PATH + "/wp_data";
    public static final String STEPS_DATA_PATH = SCENARIO_PATH + "/steps_data";
    public static final String RAW_PATH = STEPS_DATA_PATH + "/RAW";
    public static final String WORKING_PATH = STEPS_DATA_PATH + "/WORKING";
    public static final String L0U_DUMP_PATH = STEPS_DATA_PATH + "/L0U_DUMP";
    public static final String IMGCH1_PATH = L0U_DUMP_PATH + "/imgch1";
    public static final String IMGCH2_PATH = L0U_DUMP_PATH + "/imgch2";
    public static final String MSIMERGE_PATH = L0U_DUMP_PATH + "/MSIMerge";
    public static final String MSISADMERGE_PATH = L0U_DUMP_PATH + "/MSISADMerge";
    public static final String PR_PATH = L0U_DUMP_PATH + "/PR";
    public static final String SADCH1_PATH = L0U_DUMP_PATH + "/sadch1";
    public static final String SADCH2_PATH = L0U_DUMP_PATH + "/sadch2";
    public static final String SADPDI_PATH = L0U_DUMP_PATH + "/SADPDI";

    public static final Set<String> WORKSPACE_FOLDERS = Set.of(
            INPUT_PATH,
            CH_1_PATH,
            CH_2_PATH,
            WORKPLANS_PATH,
            SCENARIO_PATH,
            APP_DATA_PATH,
            WP_DATA_PATH,
            STEPS_DATA_PATH,
            RAW_PATH,
            WORKING_PATH,
            L0U_DUMP_PATH,
            IMGCH1_PATH,
            IMGCH2_PATH,
            MSIMERGE_PATH,
            MSISADMERGE_PATH,
            PR_PATH,
            SADCH1_PATH,
            SADCH2_PATH,
            SADPDI_PATH
    );

    // -------------------------------------------------------- Install files -----------------------------------------------------------------------

    public static final String INSTALL_PATH = "/usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-3.0.4"; //NOSONAR
    public static final String INSTALL_CONF_PATH = INSTALL_PATH + "/ingestion/conf";
    public static final String INSTALL_SCRIPT_PATH = INSTALL_PATH + "/ingestion/scripts";
    public static final String INSTALL_ARCHIVE_PATH = INSTALL_CONF_PATH + "/DATA/archive";
    public static final String INSTALL_OUTPUT_FILES_PATH = INSTALL_CONF_PATH + "/DATA/outputFiles";
    public static final String SCRIPT_PATH = INSTALL_SCRIPT_PATH + "/launch_eisp_ing_typ.bash";

    // ----------------------------------------------------------------------------------------------------------------------------------------------

    private L0uFolderParameters() {
    }

}
