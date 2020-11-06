package HouseObjects;

import java.util.ArrayList;

/**
 *
 * @author a_richard, d_ruiz-cigana
 */
public class Room {

    /**
     * Name of the room.
     */
    String name;
    /**
     * Temperature of the room.
     */
    double heatingTemp;

    /**
     * People currently in the room.
     */
    ArrayList<Person> people = new ArrayList<>();

    /**
     * Doors in the room.
     */
    ArrayList<Door> doors = new ArrayList<>();

    /**
     * Windows in the room.
     */
    ArrayList<Window> windows = new ArrayList<>();

    /**
     * Lights in the room.
     */
    ArrayList<Light> lights = new ArrayList<>();

    /**
     * Default constructor.
     */
    public Room() {
        this.name = "Empty Room";
        this.heatingTemp = 21;
    }

    /**
     * Constructor with room name.
     *
     * @param name the name of the room
     */
    public Room(String name) {
        this.name = name;
        this.heatingTemp = 21;
    }

    /**
     * Parameterized constructor.
     *
     * @param name
     * @param temp
     */
    public Room(String name, double temp) {
        this.name = name;
        this.heatingTemp = temp;
    }

    /**
     * Get method for name.
     *
     * @return the name of the room
     */
    public String getName() {
        return name;
    }

    /**
     * Get method for temperature.
     *
     * @return the temperature of the room
     */
    public double getTemp() {
        return heatingTemp;
    }

    /**
     * Set method for name.
     *
     * @param name the name of the room
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set method for temperature.
     *
     * @param temp the temperature of the room
     */
    public void setTemp(double temp) {
        this.heatingTemp = temp;
    }

    /**
     * Add a Person to the room.
     *
     * @param person the person to be added
     */
    public void addPerson(Person person) {
        people.add(person);
    }

    /**
     * Remove a Person from the room if they exist.
     *
     * @param person the person to be removed.
     */
    public void removePerson(Person person) {
        people.remove(person);
    }

    /**
     * Get the number of people in the room.
     *
     * @return the number of people in the room
     */
    public int numberOfPeople() {
        return people.size();
    }

    /**
     * Add a Door to the room.
     *
     * @param door the door to be added
     */
    public void addDoor(Door door) {
        doors.add(door);
    }

    /**
     * Remove a Door from the room if it exists.
     *
     * @param door the door to be removed
     */
    public void removeDoor(Door door) {
        for (int i = 1; i <= doors.size(); i++) {
            if (doors.get(i).getID() == door.getID()) {
                doors.remove(i);
            }
        }
    }

    /**
     * Get the number of doors in the room.
     *
     * @return the number of doors in the room
     */
    public int numberOfDoors() {
        return doors.size();
    }

    /**
     * Add a Window to the room.
     *
     * @param window the window to be added
     */
    public void addWindow(Window window) {
        windows.add(window);
    }

    /**
     * Remove a Window from the room if it exists.
     *
     * @param window the window to be removed
     */
    public void removeWindow(Window window) {
        for (int i = 1; i <= windows.size(); i++) {
            if (windows.get(i).id == window.id) {
                windows.remove(i);
            }
        }
    }

    /**
     * Get the number of Windows in the room.
     *
     * @return the number of windows in the room
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

    /**
     * Adds a light to the light ArrayList
     *
     * @param light the light to be added
     */
    public void addLight(Light light) {
        lights.add(light);
    }

    /**
     * Remove a light from the light ArrayList
     *
     * @param light the light to be removed
     */
    public void removeLight(Light light) {
        for (int i = 1; i <= lights.size(); i++) {

            if (lights.get(i).id == light.id) {
                windows.remove(i);
            }
        }
    }

    /**
     * A method for returning the number of lights in a room
     *
     * @return number of lights in the given room.
     */
    public int numberOfLights() {
        return lights.size();
    }

    /**
     * A method for getting the ArrayList of lights in the room
     *
     * @return ArrayList of lights
     */
    public ArrayList<Light> getLights() {
        return lights;
    }

    /**
     * Prints the given room's name
     */
    public String toString() {
        return name;
    }

}
