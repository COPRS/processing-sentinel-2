package eu.csgroup.coprs.ps2.ew.l2tl.service.setup;

import eu.csgroup.coprs.ps2.core.ew.service.EWDownloadService;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class L2tlEWDownloadService extends EWDownloadService {

    public L2tlEWDownloadService(ObsService obsService) {
        super(obsService);
    }

}
