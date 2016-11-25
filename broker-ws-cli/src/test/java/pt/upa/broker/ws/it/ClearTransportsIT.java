package pt.upa.broker.ws.it;

import org.junit.*;

import pt.upa.broker.ws.UnknownTransportFault_Exception;

import static org.junit.Assert.*;

/**
 *  Integration Test example
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 */
public class ClearTransportsIT extends SuperIT {
	
    // tests

    /*@Test
    public void test() {
    	final String result = CLIENT.clearTransports();
        // assertEquals(expected, actual);
    	assertEquals("All transports have been cleared", result);
        // if the assert fails, the test fails
    }
    
    //public tests
    
 	// assertEquals(expected, actual);

 	// public void clearTransports();

 	@Test(expected = UnknownTransportFault_Exception.class)
 	public void testClearTransports() throws Exception {
 		String rt = CLIENT.requestTransport(CENTER_1, SOUTH_1, PRICE_SMALLEST_LIMIT);
 		CLIENT.clearTransports();
 		assertEquals(0, CLIENT.listTransports().size());
 		CLIENT.viewTransport(rt);
 	}*/

}