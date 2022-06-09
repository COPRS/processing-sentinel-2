package eu.csgroup.coprs.ps2.core.common.exception;

public class ProcessingException extends PS2Exception {

    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessingException(String message) {
        super(message);
    }

}
