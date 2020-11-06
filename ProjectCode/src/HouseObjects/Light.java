package HouseObjects;

/**
 * A class for keeping track of light objects attached to rooms.
 * 
 * @author a_richard
 */
public class Light {
	
	int id;
	boolean isOn;
	String name;
	
	public Light(){
		id = 0;
		isOn = false;
	}
	
	public Light(int id, boolean isOn, String name){
		this.id = id;
		this.isOn = isOn;
		this.name = name;
	}
	
	public int getID(){
		return id;
	}
	
	public boolean getIsOn(){
		return isOn;
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	public void setIsOn(boolean isOn){
		this.isOn = isOn;
	}
	
	public String toString(){
		return name;
	}

}
