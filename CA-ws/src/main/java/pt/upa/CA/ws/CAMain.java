package pt.upa.CA.ws;

public class CAMain {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", CAMain.class.getName());
			return;
		}

		String uddiURL = args[0];
		String wsName = args[1];
		String wsURL = args[2];
		final String KeyPath = args[3];
        

		CAEndpointManager endpoint = null;
		
		if (args.length == 1) {
			wsURL = args[0];
			endpoint = new CAEndpointManager(wsURL, KeyPath);
		} else if (args.length >= 3) {
			uddiURL = args[0];
			wsName = args[1];
			wsURL = args[2];
			endpoint = new CAEndpointManager(uddiURL, wsName, wsURL, KeyPath);
			endpoint.setVerbose(true);
		}

		try {
			endpoint.start();
			endpoint.awaitConnections();
		} finally {
			endpoint.stop();
		}

	}

}
