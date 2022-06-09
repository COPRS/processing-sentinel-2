package eu.csgroup.coprs.ps2.core.common.model.l0;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum L0cTask {

    GSE("GSE", 1, List.of("A")),
    INIT_L0C_L0("INIT_L0C_L0", 1, List.of("A", "B")),
    FORMAT_ISP("FORMAT_ISP", 12, List.of("A", "B")),

    QL_GEO("QL_GEO", 12, List.of("A", "B")),
    QL_CLOUD_MASK("QL_CLOUD_MASK", 1, List.of("A", "B")),
    FORMAT_METADATA_GR_L0C("FORMAT_METADATA_GR_L0C", 12, List.of("A", "B")),
    FORMAT_IMG_QL_L0("FORMAT_IMG_QL_L0", 7, List.of("A", "B")),
    OLQC_L0CGR("OLQC-L0cGr", 12, List.of("A", "B")),
    FORMAT_METADATA_DS_L0C("FORMAT_METADATA_DS_L0C", 1, List.of("A", "B")),
    OLQC_L0CDS("OLQC-L0cDs", 1, List.of("A", "B"));

    private final String taskName;
    private final int jobCount;
    private final List<String> satellites;

}
