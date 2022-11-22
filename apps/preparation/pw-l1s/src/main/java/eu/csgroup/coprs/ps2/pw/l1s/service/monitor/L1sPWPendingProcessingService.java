package eu.csgroup.coprs.ps2.pw.l1s.service.monitor;

import eu.csgroup.coprs.ps2.core.pw.service.PWPendingProcessingService;
import eu.csgroup.coprs.ps2.pw.l1s.service.prepare.L1sDatastripService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class L1sPWPendingProcessingService extends PWPendingProcessingService {

    protected L1sPWPendingProcessingService(MeterRegistry registry, L1sDatastripService itemService) {
        super(registry, itemService);
    }

    @Override
    protected String getLevel() {
        return "1";
    }

    @Override
    protected String getAddonName() {
        return "l1";
    }

}
