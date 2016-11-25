package pt.upa.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import pt.upa.ca.ws.cli.CAClient;
import pt.upa.ca.ws.cli.CAClientException;

public class SecurityClass {
	
	private final String STORE_PASS = "ins3cur3";
	private final String KEY_PASS = "1nsecure";
	//private final String CA_CERTIFICATE_PASS = "1ns3cur3";
	private final String alias = "ca";
	private final String uddiURL = "http://localhost:9090";
	private final String wsNameCA = "CA";
	private CAClient _client = null;
	
	public SecurityClass(){
		try {
			setCAClient(new CAClient(uddiURL, wsNameCA));
		} catch (CAClientException e) {
			e.printStackTrace();
		} 
	}
	
	private void setCAClient(CAClient client){_client = client;}
	
	private CAClient getCAClient(){return _client;}
	
	public PrivateKey getBrokerPrivateKey(String outputDirectory){
		KeyStore ks = null;
		InputStream in = null;
		Key key = null;
		
		try{
			ks = KeyStore.getInstance("JKS");
			in = new FileInputStream(outputDirectory + "/broker-ws/broker-ws.jks");
			ks.load(in, STORE_PASS.toCharArray());	
			key = ks.getKey("broker-ws", KEY_PASS.toCharArray());
			in.close();
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
			e.printStackTrace();
		}
		
		return (PrivateKey) key;
	}
	
	public PrivateKey getTransporterPrivateKey(String outputDirectory, int parity){
		KeyStore ks = null;
		InputStream in = null;
		Key key = null;
		
		try{
			ks = KeyStore.getInstance("JKS");
			in = new FileInputStream(outputDirectory + "/transporter" + parity + "-ws/transporter" + parity + "-ws.jks");
			ks.load(in, STORE_PASS.toCharArray());	
			key = ks.getKey("transporter" + parity + "-ws", KEY_PASS.toCharArray());
			in.close();
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
			e.printStackTrace();
		}
		
		return (PrivateKey) key;	
	}
	
	public PublicKey getCApublickey(String outputDirectory, int parity){
		KeyStore ks = null;
		InputStream in = null;
		Certificate cert = null;
			
		try{
			ks = KeyStore.getInstance("JKS");
			if(parity==0){
				in = new FileInputStream(outputDirectory + "/broker-ws/broker-ws.jks");
			}
			else{
				in = new FileInputStream(outputDirectory + "/transporter" + parity + "-ws/transporter" + parity + "-ws.jks");
			}			
			ks.load(in, STORE_PASS.toCharArray());	
			cert = ks.getCertificate(alias);
			in.close();
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			e.printStackTrace();
		}
		
		return cert.getPublicKey();
	}
	
	public X509Certificate getBrokerPublicKey(){
		return getCAClient().requestBrokerPublicKey();
	}
	
	public X509Certificate getTransporter1PublicKey(){
		return getCAClient().requestTransporter1PublicKey();
	}
	
	public X509Certificate getTransporter2PublicKey(){
		return getCAClient().requestTransporter2PublicKey();
	}
	
	
	public boolean verifySignedCertificate(Certificate certificate, PublicKey caPublicKey) {
		try {
			certificate.verify(caPublicKey);
		} catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
				| SignatureException e) {
			return false;
		}
		return true;
	}
}


