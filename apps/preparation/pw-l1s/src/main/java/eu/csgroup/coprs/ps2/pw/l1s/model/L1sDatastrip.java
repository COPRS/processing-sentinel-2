package eu.csgroup.coprs.ps2.pw.l1s.model;

import eu.csgroup.coprs.ps2.core.pw.model.PWItem;
import eu.csgroup.coprs.ps2.core.common.model.processing.DatatakeType;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
public class L1sDatastrip extends PWItem {

    private String folder;
    private DatatakeType datatakeType;
    private Map<String, Boolean> availableByGR;
    private boolean grComplete;

    public boolean allGRAvailable() {
        return availableByGR.values().stream().allMatch(Boolean::booleanValue);
    }

}
