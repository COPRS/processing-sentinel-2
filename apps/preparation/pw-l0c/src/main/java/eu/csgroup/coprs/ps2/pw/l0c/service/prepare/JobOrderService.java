package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.catalog.service.CatalogService;
import eu.csgroup.coprs.ps2.pw.l0c.model.Datastrip;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JobOrderService {

    private final CatalogService catalogService;

    public JobOrderService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public Map<String, String> create(Datastrip datastrip) {

        // TODO
        // Check all aux and fetch names
        // Insert into templates
        // Use parallel detector or band
        // Add S2A step if A

        return null;

    }

}
