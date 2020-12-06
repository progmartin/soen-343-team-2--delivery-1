package simulation;

import static org.junit.Assert.*;

import org.junit.Test;

import simulation.SHH_Module.Zone;

/**
*
* @author Francesco Benvenuto
*/
public class JUnitTest_ManageRoomZones {
	/**
	 *
	 *1.Tests that new zones can be added
	 *
	 */
	
	@Test
	public void testSetZone() {
		
		SHH_Module testModule = new SHH_Module();
		
		double [] temps = new double[1];
		
		String zone = "Kitchen";
		
		testModule.addNewZone("Kitchen", temps);
		
		assertEquals(zone, testModule.getZone("Kitchen").getName());
		
		
		
		
		
		
	}

}
