package eu.csgroup.coprs.ps2.core.common.service.pw;

import eu.csgroup.coprs.ps2.core.common.model.PWItem;

import java.util.List;
import java.util.Set;

public interface PWItemService<S extends PWItem> {

    void deleteAll(Set<String> itemNameSet);

    void updateAll(List<S> itemList);

}
