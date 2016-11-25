package pt.upa.CA.ws;

/**
 * This exception is used by the CA to throw exceptions with 
 * more meaningful error messages.
 */
public class CAException extends Exception {

	private static final long serialVersionUID = 1L;

	public CAException() {
	}

	public CAException(String message) {
		super(message);
	}

	public CAException(Throwable cause) {
		super(cause);
	}

	public CAException(String message, Throwable cause) {
		super(message, cause);
	}

}
