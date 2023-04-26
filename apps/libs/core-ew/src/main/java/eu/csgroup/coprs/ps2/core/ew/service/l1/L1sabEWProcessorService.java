package eu.csgroup.coprs.ps2.core.ew.service.l1;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.Level;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.MissingOutputProductType;
import eu.csgroup.coprs.ps2.core.common.model.trace.missing.TaskMissingOutput;
import eu.csgroup.coprs.ps2.core.ew.service.EWSetupService;
import eu.csgroup.coprs.ps2.core.ew.service.l01.L01EWExecutionService;

import java.util.ArrayList;
import java.util.List;

public abstract class L1sabEWProcessorService extends L1EWProcessorService {

    protected L1sabEWProcessorService(L1EWInputService inputService,
            EWSetupService<L1ExecutionInput> setupService,
            L01EWExecutionService<L1ExecutionInput> executionService, L1EWOutputService outputService
    ) {
        super(inputService, setupService, executionService, outputService);
    }

    public static final int GR_TO_TL_RATIO = 6;

    protected List<TaskMissingOutput> getL1abMissingOutput(L1ExecutionInput executionInput) {

        List<TaskMissingOutput> missingOutputs = new ArrayList<>();
        int grCount = (int) getGrCount(executionInput);

        if (executionInput.getDatatakeType().getLevelList().contains(Level.L1A)) {
            missingOutputs.add(buildL1MissingOutput(MissingOutputProductType.L1A_DS, 1, executionInput));
            missingOutputs.add(buildL1MissingOutput(MissingOutputProductType.L1A_GR, grCount, executionInput));
        }
        if (executionInput.getDatatakeType().getLevelList().contains(Level.L1B)) {
            missingOutputs.add(buildL1MissingOutput(MissingOutputProductType.L1B_DS, 1, executionInput));
            missingOutputs.add(buildL1MissingOutput(MissingOutputProductType.L1B_GR, grCount, executionInput));
        }

        return missingOutputs;
    }

    protected List<TaskMissingOutput> getL1sabMissingOutputs(L1ExecutionInput executionInput) {

        List<TaskMissingOutput> missingOutputs = getL1abMissingOutput(executionInput);

        if (executionInput.getDatatakeType().getLevelList().contains(Level.L1C)) {
            double grCount = getGrCount(executionInput);
            missingOutputs.addAll(new ArrayList<>(List.of(
                    buildL1MissingOutput(MissingOutputProductType.L1C_TL, calculateTileApproximation(grCount), executionInput),
                    buildL1MissingOutput(MissingOutputProductType.L1C_TC, calculateTileApproximation(grCount), executionInput)
            )));
            missingOutputs.add(buildL1MissingOutput(MissingOutputProductType.L1C_DS, 1, executionInput));
        }

        return missingOutputs;
    }

    private long getGrCount(L1ExecutionInput executionInput) {
        return executionInput.getFiles().stream().filter(fileInfo -> fileInfo.getProductFamily().equals(ProductFamily.S2_L0_GR)).count();
    }

    private int calculateTileApproximation(double grCount) {
        return (int) Math.ceil(grCount / GR_TO_TL_RATIO);
    }

}
