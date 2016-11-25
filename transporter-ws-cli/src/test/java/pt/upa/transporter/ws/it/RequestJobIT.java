/*package pt.upa.transporter.ws.it;

import org.junit.*;

import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;

import static org.junit.Assert.*;

import java.util.List;

*//**
 *  Integration Test example
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 *//*
public class RequestJobIT extends SuperIT {


    // tests
    @Test
    public void test() throws BadLocationFault_Exception, BadPriceFault_Exception {
    	JobView expected = null;
    	JobView result = CLIENT.requestJob("Lisboa", "Faro", 30);
    	List<JobView> jobsList = CLIENT.listJobs();
    	for(JobView jv : jobsList){
    		if(result.getJobIdentifier().equals(jv.getJobIdentifier())){
    			expected = jv;
    		}
    	}
    	
        // assertEquals(expected, actual);
    	assertEquals(expected.getJobIdentifier(), result.getJobIdentifier());
    	CLIENT.clearJobs();
        // if the assert fails, the test fails
    }
    
    @Test
    public void testBadLocationFault_Exception() throws BadLocationFault_Exception, BadPriceFault_Exception {
    	JobView result = CLIENT.requestJob("Lisboa", "Porto", 30);
    	assertEquals(null, result);
    	CLIENT.clearJobs();

    }
    
    @Test(expected=BadPriceFault_Exception.class)
    public void testBadPriceFault_Exception() throws BadLocationFault_Exception, BadPriceFault_Exception{
    	JobView result = CLIENT.requestJob("Lisboa", "Faro", -1);
    	assertEquals("NONE", result.getJobIdentifier());
    	CLIENT.clearJobs();
    }
    
    @Test
    public void testPriceOver100() throws BadLocationFault_Exception, BadPriceFault_Exception {
    	JobView result = CLIENT.requestJob("Lisboa", "Faro", 101);
    	assertEquals(null, result);
    	CLIENT.clearJobs();

    }
    
    //public tests
    
    *//**
	 * Request a job (with valid origin, destination and price) with a price of
	 * 10.
	 * 
	 * @result The job should be successfully created and stored by the
	 *         transporter.
	 * @throws Exception
	 *//*
	@Test
	public void testRequestJob() throws Exception {
		CLIENT.requestJob(CENTRO_1, SUL_1, PRICE_SMALLEST_LIMIT);
	}

	// -------------- invalid inputs test cases ---------------

	*//**
	 * Invoke CLIENT.requestJob on an invalid (empty string) origin.
	 * 
	 * @result Should throw BadLocationFault_Exception as the origin is invalid.
	 * @throws Exception
	 *//*
	@Test(expected = BadLocationFault_Exception.class)
	public void testRequestJobInvalidOrigin() throws Exception {
		CLIENT.requestJob(EMPTY_STRING, CENTRO_1, PRICE_SMALLEST_LIMIT);
	}

	*//**
	 * Invoke CLIENT.requestJob on an invalid (null) origin.
	 * 
	 * @result Should throw BadLocationFault_Exception as the origin is invalid.
	 * @throws Exception
	 *//*
	@Test(expected = BadLocationFault_Exception.class)
	public void testRequestJobNullOrigin() throws Exception {
		CLIENT.requestJob(null, SUL_1, PRICE_SMALLEST_LIMIT);
	}

	*//**
	 * Invoke CLIENT.requestJob on an invalid (empty string) destination.
	 * 
	 * @result Should throw BadLocationFault_Exception as the destination is
	 *         invalid.
	 * @throws Exception
	 *//*
	@Test(expected = BadLocationFault_Exception.class)
	public void testRequestJobInvalidDestination() throws Exception {
		CLIENT.requestJob(CENTRO_1, EMPTY_STRING, PRICE_SMALLEST_LIMIT);
	}

	*//**
	 * Invoke CLIENT.requestJob on an invalid (null) destination.
	 * 
	 * @result Should throw BadLocationFault_Exception as the destination is
	 *         invalid.
	 * @throws Exception
	 *//*
	@Test(expected = BadLocationFault_Exception.class)
	public void testRequestJobNullDestination() throws Exception {
		CLIENT.requestJob(SUL_1, null, PRICE_SMALLEST_LIMIT);
	}

	*//**
	 * Invoke CLIENT.requestJob on both invalid (empty string) origin and
	 * destination.
	 * 
	 * @result Should throw BadLocationFault_Exception as both the origin and
	 *         the destination is invalid.
	 * @throws Exception
	 *//*
	@Test(expected = BadLocationFault_Exception.class)
	public void testRequestJobInvalidOD() throws Exception {
		CLIENT.requestJob(EMPTY_STRING, EMPTY_STRING, PRICE_SMALLEST_LIMIT);
	}

	*//**
	 * Invoke CLIENT.requestJob on both invalid (null) origin and destination.
	 * 
	 * @result Should throw BadLocationFault_Exception as both the origin and
	 *         the destination is invalid.
	 * @throws Exception
	 *//*
	@Test(expected = BadLocationFault_Exception.class)
	public void testRequestJobNullOD() throws Exception {
		CLIENT.requestJob(null, null, PRICE_SMALLEST_LIMIT);
	}

	*//**
	 * Invoke CLIENT.requestJob with an invalid (negative) price.
	 * 
	 * @result Should throw BadPriceFault_Exception as the price given was
	 *         negative.
	 * @throws Exception
	 *//*
	@Test(expected = BadPriceFault_Exception.class)
	public void testRequestJobInvalidPrice() throws Exception {
		CLIENT.requestJob(CENTRO_1, SUL_1, INVALID_PRICE);
	}

	*//**
	 * Invoke CLIENT.requestJob with all invalid parameters (empty string
	 * locations and negative price) of origin, destination and price.
	 * 
	 * @result Should throw BadLocationFault_Exception as both the origin and
	 *         the destination are invalid or BadPriceFault_Exception as an
	 *         invalid price given.
	 * @throws Exception
	 *//*
	public void testRequestJobInvalidArgs1() throws Exception {
		try {
			CLIENT.requestJob(EMPTY_STRING, EMPTY_STRING, INVALID_PRICE);
		} catch (BadLocationFault_Exception | BadPriceFault_Exception e) {
			// do nothing because both exceptions can be expected
		}
	}

	*//**
	 * Invoke CLIENT.requestJob with all invalid parameters (null locations and
	 * negative price) of origin, destination and price.
	 * 
	 * @result Should throw BadLocationFault_Exception as both the origin and
	 *         the destination are invalid or BadPriceFault_Exception as an
	 *         invalid price given.
	 * @throws Exception
	 *//*
	public void testRequestJobInvalidArgs2() throws Exception {
		try {
			CLIENT.requestJob(null, null, INVALID_PRICE);
		} catch (BadLocationFault_Exception | BadPriceFault_Exception e) {
			// do nothing because both exceptions can be expected
		}
	}

	// -------------- reference price > 100 ---------------

	*//**
	 * Test that a job request with a price over 100 returns null.
	 * 
	 * @return A null JobView reference.
	 * @throws Exception
	 *//*
	@Test
	public void testUpperPriceLimit() throws Exception {
		JobView jv1 = CLIENT.requestJob(SUL_1, CENTRO_1, PRICE_UPPER_LIMIT + 1);
		assertNull(jv1);
	}

	// -------------- reference price <= 10 ---------------

	*//**
	 * Test that a job requested with a price below 10 returns a positive price
	 * lower or equal to 10.
	 * 
	 * @throws Exception
	 *//*
	@Test
	public void testPriceBelowSmallestLimit() throws Exception {
		final int referencePrice = PRICE_SMALLEST_LIMIT - UNITARY_PRICE;
		JobView jv1 = CLIENT.requestJob(CENTRO_1, SUL_1, referencePrice);
		final int price = jv1.getJobPrice();
		assertTrue(price >= UNITARY_PRICE && price < referencePrice);
	}

	*//**
	 * Test a job request with a price of 10. The proposed price should be
	 * greater or equal to 1 and lower than 10.
	 * 
	 * @result JobView with a price value under the constraint mentioned above.
	 * @throws Exception
	 *//*
	@Test
	public void testLowerEqualPriceLimit() throws Exception {
		final int referencePrice = PRICE_SMALLEST_LIMIT;
		JobView jv1 = CLIENT.requestJob(SUL_1, CENTRO_1, referencePrice);
		final int price = jv1.getJobPrice();
		assertTrue(price >= UNITARY_PRICE && price < referencePrice);
	}

	// -------------- reference price > 10 ---------------

	*//**
	 * Odd transporter, odd price
	 * 
	 * Test that an odd-numbered transporter (e.g. UpaTransporter1) with an odd
	 * price request returns a proposal between [1, price + 1[.
	 * 
	 * @throws Exception
	 *//*
	@Test
	public void testOddPriceAboveSmallestLimit() throws Exception {
		int oddReferencePrice = PRICE_SMALLEST_LIMIT + 1;
		assertTrue(oddReferencePrice % 2 == 1);

		JobView jv1 = CLIENT.requestJob(CENTRO_1, SUL_1, oddReferencePrice);
		final int price = jv1.getJobPrice();
		assertTrue(price >= UNITARY_PRICE && price < oddReferencePrice);
	}

	*//**
	 * Odd transporter, even price
	 *
	 * Test that an odd-numbered transporter (e.g. UpaTransporter1) with an even
	 * price request of 12 returns a proposal between ]price,
	 * Integer.MAX_VALUE[.
	 * 
	 * @throws Exception
	 *//*
	@Test
	public void testEvenPriceAboveSmallestLimit() throws Exception {
		int evenReferencePrice = PRICE_SMALLEST_LIMIT + 2;
		assertTrue(evenReferencePrice % 2 == 0);
		assertTrue(evenReferencePrice < Integer.MAX_VALUE - 1);

		JobView jv1 = CLIENT.requestJob(CENTRO_1, SUL_1, evenReferencePrice);
		final int price = jv1.getJobPrice();
		assertTrue(price > evenReferencePrice && price < Integer.MAX_VALUE);
	}

	// -------------- reference price border cases ---------------

	*//**
	 * Test that a job request with a price of 1 returns a proposal with a price
	 * of 0.
	 * 
	 * @return JobView reference with price set to 0.
	 * @throws Exception
	 *//*
	// @Test
	// not tested for evaluation as stated in project Q&A:
	// http://disciplinas.tecnico.ulisboa.pt/leic-sod/2015-2016/labs/proj/faq.html
	public void testZeroPrice() throws Exception {
		JobView jv1 = CLIENT.requestJob(SUL_1, CENTRO_1, ZERO_PRICE);
		final int price = jv1.getJobPrice();
		assertEquals(ZERO_PRICE, price);
	}

	*//**
	 * Test that a job request with a price of 1 returns a proposal with a price
	 * of 0.
	 * 
	 * @return JobView reference with price set to 0.
	 * @throws Exception
	 *//*
	// @Test
	// not tested for evaluation as stated in project Q&A:
	// http://disciplinas.tecnico.ulisboa.pt/leic-sod/2015-2016/labs/proj/faq.html
	public void testUnitaryPrice() throws Exception {
		JobView jv1 = CLIENT.requestJob(SUL_1, CENTRO_1, UNITARY_PRICE);
		final int price = jv1.getJobPrice();
		assertEquals(ZERO_PRICE, price);
	}
   
    
}*/