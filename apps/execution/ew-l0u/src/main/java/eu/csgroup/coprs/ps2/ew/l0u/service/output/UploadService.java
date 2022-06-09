package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.service.ew.AbstractEWUploadService;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.ew.l0u.config.L0uExecutionProperties;
import eu.csgroup.coprs.ps2.ew.l0u.settings.L0uFolderParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
public class UploadService extends AbstractEWUploadService {

    private final L0uExecutionProperties l0uExecutionProperties;
    private final ObsService obsService;

    public UploadService(L0uExecutionProperties l0uExecutionProperties, ObsService obsService) {
        this.l0uExecutionProperties = l0uExecutionProperties;
        this.obsService = obsService;
    }

    public Map<ProductFamily, Set<FileInfo>> upload() {

        log.info("Uploading AUX files to OBS");

        final Map<ProductFamily, Set<FileInfo>> fileInfosByFamily = new EnumMap<>(ProductFamily.class);

        final Path rootPath = Paths.get(L0uFolderParameters.L0U_DUMP_PATH);

        try {

            final List<Path> sadFolders = FileOperationUtils.findFolders(rootPath, S2FileParameters.SAD_REGEX);
            final List<Path> hktmFolders = FileOperationUtils.findFoldersInTree(rootPath, S2FileParameters.HKTM_REGEX);

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

}
