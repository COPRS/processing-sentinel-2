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