package simulation;

import static org.junit.Assert.*;

import org.junit.Test;

import simulation.SHH_Module.Zone;

/**
*
* @author Francesco Benvenuto
*/
public class JUnitTest_ManageTemperature {

	/**
	 *
	 *1.Tests that Zone temperatures can be set properly
	 *
	 */
	@Test
	public void test_setZoneTemperature() {
		
		SHH_Module testModule = new SHH_Module();
	
		SHH_Module.Zone testZone = testModule.new Zone();
		
		double temp = 23.5;
		
		testZone.setTemp(23.5, 1);
		
		assertEquals(temp,testZone.getTemp(1),0.01);
		
		
		
		
		
		
		
		
	}

}
