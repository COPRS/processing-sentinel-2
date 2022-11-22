package eu.csgroup.coprs.ps2.pw.l0u.service.monitor;

import eu.csgroup.coprs.ps2.core.pw.service.PWPendingProcessingService;
import eu.csgroup.coprs.ps2.pw.l0u.service.prepare.SessionService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class L0uPWPendingProcessingService extends PWPendingProcessingService {

    protected L0uPWPendingProcessingService(MeterRegistry registry, SessionService itemService) {
        super(registry, itemService);
    }

    @Override
    protected String getLevel() {
        return "0";
    }

    @Override
    protected String getAddonName() {
        return "l0u";
    }

}
