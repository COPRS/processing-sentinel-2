package eu.csgroup.coprs.ps2.core.pw.service;

import eu.csgroup.coprs.ps2.core.common.model.ExecutionInput;
import eu.csgroup.coprs.ps2.core.pw.model.PWItem;

import java.util.List;

public interface PWExecutionInputService<T extends ExecutionInput, S extends PWItem> {

    List<T> create(List<S> itemList);

}
