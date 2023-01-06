package eu.csgroup.coprs.ps2.ew.l1ab.service.output;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.common.settings.L12Parameters;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWUploadService;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class L1abEWUploadService extends EWUploadService<L1ExecutionInput> {

    private final ObsBucketProperties bucketProperties;
    private final ObsService obsService;

    public L1abEWUploadService(ObsBucketProperties bucketProperties, ObsService obsService) {
        this.bucketProperties = bucketProperties;
        this.obsService = obsService;
    }

    @Override
    public Map<ProductFamily, Set<FileInfo>> upload(L1ExecutionInput executionInput, UUID parentUid) {

        log.info("Uploading L1A and/or L1B files to OBS");

        final Map<ProductFamily, Set<FileInfo>> fileInfosByFamily = new EnumMap<>(ProductFamily.class);
        final Path rootPath = Paths.get(FolderParameters.WORKING_FOLDER_ROOT);

        try {
            fileInfosByFamily.putAll(add(rootPath.resolve(L12Parameters.L1A_DS_ROOT), S2FileParameters.L1A_DS_REGEX, ProductFamily.S2_L1A_DS, bucketProperties.getL1DSBucket()));
            fileInfosByFamily.putAll(add(rootPath.resolve(L12Parameters.L1A_GR_ROOT), S2FileParameters.L1A_GR_REGEX, ProductFamily.S2_L1A_GR, bucketProperties.getL1GRBucket()));
            fileInfosByFamily.putAll(add(rootPath.resolve(L12Parameters.L1B_DS_ROOT), S2FileParameters.L1B_DS_REGEX, ProductFamily.S2_L1B_DS, bucketProperties.getL1DSBucket()));
            fileInfosByFamily.putAll(add(rootPath.resolve(L12Parameters.L1B_GR_ROOT), S2FileParameters.L1B_GR_REGEX, ProductFamily.S2_L1B_GR, bucketProperties.getL1GRBucket()));
            obsService.uploadWithMd5(fileInfosByFamily.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()), parentUid);
        } catch (Exception e) {
            throw new FileOperationException("Unable to upload files to OBS", e);
        }

        log.info("Uploaded L1A and/or L1B files to OBS");

        return fileInfosByFamily;
    }



}
