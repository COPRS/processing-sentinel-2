package eu.csgroup.coprs.ps2.ew.l0c.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class L0cEWDownloadService {

    private final ObsService obsService;

    public L0cEWDownloadService(ObsService obsService) {
        this.obsService = obsService;
    }

    public void download(Set<FileInfo> fileInfoSet) {

        log.info("Downloading files from object storage");

        obsService.download(fileInfoSet);

        Set<String> trashFolders = new HashSet<>();

        fileInfoSet.forEach(fileInfo -> {
            AuxProductType productType = AuxProductType.valueOf(fileInfo.getType());
            final String localPath = fileInfo.getLocalPath();
            final String downloadName = fileInfo.getLocalName();
            final String dblName = downloadName + S2FileParameters.AUX_FILE_EXTENSION;
            final Path dblPath = Paths.get(fileInfo.getFullLocalPath(), dblName);
            final Path destPath = Paths.get(localPath, downloadName + productType.getExtension());
            FileOperationUtils.move(dblPath, destPath);
            trashFolders.add(localPath + "/" + downloadName);
        });

        FileOperationUtils.deleteFolders(trashFolders);

        log.info("Finished downloading files from object storage");
    }

}
