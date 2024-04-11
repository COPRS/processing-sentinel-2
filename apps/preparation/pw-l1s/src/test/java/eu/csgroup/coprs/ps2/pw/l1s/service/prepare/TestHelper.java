/*
 * Copyright 2023 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.csgroup.coprs.ps2.pw.l1s.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.processing.DatatakeType;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sAuxFile;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastrip;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastripEntity;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class TestHelper {

    public static final Path FOLDER_PATH = Paths.get("src/test/resources/l0c");
    public static final String DATASTRIP_NAME = "S2B_OPER_MSI_L0__DS_REFS_20221124T163650_S20221011T064025_N02.08";

    public static final Path DS_PATH = FOLDER_PATH.resolve(DATASTRIP_NAME);
    public static final String FOLDER = FOLDER_PATH.toAbsolutePath().toString();
    public static final Instant START_TIME = Instant.now().minus(60, ChronoUnit.DAYS);
    public static final Instant STOP_TIME = Instant.now().minus(30, ChronoUnit.DAYS);

    public static final Pair<Instant, Instant> DATASTRIP_TIMES = Pair.of(START_TIME, STOP_TIME);
    public static final String SATELLITE = "B";
    public static final Instant T0_PDGS_DATE = Instant.now().minus(2, ChronoUnit.HOURS);
    public static final DatatakeType DATATAKE_TYPE = DatatakeType.NOBS;

    public static final List<String> GR_LIST = List.of(
            "S2B_OPER_MSI_L0__GR_REFS_20221124T163650_S20221011T064025_D01_N02.08",
            "S2B_OPER_MSI_L0__GR_REFS_20221124T163650_S20221011T064029_D01_N02.08",
            "S2B_OPER_MSI_L0__GR_REFS_20221124T163650_S20221011T064032_D01_N02.08"
    );

    public static final L1sDatastrip DATASTRIP = ((L1sDatastrip) new L1sDatastrip()
            .setFolder(FOLDER)
            .setDatatakeType(DATATAKE_TYPE)
            .setAvailableByGR(GR_LIST.stream().collect(Collectors.toMap(Function.identity(), o -> false)))
            .setName(DATASTRIP_NAME)
            .setAvailableByAux(Arrays.stream(L1sAuxFile.values()).collect(Collectors.toMap(auxFile -> auxFile.getAuxProductType().name(), o -> false)))
            .setStartTime(START_TIME)
            .setStopTime(STOP_TIME)
            .setT0PdgsDate(T0_PDGS_DATE)
            .setSatellite(SATELLITE));

    public static final L1sDatastripEntity DATASTRIP_ENTITY = (L1sDatastripEntity) new L1sDatastripEntity()
            .setFolder(FOLDER)
            .setDatatakeType(DATATAKE_TYPE)
            .setAvailableByGR(GR_LIST.stream().collect(Collectors.toMap(Function.identity(), o -> false)))
            .setName(DATASTRIP_NAME)
            .setAvailableByAux(Arrays.stream(L1sAuxFile.values()).collect(Collectors.toMap(auxFile -> auxFile.getAuxProductType().name(), o -> false)))
            .setStartTime(START_TIME)
            .setStopTime(STOP_TIME)
            .setT0PdgsDate(T0_PDGS_DATE)
            .setSatellite(SATELLITE);

    public static final L1sDatastrip UPDATED_DATASTRIP = ((L1sDatastrip) new L1sDatastrip()
            .setFolder(FOLDER)
            .setDatatakeType(DATATAKE_TYPE)
            .setAvailableByGR(GR_LIST.stream().collect(Collectors.toMap(Function.identity(), o -> true)))
            .setName(DATASTRIP_NAME)
            .setAvailableByAux(Arrays.stream(L1sAuxFile.values()).collect(Collectors.toMap(auxFile -> auxFile.getAuxProductType().name(), o -> true)))
            .setStartTime(START_TIME)
            .setStopTime(STOP_TIME)
            .setT0PdgsDate(T0_PDGS_DATE)
            .setSatellite(SATELLITE)
            .setReady(true));

    private TestHelper() {
    }

}
