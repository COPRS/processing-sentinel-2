package eu.csgroup.coprs.ps2.ew.l1sa.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.ew.service.l1.L1EWInputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class L1saEWInputService extends L1EWInputService {

    @Override
    public Set<String> getTaskInputs(L1ExecutionInput executionInput) {
        final Set<String> inputs = executionInput.getFiles().stream()
                .filter(fileInfo -> fileInfo.getProductFamily().equals(ProductFamily.S2_L0_GR))
                .map(FileInfo::getObsName)
                .collect(Collectors.toSet());
        inputs.add(executionInput.getDatastrip());
        return inputs;
    }

}
