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

import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.output.EmptyTaskOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.output.TaskOutput;
import eu.csgroup.coprs.ps2.core.common.model.trace.quality.EmptyTaskQuality;
import eu.csgroup.coprs.ps2.core.common.model.trace.quality.TaskQuality;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class EndTask extends AbstractTask {

    private TaskStatus status;
    private Integer errorCode = 0;
    private Double durationInSeconds;

    private TaskOutput output;
    private TaskQuality quality;
    private List<TaskMissingOutput> missingOutput;

    private Double dataRateMebibytesSec;
    private Double dataVolumeMebibytes;

    public EndTask() {
        this.setEvent(TaskEvent.END);
        this.setOutput(new EmptyTaskOutput());
        this.setQuality(new EmptyTaskQuality());
    }

}
