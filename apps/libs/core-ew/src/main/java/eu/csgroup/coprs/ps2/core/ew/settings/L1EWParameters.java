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

package eu.csgroup.coprs.ps2.core.ew.settings;

public final class L1EWParameters {

    public static final String SCRIPT_NAME = "/wrapper/orchestrator/scripts/LaunchIPF.sh"; // NOSONAR
    public static final String VERSION_FILE = "/wrapper/orchestrator/scripts/config/IDPSC_EXE_export.json"; // NOSONAR
    public static final String GRI_FILE_LIST_PATH = "L1A/GET_GRI/output/GRI_LIST_FILE/gri_list_file.xml";

    private L1EWParameters() {
    }

}
