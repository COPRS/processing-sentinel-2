package eu.csgroup.coprs.ps2.ew.l1c.service.output;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.L1Parameters;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWUploadService;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.ew.l1c.config.L1cExecutionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class L1cEWUploadService extends EWUploadService<L1ExecutionInput> {

    private final L1cExecutionProperties executionProperties;
    private final ObsService obsService;

    public L1cEWUploadService(L1cExecutionProperties executionProperties, ObsService obsService) {
        this.executionProperties = executionProperties;
        this.obsService = obsService;
    }

    @Override
    public Map<ProductFamily, Set<FileInfo>> upload(L1ExecutionInput executionInput) {

        log.info("Uploading L1C files to OBS");

        final Map<ProductFamily, Set<FileInfo>> fileInfosByFamily = new EnumMap<>(ProductFamily.class);
        final Path rootPath = Paths.get(L1Parameters.WORKING_FOLDER_ROOT);

        try {

            ProductFamily productFamily;
            List<Path> folders;
            String bucket;

            if (StringUtils.hasText(executionInput.getTile())) {
                productFamily = ProductFamily.S2_L1C_TL;
                bucket = executionProperties.getL1TLBucket();
                folders = FileOperationUtils.findFolders(rootPath.resolve(L1Parameters.L1C_TL_ROOT), S2FileParameters.L1C_TL_REGEX);
            } else {
                productFamily = ProductFamily.S2_L1C_DS;
                bucket = executionProperties.getL1DSBucket();
                folders = FileOperationUtils.findFolders(rootPath.resolve(L1Parameters.L1C_DS_ROOT), S2FileParameters.L1C_DS_REGEX);
            }

            fileInfosByFamily.put(productFamily, getFileInfoSet(folders, bucket));

            obsService.uploadWithMd5(fileInfosByFamily.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));

        } catch (Exception e) {
            throw new FileOperationException("Unable to upload files to OBS", e);
        }

        log.info("Uploaded L1C files to OBS");

        return fileInfosByFamily;
    }

}
