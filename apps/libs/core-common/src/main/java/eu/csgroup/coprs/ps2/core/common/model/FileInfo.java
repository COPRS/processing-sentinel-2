package eu.csgroup.coprs.ps2.core.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    // Used for AuxProductType
    private String type;
    private String md5;
    private Long size;

    @JsonIgnore
    public FileInfo setFullLocalPath(String fullLocalPath) {
        final Path path = Paths.get(fullLocalPath);
        localName = path.getFileName().toString();
        localPath = path.getParent().toString();
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
