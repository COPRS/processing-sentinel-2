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

package eu.csgroup.coprs.ps2.pw.l1s.model;

import eu.csgroup.coprs.ps2.core.pw.model.PWItem;
import eu.csgroup.coprs.ps2.core.common.model.processing.DatatakeType;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
public class L1sDatastrip extends PWItem {

    private String folder;
    private DatatakeType datatakeType;
    private Map<String, Boolean> availableByGR;
    private boolean grComplete;

    public boolean allGRAvailable() {
        return availableByGR.values().stream().allMatch(Boolean::booleanValue);
    }

}
