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

package eu.csgroup.coprs.ps2.core.common.settings;

public final class FolderParameters {

    // Local path for work files
    public static final String WORKING_FOLDER_ROOT = "/workspace"; // NOSONAR

    // Local path for temporary files
    public static final String TMP_DOWNLOAD_FOLDER = "/tmp"; // NOSONAR

    // Folders inside the workspace folder
    public static final String L0C_DS_ROOT = "INV_L0C_DS";
    public static final String L0C_GR_ROOT = "INV_L0C_GR";
    public static final String L1A_DS_ROOT = "INV_L1A_DS";
    public static final String L1A_GR_ROOT = "INV_L1A_GR";
    public static final String L1B_DS_ROOT = "INV_L1B_DS";
    public static final String L1B_GR_ROOT = "INV_L1B_GR";
    public static final String L1C_DS_ROOT = "INV_L1C_DS";
    public static final String L1C_TL_ROOT = "INV_L1C_TL";
    public static final String L2A_DS_ROOT = "INV_L2A_DS";
    public static final String L2A_TL_ROOT = "INV_L2A_TL";

    // Folder names inside the shared folder
    public static final String INPUT_FOLDER = "input";
    public static final String OUTPUT_FOLDER = "output";
    public static final String AUX_FOLDER = "aux";
    public static final String DS_FOLDER = "DS";
    public static final String GR_FOLDER = "GR";
    public static final String TL_FOLDER = "TL";
    public static final String TMP_DS_SUFFIX_L1SA = ".L1SA";
    public static final String TMP_DS_SUFFIX_L1SB = ".L1SB";

    private FolderParameters() {
    }

}
