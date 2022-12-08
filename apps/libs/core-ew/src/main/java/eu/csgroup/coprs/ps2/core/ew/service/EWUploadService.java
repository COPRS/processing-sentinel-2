package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class EWUploadService<T extends ExecutionInput> {

    public abstract Map<ProductFamily, Set<FileInfo>> upload(T executionInput, UUID parentUid);

    protected Set<FileInfo> getFileInfoSet(List<Path> folderPathList, String bucket) {
        return folderPathList
                .stream()
                .map(path -> new FileInfo()
                        .setFullLocalPath(path.toString())
                        .setBucket(bucket)
                        .setObsName(path.getFileName().toString()))
                .collect(Collectors.toSet());
    }

}
