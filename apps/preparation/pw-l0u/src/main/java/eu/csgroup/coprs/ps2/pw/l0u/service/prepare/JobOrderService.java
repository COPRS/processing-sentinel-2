package eu.csgroup.coprs.ps2.pw.l0u.service.prepare;

import eu.csgroup.coprs.ps2.core.common.exception.JobOrderException;
import eu.csgroup.coprs.ps2.core.common.settings.JobParameters;
import eu.csgroup.coprs.ps2.core.common.utils.DateUtils;
import eu.csgroup.coprs.ps2.core.common.utils.FileContentUtils;
import eu.csgroup.coprs.ps2.pw.l0u.model.AuxValue;
import eu.csgroup.coprs.ps2.pw.l0u.model.JobOrderFields;
import eu.csgroup.coprs.ps2.pw.l0u.model.Session;
import eu.csgroup.coprs.ps2.pw.l0u.settings.L0uPreparationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class JobOrderService {

    private static final String JOB_ORDER_TEMPLATE = "templates/job_order_eisp_ing_typ-V2.xml";

    private final AuxService auxService;
    private final L0uPreparationProperties l0uPreparationProperties;

    public JobOrderService(AuxService auxService, L0uPreparationProperties l0uPreparationProperties) {
        this.auxService = auxService;
        this.l0uPreparationProperties = l0uPreparationProperties;
    }


    public Map<String, String> create(Session session) {

        log.info("Creating Job Orders for session: {}", session.getName());

        final Map<AuxValue, String> infoByAuxValue = auxService.getValues(session.getSatellite(), session.getStartTime(), session.getStopTime());

        final Map<String, String> jobOrders = new HashMap<>();
        Resource resource = new ClassPathResource(JOB_ORDER_TEMPLATE);

        try {

            String jobOrder = Files.readString(resource.getFile().toPath());
            String startTime = DateUtils.toShortDate(session.getStartTime());

            final Map<String, String> values = new HashMap<>();
            values.put(JobOrderFields.MISSION_ID.getPlaceholder(), "S2" + session.getSatellite());
            values.put(JobOrderFields.DOWNLINK_TIME.getPlaceholder(), startTime);
            values.put(JobOrderFields.ACQUISITION_START.getPlaceholder(), startTime);
            values.put(JobOrderFields.PROCESSING_STATION.getPlaceholder(), JobParameters.PROC_STATION);
            values.put(JobOrderFields.ACQUISITION_STATION.getPlaceholder(), session.getStationCode());
            values.put(JobOrderFields.THEORETICAL_LINE_PERIOD.getPlaceholder(), infoByAuxValue.get(AuxValue.THEORETICAL_LINE_PERIOD));
            values.put(JobOrderFields.MINIMUM_PACKET_LENGTH.getPlaceholder(), infoByAuxValue.get(AuxValue.SP_MIN_SIZE));
            values.put(JobOrderFields.GPS_TO_TAI.getPlaceholder(), infoByAuxValue.get(AuxValue.GPS_TIME_TAI));
            values.put(JobOrderFields.TAI_TO_UTC.getPlaceholder(), infoByAuxValue.get(AuxValue.TAI_TO_UTC));
            values.put(JobOrderFields.ORBIT_OFFSET.getPlaceholder(), infoByAuxValue.get(AuxValue.ORBIT_OFFSET));
            values.put(JobOrderFields.CYCLIC_ORBIT_OFFSET.getPlaceholder(), infoByAuxValue.get(AuxValue.TOTAL_ORBIT));

            jobOrder = FileContentUtils.replaceAll(jobOrder, values);

            jobOrders.put(l0uPreparationProperties.getJobOrderName(), jobOrder);

        } catch (Exception e) {
            throw new JobOrderException("Unable to create Job Order", e);
        }

        log.info("Finished creating Job Orders for session: {}", session.getName());

        return jobOrders;
    }

}
