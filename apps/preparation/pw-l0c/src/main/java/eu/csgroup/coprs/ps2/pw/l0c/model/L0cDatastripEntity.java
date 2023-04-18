package eu.csgroup.coprs.ps2.pw.l0c.model;

import eu.csgroup.coprs.ps2.core.pw.model.PWItemEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@Document(collection = "PW_Item")
public class L0cDatastripEntity extends PWItemEntity {

    private String folder;
    private String dtFolder;

}
