package eu.csgroup.coprs.ps2.ew.l1c.service.output;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.common.settings.L12Parameters;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.ew.service.EWUploadService;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
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

    private final ObsBucketProperties bucketProperties;
    private final ObsService obsService;

    public L1cEWUploadService(ObsBucketProperties bucketProperties, ObsService obsService) {
        this.bucketProperties = bucketProperties;
        this.obsService = obsService;
    }

    @Override
    public Map<ProductFamily, Set<FileInfo>> upload(L1ExecutionInput executionInput, UUID parentUid) {

        log.info("Uploading L1C files to OBS");

        final Map<ProductFamily, Set<FileInfo>> fileInfoByFamily = new EnumMap<>(ProductFamily.class);
        final Path rootPath = Paths.get(FolderParameters.WORKING_FOLDER_ROOT);

        try {

            if (StringUtils.hasText(executionInput.getTile())) {
                fileInfoByFamily.putAll(buildFolderInfoInFolder(rootPath.resolve(L12Parameters.L1C_TL_ROOT), S2FileParameters.L1C_TL_REGEX, ProductFamily.S2_L1C_TL,
                        bucketProperties.getL1TLBucket()));
                fileInfoByFamily.putAll(buildFileInfoInFolder(rootPath.resolve(L12Parameters.L1C_TL_ROOT), S2FileParameters.L1C_TC_REGEX, ProductFamily.S2_L1C_TC,
                        bucketProperties.getL1TCBucket()));
            } else {
                fileInfoByFamily.putAll(buildFolderInfoInFolder(rootPath.resolve(L12Parameters.L1C_DS_ROOT), S2FileParameters.L1C_DS_REGEX, ProductFamily.S2_L1C_DS,
                        bucketProperties.getL1DSBucket()));
            }

            obsService.uploadWithMd5(fileInfoByFamily.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()), parentUid);

        } catch (Exception e) {
            throw new FileOperationException("Unable to upload files to OBS", e);
        }

        log.info("Uploaded L1C files to OBS");

        return fileInfoByFamily;
    }

}
