package eu.csgroup.coprs.ps2.core.common.exception;

public class TraceException extends PS2Exception {

    public TraceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TraceException(String message) {
        super(message);
    }

}
