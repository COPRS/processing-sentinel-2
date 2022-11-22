package eu.csgroup.coprs.ps2.pw.l0c.service.monitor;

import eu.csgroup.coprs.ps2.core.pw.service.PWPendingProcessingService;
import eu.csgroup.coprs.ps2.pw.l0c.service.prepare.L0cDatastripService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class L0cPWPendingProcessingService extends PWPendingProcessingService {

    protected L0cPWPendingProcessingService(MeterRegistry registry, L0cDatastripService itemService) {
        super(registry, itemService);
    }

    @Override
    protected String getLevel() {
        return "0";
    }

    @Override
    protected String getAddonName() {
        return "l0c";
    }

}
