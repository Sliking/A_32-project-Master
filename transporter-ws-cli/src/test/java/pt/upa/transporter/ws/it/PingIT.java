/*package pt.upa.transporter.ws.it;

import org.junit.*;

import static org.junit.Assert.*;

*//**
 *  Integration Test example
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 *//*
public class PingIT extends SuperIT {

    // tests

    @Test
    public void test() {
    	CLIENT.setrequestContextString("test");
    	final String result = CLIENT.ping("connectivity");
        // assertEquals(expected, actual);
    	assertEquals("Testing connectivity\n", result);
        // if the assert fails, the test fails
    }
    
    //public tests
    
    *//**
	 * Receive a non-null reply from the transporter that was pinged through
	 * CLIENT.
	 *//*
	@Test
	public void pingEmptyTest() {
		assertNotNull(CLIENT.ping("test"));
	}

}*/