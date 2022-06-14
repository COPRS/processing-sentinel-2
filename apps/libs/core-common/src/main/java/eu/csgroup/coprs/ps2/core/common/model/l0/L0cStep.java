package eu.csgroup.coprs.ps2.core.common.model.l0;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum L0cStep {

    GSE("GSE"),
    INIT_L0C_L0("INIT_L0C_L0"),
    FORMAT_ISP("FORMAT_ISP"),
    QL_GEO("QL_GEO"),
    QL_CLOUD_MASK("QL_CLOUD_MASK"),
    FORMAT_METADATA_GR_L0C("FORMAT_METADATA_GR_L0C"),
    FORMAT_IMG_QL_L0("FORMAT_IMG_QL_L0"),
    OLQC_L0CGR("OLQC-L0cGr"),
    FORMAT_METADATA_DS_L0C("FORMAT_METADATA_DS_L0C"),
    OLQC_L0CDS("OLQC-L0cDs");

    private final String taskName;

}
