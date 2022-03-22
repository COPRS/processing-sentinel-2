package eu.csgroup.coprs.ps2.core.common.utils;

import org.apache.commons.lang3.StringUtils;

public final class SessionUtils {

    public static String sessionFromFilename(String filename) {

        // DCS_02_L20191003131732787001008_ch1_DSDB_00005.raw
        // DCS_02_L20191003131732787001008_ch1_DSIB.xml
        // DCS_02_L20191003131732787001008_ch2_DSDB_00009.raw
        // DCS_02_L20191003131732787001008_ch2_DSIB.xml
        // DCS_05_S2B_20220413132241026648_ch2_DSDB_00083.raw
        // DCS_05_S2B_20220413132241026648_ch2_DSIB.xml
        // DCS_05_S2B_20220413132241026648_ch1_DSDB_00001.raw
        // DCS_05_S2B_20220413132241026648_ch1_DSIB.xml

        return StringUtils.substring(filename, 7, 31);
    }

    private SessionUtils() {
    }

}
