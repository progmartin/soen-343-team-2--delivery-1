package simulation;

import static org.junit.Assert.*;

import org.junit.Test;

import simulation.SHH_Module.Zone;

/**
 * 
 * @author a_richard
 *
 */
public class JUnitTest_addZone {
	
	@Test
	public void testAddZone(){
		
		SHH_Module testModule = new SHH_Module();
		
		String name = "Test Zone";
		
		Double temp = 23.5;
		
		testModule.addNewZone(name, temp);
		
		assertEquals("Test Zone", testModule.getZone("Test Zone").getName());
		
	}

}
