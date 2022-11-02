package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.S2FileParameters;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public abstract class EWDownloadService {

    protected final ObsService obsService;

    protected EWDownloadService(ObsService obsService) {
        this.obsService = obsService;
    }

    protected abstract void prepareStandardFiles(Set<FileInfo> fileInfoSet);

    protected abstract Predicate<FileInfo> customAux();

    protected abstract void downloadCustomAux(Set<FileInfo> fileInfoSet);

    public void download(Set<FileInfo> fileInfoSet) {

        log.info("Downloading {} files from object storage", fileInfoSet.size());

        final Map<Boolean, List<FileInfo>> filesByIsAux = fileInfoSet
                .stream()
                .collect(Collectors.partitioningBy(
                        fileInfo -> fileInfo.getProductFamily().equals(ProductFamily.S2_AUX)
                ));

        final Set<FileInfo> stdFiles = new HashSet<>(filesByIsAux.get(false));
        if (!stdFiles.isEmpty()) {
            prepareStandardFiles(stdFiles);
            obsService.download(stdFiles);
        }

        final HashSet<FileInfo> auxFiles = new HashSet<>(filesByIsAux.get(true));
        if (!auxFiles.isEmpty()) {
            final Map<Boolean, List<FileInfo>> auxByIsCustom = auxFiles
                    .stream()
                    .collect(Collectors.partitioningBy(customAux()));
            downloadStandardAux(new HashSet<>(auxByIsCustom.get(false)));
            downloadCustomAux(new HashSet<>(auxByIsCustom.get(true)));
        }

        log.info("Finished downloading files from object storage");
    }

    protected void downloadStandardAux(Set<FileInfo> fileInfoSet) {

        if (!fileInfoSet.isEmpty()) {

            obsService.download(fileInfoSet);

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

            FileOperationUtils.deleteFolders(trashFolders);
        }
    }

}
