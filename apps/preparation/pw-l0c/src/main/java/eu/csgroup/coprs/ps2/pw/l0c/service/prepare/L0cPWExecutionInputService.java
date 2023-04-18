package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.settings.L12Parameters;
import eu.csgroup.coprs.ps2.core.pw.service.PWExecutionInputService;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cDatastrip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class L0cPWExecutionInputService implements PWExecutionInputService<L0cExecutionInput, L0cDatastrip> {

    private final L0cAuxService auxService;

    public L0cPWExecutionInputService(L0cAuxService auxService) {
        this.auxService = auxService;
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

        final Path dtFolderPath = Path.of(datastrip.getDtFolder());

        final L0cExecutionInput l0cExecutionInput = new L0cExecutionInput();
        l0cExecutionInput
                .setDtFolder(dtFolderPath.toString())
                .setDatastrip(datastrip.getName())
                .setInputFolder(datastrip.getDtFolder())
                .setOutputFolder(dtFolderPath.resolve(L12Parameters.OUTPUT_FOLDER).toString())
                .setAuxFolder(dtFolderPath.resolve(L12Parameters.AUX_FOLDER).toString())
                .setSatellite(datastrip.getSatellite())
                .setStation(datastrip.getStationCode())
                .setStartTime(datastrip.getStartTime())
                .setStopTime(datastrip.getStopTime())
                .setT0PdgsDate(datastrip.getT0PdgsDate());

        l0cExecutionInput.setFiles(auxService.getAux(datastrip).values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));

        log.info("Finished building execution input for Datastrip {}", datastrip.getName());

        return l0cExecutionInput;
    }

}
