package HouseObjects;

import java.util.ArrayList;

/**
*
* @author a_richard
*/
//room contains Arraylist of windows and doors
public class Room {
	ArrayList<Window> window = new ArrayList<Window>();
	ArrayList<Door> door = new ArrayList<Door>();
	
	//add door to room
	public void addWindow(Window w) {
		window.add(w);
		
	}
	//add window to room
	public void addDoor(Door d) {
		door.add(d);
		
	}

}
