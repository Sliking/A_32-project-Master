package pt.upa.transporter.ws;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import example.ws.handler.DigitalMarkHandler;
import pt.upa.security.SecurityClass;

@WebService(
	    endpointInterface="pt.upa.transporter.ws.TransporterPortType",
	    wsdlLocation="transporter.1_0.wsdl",
	    name="TransporterWebService",
	    portName="TransporterPort",
	    targetNamespace="http://ws.transporter.upa.pt/",
	    serviceName="TransporterService"
	)
@HandlerChain(file = "/transporter_handler-chain.xml")
public class TransporterPort implements TransporterPortType {
	
	@Resource
	private WebServiceContext webServiceContext;
	private MessageContext messageContext = null;
	public static final String CLASS_NAME = TransporterPort.class.getSimpleName();
	private List<JobView> jobViews = new ArrayList<JobView>();
	private String _identifier = UUID.randomUUID().toString();
	private int _parity;
	private PrivateKey privKey = null;
	private PublicKey CApublicKey = null;
	private String _outputDirectory = null;
	private SecurityClass security = null;
	private PublicKey BrokerPubKey = null;
	
	public TransporterPort(String name, String outputDirectory){
		_parity = Integer.parseInt(name.substring(name.length()-1));
		_outputDirectory = outputDirectory;
		setSecurityClass(new SecurityClass());
		getPrivateKeyfromKS();		
		getCApublicKeyFromKS();	
		getBrokerPublicKeyFromCA();	
	}
	
	public TransporterPort(){}
	
	private void getPrivateKeyfromKS(){
		setPrivateKey(getSecurityClass().getTransporterPrivateKey(getoutputDirectory(), getParity()));
	}
	
	private void getCApublicKeyFromKS(){
		setCAPublicKey(getSecurityClass().getCApublickey(getoutputDirectory(), getParity()));
	}
	
	private void getBrokerPublicKeyFromCA(){
		X509Certificate cert = getSecurityClass().getBrokerPublicKey();
		
		if(getSecurityClass().verifySignedCertificate(cert, getCAPublicKey())){
			setBrokerPublicKey(cert.getPublicKey());
		}
	}
	
	public void setBrokerPublicKey(PublicKey key){BrokerPubKey = key;}
	
	public void setCAPublicKey(PublicKey key){ CApublicKey = key;}
	
	private void setSecurityClass(SecurityClass security){this.security = security;}
	
	public void setParity(int parity){_parity = parity;}
	
	private void setPrivateKey(PrivateKey key){privKey = key;}
	
	public PublicKey getBrokerPublicKey(){return BrokerPubKey;}
	
	private SecurityClass getSecurityClass(){return this.security;}
	
	private PublicKey getCAPublicKey(){return CApublicKey;}
	
	private PrivateKey getPrivateKey(){return privKey;}
	
	public int getParity(){return _parity;}
	
	public String getoutputDirectory(){return _outputDirectory;}
	
	private String[][] locations = new String[][] {
		{"Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança"}, //Norte
		{"Lisboa", "Leiria", "Santarem", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda"}, //Centro
		{"Setubal", "Evora", "Portalegre", "Beja", "Faro"} //Sul
	};
	
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
	
	public JobView getJobViewbyId(String id){
		for(JobView jv : jobViews){
			if(jv.getJobIdentifier().equals(id)){
				return jv;
			}
		}
		return null;
	}
	
	public void newIdentifier(){_identifier=UUID.randomUUID().toString();}
	
	public String getIdentifier(){return _identifier;}
	
	public JobView createJobView(String origin, String destination, int price){
		JobView result = new JobView();
		result.setCompanyName("UpaTransporter" + getParity());
		result.setJobDestination(destination);
		result.setJobIdentifier(getIdentifier());
		result.setJobOrigin(origin);
		result.setJobPrice(price);
		result.setJobState(JobStateView.PROPOSED);
		jobViews.add(result);
		newIdentifier();
		return result;
	}
	
	private String JobViewToString(JobView jv){
		return jv.getCompanyName()+jv.getJobIdentifier()+jv.getJobOrigin()+jv.getJobDestination()+jv.getJobPrice()+jv.getJobState();
	}
	
	private String retrieveMessageContext(){
		// retrieve message context
		messageContext = webServiceContext.getMessageContext();
		// get token from message context
		String propertyValue = (String) messageContext.get(DigitalMarkHandler.REQUEST_PROPERTY);
		System.out.printf("%s got token '%s' from response context%n", CLASS_NAME, propertyValue);
		return propertyValue;
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
	 
	@Override
	public String ping(String name) {
		String result = "Testing " + name + "\n";
		String propertyValue = retrieveMessageContext();
		if(verifyDigitalSignature(propertyValue, name, getBrokerPublicKey())){
			System.out.println("Verified");
			// put token in message context
			System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, makeDigitalSignature(result.getBytes(), getPrivateKey()));
			messageContext.put(DigitalMarkHandler.REQUEST_PROPERTY, makeDigitalSignature(result.getBytes(), getPrivateKey()));
			return "Testing " + name + "\n";
		}
		
		System.out.println("[ERROR] Invalid digital signature");
		return null;	
	}

	@Override
	public JobView requestJob(String origin, String destination, int price)
			throws BadLocationFault_Exception, BadPriceFault_Exception {
		
		//Origem desconhecida
		if(!verifyLocation(origin)){
			BadLocationFault faultInfo = new BadLocationFault();
			faultInfo.setLocation(origin);
			throw new BadLocationFault_Exception("Invalid origin", faultInfo);
		}
		
		//Destino desconhecido
		else if(!verifyLocation(destination)){
			BadLocationFault faultInfo = new BadLocationFault();
			faultInfo.setLocation(destination);
			throw new BadLocationFault_Exception("Invalid destination", faultInfo);
		}
		
		//O preco e menor que 0
		else if(price<=0){
			BadPriceFault faultInfo = new BadPriceFault();
			faultInfo.setPrice(price);
			throw new BadPriceFault_Exception("Invalid price", faultInfo);			
		}
		
		/*
		 * Caso a Transportadora seja par (2, 4, ...) e a regiao seja Sul (2) entao envia null
		 * "... nao opere na origem ou destino deve devolver null."
		 */
		else if(getParity()%2==0 && (getRegion(destination)==2 || getRegion(origin)==2)){
			return null;
		}
		
		/*
		 * Caso a Transportadora seja impar (2, 4, ...) e a regiao seja Norte (0) entao null
		 * "... nao opere na origem ou destino deve devolver null."
		 */
		else if(getParity()%2==1 && (getRegion(destination)==0 || getRegion(origin)==0)){
			return null;
		}
		
		else{
			if(price>100){
				return null;
			}
			else if(price<=10){	
				JobView jv = createJobView(origin, destination, price-2);
				String propertyValue = retrieveMessageContext();
				if(verifyDigitalSignature(propertyValue, (origin+destination+price), getBrokerPublicKey())){
					System.out.println("Verified");
					// put token in message context
					System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, makeDigitalSignature(JobViewToString(jv).getBytes(), getPrivateKey()));
					messageContext.put(DigitalMarkHandler.REQUEST_PROPERTY, makeDigitalSignature(JobViewToString(jv).getBytes(), getPrivateKey()));
					return jv;
				}
				
				System.out.println("[ERROR] Invalid digital signature");
				return null;
			}
			else{
				if((price%2)==0){
					if(getParity()%2==0){
						JobView jv = createJobView(origin, destination, price-3);
						String propertyValue = retrieveMessageContext();
						if(verifyDigitalSignature(propertyValue, (origin+destination+price), getBrokerPublicKey())){
							System.out.println("Verified");
							// put token in message context
							System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, makeDigitalSignature(JobViewToString(jv).getBytes(), getPrivateKey()));
							messageContext.put(DigitalMarkHandler.REQUEST_PROPERTY, makeDigitalSignature(JobViewToString(jv).getBytes(), getPrivateKey()));
							return jv;
						}
						
						System.out.println("[ERROR] Invalid digital signature");
						return null;
					}
				}
				if((price%2)==1){
					if(getParity()%2==1){
						JobView jv = createJobView(origin, destination, price-3);
						String propertyValue = retrieveMessageContext();
						if(verifyDigitalSignature(propertyValue, (origin+destination+price), getBrokerPublicKey())){
							System.out.println("Verified");
							// put token in message context
							System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, makeDigitalSignature(JobViewToString(jv).getBytes(), getPrivateKey()));
							messageContext.put(DigitalMarkHandler.REQUEST_PROPERTY, makeDigitalSignature(JobViewToString(jv).getBytes(), getPrivateKey()));
							return jv;
						}
						
						System.out.println("[ERROR] Invalid digital signature");
						return null;
					}
				}

				if((price + 3) >= 100){
					JobView jv = createJobView(origin, destination, 100);
					String propertyValue = retrieveMessageContext();
					if(verifyDigitalSignature(propertyValue, (origin+destination+price), getBrokerPublicKey())){
						System.out.println("Verified");
						// put token in message context
						System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, makeDigitalSignature(JobViewToString(jv).getBytes(), getPrivateKey()));
						messageContext.put(DigitalMarkHandler.REQUEST_PROPERTY, makeDigitalSignature(JobViewToString(jv).getBytes(), getPrivateKey()));
						return jv;
					}
					
					System.out.println("[ERROR] Invalid digital signature");
					return null;
				}
				else{
					JobView jv = createJobView(origin, destination, price+3);
					String propertyValue = retrieveMessageContext();
					if(verifyDigitalSignature(propertyValue, (origin+destination+price), getBrokerPublicKey())){
						System.out.println("Verified");
						// put token in message context
						System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, makeDigitalSignature(JobViewToString(jv).getBytes(), getPrivateKey()));
						messageContext.put(DigitalMarkHandler.REQUEST_PROPERTY, makeDigitalSignature(JobViewToString(jv).getBytes(), getPrivateKey()));
						return jv;
					}
					
					System.out.println("[ERROR] Invalid digital signature");
					return null;
				}					
			}
		}		
	}

	@Override
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		JobView jv = getJobViewbyId(id);
		if((jv == null) || (!jv.getJobState().equals(JobStateView.PROPOSED))){
			BadJobFault faultInfo = new BadJobFault();
			faultInfo.setId(id);
			throw new BadJobFault_Exception("Invalid id", faultInfo);
		}
		else{
			if(accept){
				jv.setJobState(JobStateView.ACCEPTED);
				startProccess(jv);
			}
			else{
				jv.setJobState(JobStateView.REJECTED);
			}
			
			String propertyValue = retrieveMessageContext();
			if(verifyDigitalSignature(propertyValue, (id+accept), getBrokerPublicKey())){
				System.out.println("Verified");
				// put token in message context
				System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, makeDigitalSignature(JobViewToString(jv).getBytes(), getPrivateKey()));
				messageContext.put(DigitalMarkHandler.REQUEST_PROPERTY, makeDigitalSignature(JobViewToString(jv).getBytes(), getPrivateKey()));
				return jv;
			}
			
			System.out.println("[ERROR] Invalid digital signature");
			return null;
		}
		
	}
	
	private int randomNumber(int min, int max){
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}
	
	private void startProccess(JobView jv) {
		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		                jv.setJobState(JobStateView.HEADING);
		            }
		        }, 
		        randomNumber(1, 5)*1000
		);
		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		                jv.setJobState(JobStateView.ONGOING);
		            }
		        }, 
		        randomNumber(5, 10)*1000
		);
		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		                jv.setJobState(JobStateView.COMPLETED);
		            }
		        }, 
		        randomNumber(10, 15)*1000
		);
		
		
	}

	@Override
	public JobView jobStatus(String id) {
		for(JobView jobView : jobViews){
			if(jobView.getJobIdentifier().equals(id)){
				String propertyValue = retrieveMessageContext();
				if(verifyDigitalSignature(propertyValue, id, getBrokerPublicKey())){
					System.out.println("Verified");
					// put token in message context
					System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, makeDigitalSignature(JobViewToString(jobView).getBytes(), getPrivateKey()));
					messageContext.put(DigitalMarkHandler.REQUEST_PROPERTY, makeDigitalSignature(JobViewToString(jobView).getBytes(), getPrivateKey()));
					return jobView;
				}			
				System.out.println("[ERROR] Invalid digital signature");
				return null;
			}
		}
		return null;
	}

	@Override
	public List<JobView> listJobs() {
		return jobViews;
	}

	@Override
	public void clearJobs() {
		jobViews.clear();
		
	}
	

}
