package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
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

    protected Map<ProductFamily, Set<FileInfo>> buildFolderInfoInFolder(Path root, String regex, ProductFamily productFamily, String bucket) {
        return buildFileInfo(root, regex, productFamily, bucket, FileOperationUtils::findFolders);
    }

    protected Map<ProductFamily, Set<FileInfo>> buildFileInfoInFolder(Path root, String regex, ProductFamily productFamily, String bucket) {
        return buildFileInfo(root, regex, productFamily, bucket, FileOperationUtils::findFiles);
    }

    protected Map<ProductFamily, Set<FileInfo>> buildFolderInfoInTree(Path root, String regex, ProductFamily productFamily, String bucket) {
        return buildFileInfo(root, regex, productFamily, bucket, FileOperationUtils::findFoldersInTree);
    }

    private Map<ProductFamily, Set<FileInfo>> buildFileInfo(Path root, String regex, ProductFamily productFamily, String bucket,
            BiFunction<Path, String, List<Path>> fileOperation
    ) {
        Map<ProductFamily, Set<FileInfo>> fileInfosByFamily = Collections.emptyMap();
        if (Files.exists(root)) {
            final List<Path> folders = fileOperation.apply(root, regex);
            log.info("Found {} {} files", folders.size(), productFamily.name());
            fileInfosByFamily = Map.of(productFamily, getFileInfoSet(folders, bucket));
        }
        return fileInfosByFamily;
    }

}
