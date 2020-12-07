package simulation;

import static org.junit.Assert.*;

import org.junit.Test;


/**
*
* @author Francesco Benvenuto
*/
public class JUnitTest_ManageWindows {

	/**
	 *
	 *1.Tests that the windows are recognized as blocked or not
	 *
	 */
	@Test
	public void testnoWindow() {
		
		boolean blockedWindow = true;
		
		SHH_Module testModule = new SHH_Module();
		
		testModule.setBlockedWindow(true);
		
		assertTrue("The window is blocked", testModule.isBlockedWindow());
	}

}
