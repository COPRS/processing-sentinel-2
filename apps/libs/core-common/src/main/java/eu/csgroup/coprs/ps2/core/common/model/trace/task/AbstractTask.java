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

package eu.csgroup.coprs.ps2.core.common.model.trace.task;

import eu.csgroup.coprs.ps2.core.common.model.trace.input.TaskInput;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public abstract class AbstractTask {

    private UUID uid;
    private String name;
    private TaskEvent event;
    private String satellite;

    private TaskInput input;

    @Override
    public String toString() {
        return "Task{" +
                "uid='" + uid + '\'' +
                ", event=" + event +
                '}';
    }

}
