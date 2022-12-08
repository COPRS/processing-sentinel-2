package eu.csgroup.coprs.ps2.core.common.model.trace.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportTask {

    PRODUCTION_TRIGGER("ProductionTrigger"),
    JOB_GENERATOR("JobGenerator"),
    JOB_PROCESSING("JobProcessing"),
    PROCESSING("Processing"),
    PROCESSING_TASK("ProcessingTask"),
    OBS_READ("ObsRead"),
    OBS_WRITE("ObsWrite");

    private final String name;

}
