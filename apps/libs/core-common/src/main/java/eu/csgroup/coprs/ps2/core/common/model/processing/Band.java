package eu.csgroup.coprs.ps2.core.common.model.processing;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum Band {

    BAND_01("01"),
    BAND_02("02"),
    BAND_03("03"),
    BAND_04("04"),
    BAND_05("05"),
    BAND_06("06"),
    BAND_07("07"),
    BAND_08("08"),
    BAND_09("09"),
    BAND_10("10"),
    BAND_11("11"),
    BAND_12("12"),
    BAND_8A("8A");

    private final String id;

    public static List<String> allBandIndexIds() {
        return Arrays.stream(Band.values()).map(Band::getId).toList();
    }

}
