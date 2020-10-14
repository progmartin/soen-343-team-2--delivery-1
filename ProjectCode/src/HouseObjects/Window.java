package HouseObjects;

/**
*
* @author a_richard
*/
//A class for keeping track of windows inside a room
public class Window {
	
	//unique identifier for window
	int id;
	//is window open?
	boolean open;
	//is window blocked?
	boolean blocked;
	
	//default constructor for Window
	/**
	 * constructor -- default
	 */
	public Window(){
		id = 0;
		open = false;
		blocked = false;
	}
	
	//parametrized constructor for Window
	/**
	 * constructor -- parametrized
	 * @param i
	 * @param o
	 * @param b
	 */
	public Window(int i, boolean o, boolean b){
		id = i;
		open = o;
		blocked = b;
	}
	
	//get methods
	/**
	 * Get Window ID
	 * @return id
	 */
	public int getId(){
		return id;
	}
	
	/**
	 * Get Window Open Status
	 * @return open
	 */
	public boolean getOpen(){
		return open;
	}
	
	/**
	 * Get Window Blocked Status
	 * @return blocked
	 */
	public boolean getBlocked(){
		return blocked;
	}
	
	//set methods
	/**
	 * Set ID
	 * @param i
	 */
	public void setId(int i){
		id = i;
	}
	
	/**
	 * Set Open Status
	 * @param o
	 */
	public void setOpen(boolean o){
		open = o;
	}
	
	/**
	 * Set Blocked Status
	 * @param b
	 */
	public void setBlocked(boolean b){
		blocked = b;
	}

}
