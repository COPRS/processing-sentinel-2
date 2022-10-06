package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.pw.service.PWItemManagementService;
import eu.csgroup.coprs.ps2.core.common.utils.DatastripUtils;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastrip;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastripEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;


@Slf4j
@Service
@Transactional
public class L0cDatastripManagementService extends PWItemManagementService<L0cDatastrip, L0cDatastripEntity, L0cDatastripService> {

    public L0cDatastripManagementService(L0cDatastripService datastripService, CatalogService catalogService) {
        super(catalogService, datastripService);
    }


    @Override
    public List<L0cDatastrip> getReady() {
        return itemService.readAll(true, false);
    }

    @Override
    public List<L0cDatastrip> getNotReady() {
        return itemService.readAll(false, false);
    }

    @Override
    public List<L0cDatastrip> getMissingAux() {
        return getNotReady().stream()
                .filter(datastrip -> !datastrip.allAuxAvailable())
                .toList();
    }

    @Override
    public boolean isReady(L0cDatastrip item) {
        return item.allAuxAvailable();
    }


    public void create(Path datastripPath, String satellite, String stationCode, Instant t0PdgsDate) {

        final String datastripName = datastripPath.getFileName().toString();

        if (!itemService.exists(datastripName)) {

            log.info("Creating Datastrip {}", datastripName);

            final String datastripFolder = datastripPath.getParent().toString();
            final Pair<Instant, Instant> datastripTimes = DatastripUtils.getDatastripTimes(datastripPath);

            itemService.create(datastripName, datastripFolder, datastripTimes.getLeft(), datastripTimes.getRight(), satellite, stationCode, t0PdgsDate);
        }
    }

}
