package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.model.processing.DatatakeType;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatastripUtilsTest {

    private static final Path l0cDatastripPath =
            Paths.get("src/test/resources/datastripUtilsTest/S2B_OPER_MSI_L0__DS_REFS_20220629T125610_S20220413T115356_N02.08").toAbsolutePath();
    private static final Path l0uDatastripPath =
            Paths.get("src/test/resources/datastripUtilsTest/S2B_OPER_MSI_L0U_DS_REFS_20220614T090618_S20220413T114741_N00.00").toAbsolutePath();
    private static final Path l1DatastripPath =
            Paths.get("src/test/resources/datastripUtilsTest/S2B_OPER_MSI_L1C_DS_REFS_20221124T170956_S20221011T064025_N04.00").toAbsolutePath();

    @Test
    void getDatastripTimes_L0u() {

        final Pair<Instant, Instant> datastripTimes = DatastripUtils.getDatastripTimes(l0uDatastripPath);

        assertNotNull(datastripTimes.getLeft());
        assertNotNull(datastripTimes.getRight());
        assertEquals(Instant.parse("2022-04-13T11:47:41.009Z"), datastripTimes.getLeft());
        assertEquals(Instant.parse("2022-04-13T11:50:08.928Z"), datastripTimes.getRight());
    }

    @Test
    void getDatastripTimes_L0c() {

        final Pair<Instant, Instant> datastripTimes = DatastripUtils.getDatastripTimes(l0cDatastripPath);

        assertNotNull(datastripTimes.getLeft());
        assertNotNull(datastripTimes.getRight());
        assertEquals(Instant.parse("2022-04-13T11:53:56.462Z"), datastripTimes.getLeft());
        assertEquals(Instant.parse("2022-04-13T11:57:32.928Z"), datastripTimes.getRight());
    }

    @Test
    void getGRList_L0c() {

        final List<String> grList = DatastripUtils.getGRList(l0cDatastripPath);

        assertEquals(732, grList.size());
        assertTrue(grList.stream().allMatch(s -> s.startsWith("S2B_")));
    }

    @Test
    void getGRList_L0u() {

        final List<String> grList = DatastripUtils.getGRList(l0uDatastripPath);

        assertEquals(504, grList.size());
        assertTrue(grList.stream().allMatch(s -> s.startsWith("S2B_")));
    }

    @Test
    void getTLList() {

        final List<String> grList = DatastripUtils.getTLList(l1DatastripPath);

        assertEquals(18, grList.size());
        assertTrue(grList.stream().allMatch(s -> s.startsWith("S2B_")));
    }

    @Test
    void getDatatakeType_L0u() {

        final DatatakeType datatakeType = DatastripUtils.getDatatakeType(l0uDatastripPath);

        assertEquals(DatatakeType.NOBS, datatakeType);
    }

    @Test
    void getDatatakeType_L0c() {

        final DatatakeType datatakeType = DatastripUtils.getDatatakeType(l0cDatastripPath);

        assertEquals(DatatakeType.NOBS, datatakeType);
    }



}
