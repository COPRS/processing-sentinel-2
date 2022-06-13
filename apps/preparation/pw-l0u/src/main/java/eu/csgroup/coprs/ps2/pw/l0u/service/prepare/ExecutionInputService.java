package eu.csgroup.coprs.ps2.pw.l0u.service.prepare;

import eu.csgroup.coprs.ps2.core.catalog.service.CatalogService;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.pw.l0u.model.Session;
import eu.csgroup.coprs.ps2.pw.l0u.settings.L0uPreparationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ExecutionInputService {

    private final CatalogService catalogService;
    private final JobOrderService jobOrderService;
    private final L0uPreparationProperties l0uPreparationProperties;

    public ExecutionInputService(CatalogService catalogService, JobOrderService jobOrderService, L0uPreparationProperties l0uPreparationProperties) {
        this.catalogService = catalogService;
        this.jobOrderService = jobOrderService;
        this.l0uPreparationProperties = l0uPreparationProperties;
    }


    public List<L0uExecutionInput> create(List<Session> sessionList) {

        log.info("Creating output payload for all ready sessions ({})", sessionList.size());

        final List<L0uExecutionInput> l0uExecutionInputs = sessionList.stream().map(this::create).toList();

        log.info("Finished creating output payload for all ready sessions ({})", sessionList.size());

        return l0uExecutionInputs;
    }

    private L0uExecutionInput create(Session session) {

        final String sessionName = session.getName();

        log.info("Creating output payload for session {}", sessionName);

        final L0uExecutionInput l0uExecutionInput = new L0uExecutionInput()
                .setSession(sessionName)
                .setSatellite(session.getSatellite())
                .setFiles(getFileInfos(sessionName))
                .setJobOrders(jobOrderService.create(session));

        log.info("Finished creating output payload for session {}", sessionName);

        return l0uExecutionInput;
    }

    private Set<FileInfo> getFileInfos(String sessionName) {
        return catalogService.retrieveSessionData(sessionName)
                .stream()
                .map(sessionCatalogData -> new FileInfo()
                        .setBucket(l0uPreparationProperties.getCaduBucket())
                        .setKey(sessionCatalogData.getKeyObjectStorage()))
                .collect(Collectors.toSet());
    }

}
