package pt.upa.ca.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.xml.ws.BindingProvider;

// classes generated from WSDL

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.ca.ws.cli.CAClientException;
import pt.upa.ca.ws.AsymKeys;
import pt.upa.ca.ws.AsymKeysImplService;

public class CAClient {
	
	//wsService
	private AsymKeysImplService _service = null;
	
	//wsPort
	private static AsymKeys _port = null;

	// wsURL
	private String _wsURL = null;
	// UDDI server URL
	private String _uddiURL = null;
	
	// output option
	private boolean _verbose = false;
	
	// WS name
	private String _wsName = null;
	
	public CAClient(String wsURL) throws CAClientException{
		_wsURL = wsURL;
		createStub();
	}
	
	public CAClient(String uddiURL, String wsName) throws CAClientException{
		_uddiURL = uddiURL;
		_wsName = wsName;
		uddiLookup();
		createStub();
		
	}
	
	public String getWsURL() {return _wsURL;}
	
	public static AsymKeys getPort(){return _port;}
	
	public boolean getVerbose(){return _verbose;}
	
	public String getWsName(){return _wsName;}
	
	public String getuddiURL(){return _uddiURL;}
	
	public AsymKeysImplService getService(){return _service;}
	
	public void setService(AsymKeysImplService service){_service = service;}
	
	public void setWsURL(String wsURL){_wsURL = wsURL;}
	
	public void setPort(AsymKeys port){_port = port;}
	
	/** UDDI lookup */
	private void uddiLookup() throws CAClientException {
		try {
			if (getVerbose())
				System.out.printf("Contacting UDDI at %s%n", getuddiURL());
			UDDINaming uddiNaming = new UDDINaming(getuddiURL());

			if (_verbose)
				System.out.printf("Looking for '%s'%n", getWsName());
			setWsURL(uddiNaming.lookup(getWsName()));

		} catch (Exception e) {
			String msg = String.format("Client failed lookup on UDDI at %s!", getuddiURL());
			throw new CAClientException(msg, e);
		}

		if (getWsURL() == null) {
			String msg = String.format("Service with name %s not found on UDDI at %s", getWsName(), getuddiURL());
			throw new CAClientException(msg);
		}
	}
	
	/** Stub creation and configuration */
	private void createStub() {
		if (getVerbose())
			System.out.println("Creating stub ...");
		setService(new AsymKeysImplService());
		setPort(getService().getAsymKeysImplPort());

		if (getWsURL() != null) {
			if (getVerbose())
				System.out.println("Setting endpoint address ...");
			BindingProvider bindingProvider = (BindingProvider) getPort();
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, getWsURL());
		}
	}
	
	public X509Certificate requestTransporter1PublicKey(){	
		try{
			byte[] bytes = getPort().requestTransport1Certificate();
			InputStream in = new ByteArrayInputStream(bytes);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate)cf.generateCertificate(in);
			return cert;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public X509Certificate requestTransporter2PublicKey(){	
		try{
			byte[] bytes = getPort().requestTransport2Certificate();
			InputStream in = new ByteArrayInputStream(bytes);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate)cf.generateCertificate(in);
			return cert;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public X509Certificate requestBrokerPublicKey(){
		try{
			byte[] bytes = getPort().requestBrokerCertificate();
			InputStream in = new ByteArrayInputStream(bytes);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate)cf.generateCertificate(in);
			return cert;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
