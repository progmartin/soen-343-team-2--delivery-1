package HouseObjects;

/**
*
* @author a_richard
*/
//A class for keeping track of Person objects inside the house
public class Person {
	
	//individual's name
	String name;
	//true if person is admin
	boolean isAdmin;
	
	//default constructor
	/**
	 * constructor -- default
	 */
	public Person(){
		name = "Default";
		isAdmin = false;
	}
	
	//parametrized constructor
	/**
	 * constructor -- parametrized
	 * @param s
	 * @param b
	 */
	public Person(String s, boolean b){
		name = s;
		isAdmin = b;
	}
	
	//get methods
	/**
	 * Get Name
	 * @return name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Get Admin Status
	 * @return isAdmin
	 */
	public boolean getIsAdmin(){
		return isAdmin;
	}
	
	//set methods
	/**
	 * Set Name
	 * @param s
	 */
	public void setName(String s){
		name = s;
	}
	
	/**
	 * Set Admin Status
	 * @param b
	 */
	public void setAdmin(boolean b){
		isAdmin = b;
	}

}
