package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.pw.service.PWExecutionInputService;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cAuxFile;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastrip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class L0cPWExecutionInputService implements PWExecutionInputService<L0cExecutionInput, L0cDatastrip> {

    private final L0cAuxService auxService;
    private final L0cJobOrderService jobOrderService;

    public L0cPWExecutionInputService(L0cAuxService auxService, L0cJobOrderService jobOrderService) {
        this.auxService = auxService;
        this.jobOrderService = jobOrderService;
    }

    @Override
    public List<L0cExecutionInput> create(List<L0cDatastrip> datastripList) {

        log.info("Creating output payload for all ready Datastrips ({})", datastripList.size());

        final List<L0cExecutionInput> l0uExecutionInputs = datastripList.stream().map(this::create).toList();

        log.info("Finished creating output payload for all ready Datastrips ({})", datastripList.size());

        return l0uExecutionInputs;
    }

    private L0cExecutionInput create(L0cDatastrip datastrip) {

        log.info("Building execution input for Datastrip {}", datastrip.getName());

        final L0cExecutionInput l0cExecutionInput = new L0cExecutionInput();
        l0cExecutionInput.setDatastrip(datastrip.getName())
                .setDtFolder(Paths.get(datastrip.getFolder()).getParent().toString())
                .setSatellite(datastrip.getSatellite())
                .setStation(datastrip.getStationCode())
                .setStartTime(datastrip.getStartTime())
                .setStopTime(datastrip.getStopTime())
                .setT0PdgsDate(datastrip.getT0PdgsDate());

        final Map<L0cAuxFile, List<FileInfo>> auxFilesByType = auxService.getAux(datastrip);

        l0cExecutionInput.setFiles(auxFilesByType.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));

        l0cExecutionInput.setJobOrders(jobOrderService.create(datastrip, auxFilesByType));

        log.info("Finished building execution input for Datastrip {}", datastrip.getName());

        return l0cExecutionInput;
    }

}
