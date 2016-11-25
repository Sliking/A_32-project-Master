package pt.upa.transporter.ws;

import java.io.IOException;

import javax.xml.ws.Endpoint;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

/** Endpoint manager */
public class TransporterEndpointManager {

	/** UDDI naming server location */
	private String _uddiURL = null;
	/** Web Service name */
	private String _wsName = null;
	/** Web Service location to publish */
	private String _wsURL = null;
	/** Web Service endpoint */
	private Endpoint _endpoint = null;
	/** UDDI Naming instance for contacting UDDI server */
	private UDDINaming _uddiNaming = null;
	/** output option **/
	private boolean _verbose = true;
	
	/** constructor with provided UDDI location, WS name, and WS URL */
	public TransporterEndpointManager(String uddiURL, String wsName, String wsURL, String outputDirectory) {
		_uddiURL = uddiURL;
		_wsName = wsName;
		_wsURL = wsURL;
		_portImpl = new TransporterPort(getWsName(), outputDirectory);
	}

	/** constructor with provided web service URL */
	public TransporterEndpointManager(String wsURL) {
		if (wsURL == null)
			throw new NullPointerException("Web Service URL cannot be null!");
		_wsURL = wsURL;
	}
	
	/** Get Web Service UDDI publication name */
	public String getWsName() {return _wsName;}

	/** Obtain Port implementation */
	public TransporterPortType getPort() {return _portImpl;}

	/** Get UDDI Naming instance for contacting UDDI server */
	UDDINaming getUddiNaming() {return _uddiNaming;}

	public boolean isVerbose() {return _verbose;}

	public void setVerbose(boolean verbose) {_verbose = verbose;}
	
	/** Port implementation */
	private TransporterPort _portImpl = null;

	/* endpoint management */

	public void start() throws Exception {
		try {
			// publish endpoint
			_endpoint = Endpoint.create(_portImpl);
			if (_verbose) {
				System.out.printf("Starting %s%n", _wsURL);
			}
			_endpoint.publish(_wsURL);
		} catch (Exception e) {
			_endpoint = null;
			if (_verbose) {
				System.out.printf("Caught exception when starting: %s%n", e);
				e.printStackTrace();
			}
			throw e;
		}
		publishToUDDI();
	}

	public void awaitConnections() {
		if (_verbose) {
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
		}
		try {
			System.in.read();
		} catch (IOException e) {
			if (_verbose) {
				System.out.printf("Caught i/o exception when awaiting requests: %s%n", e);
			}
		}
	}

	public void stop() throws Exception {
		try {
			if (_endpoint != null) {
				// stop endpoint
				_endpoint.stop();
				if (_verbose) {
					System.out.printf("Stopped %s%n", _wsURL);
				}
			}
		} catch (Exception e) {
			if (_verbose) {
				System.out.printf("Caught exception when stopping: %s%n", e);
			}
		}
		_portImpl = null;
		unpublishFromUDDI();
	}

	/* UDDI */

	void publishToUDDI() throws Exception {
		try {
			// publish to UDDI
			if (_uddiURL != null) {
				if (_verbose) {
					System.out.printf("Publishing '%s' to UDDI at %s%n", _wsName, _uddiURL);
				}
				_uddiNaming = new UDDINaming(_uddiURL);
				_uddiNaming.rebind(_wsName, _wsURL);
			}
		} catch (Exception e) {
			_uddiNaming = null;
			if (_verbose) {
				System.out.printf("Caught exception when binding to UDDI: %s%n", e);
			}
			throw e;
		}
	}

	void unpublishFromUDDI() {
		try {
			if (_uddiNaming != null) {
				// delete from UDDI
				_uddiNaming.unbind(_wsName);
				if (_verbose) {
					System.out.printf("Unpublished '%s' from UDDI%n", _wsName);
				}
				_uddiNaming = null;
			}
		} catch (Exception e) {
			if (_verbose) {
				System.out.printf("Caught exception when unbinding: %s%n", e);
			}
		}
	}

}
