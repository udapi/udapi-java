package cz.ufal.udapi.core.io;

import cz.ufal.udapi.exception.UdapiException;

/**
 * Exception encapsulating any exception occured during read/write operations.
 *
 * @author Martin Vojtek
 */
public class UdapiIOException extends UdapiException {
    /**
     * Default constructor.
     */
    public UdapiIOException() {
    }

    public UdapiIOException(String message) {
        super(message);
    }

    public UdapiIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public UdapiIOException(Throwable cause) {
        super(cause);
    }

    public UdapiIOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
