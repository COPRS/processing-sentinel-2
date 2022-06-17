package eu.csgroup.coprs.ps2.pw.l0c.model;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cTask;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum Task {

    GSE(L0cTask.GSE, List.of("A"), "templates/part1/task1"),
    INIT_L0C_L0(L0cTask.INIT_L0C_L0, List.of("A", "B"), "templates/part1/task2"),
    FORMAT_ISP(L0cTask.FORMAT_ISP, List.of("A", "B"), "templates/part1/task3"),

    QL_GEO(L0cTask.QL_GEO, List.of("A", "B"), "templates/part2/task1"),
    QL_CLOUD_MASK(L0cTask.QL_CLOUD_MASK, List.of("A", "B"), "templates/part2/task2"),
    FORMAT_METADATA_GR_L0C(L0cTask.FORMAT_METADATA_GR_L0C, List.of("A", "B"), "templates/part2/task3"),
    FORMAT_IMG_QL_L0(L0cTask.FORMAT_IMG_QL_L0, List.of("A", "B"), "templates/part2/task4"),
    OLQC_L0CGR(L0cTask.OLQC_L0CGR, List.of("A", "B"), "templates/part2/task5"),
    FORMAT_METADATA_DS_L0C(L0cTask.FORMAT_METADATA_DS_L0C, List.of("A", "B"), "templates/part2/task6"),
    OLQC_L0CDS(L0cTask.OLQC_L0CDS, List.of("A", "B"), "templates/part2/task7");


    private final L0cTask l0CTask;
    private final List<String> satellites;
    private final String templateFolder;

}
