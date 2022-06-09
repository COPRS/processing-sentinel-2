package eu.csgroup.coprs.ps2.core.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObsUtilsTest {

    private static final String URL = "s3://bucket/path/name";
    private static final String PATH = "bucket/path/name";
    private static final String BUCKET = "bucket";
    private static final String KEY = "path/name";
    private static final String NAME = "name";
    private static final String PARENT_KEY = "path";

    @Test
    void toUrl() {
        assertEquals(URL, ObsUtils.toUrl(BUCKET, PARENT_KEY, NAME));
    }

    @Test
    void toKey() {
        assertEquals(KEY, ObsUtils.toKey(PARENT_KEY, NAME));
    }

    @Test
    void urlToPath() {
        assertEquals(PATH, ObsUtils.urlToPath(URL));
    }

    @Test
    void urlToBucket() {
        assertEquals(BUCKET, ObsUtils.urlToBucket(URL));
    }

    @Test
    void urlToKey() {
        assertEquals(KEY, ObsUtils.urlToKey(URL));
    }

    @Test
    void urlToParentKey() {
        assertEquals(PARENT_KEY, ObsUtils.urlToParentKey(URL));
    }

    @Test
    void urlToName() {
        assertEquals(NAME, ObsUtils.urlToName(URL));
    }

    @Test
    void keyToParentKey() {
        assertEquals(PARENT_KEY, ObsUtils.keyToParentKey(KEY));
    }

    @Test
    void keyToName() {
        assertEquals(NAME, ObsUtils.keyToName(KEY));
    }

}