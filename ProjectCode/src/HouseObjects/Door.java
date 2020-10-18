package HouseObjects;

/**
*
* @author a_richard
*/
//A class for keeping track of Doors attached to rooms
public class Door {
	
	//unique identifier for door
	int id;
	//whether or not door is open
	boolean open;
        public String name;
	
	//Default constructor for Door
	/**
	 * Constructor -- Default
	 */
	public Door(){
		id = 0;
		open = false;
	}
	
	//Parametrized constructor for Door
	/**
	 * Constructor -- Parametrized
	 * @param i
	 * @param o
	 */
	public Door(int i, boolean o){
		id = i;
		open = o;
	}
	
	//Get methods
	/**
	 * Get method for ID
	 * @return id
	 */
	public int getID(){
		return id;
	}
	
	/**
	 * Get method for open status
	 * @return open
	 */
	public boolean getOpen(){
		return open;
	}
	
	//Set methods
	/**
	 * Set method for ID
	 * @param i
	 */
	public void setID(int i){
		id = i;
	}
	
	/**
	 * Set method for open status
	 * @param o
	 */
	public void setOpen(boolean o){
		open = o;
	}

}
