package HouseObjects;

/**
 * A class for keeping track of light objects attached to rooms.
 * 
 * @author a_richard
 */
public class Light {
	
	/**
	 * Unique identifier for light.
	 */
	int id;
	
	/**
	 * Whether or not light is on
	 */
	boolean isOn;
	
	/**
	 * Light name to display to user.
	 */
	String name;
	
	/**
	 * Default constructor.
	 */
	public Light(){
		id = 0;
		isOn = false;
	}
	
	/**
	 * Parametrized constructor.
	 * 
	 * @param id new id of the door
	 * @param isOn if the light is on
	 * @param name name of the light
	 */
	public Light(int id, boolean isOn, String name){
		this.id = id;
		this.isOn = isOn;
		this.name = name;
	}
	
	/**
	 * Get method for ID
	 * 
	 * @return the light's unique ID
	 */
	public int getID(){
		return id;
	}
	
	/**
	 * Get method for on status
	 * 
	 * @return if the light is on
	 */
	public boolean getIsOn(){
		return isOn;
	}
	
	/**
	 * Get method for the name
	 * 
	 * @return name of the light
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Set method for the unique ID
	 * 
	 * @param id
	 */
	public void setID(int id){
		this.id = id;
	}
	
	/**
	 * Set method for on status
	 * 
	 * @param isOn
	 */
	public void setIsOn(boolean isOn){
		this.isOn = isOn;
	}
	
	/**
	 * Set method for the name of the light
	 * 
	 * @param name the name of the light
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Prints the name of the light
	 */
	public String toString(){
		return name;
	}

}
