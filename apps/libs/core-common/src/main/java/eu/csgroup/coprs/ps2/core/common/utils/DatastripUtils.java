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


    public static Pair<Instant, Instant> getDatastripTimes(Path datastripPath) {

        final Path datastripXmlPath = getXmlPath(datastripPath);

        Instant startTime = DateUtils.toInstant(FileContentUtils.extractXmlTagValue(datastripXmlPath, START_TIME_TAG));
        Instant stopTime = DateUtils.toInstant(FileContentUtils.extractXmlTagValue(datastripXmlPath, STOP_TIME_TAG));

        return Pair.of(startTime, stopTime);
    }

    public static List<String> getGRList(Path datastripPath) {

        final Path datastripXmlPath = getXmlPath(datastripPath);

        // <Granule granuleId="S2B_OPER_MSI_L0__GR_REFS_20220629T125610_S20220413T115421_D01_N02.08">

        return FileContentUtils.grepAll(datastripXmlPath, GRANULE_ID_STRING)
                .stream()
                .map(s -> StringUtils.substringBetween(s, "\""))
                .toList();
    }

    public static DatatakeType getDatatakeType(Path datastripPath) {

        final Path datastripXmlPath = getXmlPath(datastripPath);

        // <DATATAKE_TYPE>INS-NOBS</DATATAKE_TYPE>

        return DatatakeType.valueOf(
                FileContentUtils.extractXmlTagValue(datastripXmlPath, DATATAKE_TYPE_TAG).substring(4).toUpperCase()
        );
    }

    private static Path getXmlPath(Path datastripPath) {

        final String datastripName = datastripPath.getFileName().toString();

        // DS  : S2B_OPER_MSI_L0__DS_REFS_20220629T125610_S20220413T115356_N02.08
        // XML : S2B_OPER_MTD_L0__DS_REFS_20220629T125610_S20220413T115356.xml
        // DS  : S2B_OPER_MSI_L0U_DS_REFS_20220614T090618_S20220413T114741_N00.00
        // XML : S2B_OPER_MTD_L0U_DS_REFS_20220614T090618_S20220413T114741.xml

        final String datastripXmlName = datastripName
                .substring(0, datastripName.length() - 7)
                .replace("_MSI_", "_MTD_")
                .concat(".xml");

        return datastripPath.resolve(datastripXmlName);
    }


    private DatastripUtils() {
    }

}
