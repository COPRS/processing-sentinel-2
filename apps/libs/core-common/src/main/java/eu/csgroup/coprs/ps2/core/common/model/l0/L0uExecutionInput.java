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

package eu.csgroup.coprs.ps2.core.common.model.l0;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Getter
@Setter
@Accessors(chain = true)
public class L0uExecutionInput extends ExecutionInput {

    private String session;

    private Map<String, String> jobOrders;

    public List<String> listJobOrders() {
        return jobOrders.keySet().stream().toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof L0uExecutionInput that)) return false;
        return Objects.equals(session, that.session) && Objects.equals(jobOrders, that.jobOrders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session, jobOrders);
    }

}
