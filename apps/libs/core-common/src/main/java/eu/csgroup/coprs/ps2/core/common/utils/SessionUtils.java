/*
 * Copyright 2023 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
