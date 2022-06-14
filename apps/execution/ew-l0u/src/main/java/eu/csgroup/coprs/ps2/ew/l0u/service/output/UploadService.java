package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.ew.l0u.settings.FolderParameters;
import eu.csgroup.coprs.ps2.ew.l0u.settings.L0uExecutionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
public class UploadService {

    private final L0uExecutionProperties l0uExecutionProperties;
    private final ObsService obsService;

    public UploadService(L0uExecutionProperties l0uExecutionProperties, ObsService obsService) {
        this.l0uExecutionProperties = l0uExecutionProperties;
        this.obsService = obsService;
    }

    public Map<ProductFamily, Set<FileInfo>> upload() {

        log.info("Uploading AUX files to OBS");

        final Path rootPath = Paths.get(FolderParameters.L0U_DUMP_PATH);

        final Map<ProductFamily, Set<FileInfo>> fileInfosByFamily = new EnumMap<>(ProductFamily.class);

        try {

            final List<Path> sadFolders = FileOperationUtils.findFolders(rootPath, FileParameters.SAD_REGEX);
            final List<Path> hktmFolders = FileOperationUtils.findFoldersInTree(rootPath, FileParameters.HKTM_REGEX);

            log.info("Found {} SAD files", sadFolders.size());
            log.info("Found {} HKTM files", hktmFolders.size());

            fileInfosByFamily.put(ProductFamily.S2_SAD, getFileInfoSet(sadFolders, l0uExecutionProperties.getSadUploadBucket()));
            fileInfosByFamily.put(ProductFamily.S2_HKTM, getFileInfoSet(hktmFolders, l0uExecutionProperties.getHktmUploadBucket()));

            obsService.uploadAll(fileInfosByFamily.values().stream().flatMap(Collection::parallelStream).collect(Collectors.toSet()));

        } catch (Exception e) {
            throw new FileOperationException("Unable to upload files to OBS", e);
        }

        log.info("Uploaded AUX files to OBS");

        return fileInfosByFamily;
    }

    private Set<FileInfo> getFileInfoSet(List<Path> folderPathList, String bucket) {
        return folderPathList
                .stream()
                .map(path -> new FileInfo()
                        .setFullLocalPath(path.toString())
                        .setBucket(bucket)
                        .setObsName(path.getFileName().toString()))
                .collect(Collectors.toSet());
    }

}
