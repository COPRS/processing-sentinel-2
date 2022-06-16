package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.pw.l0c.model.AuxFile;
import eu.csgroup.coprs.ps2.pw.l0c.model.Datastrip;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExecutionInputService {

    private final AuxService auxService;
    private final JobOrderService jobOrderService;

    public ExecutionInputService(AuxService auxService, JobOrderService jobOrderService) {
        this.auxService = auxService;
        this.jobOrderService = jobOrderService;
    }

    public List<L0cExecutionInput> create(List<Datastrip> datastripList) {

        log.info("Creating output payload for all ready Datastrips ({})", datastripList.size());

        final List<L0cExecutionInput> l0uExecutionInputs = datastripList.stream().map(this::create).toList();

        log.info("Finished creating output payload for all ready Datastrips ({})", datastripList.size());

        return l0uExecutionInputs;
    }

    private L0cExecutionInput create(Datastrip datastrip) {

        log.info("Building execution input for Datastrip {}", datastrip.getName());

        final L0cExecutionInput l0cExecutionInput = new L0cExecutionInput()
                .setDatastrip(datastrip.getName())
                .setSatellite(datastrip.getSatellite())
                .setStation(datastrip.getStationCode());

        final Map<AuxFile, List<FileInfo>> auxFilesByType = auxService.getAux(datastrip);

        l0cExecutionInput.setFiles(auxFilesByType.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));

        Map<String, String> auxValues = new HashMap<>();

        auxFilesByType.forEach((auxFile, fileInfoList) -> {

            final String placeHolder = auxFile.getPlaceHolder();
            final String extension = auxFile.getAuxProductType().getExtension();

            if (!Strings.isEmpty(placeHolder) && !CollectionUtils.isEmpty(fileInfoList)) {

                if (fileInfoList.size() == 1) {
                    auxValues.put("@" + placeHolder + "@", fileInfoList.get(0).getLocalName() + extension);
                } else {
                    for (int i = 0; i < fileInfoList.size(); i++) {
                        auxValues.put("@" + placeHolder + String.format("%02d", i + 1) + "@", fileInfoList.get(i).getLocalName() + extension);
                    }
                }
            }
        });

        l0cExecutionInput.setJobOrders(jobOrderService.create(datastrip, auxValues));

        log.info("Finished building execution input for Datastrip {}", datastrip.getName());

        return l0cExecutionInput;
    }

}
