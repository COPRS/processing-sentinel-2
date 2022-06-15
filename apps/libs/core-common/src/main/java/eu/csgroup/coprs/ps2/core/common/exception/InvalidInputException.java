package eu.csgroup.coprs.ps2.core.common.exception;

public class InvalidInputException extends PS2Exception {

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputException(String message) {
        super(message);
    }

}
