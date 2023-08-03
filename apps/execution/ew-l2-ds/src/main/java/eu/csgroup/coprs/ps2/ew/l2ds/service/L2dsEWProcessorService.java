package eu.csgroup.coprs.ps2.ew.l2ds.service;

import eu.csgroup.coprs.ps2.core.common.model.l2.L2ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.ew.config.MissingOutputProperties;
import eu.csgroup.coprs.ps2.core.ew.service.EWProcessorService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.exec.L2dsEWExecutionService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.output.L2dsEWOutputService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.setup.L2dsEWInputService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.setup.L2dsEWSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Slf4j
@Configuration
public class L2dsEWProcessorService extends EWProcessorService<L2ExecutionInput> {

    protected L2dsEWProcessorService(
            L2dsEWInputService inputService,
            L2dsEWSetupService setupService,
            L2dsEWExecutionService executionService,
            L2dsEWOutputService outputService,
            MissingOutputProperties missingOutputProperties
    ) {
        super(inputService, setupService, executionService, outputService, missingOutputProperties);
    }

    @Override
    protected List<TaskMissingOutput> getMissingOutputs(L2ExecutionInput executionInput) {
        final int tileCount = executionInput.getTileList().size();
        final String satellite = executionInput.getSatellite();
        final String l2IpfVersion = missingOutputProperties.getL2IpfVersion();
        return List.of(
                buildMissingOutput(MissingOutputProductType.L2A_DS, 1, satellite, 2, true, l2IpfVersion),
                buildMissingOutput(MissingOutputProductType.L2A_TL, tileCount, satellite, 2, true, l2IpfVersion),
                buildMissingOutput(MissingOutputProductType.L2A_TC, tileCount, satellite, 2, true, l2IpfVersion)
        );
    }

}
