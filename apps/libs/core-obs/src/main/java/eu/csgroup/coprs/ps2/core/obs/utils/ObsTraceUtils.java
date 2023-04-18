package eu.csgroup.coprs.ps2.core.obs.utils;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.trace.TaskReport;
import eu.csgroup.coprs.ps2.core.common.model.trace.input.ObsInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.task.ReportTask;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class ObsTraceUtils {

    private static final int DOUBLE_PRECISION = 3;

    public static void traceTransfer(Set<FileInfo> fileInfoSet, ReportTask reportTask, UUID parentUid, Consumer<Set<FileInfo>> operation) {

        TaskReport taskReport = new TaskReport().setTaskName(reportTask.getName()).setParentUid(parentUid);

        taskReport.begin("Start " + reportTask.getName(), new ObsInput(getBuckets(fileInfoSet), getFileNames(fileInfoSet)));

        try {

            final Instant start = Instant.now();

            operation.accept(fileInfoSet);

            final Duration elapsed = Duration.between(start, Instant.now());

            final double dataVolumeMebibytes = getMebibytes(FileOperationUtils.getSize(getLocalPaths(fileInfoSet)));

            taskReport.end("End " + reportTask.getName(), getDataRate(dataVolumeMebibytes, elapsed), dataVolumeMebibytes);

        } catch (Exception e) {
            taskReport.error(e.getLocalizedMessage());
            throw e;
        }
    }

    private static Set<String> getLocalPaths(Set<FileInfo> fileInfoSet) {
        return fileInfoSet.stream().map(FileInfo::getFullLocalPath).collect(Collectors.toSet());
    }

    private static Set<String> getBuckets(Set<FileInfo> fileInfoSet) {
        return fileInfoSet.stream().map(FileInfo::getBucket).collect(Collectors.toSet());
    }

    private static Set<String> getFileNames(Set<FileInfo> fileInfoSet) {
        return fileInfoSet.stream().map(FileInfo::getLocalName).collect(Collectors.toSet());
    }

    private static double getMebibytes(long volume) {
        return setPrecision((double) volume / (1_024 * 1_024));
    }

    private static double getDataRate(double volume, Duration duration) {
        return setPrecision(volume / (((double) duration.toMillis()) / 1_000));
    }

    private static Double setPrecision(Double value) {
        return BigDecimal.valueOf(value)
                .setScale(DOUBLE_PRECISION, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private ObsTraceUtils() {
    }

}
