package eu.csgroup.coprs.ps2.pw.l0u.model;

import eu.csgroup.coprs.ps2.core.catalog.model.AuxProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum AuxValue {

    THEORETICAL_LINE_PERIOD(
            AuxProductType.GIP_DATATI,
            "THEORETICAL_LINE_PERIOD",
            List.of(".*<THEORETICAL_LINE_PERIOD[^>]*>", "</THEORETICAL_LINE_PERIOD>"),
            ""
    ),

    GPS_TIME_TAI(
            AuxProductType.GIP_DATATI,
            "GPS_TIME_TAI",
            List.of(".*<GPS_TIME_TAI[^>]*>-", "</GPS_TIME_TAI>"),
            ""
    ),

    TAI_TO_UTC(
            AuxProductType.AUX_UT1UTC,
            "TAI-UTC =",
            List.of("TAI-UTC =", "\\..*", "[ ]*"),
            "-"
    ),

    ORBIT_OFFSET(AuxProductType.GIP_ATMSAD,
            "ORBIT_OFFSET",
            List.of(".*<ORBIT_OFFSET>", "</ORBIT_OFFSET>"),
            ""),

    TOTAL_ORBIT(AuxProductType.GIP_ATMSAD,
            "TOTAL_ORBITS",
            List.of(".*<TOTAL_ORBITS>", "</TOTAL_ORBITS>"),
            ""),

    SP_MIN_SIZE(AuxProductType.GIP_ATMIMA,
            "SP_MIN_SIZE",
            List.of(".*<SP_MIN_SIZE>", "</SP_MIN_SIZE>"),
            "");

    private final AuxProductType auxProductType;
    private final String lineFilter;
    private final List<String> regexList;
    private final String prefix;

}
