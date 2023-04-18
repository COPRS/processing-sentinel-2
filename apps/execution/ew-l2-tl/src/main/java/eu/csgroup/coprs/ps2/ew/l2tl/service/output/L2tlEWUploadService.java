package eu.csgroup.coprs.ps2.ew.l2tl.service.output;

import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.common.settings.L12Parameters;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ArchiveUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWUploadService;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class L2tlEWUploadService extends EWUploadService<L2ExecutionInput> {

    private final ObsBucketProperties bucketProperties;
    private final ObsService obsService;

    public L2tlEWUploadService(ObsBucketProperties bucketProperties, ObsService obsService) {
        this.bucketProperties = bucketProperties;
        this.obsService = obsService;
    }

    @Override
    public Map<ProductFamily, Set<FileInfo>> upload(L2ExecutionInput executionInput, UUID parentUid) {

        log.info("Uploading L2A TL to OBS");

        final Map<ProductFamily, Set<FileInfo>> fileInfoByFamily = new EnumMap<>(ProductFamily.class);
        final Path rootPath = Path.of(FolderParameters.WORKING_FOLDER_ROOT, L12Parameters.L2A_TL_ROOT);

        try {

            // Currently L2A is produced as tar archive, need to extract that first
            FileOperationUtils.findFiles(rootPath, S2FileParameters.L2A_TL_TAR_REGEX).forEach(archive -> ArchiveUtils.unTar(archive.toString(), false));

            fileInfoByFamily.putAll(buildFolderInfoInFolder(rootPath, S2FileParameters.L2A_TL_REGEX, ProductFamily.S2_L2A_TL, bucketProperties.getL2TLBucket()));
            fileInfoByFamily.putAll(buildFileInfoInFolder(rootPath, S2FileParameters.L2A_TC_REGEX, ProductFamily.S2_L2A_TC, bucketProperties.getL2TCBucket()));

            obsService.uploadWithMd5(fileInfoByFamily.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()), parentUid);

        } catch (Exception e) {
            throw new FileOperationException("Unable to upload files to OBS", e);
        }

        log.info("Uploaded L2A TL files to OBS");

        return fileInfoByFamily;
    }

}
