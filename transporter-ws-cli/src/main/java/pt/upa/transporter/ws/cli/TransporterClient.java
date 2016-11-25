package pt.upa.transporter.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.List;
import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;

import example.ws.handler.DigitalMarkHandler;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.TransporterService;
import pt.upa.transporter.ws.cli.TransporterClientException;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterPortType;

public class TransporterClient {

	
	Map<String, Object> requestContext = null;
	
	/** VARIABLES **/
	
	private static final String CLASS_NAME = TransporterClient.class.getSimpleName();
	//wsService
	TransporterService _service = null;
	
	//wsPort
	TransporterPortType _port = null;
	
	//requestContextString
	private String _requestContextString = null;
	
	// wsURL
	private String _wsURL = null;
	
	// UDDI server URL
	private String _uddiURL = null;
	
	// output option
	private boolean _verbose = false;
	
	// WS name
	private String _transporterName;
	
	//Binding Provider
	private BindingProvider bindingProvider = null;
	
	private String[][] locations = new String[][] {
		{"Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança"}, //Norte
		{"Lisboa", "Leiria", "Santarem", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda"}, //Centro
		{"Setubal", "Evora", "Portalegre", "Beja", "Faro"} //Sul
	};
	
	public TransporterClient(String wsURL){
		_wsURL = wsURL;
		createStub();
	}
	
	public TransporterClient(String uddiURL, String wsName) throws JAXRException, TransporterClientException{
		_uddiURL = uddiURL;
		setTransporterName(wsName);
		uddiLookup();
		createStub();
	}
	
	public TransporterClient(TransporterPortType port){
		_port = port;
	}
	
	public String getrequestContextString(){return _requestContextString;}
	
	public String getWsURL() {return _wsURL;}
	
	public void setPort(TransporterPortType port){_port = port;}
	
	public TransporterPortType getPort(){return _port;}
	
	public void setrequestContextString(String string){ _requestContextString = string;}
	
	public void setTransporterName(String name){_transporterName = name;}
	
	public String getTransporterName(){return _transporterName;}
	
	public boolean getVerbose(){return _verbose;}
	
	public String getuddiURL(){return _uddiURL;}
	
	public TransporterService getService(){return _service;}
	
	public void setVerbose(boolean verbose) {_verbose = verbose;}
	
	public void setWsURL(String wsURL){_wsURL = wsURL;}
	
	public void setService(TransporterService service){_service = service;}
	
	public boolean isVerbose() {return _verbose;}
	
	/** UDDI lookup */
	private void uddiLookup() throws TransporterClientException {
		try {
			if (getVerbose())
				System.out.printf("Contacting UDDI at %s%n", getuddiURL());
			UDDINaming uddiNaming = new UDDINaming(getuddiURL());

			if (_verbose)
				System.out.printf("Looking for '%s'%n", getTransporterName());
			setWsURL(uddiNaming.lookup(getTransporterName()));

		} catch (Exception e) {
			String msg = String.format("Client failed lookup on UDDI at %s!", getuddiURL());
			throw new TransporterClientException(msg, e);
		}

		if (getWsURL() == null) {
			String msg = String.format("Service with name %s not found on UDDI at %s", getTransporterName(), getuddiURL());
			throw new TransporterClientException(msg);
		}
	}
	
	/** Stub creation and configuration */
	private void createStub() {
		if (getVerbose())
			System.out.println("Creating stub ...");
		setService(new TransporterService());
		setPort(getService().getTransporterPort());

		if (getWsURL() != null) {
			if (getVerbose())
				System.out.println("Setting endpoint address ...");
			bindingProvider = (BindingProvider) getPort();
			requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, getWsURL());
		}
	}
	
	private void putonrequestContext(String initialValue){
		System.out.println("Recebido: " + initialValue);
		requestContext.put(DigitalMarkHandler.REQUEST_PROPERTY, initialValue);
	}
	
	private String retrieveMessageContext(){
		// retrieve message context
		Map<String, Object> responseContext = bindingProvider.getResponseContext();

		// get token from message context
		String propertyValue = (String) responseContext.get(DigitalMarkHandler.REQUEST_PROPERTY);
		System.out.printf("%s got token '%s' from response context%n", CLASS_NAME, propertyValue);
		
		return propertyValue;
	}
	
	public int getRegion(String location){
		int rows = 3;
		for(int i=0; i < rows; i++){
			for(int j=0; j<locations[i].length; j++){
				if(locations[i][j].equals(location)){
					return i;
				}
			}
		}
		return -1;
	}
	
	public JobView createJobView(String origin, String destination, int price){
		JobView jv = new JobView();
		jv.setJobOrigin(origin);
		jv.setJobDestination(destination);
		jv.setJobPrice(price);
		jv.setCompanyName(getTransporterName());
		jv.setJobState(JobStateView.REJECTED);
		jv.setJobIdentifier("NONE");
		return jv;
	}
	
	public String ping(String message){
		putonrequestContext(getrequestContextString());
		String result = getPort().ping(message);
		setrequestContextString(retrieveMessageContext());
		return result;		
	}
	
	public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception {
		putonrequestContext(getrequestContextString());
		JobView jv = getPort().requestJob(origin, destination, price);
		setrequestContextString(retrieveMessageContext());
		return jv;
	}
	
	public JobView jobStatus(String id){
		putonrequestContext(getrequestContextString());
		JobView jv = getPort().jobStatus(id);
		setrequestContextString(retrieveMessageContext());
		return jv;
	}
	
	public void clearJobs(){
		getPort().clearJobs();
	}
	
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		putonrequestContext(getrequestContextString());
		JobView jv = getPort().decideJob(id, accept);
		setrequestContextString(retrieveMessageContext());
		return jv;
	}
	
	public List<JobView> listJobs(){
		return getPort().listJobs();
	}

}
