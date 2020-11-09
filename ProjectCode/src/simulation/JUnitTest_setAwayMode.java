package simulation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
*
* @author Francesco Benvenuto
*/
class JUnitTest_setAwayMode {

	/**
	 *
	 *1.Tests that setAwayOn returns false when set to on, but is already on.
	 *
	 */
	@Test
	void testsetAwayOn() {
		
		SHP_Module testObject = new SHP_Module();
		
		testObject.awayMode = true;
		
		assertEquals(false, testObject.setAwayOn());
		
		
		
	}
	
	/**
	 *
	 *1.Tests that setAwayoff returns true when set to off.
	 *
	 */
	@Test
	void testsetAwayOff() {
		
		SHP_Module testObject = new SHP_Module();
		
		testObject.awayMode = true;
		
		assertEquals(true, testObject.setAwayOff());
		
	}
	
	

}
