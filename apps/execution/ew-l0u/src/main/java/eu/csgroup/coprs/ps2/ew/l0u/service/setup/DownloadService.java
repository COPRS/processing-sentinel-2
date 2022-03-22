package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.ew.l0u.settings.FolderParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class DownloadService {

    private final ObsService obsService;

    public DownloadService(ObsService obsService) {
        this.obsService = obsService;
    }

    public void download(Set<FileInfo> fileInfoSet) {

        log.info("Downloading files from object storage");

        fileInfoSet.forEach(fileInfo -> {

            fileInfo.setLocalName(fileInfo.getObsName());

            String localPath = FolderParameters.INPUT_PATH;
            if (fileInfo.getObsName().contains("ch1")) {
                localPath += "/ch_1";
            } else if (fileInfo.getObsName().contains("ch2")) {
                localPath += "/ch_2";
            }

            fileInfo.setLocalPath(localPath);
        });

        obsService.downloadAll(fileInfoSet);

        log.info("Finished downloading files from object storage");
    }

}
