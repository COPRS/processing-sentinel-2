package eu.csgroup.coprs.ps2.ew.l0c.service.output;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.EWUploadService;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.ew.l0c.config.L0cExecutionProperties;
import eu.csgroup.coprs.ps2.ew.l0c.settings.L0cFolderParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class L0cEWUploadService extends EWUploadService {

    private final L0cExecutionProperties l0cExecutionProperties;
    private final ObsService obsService;

    public L0cEWUploadService(L0cExecutionProperties l0cExecutionProperties, ObsService obsService) {
        this.l0cExecutionProperties = l0cExecutionProperties;
        this.obsService = obsService;
    }

    @Override
    public Map<ProductFamily, Set<FileInfo>> upload() {

        log.info("Uploading L0C files to OBS");

        final Map<ProductFamily, Set<FileInfo>> fileInfosByFamily = new EnumMap<>(ProductFamily.class);

        final Path dsPath = Paths.get(L0cFolderParameters.DS_PATH);
        final Path grPath = Paths.get(L0cFolderParameters.GR_DB_PATH);

        try {

            final List<Path> dsFolders = FileOperationUtils.findFolders(dsPath, S2FileParameters.L0C_DS_REGEX);
            final List<Path> grFolders = FileOperationUtils.findFolders(grPath, S2FileParameters.L0C_GR_REGEX);

            log.info("Found {} DS files", dsFolders.size());
            log.info("Found {} GR files", grFolders.size());

            fileInfosByFamily.put(ProductFamily.S2_L0_DS, getFileInfoSet(dsFolders, l0cExecutionProperties.getDsUploadBucket()));
            fileInfosByFamily.put(ProductFamily.S2_L0_GR, getFileInfoSet(grFolders, l0cExecutionProperties.getGrUploadBucket()));

            obsService.uploadWithMd5(fileInfosByFamily.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));

        } catch (Exception e) {
            throw new FileOperationException("Unable to upload files to OBS", e);
        }

        log.info("Uploaded L0C files to OBS");

        return fileInfosByFamily;
    }

}
