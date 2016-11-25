/*package pt.upa.broker.ws.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

public class BrokerClientUDDIMockTest{
	
	private static final String uddiURL = "http://localhost:9090";
	private static final String wsName = "WebServiceName";
	private static final String wsURL = "http://host:port/endpoint";
	
	@Test
    public void testMockUddi(@Mocked final UDDINaming uddiNaming) throws Exception {

        new Expectations() {{
                new UDDINaming(uddiURL);
                uddiNaming.lookup(wsName);
                result = wsURL;
        }};

        new BrokerClient(uddiURL, wsName);

        new Verifications() {{
 
                new UDDINaming(uddiURL);
                uddiNaming.lookup(wsName);
                maxTimes = 1;
                uddiNaming.unbind(null);
                maxTimes = 0;
                uddiNaming.bind(null, null);
                maxTimes = 0;
                uddiNaming.rebind(null, null);
                maxTimes = 0;
        }};

    }
	
	 @Test
	    public void testMockUddiNameNotFound(@Mocked final UDDINaming uddiNaming) throws Exception {

	        new Expectations() {
	            {
	                new UDDINaming(uddiURL);
	                uddiNaming.lookup(wsName);
	                result = null;
	            }
	        };

	        try {
	            new BrokerClient(uddiURL, wsName);
	            fail();

	        } catch (BrokerClientException e) {
	            final String expectedMessage = String.format(
	                    "Service with name %s not found on UDDI at %s", wsName,
	                    uddiURL);
	            assertEquals(expectedMessage, e.getMessage());
	        }


	        new Verifications() {
	            {
	                new UDDINaming(uddiURL);
	                uddiNaming.lookup(wsName);
	                maxTimes = 1;
	            }
	        };

	    }
}*/