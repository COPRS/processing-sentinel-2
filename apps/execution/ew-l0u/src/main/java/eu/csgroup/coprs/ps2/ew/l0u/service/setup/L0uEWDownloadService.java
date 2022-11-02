package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.ew.service.EWDownloadService;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.ew.l0u.settings.L0uFolderParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Predicate;

@Slf4j
@Component
public class L0uEWDownloadService extends EWDownloadService {


    public L0uEWDownloadService(ObsService obsService) {
        super(obsService);
    }

    @Override
    protected void prepareStandardFiles(Set<FileInfo> fileInfoSet) {

        fileInfoSet.forEach(fileInfo -> {

            fileInfo.setLocalName(fileInfo.getObsName());

            String localPath = L0uFolderParameters.INPUT_PATH;
            if (fileInfo.getObsName().contains("ch1")) {
                localPath += "/ch_1";
            } else if (fileInfo.getObsName().contains("ch2")) {
                localPath += "/ch_2";
            }

            fileInfo.setLocalPath(localPath);
        });
    }

    @Override
    protected Predicate<FileInfo> customAux() {
        return fileInfo -> false;
    }

    @Override
    protected void downloadCustomAux(Set<FileInfo> fileInfoSet) {
        // N/A
    }

}
