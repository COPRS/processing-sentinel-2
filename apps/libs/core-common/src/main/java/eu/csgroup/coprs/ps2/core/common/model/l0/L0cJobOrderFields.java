package eu.csgroup.coprs.ps2.core.common.model.l0;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum L0cJobOrderFields {

    ACQUISITION_STATION("@acquisitionStation@"),
    PROCESSING_STATION("@processingStation@"),
    START_TIME("@start_time@"),
    STOP_TIME("@stop_time@"),
    DATASTRIP_NAME("@l0u_dsname@"),
    DT_DIR("@dt_dir@"),
    DEM_PATH("@dem_path@"),
    CREATION_DATE("@creationDate@"),
    GRANULE_END("@granule_end@"),
    GPS_UTC("@gps_utc@"),
    L0_DS_NAME("@l0_dsname@"),
    L0_GR_LIST("@l0_gr_list_XX@"),
    L0_GR_COUNT("@l0_gr_count_XX@");

    private final String placeholder;

}
