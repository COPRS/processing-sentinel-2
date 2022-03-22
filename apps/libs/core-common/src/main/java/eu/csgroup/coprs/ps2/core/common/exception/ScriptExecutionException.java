package eu.csgroup.coprs.ps2.core.common.exception;

public class ScriptExecutionException extends PS2Exception {

    public ScriptExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptExecutionException(String message) {
        super(message);
    }

}
