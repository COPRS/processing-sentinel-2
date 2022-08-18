package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.pw.l0c.model.AuxFile;
import eu.csgroup.coprs.ps2.pw.l0c.model.Datastrip;
import eu.csgroup.coprs.ps2.pw.l0c.model.DatastripEntity;

import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public final class TestHelper {

    public static final String DATASTRIP_NAME = "S2B_OPER_MSI_L0U_DS_REFS_20220614T090618_S20220413T114741_N00.00";
    public static final String FOLDER = Paths.get("src/test/resources/l0u_output/DT35/DS").toAbsolutePath().toString();
    public static final Instant START_TIME = Instant.now().minus(60, ChronoUnit.DAYS);
    public static final Instant STOP_TIME = Instant.now().minus(30, ChronoUnit.DAYS);
    public static final String SATELLITE = "B";
    public static final String STATION_CODE = "_SGS";
    public static final Instant T0_PDGS_DATE = Instant.now().minus(2, ChronoUnit.HOURS);

    public static final Datastrip DATASTRIP = ((Datastrip) new Datastrip()
            .setFolder(FOLDER)
            .setName(DATASTRIP_NAME)
            .setStartTime(START_TIME)
            .setStopTime(STOP_TIME)
            .setT0PdgsDate(T0_PDGS_DATE)
            .setSatellite(SATELLITE)
            .setStationCode(STATION_CODE));

    public static final DatastripEntity DATASTRIP_ENTITY = new DatastripEntity()
            .setFolder(FOLDER)
            .setName(DATASTRIP_NAME)
            .setStartTime(START_TIME)
            .setStopTime(STOP_TIME)
            .setT0PdgsDate(T0_PDGS_DATE)
            .setSatellite(SATELLITE)
            .setStationCode(STATION_CODE);

    public static final Datastrip UPDATED_DATASTRIP = ((Datastrip) new Datastrip()
            .setFolder(FOLDER)
            .setName(DATASTRIP_NAME)
            .setStartTime(START_TIME)
            .setStopTime(STOP_TIME)
            .setT0PdgsDate(T0_PDGS_DATE)
            .setSatellite(SATELLITE)
            .setStationCode(STATION_CODE)
            .setReady(true));

    public static final Map<AuxFile, List<FileInfo>> auxFilesByType = Map.of(
            AuxFile.AUX_UT1UTC, List.of(
                    new FileInfo().setObsName("S2__OPER_AUX_UT1UTC_PDMC_20220407T000000_V20220408T000000_20230407T000000")
                            .setLocalName("S2__OPER_AUX_UT1UTC_PDMC_20220407T000000_V20220408T000000_20230407T000000")),
            AuxFile.GIP_ATMIMA, List.of(
                    new FileInfo().setObsName("S2B_OPER_GIP_ATMIMA_MPC__20170206T103051_V20170101T000000_21000101T000000_B00")
                            .setLocalName("S2B_OPER_GIP_ATMIMA_MPC__20170206T103051_V20170101T000000_21000101T000000_B00"))
    );

    public static final String DEM_FULL_PATH = Paths.get("src/test/resources/dem").toAbsolutePath().toString();
    public static final String DEM_BAD_FULL_PATH = Paths.get("src/test/resources/dem_bad").toAbsolutePath().toString();
    public static final String DEM_NAME = DEM_FULL_PATH + "/S2__OPER_DEM_GLOBEF_PDMC_20091210T235100_S20091210T235134";

    private TestHelper() {
    }

}
