package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cAuxFile;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastrip;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastripEntity;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class TestHelper {

    public static final Path DT_PATH = Path.of("src/test/resources/l0u_output/DT35").toAbsolutePath();
    public static final String DT_FOLDER = DT_PATH.toString();
    public static final Path DS_PATH = DT_PATH.resolve("DS");
    public static final String DS_FOLDER = DS_PATH.toString();
    public static final String DATASTRIP_NAME = "S2B_OPER_MSI_L0U_DS_REFS_20220614T090618_S20220413T114741_N00.00";
    public static final Path DATASTRIP_PATH = DS_PATH.resolve(DATASTRIP_NAME);
    public static final Instant START_TIME = Instant.now().minus(60, ChronoUnit.DAYS);
    public static final Instant STOP_TIME = Instant.now().minus(30, ChronoUnit.DAYS);
    public static final String SATELLITE = "B";
    public static final String STATION_CODE = "_SGS";
    public static final Instant T0_PDGS_DATE = Instant.now().minus(2, ChronoUnit.HOURS);

    public static final L0cDatastrip DATASTRIP = ((L0cDatastrip) new L0cDatastrip()
            .setFolder(DS_FOLDER)
            .setDtFolder(DT_FOLDER)
            .setName(DATASTRIP_NAME)
            .setAvailableByAux(Arrays.stream(L0cAuxFile.values()).collect(Collectors.toMap(auxFile -> auxFile.getAuxProductType().name(), o -> false)))
            .setStartTime(START_TIME)
            .setStopTime(STOP_TIME)
            .setT0PdgsDate(T0_PDGS_DATE)
            .setSatellite(SATELLITE)
            .setStationCode(STATION_CODE));

    public static final L0cDatastripEntity DATASTRIP_ENTITY = (L0cDatastripEntity) new L0cDatastripEntity()
            .setFolder(DS_FOLDER)
            .setDtFolder(DT_FOLDER)
            .setName(DATASTRIP_NAME)
            .setAvailableByAux(Arrays.stream(L0cAuxFile.values()).collect(Collectors.toMap(auxFile -> auxFile.getAuxProductType().name(), o -> false)))
            .setStartTime(START_TIME)
            .setStopTime(STOP_TIME)
            .setT0PdgsDate(T0_PDGS_DATE)
            .setSatellite(SATELLITE)
            .setStationCode(STATION_CODE);

    public static final L0cDatastrip UPDATED_DATASTRIP = ((L0cDatastrip) new L0cDatastrip()
            .setFolder(DS_FOLDER)
            .setDtFolder(DT_FOLDER)
            .setName(DATASTRIP_NAME)
            .setAvailableByAux(Arrays.stream(L0cAuxFile.values()).collect(Collectors.toMap(auxFile -> auxFile.getAuxProductType().name(), o -> true)))
            .setStartTime(START_TIME)
            .setStopTime(STOP_TIME)
            .setT0PdgsDate(T0_PDGS_DATE)
            .setSatellite(SATELLITE)
            .setStationCode(STATION_CODE)
            .setReady(true));

    public static final Map<AuxProductType, List<FileInfo>> auxFilesByType = Map.of(
            AuxProductType.AUX_UT1UTC, List.of(
                    new FileInfo().setObsName("S2__OPER_AUX_UT1UTC_PDMC_20220407T000000_V20220408T000000_20230407T000000")
                            .setLocalName("S2__OPER_AUX_UT1UTC_PDMC_20220407T000000_V20220408T000000_20230407T000000")),
            AuxProductType.GIP_ATMIMA, List.of(
                    new FileInfo().setObsName("S2B_OPER_GIP_ATMIMA_MPC__20170206T103051_V20170101T000000_21000101T000000_B00")
                            .setLocalName("S2B_OPER_GIP_ATMIMA_MPC__20170206T103051_V20170101T000000_21000101T000000_B00")),
            AuxProductType.GIP_DATATI, List.of(
                    new FileInfo().setObsName("S2B_OPER_GIP_DATATI_MPC__20170428T123038_V20170322T000000_21000101T000000_B00")
                            .setLocalName("S2B_OPER_GIP_DATATI_MPC__20170428T123038_V20170322T000000_21000101T000000_B00"))
    );

    public static final String DEM_PATH = Paths.get("src/test/resources/dem").toAbsolutePath().toString();
    public static final String DEM_NAME = "S2__OPER_DEM_GLOBEF_PDMC_20091210T235100_S20091210T235134";
    public static final String DEM_FULL_PATH = Paths.get(DEM_PATH, DEM_NAME).toString();

    private TestHelper() {
    }

}
