package eu.csgroup.coprs.ps2.core.common.exception;

public class InvalidMessageException extends PS2Exception {

    public InvalidMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMessageException(String message) {
        super(message);
    }

}
