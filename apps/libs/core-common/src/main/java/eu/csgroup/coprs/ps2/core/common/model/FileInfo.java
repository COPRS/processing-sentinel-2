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

package eu.csgroup.coprs.ps2.core.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.utils.ObsUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FileInfo {

    @EqualsAndHashCode.Include
    private String obsName;

    private String bucket;
    private String obsPath;
    private String localName;
    private String localPath;

    private ProductFamily productFamily;
    private AuxProductType auxProductType;

    boolean isSimpleFile;

    @JsonIgnore
    public FileInfo setFullLocalPath(String fullLocalPath) {
        final Path path = Paths.get(fullLocalPath);
        localName = path.getFileName().toString();
        if (path.getParent() != null) {
            localPath = path.getParent().toString();
        }
        return this;
    }

    @JsonIgnore
    public String getFullLocalPath() {
        return Paths.get(localPath, localName).toString();
    }

    @JsonIgnore
    public String getKey() {
        return ObsUtils.toKey(obsPath, obsName);
    }

    @JsonIgnore
    public FileInfo setKey(String key) {
        obsPath = ObsUtils.keyToParentKey(key);
        obsName = ObsUtils.keyToName(key);
        return this;
    }

    @JsonIgnore
    public FileInfo setObsURL(String obsURL) {
        bucket = ObsUtils.urlToBucket(obsURL);
        obsPath = ObsUtils.urlToParentKey(obsURL);
        obsName = ObsUtils.urlToName(obsURL);
        return this;
    }

    @JsonIgnore
    public String getObsURL() {
        return ObsUtils.toUrl(bucket, obsPath, obsName);
    }

}
