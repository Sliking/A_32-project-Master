/*package pt.upa.broker.ws.cli;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import mockit.*;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.BindingProvider;

public class BrokerClientServicePortMockTest{
		
	*//** mocked web service endpoint address *//*
	private static String wsURL = "http://host:port/endpoint";
	
	Map<String,Object> contextMap = null;
	
	@Before
    public void setUp() {
    	contextMap = new HashMap<String,Object>();
    }

    @After
    public void tearDown() {
    	contextMap = null;
    }
    
	@Test(expected=WebServiceException.class)
	public <P extends BrokerPortType & BindingProvider>void testMocks(
			@Mocked BrokerService service, 
			@Mocked P port) throws Exception{
		
		new Expectations(){{
			new BrokerService();
			service.getBrokerPort(); result = port;
			port.getRequestContext(); result = contextMap;
			port.ping("connectivity");
			result = new WebServiceException("fabricated");
		}};
		
		BrokerClient client = new BrokerClient(wsURL);
		
		client.ping("connectivity");
	}
	
	@Test
    public <P extends BrokerPortType & BindingProvider> void testMockServerExceptionOnSecondCall(
        @Mocked final BrokerService service,
        @Mocked final P port)
        throws Exception {

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new Expectations() {{
            new BrokerService();
            service.getBrokerPort(); result = port;
            port.getRequestContext(); result = contextMap;
            port.ping("connectivity");
            // first call to sum returns the result
            result = "testing connectivity";
            // second call throws an exception
            result = new WebServiceException("fabricated");
        }};


        // Unit under test is exercised.
        BrokerClient client = new BrokerClient(wsURL);

        // first call to mocked server
        try {
            client.ping("connectivity");
        } catch(WebServiceException e) {
            // exception is not expected
            fail();
        }

        // second call to mocked server
        try {
            client.ping("connectivity");
            fail();
        } catch(WebServiceException e) {
            // exception is expected
            assertEquals("fabricated", e.getMessage());
        }
    }
}*/