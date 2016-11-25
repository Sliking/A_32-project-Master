package pt.upa.broker.ws;

/**
 * This exception is used by the Broker to throw exceptions with 
 * more meaningful error messages.
 */
public class BrokerException extends Exception {

	private static final long serialVersionUID = 1L;

	public BrokerException() {
	}

	public BrokerException(String message) {
		super(message);
	}

	public BrokerException(Throwable cause) {
		super(cause);
	}

	public BrokerException(String message, Throwable cause) {
		super(message, cause);
	}

}
