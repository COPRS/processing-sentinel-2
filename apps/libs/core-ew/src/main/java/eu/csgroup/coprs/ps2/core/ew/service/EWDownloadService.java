package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.FolderParameters;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public abstract class EWDownloadService {

    protected final ObsService obsService;

    protected EWDownloadService(ObsService obsService) {
        this.obsService = obsService;
    }

    public void download(Set<FileInfo> fileInfoSet, UUID parentUid) {

        log.info("Downloading files from object storage");

        final Map<Boolean, List<FileInfo>> filesByIsAux = fileInfoSet
                .stream()
                .collect(Collectors.partitioningBy(
                        fileInfo -> fileInfo.getProductFamily().equals(ProductFamily.S2_AUX)
                ));

        final Set<FileInfo> stdFiles = new HashSet<>(filesByIsAux.get(false));
        if (!stdFiles.isEmpty()) {
            log.info("Downloading standard files ({})", stdFiles.size());
            prepareStandardFiles(stdFiles);
            obsService.download(stdFiles, parentUid);
            log.info("Finished downloading standard files");
        }

        final HashSet<FileInfo> auxFiles = new HashSet<>(filesByIsAux.get(true));
        if (!auxFiles.isEmpty()) {
            log.info("Downloading aux files ({})", auxFiles.size());

            log.info("Cleaning up aux folder");
            // Quick hack to get aux folder root - will do better another time, for sure
            final String auxFolder = StringUtils.substringBefore(auxFiles.iterator().next().getLocalPath(), FolderParameters.AUX_FOLDER) + FolderParameters.AUX_FOLDER;
            FileOperationUtils.deleteFolderContent(auxFolder);
            log.info("Finished cleaning up aux folder");

            final Map<Boolean, List<FileInfo>> auxByIsCustom = auxFiles
                    .stream()
                    .collect(Collectors.partitioningBy(customAux()));
            downloadStandardAux(new HashSet<>(auxByIsCustom.get(false)), parentUid);
            downloadCustomAux(new HashSet<>(auxByIsCustom.get(true)), parentUid);
            log.info("Finished downloading aux files");
        }

        log.info("Finished downloading files from object storage");
    }

    protected void prepareStandardFiles(Set<FileInfo> fileInfoSet) {
        // By default, nothing to do
    }

    protected Predicate<FileInfo> customAux() {
        // By default, there's no custom aux
        return fileInfo -> false;
    }

    protected void downloadStandardAux(Set<FileInfo> fileInfoSet, UUID parentUid) {

        if (!fileInfoSet.isEmpty()) {

            obsService.download(fileInfoSet, parentUid);

            Set<String> trashFolders = new HashSet<>();

            fileInfoSet.forEach(fileInfo -> {
                AuxProductType productType = fileInfo.getAuxProductType();
                final String localPath = fileInfo.getLocalPath();
                final String downloadName = fileInfo.getLocalName();
                final String dblName = downloadName + S2FileParameters.AUX_FILE_EXTENSION;
                final Path dblPath = Paths.get(fileInfo.getFullLocalPath(), dblName);
                final Path destPath = Paths.get(localPath, downloadName + productType.getExtension());
                FileOperationUtils.move(dblPath, destPath);
                trashFolders.add(localPath + "/" + downloadName);
            });

            log.info("Cleaning up temporary files");
            FileOperationUtils.deleteFolders(trashFolders);
            log.info("Finished cleaning up temporary files");
        }
    }

    protected void downloadCustomAux(Set<FileInfo> fileInfoSet, UUID parentUid) {
        // By default, nothing to do
    }

}
