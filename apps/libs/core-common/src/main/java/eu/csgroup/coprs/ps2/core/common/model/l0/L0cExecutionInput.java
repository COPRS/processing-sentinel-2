package eu.csgroup.coprs.ps2.core.common.model.l0;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;


@Getter
@Setter
@EqualsAndHashCode
public class L0cExecutionInput {

    private String datastrip;
    private String satellite;
    private String station;

    private Set<FileInfo> files;

    private Map<String, Map<String, String>> jobOrders;

}
