package eu.csgroup.coprs.ps2.pw.l2.service.monitor;

import eu.csgroup.coprs.ps2.core.pw.service.PWPendingProcessingService;
import eu.csgroup.coprs.ps2.pw.l2.service.prepare.L2DatastripService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class L2PWPendingProcessingService extends PWPendingProcessingService {

    protected L2PWPendingProcessingService(MeterRegistry registry, L2DatastripService itemService) {
        super(registry, itemService);
    }

    @Override
    protected String getLevel() {
        return "2";
    }

    @Override
    protected String getAddonName() {
        return "l2";
    }

}
