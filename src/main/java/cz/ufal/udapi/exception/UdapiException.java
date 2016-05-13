package cz.ufal.udapi.exception;

/**
 * General Udapi exception.
 *
 * All Udapi exceptions should extend this general exception.
 *
 * @author Martin Vojtek
 */
public class UdapiException extends RuntimeException {
    /**
     * Default constructor.
     */
    public UdapiException() {
        super();
    }

    public UdapiException(String message) {
        super(message);
    }

    public UdapiException(String message, Throwable cause) {
        super(message, cause);
    }

    public UdapiException(Throwable cause) {
        super(cause);
    }

    protected UdapiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
