package eu.csgroup.coprs.ps2.core.obs.exception;

import eu.csgroup.coprs.ps2.core.common.exception.PS2Exception;

public class ObsException extends PS2Exception {

    public ObsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObsException(String message) {
        super(message);
    }

}
