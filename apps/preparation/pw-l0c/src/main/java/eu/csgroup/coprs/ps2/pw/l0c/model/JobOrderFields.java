package eu.csgroup.coprs.ps2.pw.l0c.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobOrderFields {

    ACQUISITION_STATION("@acquisitionStation@"),
    PROCESSING_STATION("@processingStation@"),
    START_TIME("@start_time@"),
    STOP_TIME("@stop_time@"),
    DATASTRIP_NAME("@l0u_dsname@"),
    DT_DIR("@dt_dir@"),
    DEM_PATH("@dem_path"),
    CREATION_DATE("@creationDate@");

    private final String placeholder;

}
