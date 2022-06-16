package eu.csgroup.coprs.ps2.core.common.model.l0;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum L0cStep {

    GSE("GSE", 1),
    INIT_L0C_L0("INIT_L0C_L0", 1),
    FORMAT_ISP("FORMAT_ISP", 12),

    QL_GEO("QL_GEO", 12),
    QL_CLOUD_MASK("QL_CLOUD_MASK", 1),
    FORMAT_METADATA_GR_L0C("FORMAT_METADATA_GR_L0C", 12),
    FORMAT_IMG_QL_L0("FORMAT_IMG_QL_L0", 7),
    OLQC_L0CGR("OLQC-L0cGr", 12),
    FORMAT_METADATA_DS_L0C("FORMAT_METADATA_DS_L0C", 1),
    OLQC_L0CDS("OLQC-L0cDs", 1);

    private final String taskName;
    private final int jobCount;

}
