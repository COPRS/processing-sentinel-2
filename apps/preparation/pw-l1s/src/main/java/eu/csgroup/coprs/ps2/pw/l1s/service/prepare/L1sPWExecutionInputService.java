package eu.csgroup.coprs.ps2.pw.l1s.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.L1Parameters;
import eu.csgroup.coprs.ps2.core.pw.service.PWExecutionInputService;
import eu.csgroup.coprs.ps2.pw.l1s.config.L1sPreparationProperties;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastrip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class L1sPWExecutionInputService implements PWExecutionInputService<L1ExecutionInput, L1sDatastrip> {

    private final L1sPreparationProperties l1sPreparationProperties;
    private final L1sAuxService auxService;

    public L1sPWExecutionInputService(L1sPreparationProperties l1sPreparationProperties, L1sAuxService auxService) {
        this.l1sPreparationProperties = l1sPreparationProperties;
        this.auxService = auxService;
    }

    @Override
    public List<L1ExecutionInput> create(List<L1sDatastrip> itemList) {

        log.info("Creating output payload for all ready Datastrips ({})", itemList.size());

        final List<L1sDatastrip> eligibleDatastrips = itemList.stream()
                .filter(datastrip -> datastrip.getAvailableByGR().size() >= l1sPreparationProperties.getMinGrRequired())
                .toList();

        final List<L1ExecutionInput> l1ExecutionInputs = eligibleDatastrips.stream()
                .map(this::create)
                .toList();

        log.info("Finished creating output payload for all ready Datastrips ({})", itemList.size());

        return l1ExecutionInputs;
    }

    private L1ExecutionInput create(L1sDatastrip datastrip) {

        log.info("Building execution input for Datastrip {}", datastrip.getName());

        final Path rootPath = Paths.get(l1sPreparationProperties.getSharedFolderRoot(), datastrip.getFolder());
        final Path inputPath = rootPath.resolve(L1Parameters.INPUT_FOLDER);

        final L1ExecutionInput executionInput = new L1ExecutionInput();
        executionInput.setDatastrip(datastrip.getName())
                .setDatatakeType(datastrip.getDatatakeType())
                .setInputFolder(inputPath.toString())
                .setOutputFolder(rootPath.resolve(L1Parameters.OUTPUT_FOLDER).toString())
                .setAuxFolder(rootPath.resolve(L1Parameters.AUX_FOLDER).toString())
                .setFiles(new HashSet<>())
                .setSatellite(datastrip.getSatellite())
                .setStation(datastrip.getStationCode())
                .setStartTime(datastrip.getStartTime())
                .setStopTime(datastrip.getStopTime())
                .setT0PdgsDate(datastrip.getT0PdgsDate());

        executionInput.getFiles().addAll(
                getGRFileInfos(datastrip, inputPath.resolve(L1Parameters.GR_FOLDER))
        );

        executionInput.getFiles().addAll(
                auxService.getAux(datastrip).values().stream().flatMap(Collection::stream).collect(Collectors.toSet())
        );

        log.info("Finished building execution input for Datastrip {}", datastrip.getName());

        return executionInput;
    }

    private Set<FileInfo> getGRFileInfos(L1sDatastrip datastrip, Path dsPath) {
        return datastrip.getAvailableByGR().keySet()
                .stream()
                .map(gr -> new FileInfo()
                        .setBucket(l1sPreparationProperties.getL0GRBucket())
                        .setObsName(gr)
                        .setLocalPath(dsPath.toString())
                        .setLocalName(gr)
                        .setProductFamily(ProductFamily.S2_L0_GR))
                .collect(Collectors.toSet());
    }

}
