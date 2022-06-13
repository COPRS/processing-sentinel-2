package eu.csgroup.coprs.ps2.pw.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.l0.FileType;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.utils.ObsUtils;
import org.springframework.stereotype.Component;

import javax.validation.Valid;


@Component
public class InputService {

    public FileType extract(@Valid ProcessingMessage processingMessage) {

        final String filename = ObsUtils.keyToName(processingMessage.getKeyObjectStorage());

        return switch (processingMessage.getProductFamily()) {
            case EDRS_SESSION -> filename.endsWith(".raw") ? FileType.DSDB : FileType.DSIB;
            case S2_AUX -> FileType.AUX;
            default -> FileType.UNKNOWN;
        };
    }

}
