package eu.csgroup.coprs.ps2.ew.l0c.settings;

import java.util.Set;

public final class L0cFolderParameters {

    public static final String WORKSPACE_PATH = "/workspace"; //NOSONAR
    public static final String JOB_ORDERS_PATH = WORKSPACE_PATH + "/JobOrders";
    public static final String APP_DATA_PATH = WORKSPACE_PATH + "/app_data";
    public static final String GSE_REPORT_PATH = APP_DATA_PATH + "/step_GSE/launch_01";
    public static final String WP_DATA_PATH = APP_DATA_PATH + "/wp_data";
    public static final String STEPS_DATA_PATH = WORKSPACE_PATH + "/steps_data";
    public static final String DS_PATH = STEPS_DATA_PATH + "/DS";
    public static final String GR_PATH = STEPS_DATA_PATH + "/GR";
    public static final String GR_DB_PATH = GR_PATH + "/DB1";
    public static final String GIPP_PATH = STEPS_DATA_PATH + "/GIPP";
    public static final String IERS_PATH = STEPS_DATA_PATH + "/IERS";
    public static final String WORKING_PATH = STEPS_DATA_PATH + "/WORKING";
    public static final String REP_ISP_INFOS_PATH = STEPS_DATA_PATH + "/REP_ISP_INFOS";
    public static final String GIP_ATMIMA_PATH = GIPP_PATH + "/GIP_ATMIMA";
    public static final String GIP_ATMSAD_PATH = GIPP_PATH + "/GIP_ATMSAD";
    public static final String GIP_BLINDP_PATH = GIPP_PATH + "/GIP_BLINDP";
    public static final String GIP_CLOINV_PATH = GIPP_PATH + "/GIP_CLOINV";
    public static final String GIP_DATATI_PATH = GIPP_PATH + "/GIP_DATATI";
    public static final String GIP_INVLOC_PATH = GIPP_PATH + "/GIP_INVLOC";
    public static final String GIP_JP2KPA_PATH = GIPP_PATH + "/GIP_JP2KPA";
    public static final String GIP_LREXTR_PATH = GIPP_PATH + "/GIP_LREXTR";
    public static final String GIP_OLQCPA_PATH = GIPP_PATH + "/GIP_OLQCPA";
    public static final String GIP_PROBAS_PATH = GIPP_PATH + "/GIP_PROBAS";
    public static final String GIP_R2ABCA_PATH = GIPP_PATH + "/GIP_R2ABCA";
    public static final String GIP_SPAMOD_PATH = GIPP_PATH + "/GIP_SPAMOD";
    public static final String GIP_VIEDIR_PATH = GIPP_PATH + "/GIP_VIEDIR";
    public static final String AUX_UT1UTC_PATH = IERS_PATH + "/AUX_UT1UTC";
    public static final String OLQC_PATH = APP_DATA_PATH + "/step_OLQC";

    public static final Set<String> WORKSPACE_FOLDERS = Set.of(
            JOB_ORDERS_PATH,
            APP_DATA_PATH,
            GSE_REPORT_PATH,
            WP_DATA_PATH,
            STEPS_DATA_PATH,
            DS_PATH,
            GR_PATH,
            WORKING_PATH,
            REP_ISP_INFOS_PATH,
            GIPP_PATH,
            GIP_ATMIMA_PATH,
            GIP_ATMSAD_PATH,
            GIP_BLINDP_PATH,
            GIP_CLOINV_PATH,
            GIP_DATATI_PATH,
            GIP_INVLOC_PATH,
            GIP_JP2KPA_PATH,
            GIP_LREXTR_PATH,
            GIP_OLQCPA_PATH,
            GIP_PROBAS_PATH,
            GIP_R2ABCA_PATH,
            GIP_SPAMOD_PATH,
            GIP_VIEDIR_PATH,
            IERS_PATH,
            AUX_UT1UTC_PATH,
            OLQC_PATH
    );

    private L0cFolderParameters() {
    }

}
