/*package pt.upa.broker.ws.it;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

*//**
 *  Integration Test example
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 *//*
public class ViewTransportIT extends SuperIT {

    
    public void test() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception{
    	CLIENT.requestTransport("Lisboa","Porto", 30);
    	CLIENT.clearTransports();
    }
    
    @Test(expected=UnknownTransportFault_Exception.class)
    public void testUnknownTransportFault_Exception() throws UnknownTransportFault_Exception {
    	CLIENT.viewTransport("");
    }
    
    @Test(expected=UnknownTransportFault_Exception.class)
    public void testNullUnknownTransportFault_Exception() throws UnknownTransportFault_Exception {
    	CLIENT.viewTransport(null);
    }
    
    //public tests
    
    @Test
	public void testTransportStateTransition() throws Exception {
		List<TransportStateView> transportStates = new ArrayList<>();
		transportStates.add(TransportStateView.BOOKED);
		transportStates.add(TransportStateView.HEADING);
		transportStates.add(TransportStateView.ONGOING);
		transportStates.add(TransportStateView.COMPLETED);

		String transportId = CLIENT.requestTransport(CENTER_1, SOUTH_1, PRICE_SMALLEST_LIMIT);
		TransportView transportView = CLIENT.viewTransport(transportId);

		for (int t = DELAY_LOWER; t <= 3 * DELAY_UPPER; t = t + DELAY_LOWER) {
			Thread.sleep(DELAY_LOWER);
			transportView = CLIENT.viewTransport(transportId);
			if (transportStates.contains(transportView.getState()))
				transportStates.remove(transportView.getState());
		}
		assertEquals(0, transportStates.size());
	}
	
	@Test(expected = UnknownTransportFault_Exception.class)
	public void testViewInvalidTransport() throws Exception {
		CLIENT.viewTransport(null);
	}
	
	@Test(expected = UnknownTransportFault_Exception.class)
	public void testViewNullTransport() throws Exception {
		CLIENT.viewTransport(EMPTY_STRING);
	}
   
    
}*/