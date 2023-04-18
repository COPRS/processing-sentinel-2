package eu.csgroup.coprs.ps2.core.pw.exception;

import eu.csgroup.coprs.ps2.core.common.exception.PS2Exception;

public class MongoDBException extends PS2Exception {

    public MongoDBException(String message, Throwable cause) {
        super(message, cause);
    }

    public MongoDBException(String message) {
        super(message);
    }

}
