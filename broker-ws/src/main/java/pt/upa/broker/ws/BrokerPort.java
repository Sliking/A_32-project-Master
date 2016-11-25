package pt.upa.broker.ws;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.registry.JAXRException;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import example.ws.handler.DigitalMarkHandler;
import pt.upa.broker.ws.cli.BrokerClient;
import pt.upa.broker.ws.cli.BrokerClientException;
import pt.upa.security.SecurityClass;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;
import pt.upa.transporter.ws.cli.TransporterClientException;

@WebService(
		endpointInterface="pt.upa.broker.ws.BrokerPortType",
	    wsdlLocation="broker.2_0.wsdl",
	    name="BrokerWebService",
	    portName="BrokerPort",
	    targetNamespace="http://ws.broker.upa.pt/",
	    serviceName="BrokerService"
		)
@HandlerChain(file = "/broker_handler-chain.xml")
public class BrokerPort implements BrokerPortType {
	
	/** VARIABLES **/ 
	
	//List with client Transporters
	private List<TransporterClient> transporterList = new ArrayList<TransporterClient>();	
	//list with actual Jobs
	private List<TransportView> transportViews = new ArrayList<TransportView>();
	@Resource
	private WebServiceContext webServiceContext;
	private MessageContext messageContext = null;
	public static final String CLASS_NAME = BrokerPort.class.getSimpleName();
	private int _counter = 1;
	private String primary;
	private final String uddiURL = "http://localhost:9090";
	private String life = null;
	private String outputDirectory = null;
	private SecurityClass security = null;
	private PublicKey CApublicKey = null;
	private PublicKey Transporter2pubKey = null;
	private PublicKey Transporter1pubKey = null;
	private PrivateKey privKey;
	private String lastNonce = null;
	
	public BrokerPort(int number, String primary, String outputDirectory){
		this.outputDirectory = outputDirectory;
		this.primary = primary;	
		
		//TransporterClient initialization
		for(int i = 0; i < number; i++){			
			String wsNametransporter = "UpaTransporter" + getCounter();			
			TransporterClient transporter = null;
			try {
				try {
					transporter = new TransporterClient(uddiURL, wsNametransporter);
				} catch (TransporterClientException e) {
					e.printStackTrace();
				}
				transporterList.add(transporter);
				addCounter();
			} catch (JAXRException e1) {
				e1.printStackTrace();
			}
		}	
		
		//Action for primary Server
		if(isPrimary()){
			isAlive();
		}	
		
		//Get all necessary keys from CA and KeyStore
		setSecurityClass(new SecurityClass());	
		getPrivateKeyfromKS();
		getCApublicKeyFromKS();
		getTransporter1PublicKeyFromCA();
		getTransporter2PublicKeyFromCA();

	}
	
	private void getTransporter1PublicKeyFromCA(){
		X509Certificate cert = getSecurityClass().getTransporter1PublicKey();
		
		if(getSecurityClass().verifySignedCertificate(cert, getCAPublicKey())){
			setTransporter1PublicKey(cert.getPublicKey());
		}
	}
	
	private void getTransporter2PublicKeyFromCA(){
		X509Certificate cert = getSecurityClass().getTransporter2PublicKey();
		
		if(getSecurityClass().verifySignedCertificate(cert, getCAPublicKey())){
			setTransporter2PublicKey(cert.getPublicKey());
		}
	}
	
	private void getPrivateKeyfromKS(){		
		setPrivateKey(getSecurityClass().getBrokerPrivateKey(getoutputDirectory()));
	}
	
	private void getCApublicKeyFromKS(){
		setCAPublicKey(getSecurityClass().getCApublickey(getoutputDirectory(), 0));
	}
	
	private String getoutputDirectory(){return this.outputDirectory;}
	
	private SecurityClass getSecurityClass(){return this.security;}
	
	private PublicKey getCAPublicKey(){return CApublicKey;}
	
	private void setPrivateKey(PrivateKey key){ privKey = key;}
	
	private PublicKey getTransporter1PublicKey(){return Transporter1pubKey;}
	
	private PublicKey getTransporter2PublicKey(){return Transporter2pubKey;}
	
	private PrivateKey getPrivateKey(){ return privKey;}
	
	public void setTransporter1PublicKey(PublicKey key){Transporter1pubKey = key;}
	
	public void setTransporter2PublicKey(PublicKey key){Transporter2pubKey = key;}
	
	public void setCAPublicKey(PublicKey key){ CApublicKey = key;}
	
	private void setSecurityClass(SecurityClass security){this.security = security;}
	
	public BrokerPort(BrokerEndpointManager endpoint) {}
	
	private String[][] locations = new String[][] {
		{"Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança"}, //Norte
		{"Lisboa", "Leiria", "Santarem", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda"}, //Centro
		{"Setubal", "Evora", "Portalegre", "Beja", "Faro"} //Sul
	};
	
	public void checkLife(){
		try {
			while(true){
				Thread.sleep(10000);
				if(life==null)
					break;
				else
					life=null;
			}			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void isAlive(){
		Alive alive = new Alive("Thread");
		alive.start();
	}
	
	private boolean isPrimary(){return primary.equals("primary");}
	
	public boolean verifyLocation(String location){
		int rows = 3;
		for(int i=0; i < rows; i++){
			for(int j=0; j<locations[i].length; j++){
				if(locations[i][j].equals(location)){
					return true;
				}
			}
		}
		return false;
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
	
	public void addCounter(){_counter++;}
	
	public int getCounter(){return _counter;}
	
	public void addTransportView(TransportView tv){transportViews.add(tv);}
	
	public void removeAllTransportViews(){transportViews.clear();}
	
	/** auxiliary method to calculate digest from text and cipher it */
    private String makeDigitalSignature(byte[] bytes, PrivateKey privKey) {
    	// get a message digest object using the specified algorithm
		MessageDigest messageDigest;
		byte[] cipherDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA-1");
			// calculate the digest and print it out
			messageDigest.update(bytes);
			byte[] digest = messageDigest.digest();
			System.out.println("Digest:");
			System.out.println(printHexBinary(digest));

			// get an RSA cipher object
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

			// encrypt the plaintext using the private key
			cipher.init(Cipher.ENCRYPT_MODE, privKey);
			cipherDigest = cipher.doFinal(digest);

			System.out.println("Cipher digest:");
			System.out.println(printHexBinary(cipherDigest));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}	
		
		String result = printBase64Binary(cipherDigest);

		return result;
    }
    
    public static boolean verifyDigitalSignature(String cipherDigest, String message, PublicKey publicKey){
		//Convert to binary
		 byte[] cipherDigestBytes = parseBase64Binary(cipherDigest);
		 byte[] bytes = message.getBytes();
		 byte[] digest = null;
		 byte[] decipheredDigest = null;
		 
		 // get a message digest object using the SHA-1 algorithm
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-1");
			// calculate the digest and print it out
			messageDigest.update(bytes);
			digest = messageDigest.digest();
			System.out.println("New digest:");
			System.out.println(printHexBinary(digest));

			// get an RSA cipher object
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

			// decrypt the ciphered digest using the public key
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			decipheredDigest = cipher.doFinal(cipherDigestBytes);
			System.out.println("Deciphered digest:");
			System.out.println(printHexBinary(decipheredDigest));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}

		// compare digests
		if (digest.length != decipheredDigest.length)
			return false;

		for (int i = 0; i < digest.length; i++)
			if (digest[i] != decipheredDigest[i])
				return false;
		return true;
	}
	
	@Override
	public String ping(String message){
		if(message.equals("alive")){
			life = message;
			return life;
		}
		else{
			String result = "";
			for(TransporterClient transporters : transporterList){
				transporters.setrequestContextString(makeDigitalSignature(message.getBytes(), getPrivateKey()));
				result = transporters.ping(message);
			}
			
			if(verifyDigitalSignature(getTransporterbyName("UpaTransporter1").getrequestContextString(), result, getTransporter1PublicKey())){
				System.out.println("Verified Transporter1");
			}
			if(verifyDigitalSignature(getTransporterbyName("UpaTransporter2").getrequestContextString(), result, getTransporter2PublicKey())){
				System.out.println("Verified Transporter2");
			}
			else{
				System.out.println("[ERROR] Failed to verify digital signature");
				return null;
			}
			return result += result;
		}
	}
	
	public String toString(TransportView tv){
		return ("Company Name: " + tv.getTransporterCompany() + "\n" + "Origin: " + tv.getOrigin() + "\n" + "Destination: " + tv.getDestination() + "\n" + "Price: " + tv.getPrice() + "\n" + "State: " + tv.getState() + "\n" + "Id: " + tv.getId() + "\n");
	}
	
	public String JobViewToString(JobView jv){
		return jv.getCompanyName()+jv.getJobIdentifier()+jv.getJobOrigin()+jv.getJobDestination()+jv.getJobPrice()+jv.getJobState();
	}
		
	public TransportStateView getCorrespondingState(JobStateView jsv){
		
		if(jsv.equals(JobStateView.PROPOSED))
			return TransportStateView.BUDGETED;
		
		else if(jsv.equals(JobStateView.REJECTED))
			return TransportStateView.FAILED;
		
		else if(jsv.equals(JobStateView.ACCEPTED))
			return TransportStateView.BOOKED;
		
		else if(jsv.equals(JobStateView.HEADING))
			return TransportStateView.HEADING;
		
		else if(jsv.equals(JobStateView.ONGOING))
			return TransportStateView.ONGOING;
		
		else if(jsv.equals(JobStateView.COMPLETED))
			return TransportStateView.COMPLETED;
		
		else{
			return null;
		}

	}
	
	public TransporterClient getTransporterbyName(String name){
		for(TransporterClient transporter : transporterList){
			if(transporter.getTransporterName().equals(name)){
				return transporter;
			}
		}
		return null;
		
	}
	
	public TransportView updateTransportView(JobView jv, TransportView tv){
		tv.setPrice(jv.getJobPrice());
		tv.setTransporterCompany(jv.getCompanyName());
		tv.setId(jv.getJobIdentifier());
		tv.setState(getCorrespondingState(jv.getJobState()));
		return tv;
	}
	
	public TransportView updateTransportView(JobView jv){
		for(TransportView tv : transportViews){
			if(tv.getId().equals(jv.getJobIdentifier())){
				tv.setState(getCorrespondingState(jv.getJobState()));
				return tv;
			}
		}
		return null;
	}
	
	public TransportView createTransportView(JobView jv){
		TransportView tv = new TransportView();
		tv.setId(jv.getJobIdentifier());
		tv.setOrigin(jv.getJobOrigin());
		tv.setDestination(jv.getJobDestination());
		tv.setPrice(jv.getJobPrice());
		tv.setState(getCorrespondingState(jv.getJobState()));
		tv.setTransporterCompany(jv.getCompanyName());
		addTransportView(tv);
		return tv;
		
	}
	
	public TransportView createTransportView(String origin, String destination, int price){
		TransportView tv = new TransportView();
		tv.setOrigin(origin);
		tv.setDestination(destination);
		tv.setPrice(price);
		tv.setState(TransportStateView.REQUESTED);
		addTransportView(tv);
		return tv;
	}
	
	public byte[] generateNonce(){
		SecureRandom random = null;;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte array[] = new byte[16];
		random.nextBytes(array);
		
		return array;
	}
	
	public String getNonce(){
		lastNonce = printBase64Binary(generateNonce());
		return lastNonce;
	}
	
	private String retrieveMessageContext(){
		// retrieve message context
		messageContext = webServiceContext.getMessageContext();
		// get token from message context
		String propertyValue = (String) messageContext.get(DigitalMarkHandler.REQUEST_PROPERTY);
		System.out.printf("%s got token '%s' from response context%n", CLASS_NAME, propertyValue);
		return propertyValue;
	}
	
	
	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
		
		if(!retrieveMessageContext().equals(lastNonce))
			return "Invalid Nonce";
		
		JobView jv = null;
		
		//Origem desconhecida
		if(!verifyLocation(origin)){
			UnknownLocationFault faultInfo = new UnknownLocationFault();
			faultInfo.setLocation(origin);
			throw new UnknownLocationFault_Exception("Invalid origin", faultInfo);
		}
		
		//Destino desconhecido
		else if(!verifyLocation(destination)){
			UnknownLocationFault faultInfo = new UnknownLocationFault();
			faultInfo.setLocation(destination);
			throw new UnknownLocationFault_Exception("Invalid destination", faultInfo);
		}
		
		//O preco e menor que 0
		else if(price<0){
			InvalidPriceFault faultInfo = new InvalidPriceFault();
			faultInfo.setPrice(price);
			throw new InvalidPriceFault_Exception("Invalid price", faultInfo);
		}
		
		else if((getRegion(origin)==0 && getRegion(destination)==2) || (getRegion(origin)==2 && getRegion(destination)==0)){
			UnavailableTransportFault faultInfo = new UnavailableTransportFault();
			faultInfo.setDestination(destination);
			faultInfo.setOrigin(origin);
			throw new UnavailableTransportFault_Exception("Invalid regions", faultInfo);
		}
		
		TransportView finaltv = createTransportView(origin, destination, price);
		
		for(TransporterClient transporter : transporterList) {
			String name = transporter.getTransporterName();
			int parity = Integer.parseInt(name.substring(name.length()-1));
			
			//Transportadora impar (Centro e Sul)
			if(parity%2==1){
				if((getRegion(origin)==1 || getRegion(origin)==2) && (getRegion(destination)==2 || getRegion(destination)==1)){
					try {
						transporter.setrequestContextString(makeDigitalSignature((origin+destination+price).getBytes(), getPrivateKey()));
						jv = transporter.requestJob(origin, destination, price);
						if(verifyDigitalSignature(transporter.getrequestContextString(), JobViewToString(jv), getTransporter1PublicKey()))
							System.out.println("Verified");
					} catch (BadLocationFault_Exception | BadPriceFault_Exception e) {
						e.printStackTrace();
					}
					if((jv != null)){
						updateTransportView(jv, finaltv);
					}
				}
			}
			
			//Transportadora par(Centro e Norte)
			if(parity%2==0){
				if((getRegion(origin)==0 && getRegion(destination)==1) || (getRegion(origin)==1 && getRegion(destination)==0) || (getRegion(origin)==0 && getRegion(destination)==0) || (getRegion(origin)==1 && getRegion(destination)==1)){
					if((getRegion(origin)==1 && getRegion(destination)==1)){
						try {
							transporter.setrequestContextString(makeDigitalSignature((origin+destination+price).getBytes(), getPrivateKey()));
							jv = transporter.requestJob(origin, destination, price);
							if(verifyDigitalSignature(transporter.getrequestContextString(), JobViewToString(jv), getTransporter2PublicKey()))
								System.out.println("Verified");
						} catch (BadLocationFault_Exception | BadPriceFault_Exception e) {
							e.printStackTrace();
						}
						if((jv != null) && jv.getJobPrice() < finaltv.getPrice()){
							updateTransportView(jv, finaltv);
						}
					}
					else{
						try {
							transporter.setrequestContextString(makeDigitalSignature((origin+destination+price).getBytes(), getPrivateKey()));
							jv = transporter.requestJob(origin, destination, price);
							if(verifyDigitalSignature(transporter.getrequestContextString(), JobViewToString(jv), getTransporter2PublicKey()))
								System.out.println("Verified");
						} catch (BadLocationFault_Exception | BadPriceFault_Exception e) {
							e.printStackTrace();
						}
						if((jv != null)){
							updateTransportView(jv, finaltv);
						}
					}
				}
			}				
		}	
		
		if(finaltv.getPrice()>price){
			UnavailableTransportPriceFault faultInfo = new UnavailableTransportPriceFault();
			faultInfo.setBestPriceFound(finaltv.getPrice());
			throw new UnavailableTransportPriceFault_Exception("Best offer was: " + faultInfo.getBestPriceFound(), faultInfo);
		}
		
		//Caso em que sai do ciclo e nao houve nenhuma transportadora a fazer uma oferta
		if(jv == null){
			finaltv.setState(TransportStateView.FAILED);
			UnavailableTransportFault faultInfo = new UnavailableTransportFault();
			faultInfo.setDestination(destination);
			faultInfo.setOrigin(origin);
			throw new UnavailableTransportFault_Exception("Invalid regions", faultInfo);			
		}
		
		TransporterClient transporter = getTransporterbyName(finaltv.getTransporterCompany());
		try {
			transporter.setrequestContextString(makeDigitalSignature((finaltv.getId()+true).getBytes(), getPrivateKey()));
			JobView jvdecided = transporter.decideJob(finaltv.getId(), true);
			if(transporter.getTransporterName().equals("UpaTransporter1")){
				if(verifyDigitalSignature(transporter.getrequestContextString(), JobViewToString(jvdecided), getTransporter1PublicKey()))
					System.out.println("Verified");
			}
			else{
				if(verifyDigitalSignature(transporter.getrequestContextString(), JobViewToString(jvdecided), getTransporter2PublicKey()))
					System.out.println("Verified");
			}
			updateTransportView(jvdecided);
		} catch (BadJobFault_Exception e) {
			e.printStackTrace();
		}
		
		//Atualizar o server secundario
		updatesecundaryBroker(finaltv);

		return finaltv.getId();
		
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		TransportView transportView = null;
		for(TransportView tv : transportViews){
			if(tv.getId().equals(id)){
				transportView = tv;
			}
		}
		
		if(transportView == null){
			UnknownTransportFault faultInfo = new UnknownTransportFault();
			faultInfo.setId(id);
			throw new UnknownTransportFault_Exception("Invalid id", faultInfo);
		}
		
		
		TransporterClient transporter = getTransporterbyName(transportView.getTransporterCompany());
		transporter.setrequestContextString(makeDigitalSignature(id.getBytes(), getPrivateKey()));
		JobView jv = transporter.jobStatus(id);
		if(jv == null){
			UnknownTransportFault faultInfo = new UnknownTransportFault();
			faultInfo.setId(id);
			throw new UnknownTransportFault_Exception("Invalid id", faultInfo);
		}
		if(transporter.getTransporterName().equals("UpaTransporter1")){
			if(verifyDigitalSignature(transporter.getrequestContextString(), JobViewToString(jv), getTransporter1PublicKey()))
				System.out.println("Verified");
		}
		else{
			if(verifyDigitalSignature(transporter.getrequestContextString(), JobViewToString(jv), getTransporter2PublicKey()))
				System.out.println("Verified");
		}
		transportView = updateTransportView(jv);

		return transportView;
	}

	@Override
	public List<TransportView> listTransports() {
		for(TransporterClient transporters : transporterList){			
			for(JobView jv : transporters.listJobs()){
				updateTransportView(jv);
			}
		}
		return transportViews;
	}

	@Override
	public void clearTransports() {
		removeAllTransportViews();
		for(TransporterClient transporter : transporterList){
			transporter.clearJobs();
		}
		
	}
	
	public void updatesecundaryBroker(TransportView transport){
		String wsURL = "http://localhost:8084/broker-ws/endpoint";
		TransportView tv = transport;
		tv.setState(TransportStateView.COMPLETED);
		try {
			BrokerClient client = new BrokerClient(wsURL);
			client.updateBroker(tv);
		} catch (BrokerClientException e) {
			e.printStackTrace();
		}
		
		System.out.println("Updated correctly");
	}

	@Override
	public void updateBroker(TransportView transport) {
		if(!isPrimary()){
			addTransportView(transport);
		}
	}


}
