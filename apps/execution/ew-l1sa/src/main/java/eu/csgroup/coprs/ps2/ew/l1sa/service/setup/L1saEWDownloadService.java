package eu.csgroup.coprs.ps2.ew.l1sa.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.ArchiveUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.ew.service.EWDownloadService;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.Predicate;

@Slf4j
@Service
public class L1saEWDownloadService extends EWDownloadService {

    private static final String AUX_ECMWFD_REGEX = "S2D.*";

    public L1saEWDownloadService(ObsService obsService) {
        super(obsService);
    }

    @Override
    protected Predicate<FileInfo> customAux() {
        return fileInfo -> fileInfo.getAuxProductType().equals(AuxProductType.AUX_CAMSFO)
                || fileInfo.getAuxProductType().equals(AuxProductType.AUX_ECMWFD);
    }

    @Override
    protected void downloadCustomAux(Set<FileInfo> fileInfoSet) {

        if (!fileInfoSet.isEmpty()) {

            obsService.download(fileInfoSet);

            fileInfoSet.forEach(fileInfo -> {

                switch (fileInfo.getAuxProductType()) {

                    case AUX_CAMSFO -> {
                        final String dblName = fileInfo.getLocalName() + S2FileParameters.AUX_FILE_EXTENSION;
                        final Path dblPath = Paths.get(fileInfo.getFullLocalPath(), dblName);
                        ArchiveUtils.unTar(dblPath.toString(), true);
                    }

                    case AUX_ECMWFD -> {

                        final String localPath = fileInfo.getLocalPath();
                        final String downloadName = fileInfo.getLocalName();
                        final String dblName = downloadName + S2FileParameters.AUX_FILE_EXTENSION;

                        final Path tmpPath = Paths.get(localPath, downloadName + "_TMP");
                        FileOperationUtils.move(Paths.get(fileInfo.getFullLocalPath()), tmpPath);

                        final Path dblPath = tmpPath.resolve(dblName);
                        ArchiveUtils.unTar(dblPath.toString(), true);

                        FileOperationUtils.mergeFiles(tmpPath, Paths.get(fileInfo.getFullLocalPath()), AUX_ECMWFD_REGEX);

                        FileOperationUtils.deleteFolders(Set.of(tmpPath.toString()));
                    }

                    default -> log.warn("Something went horribly wrong");
                }
            });
        }
    }

}
