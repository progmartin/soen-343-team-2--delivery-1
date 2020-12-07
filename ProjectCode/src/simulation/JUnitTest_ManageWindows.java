package simulation;

import static org.junit.Assert.*;

import org.junit.Test;

public class JUnitTest_ManageWindows {

	@Test
	public void testnoWindow() {
		
		boolean blockedWindow = true;
		
		SHH_Module testModule = new SHH_Module();
		
		testModule.setBlockedWindow(true);
		
		assertTrue("The window is blocked", testModule.isBlockedWindow());
	}

}
