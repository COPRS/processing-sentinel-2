package eu.csgroup.coprs.ps2.core.common.exception;

public class PS2Exception extends RuntimeException {

    public PS2Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public PS2Exception(String message) {
        super(message);
    }

}
