package pt.upa.CA.ws;

import javax.jws.WebService;

@WebService
public interface AsymKeys {

	public String ping(String ping);
	public byte[] requestBrokerCertificate() throws Exception;
	public byte[] requestTransport1Certificate() throws Exception;
	public byte[] requestTransport2Certificate() throws Exception;
	
}
