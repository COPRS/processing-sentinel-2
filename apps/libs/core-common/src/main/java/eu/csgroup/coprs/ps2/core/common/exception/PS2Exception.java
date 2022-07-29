package eu.csgroup.coprs.ps2.core.common.exception;

public abstract class PS2Exception extends RuntimeException {

    protected PS2Exception(String message, Throwable cause) {
        super(message, cause);
    }

    protected PS2Exception(String message) {
        super(message);
    }

}
