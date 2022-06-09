package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.model.processing.EventAction;
import eu.csgroup.coprs.ps2.core.common.model.processing.Mission;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;

import java.time.Instant;
import java.util.UUID;

public final class ProcessingMessageUtils {

    public static ProcessingMessage create() {
        return new ProcessingMessage()
                .setUid(UUID.randomUUID().toString())
                .setCreationDate(DateUtils.toLongDate(Instant.now()))
                .setMissionId(Mission.S2.getValue())
                .setPodName(System.getenv("HOSTNAME"))
                .setRetryCounter(0)
                .setAllowedActions(new EventAction[]{EventAction.NO_ACTION, EventAction.RESTART});
    }

    private ProcessingMessageUtils() {
    }

}
