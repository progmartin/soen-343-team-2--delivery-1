package simulation;

import static org.junit.Assert.*;

import org.junit.Test;

public class JUnitTest_testAlert {
	
	@Test
	public void testAlertSystem(){
		
		SHH_Module testModule = new SHH_Module();
		
		assertEquals("", testModule.contactUser());
		
	}

}
