package eu.csgroup.coprs.ps2.ew.l0c.settings;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cTask;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EWL0cTask {

    GSE(L0cTask.GSE, "/dpc/app/s2ipf/GSE/05.01.00/scripts/GSE.bash"),
    INIT_L0C_L0(L0cTask.INIT_L0C_L0, "/dpc/app/s2ipf/INIT_LOC_L0/05.01.00/scripts/INIT_LOC_L0.bash"),
    FORMAT_ISP(L0cTask.FORMAT_ISP, "/dpc/app/s2ipf/FORMAT_ISP/05.01.00/scripts/FORMAT_ISP.bash"),

    QL_GEO(L0cTask.QL_GEO, "/dpc/app/s2ipf/QL_GEO/05.01.00/scripts/QL_GEO.bash"),
    QL_CLOUD_MASK(L0cTask.QL_CLOUD_MASK, "/dpc/app/s2ipf/QL_CLOUD_MASK/05.01.00/scripts/QL_CLOUD_MASK.bash"),
    FORMAT_METADATA_GR_L0C(L0cTask.FORMAT_METADATA_GR_L0C, "/dpc/app/s2ipf/FORMAT_METADATA_GR_L0C/05.01.00/scripts/FORMAT_METADATA_GR_L0C.bash"),
    FORMAT_IMG_QL_L0(L0cTask.FORMAT_IMG_QL_L0, "/dpc/app/s2ipf/FORMAT_IMG_QL_L0/05.01.00/scripts/FORMAT_IMG_QL_L0.bash"),
    OLQC_L0CGR(L0cTask.OLQC_L0CGR, "/dpc/app/s2ipf/OLQC/05.01.00/scripts/OLQC.bash"),
    FORMAT_METADATA_DS_L0C(L0cTask.FORMAT_METADATA_DS_L0C, "/dpc/app/s2ipf/FORMAT_METADATA_DS_L0C/05.01.00/scripts/FORMAT_METADATA_DS_L0C.bash"),
    OLQC_L0CDS(L0cTask.OLQC_L0CDS, "/dpc/app/s2ipf/OLQC/05.01.00/scripts/OLQC.bash");

    private final L0cTask l0CTask;
    private final String script;

}
