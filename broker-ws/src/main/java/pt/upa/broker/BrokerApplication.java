package pt.upa.broker;

import pt.upa.broker.ws.BrokerEndpointManager;

public class BrokerApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(BrokerApplication.class.getSimpleName() + " starting...");
		
		
		// Check arguments
		if (args.length == 0 || args.length == 2) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + BrokerApplication.class.getName() + " wsURL OR uddiURL wsName wsURL");
			return;
		}
		
		String uddiURL = null;
		String wsName = null;
		String wsURL = null;
		String primary = null;
		String outputDirectory = null;

		// Create server implementation object, according to options
		BrokerEndpointManager endpoint = null;
		if (args.length == 1) {
			wsURL = args[0];
			endpoint = new BrokerEndpointManager(wsURL);
		} else if (args.length >= 3) {
			uddiURL = args[0];
			wsName = args[1];
			wsURL = args[2];
			primary = args[3];
			outputDirectory = args[4];
			endpoint = new BrokerEndpointManager(uddiURL, wsName, wsURL, outputDirectory);
			endpoint.setVerbose(true);
			endpoint.setPrimary(primary);
		}

		try {
			endpoint.start();
			endpoint.brokerLife();
			endpoint.awaitConnections();
		} finally {
			endpoint.stop();
		}
	}

}
