package pt.upa.broker.ws.cli;

import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

public class BrokerClient {
	
	/** VARIABLES **/ 
	
	//Aceder ao requestContext
	Map<String, Object> requestContext = null;
	
	//wsService
	BrokerService _service = null;
	
	//wsPort
	BrokerPortType _port = null;
	
	// wsURL
	private String _wsURL = null;
	
	// UDDI server URL
	private String _uddiURL = null;
	
	// output option
	private boolean _verbose = false;
	
	// WS name
	private String _wsName = null;
	
	//FrontEnd
	private FrontEnd fe = null;
	
	public BrokerClient(String wsURL) throws BrokerClientException{
		_wsURL = wsURL;
		createStub();
		fe = new FrontEnd(getPort(), requestContext);
	}
	
	public BrokerClient(String uddiURL, String wsName) throws BrokerClientException{
		_uddiURL = uddiURL;
		_wsName = wsName;
		uddiLookup();
		createStub();
		fe = new FrontEnd(getPort(), requestContext);
		
	}
	
	public BrokerClient(BrokerPortType port){
		_port = port;
	}
	
	/** Getters & Setters */
	public String getWsURL() {return _wsURL;}
	
	public BrokerPortType getPort(){return _port;}
	
	public boolean getVerbose(){return _verbose;}
	
	public String getWsName(){return _wsName;}
	
	public String getuddiURL(){return _uddiURL;}
	
	public BrokerService getService(){return _service;}
	
	public FrontEnd getFrontEnd(){return fe;}
	
	public void setVerbose(boolean verbose) {_verbose = verbose;}
	
	public void setWsURL(String wsURL){_wsURL = wsURL;}
	
	public void setPort(BrokerPortType port){_port = port;}
	
	public void setService(BrokerService service){_service = service;}
	
	public boolean isVerbose() {return _verbose;}
	
	/** UDDI lookup */
	private void uddiLookup() throws BrokerClientException {
		try {
			if (getVerbose())
				System.out.printf("Contacting UDDI at %s%n", getuddiURL());
			UDDINaming uddiNaming = new UDDINaming(getuddiURL());

			if (_verbose)
				System.out.printf("Looking for '%s'%n", getWsName());
			setWsURL(uddiNaming.lookup(getWsName()));

		} catch (Exception e) {
			String msg = String.format("Client failed lookup on UDDI at %s!", getuddiURL());
			throw new BrokerClientException(msg, e);
		}

		if (getWsURL() == null) {
			String msg = String.format("Service with name %s not found on UDDI at %s", getWsName(), getuddiURL());
			throw new BrokerClientException(msg);
		}
	}
	
	/** Stub creation and configuration */
	private void createStub() {
		if (getVerbose())
			System.out.println("Creating stub ...");
		setService(new BrokerService());
		setPort(getService().getBrokerPort());

		if (getWsURL() != null) {
			if (getVerbose())
				System.out.println("Setting endpoint address ...");
			BindingProvider bindingProvider = (BindingProvider) getPort();
			requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, getWsURL());
		}
	}
	
	/** Contract functions */
	public String toString(TransportView tv){
		return ("Company Name: " + tv.getTransporterCompany() + "\n" + "Origin: " + tv.getOrigin() + "\n" + "Destination: " + tv.getDestination() + "\n" + "Price: " + tv.getPrice() + "\n" + "State: " + tv.getState() + "\n" + "Id: " + tv.getId() + "\n");
	}	
	
	public String ping(String message){
		return getFrontEnd().ping(message);
	}	
	
	public String requestTransport(String origin, String destination, int price) throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception{
		return getFrontEnd().requestTransport(origin, destination, price);
	}
	
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception{
		return getFrontEnd().viewTransport(id);
	}
	
	public String clearTransports(){
		getPort().clearTransports();
		return "All transports have been cleared";
	}
	
	public List<TransportView> listTransports(){
		return getPort().listTransports();
	}
	
	public void updateBroker(TransportView transport){
		getPort().updateBroker(transport);
	}
}
