package eu.csgroup.coprs.ps2.pw.l2.model;

import eu.csgroup.coprs.ps2.core.pw.model.PWItem;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
public class L2Datastrip extends PWItem {

    private String folder;
    private Map<String, Boolean> availableByTL;
    private boolean tlComplete;

    public boolean allTLAvailable() {
        return availableByTL.values().stream().allMatch(Boolean::booleanValue);
    }

}
