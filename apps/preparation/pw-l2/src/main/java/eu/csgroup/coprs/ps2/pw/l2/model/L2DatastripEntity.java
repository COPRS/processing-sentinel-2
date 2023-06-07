package eu.csgroup.coprs.ps2.pw.l2.model;

import eu.csgroup.coprs.ps2.core.pw.model.PWItemEntity;
import eu.csgroup.coprs.ps2.core.pw.model.ResubmitMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Getter
@Setter
@Document(collection = "PW_Item")
public class L2DatastripEntity extends PWItemEntity {

    private String folder;

    private Map<String, Boolean> availableByTL;
    private ResubmitMessage resubmitMessage;

    private boolean tlComplete;

}
