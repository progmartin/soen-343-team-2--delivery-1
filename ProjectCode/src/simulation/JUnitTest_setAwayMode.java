package simulation;


import org.junit.Test;
import static org.junit.Assert.*;

/**
*
* @author Francesco Benvenuto
*/
public class JUnitTest_setAwayMode {

	/**
	 *
	 *1.Tests that setAwayOn returns false when set to on, but is already on.
	 *
	 */
	@Test
	public void testsetAwayOn() {
		
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
	public void testsetAwayOff() {
		
		SHP_Module testObject = new SHP_Module();
		
		testObject.awayMode = true;
		
		assertEquals(true, testObject.setAwayOff());
		
	}
	
	

}
