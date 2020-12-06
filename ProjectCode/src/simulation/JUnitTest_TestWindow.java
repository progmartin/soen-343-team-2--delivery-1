package simulation;

import static org.junit.Assert.*;

import org.junit.Test;

import HouseObjects.Window;

public class JUnitTest_TestWindow {
	
	@Test
	public void testWindow(){
		
		Window testwindow = new Window();
		
		testwindow.setBlocked(true);
		
		if(!testwindow.getBlocked()) testwindow.setOpen(true);
		else testwindow.setOpen(false);
		
		assertEquals(false, testwindow.getOpen());
	}

}
