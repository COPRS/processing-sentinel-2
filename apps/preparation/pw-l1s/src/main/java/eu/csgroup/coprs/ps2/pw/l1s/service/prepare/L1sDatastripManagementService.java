package eu.csgroup.coprs.ps2.pw.l1s.service.prepare;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.processing.DatatakeType;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.settings.L12Parameters;
import eu.csgroup.coprs.ps2.core.common.utils.DatastripUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.core.pw.model.ResubmitMessage;
import eu.csgroup.coprs.ps2.core.pw.service.PWItemManagementService;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastrip;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastripEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Service
@Transactional
public class L1sDatastripManagementService extends PWItemManagementService<L1sDatastrip, L1sDatastripEntity, L1sDatastripService> {

    private final SharedProperties sharedProperties;
    private final ObsService obsService;
    private final ObsBucketProperties bucketProperties;

    public L1sDatastripManagementService(CatalogService catalogService, L1sDatastripService itemService, SharedProperties sharedProperties, ObsService obsService,
            ObsBucketProperties bucketProperties
    ) {
        super(catalogService, itemService);
        this.sharedProperties = sharedProperties;
        this.obsService = obsService;
        this.bucketProperties = bucketProperties;
    }

    @Override
    public List<L1sDatastrip> getMissingAux() {
        return itemService.readAll(true, false, false);
    }

    @Override
    public boolean isReady(L1sDatastrip item) {
        return item.isGrComplete() && item.allAuxAvailable();
    }


    public void create(String datastripName, String satellite, Instant t0PdgsDate, String storagePath, ResubmitMessage resubmitMessage) {

        if (!itemService.exists(datastripName)) {

            log.info("Creating Datastrip entry for {}", datastripName);

            final String datastripFolder = UUID.randomUUID().toString();
            final Path datastripFolderPath = Paths.get(sharedProperties.getSharedFolderRoot()).resolve(datastripFolder);
            final Path dsFolderPath = datastripFolderPath.resolve(L12Parameters.INPUT_FOLDER).resolve(L12Parameters.DS_FOLDER);

            createSharedFolders(datastripFolderPath);

            obsService.download(Set.of(
                    new FileInfo()
                            .setObsURL(storagePath)
                            .setLocalName(datastripName)
                            .setLocalPath(dsFolderPath.toString())));

            final Path datastripPath = dsFolderPath.resolve(datastripName);

            final DatatakeType datatakeType = DatastripUtils.getDatatakeType(datastripPath);
            final Pair<Instant, Instant> datastripTimes = DatastripUtils.getDatastripTimes(datastripPath);
            final List<String> grList = DatastripUtils.getGRList(datastripPath);

            L1sDatastrip datastrip = itemService.create(datastripName, datastripFolder, datastripTimes, satellite, t0PdgsDate, datatakeType, grList);

            datastrip.setResubmitMessage(resubmitMessage);
            itemService.update(datastrip);

        }

        updateGRComplete(datastripName);
    }

    public void updateGRComplete(String datastripName) {

        if (itemService.exists(datastripName)) {

            final L1sDatastrip datastrip = itemService.read(datastripName);

            if (!datastrip.isGrComplete()) {

                log.info("Checking GR files availability for datastrip {}", datastripName);

                final Map<String, Boolean> availableByGR = datastrip.getAvailableByGR();

                final Set<String> missingGR =
                        availableByGR
                                .entrySet()
                                .stream()
                                .filter(stringBooleanEntry -> !stringBooleanEntry.getValue())
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toSet());

                log.info("Datastrip {} is missing {} GR out of {}", datastripName, missingGR.size(), availableByGR.size());

                final Map<String, Boolean> missingAvailableByGR = obsService.exists(bucketProperties.getL0GRBucket(), missingGR);

                log.info("Found {} newly available GR", missingAvailableByGR.entrySet().stream().filter(Map.Entry::getValue).count());

                datastrip.setAvailableByGR(
                        Stream.of(availableByGR, missingAvailableByGR)
                                .flatMap(map -> map.entrySet().stream())
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue,
                                        (aBoolean, aBoolean2) -> aBoolean2
                                ))
                );

                if (datastrip.allGRAvailable()) {
                    log.info("Datastrip {} is now GR complete", datastripName);
                    datastrip.setGrComplete(true);
                }

                itemService.update(datastrip);

                log.info("Finished checking GR files availability for datastrip {}", datastripName);
            }
        }
    }

    private void createSharedFolders(Path folderPath) {
        final Set<Path> folderPaths = Set.of(
                folderPath.resolve(L12Parameters.INPUT_FOLDER),
                folderPath.resolve(L12Parameters.INPUT_FOLDER).resolve(L12Parameters.DS_FOLDER),
                folderPath.resolve(L12Parameters.INPUT_FOLDER).resolve(L12Parameters.GR_FOLDER),
                folderPath.resolve(L12Parameters.OUTPUT_FOLDER),
                folderPath.resolve(L12Parameters.AUX_FOLDER)
        );
        FileOperationUtils.createFolders(folderPaths.stream().map(Path::toString).collect(Collectors.toSet()));
    }

}
