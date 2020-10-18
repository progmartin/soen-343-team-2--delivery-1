package HouseObjects;

import java.util.ArrayList; // import ArrayList class

/**
 *
 * @author a_richard
 */
public class Room {

    //name of room
    String name;
    //temperature of room
    double heatingTemp;

    //people currently in room
    ArrayList<Person> people = new ArrayList<Person>();
    //doors on room
    ArrayList<Door> doors = new ArrayList<Door>();
    //windows in room
    ArrayList<Window> windows = new ArrayList<Window>();

    //Default constructor
    /**
     * Constructor -- Default
     */
    public Room() {
        name = "Empty Room";
        heatingTemp = 21;
    }

    //constructor with only name of room
    /**
     * Constructor -- Room Name
     *
     * @param n
     */
    public Room(String n) {
        name = n;
        heatingTemp = 21;
    }

    //fully parametrized constructor
    /**
     * Constructor -- parametrized
     *
     * @param n
     * @param t
     */
    public Room(String n, double t) {
        name = n;
        heatingTemp = t;
    }

    //Get methods (name and temp)
    /**
     * Get method for name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get method for temperature
     *
     * @return heatingTemp
     */
    public double getTemp() {
        return heatingTemp;
    }

    //Set methods (name and temp)
    /**
     * Set method for name
     *
     * @param n
     */
    public void setName(String n) {
        name = n;
    }

    /**
     * Set method for temperature
     *
     * @param t
     */
    public void setTemp(double t) {
        heatingTemp = t;
    }

    //people ArrayList methods
    /**
     * Add person to room
     *
     * @param p
     */
    public void addPerson(Person p) {
        people.add(p);
    }

    /**
     * Remove person from room
     *
     * @param p
     */
    public void removePerson(Person p) {
        for (int i = 0; i < people.size(); i++) {
            if (people.get(i).getName() == p.getName()) {
                people.remove(i);
            }
        }
    }

    /**
     * Get number of people in room
     *
     * @return people.size()
     */
    public int numberOfPeople() {
        return people.size();
    }

    //doors ArrayList methods
    /**
     * Add door to room
     *
     * @param d
     */
    public void addDoor(Door d) {
        doors.add(d);
    }

    /**
     * Remove door from room
     *
     * @param d
     */
    public void removeDoor(Door d) {
        for (int i = 0; i < doors.size(); i++) {
            if (doors.get(i).getID() == d.getID()) {
                doors.remove(i);
            }
        }
    }

    /**
     * Get number of doors on room
     *
     * @return doors.size()
     */
    public int numberOfDoors() {
        return doors.size();
    }

    //windows ArrayList methods
    /**
     * Add window to room
     *
     * @param w
     */
    public void addWindow(Window w) {
        windows.add(w);
    }

    /**
     * Remove window from room
     *
     * @param w
     */
    public void removeWindow(Window w) {
        for (int i = 0; i < windows.size(); i++) {
            if (windows.get(i).id == w.id) {
                windows.remove(i);
            }
        }
    }

    /**
     * Get number of windows in room
     *
     * @return windows.size()
     */
    public int numberOfWindows() {
        return windows.size();
    }

    public ArrayList<Door> getDoors() {
        return doors;
    }

    public ArrayList<Window> getWindows() {
        return windows;
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

}
