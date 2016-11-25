package pt.upa.CA.ws;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import javax.jws.WebService;

@WebService(endpointInterface = "pt.upa.CA.ws.AsymKeys")
public class AsymKeysImpl implements AsymKeys {

	private String _outputDirectory;
	
	public AsymKeysImpl(String outputDirectory) throws Exception{
		_outputDirectory = outputDirectory;
	}
	
	private String getoutputDirectory(){return _outputDirectory;}

	@Override
	public String ping(String ping) {
		return ping + " pong!";
	}

	@Override
	public byte[] requestTransport1Certificate() throws Exception {
		Certificate cert = readCertificateFile(getoutputDirectory() + "/ca/transporter1-ws.cer");
		return cert.getEncoded();
	}

	@Override
	public byte[] requestTransport2Certificate() throws Exception{
		Certificate cert = readCertificateFile(getoutputDirectory() + "/ca/transporter2-ws.cer");
		return cert.getEncoded();
	}

	@Override
	public byte[] requestBrokerCertificate() throws Exception {	
		Certificate cert = readCertificateFile(getoutputDirectory() + "/ca/broker-ws.cer");		
		return cert.getEncoded();
	}
	
	public static Certificate readCertificateFile(String certificateFilePath) throws Exception {
		FileInputStream fis;

		try {
			fis = new FileInputStream(certificateFilePath);
		} catch (FileNotFoundException e) {
			System.err.println("Certificate file <" + certificateFilePath + "> not found.");
			return null;
		}
		BufferedInputStream bis = new BufferedInputStream(fis);

		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		if (bis.available() > 0) {
			Certificate cert = cf.generateCertificate(bis);
			return cert;
			// It is possible to print the content of the certificate file:
			// System.out.println(cert.toString());
		}
		bis.close();
		fis.close();
		return null;
	}

}
