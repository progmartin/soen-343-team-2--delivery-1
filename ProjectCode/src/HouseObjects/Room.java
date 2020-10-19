package HouseObjects;

import java.util.ArrayList; // import ArrayList class

/**
 *
 * @author a_richard, d_ruiz-cigana
 */
public class Room {

    //name of room
    String name;
    //temperature of room
    double heatingTemp;

    //people currently in room
    ArrayList<Person> people = new ArrayList<>();
    //doors on room
    ArrayList<Door> doors = new ArrayList<>();
    //windows in room
    ArrayList<Window> windows = new ArrayList<>();

    //Default constructor
    /**
     * Constructor -- Default
     */
    public Room() {
        this.name = "Empty Room";
        this.heatingTemp = 21;
    }

    //constructor with only name of room
    /**
     * Constructor -- Room Name
     *
     * @param name
     */
    public Room(String name) {
        this.name = name;
        this.heatingTemp = 21;
    }

    //fully parametrized constructor
    /**
     * Constructor -- parametrized
     *
     * @param name
     * @param temp
     */
    public Room(String name, double temp) {
        this.name = name;
        this.heatingTemp = temp;
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
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set method for temperature
     *
     * @param temp
     */
    public void setTemp(double temp) {
        this.heatingTemp = temp;
    }

    //people ArrayList methods
    /**
     * Add person to room
     *
     * @param person
     */
    public void addPerson(Person person) {
        people.add(person);
    }

    /**
     * Remove person from room
     *
     * @param person
     */
    public void removePerson(Person person) {
        people.remove(person);
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
     * @param door
     */
    public void addDoor(Door door) {
        doors.add(door);
    }

    /**
     * Remove door from room
     *
     * @param door
     */
    public void removeDoor(Door door) {
        for (int i = 0; i < doors.size(); i++) {
            if (doors.get(i).getID() == door.getID()) {
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
     * @param window
     */
    public void addWindow(Window window) {
        windows.add(window);
    }

    /**
     * Remove window from room
     *
     * @param window
     */
    public void removeWindow(Window window) {
        for (int i = 0; i < windows.size(); i++) {
            if (windows.get(i).id == window.id) {
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

    /**
     * Get a list of doors in this Room.
     *
     * @return list of doors
     */
    public ArrayList<Door> getDoors() {
        return doors;
    }

    /**
     * Get a list of windows in this Room.
     *
     * @return list of windows
     */
    public ArrayList<Window> getWindows() {
        return windows;
    }

    /**
     * Get a list of people in this Room.
     *
     * @return list of people
     */
    public ArrayList<Person> getPeople() {
        return people;
    }

}
