package eu.csgroup.coprs.ps2.pw.l0c.settings;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cTask;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PWL0cTask {

    GSE(L0cTask.GSE, "templates/part1/task1"),
    INIT_L0C_L0(L0cTask.INIT_L0C_L0, "templates/part1/task2"),
    FORMAT_ISP(L0cTask.FORMAT_ISP, "templates/part1/task3"),

    QL_GEO(L0cTask.QL_GEO, "templates/part2/task1"),
    QL_CLOUD_MASK(L0cTask.QL_CLOUD_MASK, "templates/part2/task2"),
    FORMAT_METADATA_GR_L0C(L0cTask.FORMAT_METADATA_GR_L0C, "templates/part2/task3"),
    FORMAT_IMG_QL_L0(L0cTask.FORMAT_IMG_QL_L0, "templates/part2/task4"),
    OLQC_L0CGR(L0cTask.OLQC_L0CGR, "templates/part2/task5"),
    FORMAT_METADATA_DS_L0C(L0cTask.FORMAT_METADATA_DS_L0C, "templates/part2/task6"),
    OLQC_L0CDS(L0cTask.OLQC_L0CDS, "templates/part2/task7");


    private final L0cTask l0CTask;
    private final String templateFolder;

}
