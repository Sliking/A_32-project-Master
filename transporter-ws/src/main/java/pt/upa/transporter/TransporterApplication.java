package pt.upa.transporter;

import pt.upa.transporter.ws.TransporterEndpointManager;

public class TransporterApplication {

	public static void main(String[] args) throws Exception {
		
		System.out.println(TransporterApplication.class.getSimpleName() + " starting...");
				
		// Check arguments
		if (args.length == 0 || args.length == 2) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + TransporterApplication.class.getName() + " wsURL OR uddiURL wsName wsURL");
			return;
		}
		
		String uddiURL = null;
		String wsName = null;
		String wsURL = null;
		String outputDirectory = null;

		// Create server implementation object, according to options
		TransporterEndpointManager endpoint = null;
		if (args.length == 1) {
			wsURL = args[0];
			endpoint = new TransporterEndpointManager(wsURL);
		} else if (args.length >= 3) {
			uddiURL = args[0];
			wsName = args[1];
			wsURL = args[2];
			outputDirectory = args[3];
			endpoint = new TransporterEndpointManager(uddiURL, wsName, wsURL, outputDirectory);
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
