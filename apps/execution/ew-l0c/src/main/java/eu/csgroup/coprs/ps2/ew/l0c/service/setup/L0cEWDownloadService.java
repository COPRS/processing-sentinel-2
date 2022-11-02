package eu.csgroup.coprs.ps2.ew.l0c.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.ew.service.EWDownloadService;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.Predicate;

@Slf4j
@Service
public class L0cEWDownloadService extends EWDownloadService {


    public L0cEWDownloadService(ObsService obsService) {
        super(obsService);
    }

    @Override
    protected void prepareStandardFiles(Set<FileInfo> fileInfoSet) {
        // N/A
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
