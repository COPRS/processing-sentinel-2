package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.pw.l0c.model.Datastrip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ExecutionInputService {

    private final JobOrderService jobOrderService;

    public ExecutionInputService(JobOrderService jobOrderService) {
        this.jobOrderService = jobOrderService;
    }

    public List<L0cExecutionInput> create(List<Datastrip> datastripList) {

        log.info("Creating output payload for all ready Datastrips ({})", datastripList.size());

        final List<L0cExecutionInput> l0uExecutionInputs = datastripList.stream().map(this::create).toList();

        log.info("Finished creating output payload for all ready Datastrips ({})", datastripList.size());

        return l0uExecutionInputs;
    }

    private L0cExecutionInput create(Datastrip datastrip) {

        // TODO
        // Create JO first
        // Then ExecInput

        final L0cExecutionInput l0cExecutionInput = new L0cExecutionInput();

        // TODO use AuxService to get that
        l0cExecutionInput.setFiles(null);

        // TODO have JOService use previously defined map (transformed to just PH - name)
        l0cExecutionInput.setJobOrders(jobOrderService.create(datastrip));


        return l0cExecutionInput;
    }

}
