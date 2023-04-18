package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.model.processing.DatatakeType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;


@Slf4j
public final class DatastripUtils {

    private static final String START_TIME_TAG = "DATASTRIP_SENSING_START";
    private static final String STOP_TIME_TAG = "DATASTRIP_SENSING_STOP";
    private static final String DATATAKE_TYPE_TAG = "DATATAKE_TYPE";
    private static final String GRANULE_ID_STRING = "granuleId";
    private static final String TILE_ID_STRING = "tileId";


    public static Pair<Instant, Instant> getDatastripTimes(Path datastripPath) {

        final Path datastripXmlPath = getXmlPath(datastripPath);

        Instant startTime = DateUtils.toInstant(FileContentUtils.extractXmlTagValue(datastripXmlPath, START_TIME_TAG));
        Instant stopTime = DateUtils.toInstant(FileContentUtils.extractXmlTagValue(datastripXmlPath, STOP_TIME_TAG));

        return Pair.of(startTime, stopTime);
    }

    public static List<String> getGRList(Path datastripPath) {
        // <Granule granuleId="S2B_OPER_MSI_L0__GR_REFS_20220629T125610_S20220413T115421_D01_N02.08">
        return getList(datastripPath, GRANULE_ID_STRING);
    }

    public static List<String> getTLList(Path datastripPath) {
        // <Tile tileId="S2B_OPER_MSI_L1C_TL_REFS_20221124T170956_A029232_T40MDV_N04.00"/>
        return getList(datastripPath, TILE_ID_STRING);
    }

    public static DatatakeType getDatatakeType(Path datastripPath) {

        final Path datastripXmlPath = getXmlPath(datastripPath);

        // <DATATAKE_TYPE>INS-NOBS</DATATAKE_TYPE>

        return DatatakeType.valueOf(
                FileContentUtils.extractXmlTagValue(datastripXmlPath, DATATAKE_TYPE_TAG).substring(4).toUpperCase()
        );
    }

    private static List<String> getList(Path datastripPath, String idString) {
        final Path datastripXmlPath = getXmlPath(datastripPath);
        return FileContentUtils.grepAll(datastripXmlPath, idString)
                .stream()
                .map(s -> StringUtils.substringBetween(s, "\""))
                .toList();
    }

    private static Path getXmlPath(Path datastripPath) {

        final String datastripName = datastripPath.getFileName().toString();

        // DS  : S2B_OPER_MSI_L0__DS_REFS_20220629T125610_S20220413T115356_N02.08
        // XML : S2B_OPER_MTD_L0__DS_REFS_20220629T125610_S20220413T115356.xml
        // DS  : S2B_OPER_MSI_L0U_DS_REFS_20220614T090618_S20220413T114741_N00.00
        // XML : S2B_OPER_MTD_L0U_DS_REFS_20220614T090618_S20220413T114741.xml
        // DS  : S2B_OPER_MSI_L1C_DS_REFS_20221124T170956_S20221011T064025_N04.00
        // XML : S2B_OPER_MTD_L1C_DS_REFS_20221124T170956_S20221011T064025.xml

        final String datastripXmlName = datastripName
                .substring(0, datastripName.length() - 7)
                .replace("_MSI_", "_MTD_")
                .concat(".xml");

        return datastripPath.resolve(datastripXmlName);
    }


    private DatastripUtils() {
    }

}
