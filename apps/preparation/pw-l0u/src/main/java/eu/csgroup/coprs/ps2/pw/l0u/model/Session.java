package eu.csgroup.coprs.ps2.pw.l0u.model;

import eu.csgroup.coprs.ps2.core.pw.model.PWItem;
import eu.csgroup.coprs.ps2.core.pw.model.ResubmitMessage;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Session extends PWItem {

    private boolean rawComplete;

    ResubmitMessage resubmitMessage;
}
