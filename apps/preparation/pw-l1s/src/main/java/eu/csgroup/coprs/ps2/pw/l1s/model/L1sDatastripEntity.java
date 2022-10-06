package eu.csgroup.coprs.ps2.pw.l1s.model;

import eu.csgroup.coprs.ps2.core.common.model.processing.DatatakeType;
import eu.csgroup.coprs.ps2.core.pw.model.PWItemEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Getter
@Setter
@Document(collection = "PW_Item")
public class L1sDatastripEntity extends PWItemEntity {

    private String folder;
    private DatatakeType datatakeType;

    private Map<String, Boolean> availableByGR;

    private boolean grComplete;

}
