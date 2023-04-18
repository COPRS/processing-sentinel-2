package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.settings.L12Parameters;
import eu.csgroup.coprs.ps2.core.common.utils.DatastripUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.pw.service.PWItemManagementService;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastrip;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastripEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class L0cDatastripManagementService extends PWItemManagementService<L0cDatastrip, L0cDatastripEntity, L0cDatastripService> {

    public L0cDatastripManagementService(L0cDatastripService datastripService, CatalogService catalogService) {
        super(catalogService, datastripService);
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

            // Creating required processing folders inside DT folder - Path being (...)/DT_xx/DS/<Datastrip>
            createSharedFolders(datastripPath.getParent().getParent());

            final Pair<Instant, Instant> datastripTimes = DatastripUtils.getDatastripTimes(datastripPath);

            itemService.create(datastripPath, datastripTimes.getLeft(), datastripTimes.getRight(), satellite, stationCode, t0PdgsDate);
        }
    }

    private void createSharedFolders(Path folderPath) {
        final Set<Path> folderPaths = Set.of(
                folderPath.resolve(L12Parameters.OUTPUT_FOLDER),
                folderPath.resolve(L12Parameters.AUX_FOLDER)
        );
        FileOperationUtils.createFolders(folderPaths.stream().map(Path::toString).collect(Collectors.toSet()));
    }

}
