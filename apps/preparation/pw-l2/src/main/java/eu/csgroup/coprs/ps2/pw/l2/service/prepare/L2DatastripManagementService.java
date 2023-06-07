package eu.csgroup.coprs.ps2.pw.l2.service.prepare;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.settings.L12Parameters;
import eu.csgroup.coprs.ps2.core.common.utils.DatastripUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import eu.csgroup.coprs.ps2.core.pw.model.ResubmitMessage;
import eu.csgroup.coprs.ps2.core.pw.service.PWItemManagementService;
import eu.csgroup.coprs.ps2.pw.l2.model.L2AuxFile;
import eu.csgroup.coprs.ps2.pw.l2.model.L2Datastrip;
import eu.csgroup.coprs.ps2.pw.l2.model.L2DatastripEntity;
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
public class L2DatastripManagementService extends PWItemManagementService<L2Datastrip, L2DatastripEntity, L2DatastripService> {

    private final SharedProperties sharedProperties;
    private final ObsService obsService;
    private final ObsBucketProperties bucketProperties;

    public L2DatastripManagementService(CatalogService catalogService, L2DatastripService itemService, SharedProperties sharedProperties, ObsService obsService,
            ObsBucketProperties bucketProperties
    ) {
        super(catalogService, itemService);
        this.sharedProperties = sharedProperties;
        this.obsService = obsService;
        this.bucketProperties = bucketProperties;
    }

    @Override
    public List<L2Datastrip> getMissingAux() {
        return itemService.readAll(true, false, false);
    }

    @Override
    public boolean isReady(L2Datastrip item) {
        return item.isTlComplete() &&
                // Only non-optional AUX need to be available for the DS to be ready for processing
                item.getAvailableByAux()
                        .entrySet()
                        .stream()
                        .filter(entry -> !L2AuxFile.valueOf(entry.getKey()).isOptional())
                        .allMatch(Map.Entry::getValue);
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

            final Pair<Instant, Instant> datastripTimes = DatastripUtils.getDatastripTimes(datastripPath);
            final List<String> tlList = DatastripUtils.getTLList(datastripPath);

            L2Datastrip datastrip = itemService.create(datastripName, datastripFolder, datastripTimes, satellite, t0PdgsDate, tlList);
            datastrip.setResubmitMessage(resubmitMessage);
            itemService.update(datastrip);
        }

        updateTLComplete(datastripName);
    }

    public void updateTLComplete(String datastripName) {

        if (itemService.exists(datastripName)) {

            final L2Datastrip datastrip = itemService.read(datastripName);

            if (!datastrip.isTlComplete()) {

                log.info("Checking TL files availability for datastrip {}", datastripName);

                final Map<String, Boolean> availableByTL = datastrip.getAvailableByTL();

                final Set<String> missingTL =
                        availableByTL
                                .entrySet()
                                .stream()
                                .filter(stringBooleanEntry -> !stringBooleanEntry.getValue())
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toSet());

                log.info("Datastrip {} is missing {} TL out of {}", datastripName, missingTL.size(), availableByTL.size());

                final Map<String, Boolean> missingAvailableByTL = obsService.exists(bucketProperties.getL1TLBucket(), missingTL);

                log.info("Found {} newly available TL", missingAvailableByTL.entrySet().stream().filter(Map.Entry::getValue).count());

                datastrip.setAvailableByTL(
                        Stream.of(availableByTL, missingAvailableByTL)
                                .flatMap(map -> map.entrySet().stream())
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue,
                                        (aBoolean, aBoolean2) -> aBoolean2
                                ))
                );

                if (datastrip.allTLAvailable()) {
                    log.info("Datastrip {} is now TL complete", datastripName);
                    datastrip.setTlComplete(true);
                }

                itemService.update(datastrip);

                log.info("Finished checking TL files availability for datastrip {}", datastripName);
            }
        }
    }

    private void createSharedFolders(Path folderPath) {
        final Set<Path> folderPaths = Set.of(
                folderPath.resolve(L12Parameters.INPUT_FOLDER),
                folderPath.resolve(L12Parameters.INPUT_FOLDER).resolve(L12Parameters.DS_FOLDER),
                folderPath.resolve(L12Parameters.INPUT_FOLDER).resolve(L12Parameters.TL_FOLDER),
                folderPath.resolve(L12Parameters.OUTPUT_FOLDER),
                folderPath.resolve(L12Parameters.AUX_FOLDER)
        );
        FileOperationUtils.createFolders(folderPaths.stream().map(Path::toString).collect(Collectors.toSet()));
    }

}
