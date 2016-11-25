package pt.upa.broker.ws;

import java.io.IOException;

import javax.xml.ws.Endpoint;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

/** Endpoint manager */
public class BrokerEndpointManager {

	/** Variable to see if it is the primary or secundary broker */
	private String primary = null;
	/** UDDI naming server location */
	private String uddiURL = null;
	/** Web Service name */
	private String wsName = null;

	/** Get Web Service UDDI publication name */
	public String getWsName() {return wsName;}

	/** Web Service location to publish */
	private String wsURL = null;

	/** Port implementation */
	private BrokerPort portImpl = null;

	/** Obtain Port implementation */
	public BrokerPortType getPort() {return portImpl;}

	/** Web Service endpoint */
	private Endpoint endpoint = null;
	/** UDDI Naming instance for contacting UDDI server */
	private UDDINaming uddiNaming = null;

	/** Get UDDI Naming instance for contacting UDDI server */
	UDDINaming getUddiNaming() {return uddiNaming;}
	
	/** String with the outputDirectroy in superclass pom */
	private String outputDirectory = null;

	/** output option **/
	private boolean verbose = true;

	public boolean isVerbose() {return verbose;}

	public String getPrimary(){return primary;}
	
	public void setwsURL(String wsURL){this.wsURL = wsURL;}
	
	public String getwsURL(){return this.wsURL;}
	
	public String getoutputDirectory(){return this.outputDirectory;}
	
	public void setVerbose(boolean verbose) {this.verbose = verbose;}
	
	public void setPrimary(String primary){this.primary = primary;}

	/** constructor with provided UDDI location, WS name, and WS URL */
	public BrokerEndpointManager(String uddiURL, String wsName, String wsURL, String outputDirectory) {
		this.uddiURL = uddiURL;
		this.wsName = wsName;
		this.wsURL = wsURL;
		this.outputDirectory = outputDirectory;
	}

	/** constructor with provided web service URL */
	public BrokerEndpointManager(String wsURL) {
		if (wsURL == null)
			throw new NullPointerException("Web Service URL cannot be null!");
		this.wsURL = wsURL;
	}

	public void brokerLife() throws Exception{
		if(!getPrimary().equals("primary")){
			this.portImpl.checkLife();
			setwsURL("http://localhost:8080/broker-ws/endpoint");
			redifine();
			System.out.println("Rebind successfully");
		}
	}
	
	/* endpoint management */

	public void start() throws Exception {
		this.portImpl = new BrokerPort(2, getPrimary(), getoutputDirectory()); //Create 2 TransporterClients
		try {
			// publish endpoint
			endpoint = Endpoint.create(this.portImpl);
			if (verbose) {
				System.out.printf("Starting %s%n", wsURL);
			}
			endpoint.publish(wsURL);
		} catch (Exception e) {
			endpoint = null;
			if (verbose) {
				System.out.printf("Caught exception when starting: %s%n", e);
				e.printStackTrace();
			}
			throw e;
		}
		publishToUDDI();		
	}

	public void awaitConnections() {
		if (verbose) {
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
		}
		try {
			System.in.read();
		} catch (IOException e) {
			if (verbose) {
				System.out.printf("Caught i/o exception when awaiting requests: %s%n", e);
			}
		}
	}

	public void stop() throws Exception {
		try {
			if (endpoint != null) {
				// stop endpoint
				endpoint.stop();
				if (verbose) {
					System.out.printf("Stopped %s%n", wsURL);
				}
			}
		} catch (Exception e) {
			if (verbose) {
				System.out.printf("Caught exception when stopping: %s%n", e);
			}
		}
		this.portImpl = null;
		unpublishFromUDDI();
	}

	/* UDDI */

	void publishToUDDI() throws Exception {
		try {
			// publish to UDDI
			if (uddiURL != null) {
				if (verbose) {
					System.out.printf("Publishing '%s' to UDDI at %s%n", wsName, uddiURL);
				}
				uddiNaming = new UDDINaming(uddiURL);
				uddiNaming.rebind(wsName, wsURL);
			}
		} catch (Exception e) {
			uddiNaming = null;
			if (verbose) {
				System.out.printf("Caught exception when binding to UDDI: %s%n", e);
			}
			throw e;
		}
	}
	
	void redifine() throws Exception{
		firstStop();		
		secondStart();
	}

	void unpublishFromUDDI() {
		try {
			if (uddiNaming != null) {
				// delete from UDDI
				uddiNaming.unbind(wsName);
				if (verbose) {
					System.out.printf("Unpublished '%s' from UDDI%n", wsName);
				}
				uddiNaming = null;
			}
		} catch (Exception e) {
			if (verbose) {
				System.out.printf("Caught exception when unbinding: %s%n", e);
			}
		}
	}
	
	public void firstStop(){
		try {
			if (endpoint != null) {
				// stop endpoint
				endpoint.stop();
				if (verbose) {
					System.out.printf("Stopped %s%n", wsURL);
				}
			}
		} catch (Exception e) {
			if (verbose) {
				System.out.printf("Caught exception when stopping: %s%n", e);
			}
		}
		firstunpublishFromUDDI();
	}

	public void firstunpublishFromUDDI(){
		try {
			if (uddiNaming != null) {
				// delete from UDDI
				uddiNaming.unbind(wsName);
				if (verbose) {
					System.out.printf("Unpublished '%s' from UDDI%n", wsName);
				}
			}
		} catch (Exception e) {
			if (verbose) {
				System.out.printf("Caught exception when unbinding: %s%n", e);
			}
		}
	}
	
	public void secondStart() throws Exception{
		try {
			// publish endpoint
			endpoint = Endpoint.create(this.portImpl);
			if (verbose) {
				System.out.printf("Starting %s%n", getwsURL());
			}
			endpoint.publish(getwsURL());
		} catch (Exception e) {
			endpoint = null;
			if (verbose) {
				System.out.printf("Caught exception when starting: %s%n", e);
				e.printStackTrace();
			}
			throw e;
		}
		
		try {
			// publish to UDDI
			if (uddiURL != null) {
				if (verbose) {
					System.out.printf("Publishing '%s' to UDDI at %s%n", wsName, uddiURL);
				}
				uddiNaming.rebind(wsName, getwsURL());
			}
		} catch (Exception e) {
			uddiNaming = null;
			if (verbose) {
				System.out.printf("Caught exception when binding to UDDI: %s%n", e);
			}
			throw e;
		}
	}
}
