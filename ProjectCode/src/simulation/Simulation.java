package simulation;

import java.util.ArrayList;
import HouseObjects.*;

/**
 *
 * @author d_ruiz-cigana
 */
public class Simulation {

    /**
     * List of rooms in the simulation.
     */
    private ArrayList<Room> rooms;
    private ArrayList<Module> simModules;

    /**
     * Default constructor. Creates a simulation with no rooms.
     */
    public Simulation() {
        this(new ArrayList<>());
    }

    /**
     * Constructor with arguments. Creates a simulation with the list of rooms.
     *
     * @param rooms Rooms to be added to the simulation
     */
    public Simulation(ArrayList<Room> rooms) {
        this.rooms = rooms;
        this.simModules = new ArrayList<>();
        this.simModules.add(new SHC_Module());
        this.simModules.add(new SHP_Module());
    }

    /**
     * Returns the names of all the modules loaded in the simulation.
     * @return the simulation's module's names
     */
    public ArrayList<String> getModuleNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Module m : this.simModules) {
            names.add(m.getName());
        }
        return names;
    }

    /**
     * Get the commands that a module in the simulation can make.
     * @param moduleName the module class to search for
     * @return an array of commands
     */
    public ArrayList<String> getModuleCommands(Class moduleName) {
        for (Module m : this.simModules) {
            if (m.getClass() == moduleName) {
                return m.getCommands();
            }
        }
        return new ArrayList<>();
    }
    
    /**
     * Returns the module of this simulation from the class type. If this simulation
     * does not contain a module of the given type, then null is returned. <br/>
     * An example of how to get the SHC module is as follows:<br/>
     * SHC_Module module = (SHC_Module) getModuleOfType(SHC_Module.class);
     *
     * @param moduleName the module class to search for
     * @return one of the simulation's module, null if cannot be found
     */
    private Module getModuleOfType(Class moduleName) {
        for (Module m : this.simModules) {
            if (m.getClass() == moduleName) {
                return m;
            }
        }
        return null;
    }

    /**
     * Returns the module with the given name.
     * @param moduleName the name of the module
     * @return a module from this simulation
     */
    public Module getModuleFromName(String moduleName){
        for (Module m : this.simModules) {
            if (m.getName().equals(moduleName)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Adds a room to the simulation.
     *
     * @param room the room to be added
     */
    public void addRoom(Room room) {
        this.rooms.add(room);
    }

    /**
     * Adds a list of rooms to the simulation.
     *
     * @param rooms the list of rooms to add
     */
    public void addRooms(ArrayList<Room> rooms) {
        this.rooms.addAll(rooms);
    }

    /**
     * Adds one or more rooms to the simulation.
     *
     * @param rooms the rooms that will be added to the simulation
     */
    public void addRooms(Room... rooms) {
        for (Room r : rooms) {
            this.rooms.add(r);
        }
    }

    /**
     * Returns an array list of rooms in this simulation.
     *
     * @return a list of rooms
     */
    public ArrayList<Room> getRooms() {
        return this.rooms;
    }

    /**
     * Returns the String names of all the rooms in this simulation.
     *
     * @return a list of room names
     */
    public ArrayList<String> getRoomNames() {
        ArrayList<String> roomNames = new ArrayList<>();
        for (Room r : rooms) {
            roomNames.add(r.getName());
        }
        return roomNames;
    }

    /**
     * Gets the Room object in this simulation with the name.
     *
     * @param name the room name to search for
     * @return the Room object with the name
     */
    public Room getRoom(String name) {
        for (Room r : this.rooms) {
            if (r.getName().equals(name)) {
                return r;
            }
        }
        return null;
    }

    public Window getWindow(int id) {
        for (Room r : this.rooms) {
            for (Window w : r.getWindows()) {
                if (w.getID() == id) {
                    return w;
                }
            }
        }
        return null;
    }
    
    
    /**
     * Returns an array list of doors in the room.
     *
     * @param roomName the name of the room to search
     * @return a list of door names
     */
    public ArrayList<Door> getDoors(String roomName) {
        for (Room r : rooms) {
            if (r.getName().equals(roomName)) {
                return r.getDoors();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Returns Returns an array list of windows in the room.
     *
     * @param roomName the name of the room to search
     * @return a list of room names
     */
    public ArrayList<Window> getWindows(String roomName) {
        for (Room r : rooms) {
            if (r.getName().equals(roomName)) {
                return r.getWindows();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Gets the Room object that holds the Person object with this name.
     *
     * @param name the name of the Person
     * @return the Room object containing the Person
     */
    private Room getUsersRoom(String name) {
        for (Room r : this.rooms) {
            for (Person p : r.getPeople()) {
                if (p.getName().equals(name)) {
                    return r;
                }
            }
        }
        return null;
    }

    /**
     * Gets the Person object from their name attribute.
     *
     * @param name the name of the Person
     * @return the Person object having the name
     */
    public Person getUser(String name) {
        for (Room r : this.rooms) {
            for (Person p : r.getPeople()) {
                if (p.getName().equals(name)) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * Gets the String name of the room that a Person is in.
     *
     * @param name the name of the Person
     * @return the room name
     */
    public String getUserLocation(String name) {
        for (Room r : this.rooms) {
            for (Person p : r.getPeople()) {
                if (p.getName().equals(name)) {
                    return r.getName();
                }
            }
        }
        return null;
    }

    /**
     * Gets the names of all the Persons in the simulation.
     *
     * @return a list containing names
     */
    public ArrayList<String> getAllUserNames() {
        ArrayList<String> people = new ArrayList<>();
        for (Room r : rooms) {
            for (Person p : r.getPeople()) {
                people.add(p.getName());
            }
        }
        return people;
    }

    /**
     * Adds a Person to the simulation with the following attributes.
     *
     * @param name the name of the Person
     * @param isAdmin if the Person is an administrator
     * @param userType the accessibility of the Person
     * @param room the room name the Person is in
     */
    public void addNewUser(String name, boolean isAdmin, Person.UserTypes userType, String room) {
        Room r = this.getRoom(room);
        r.addPerson(new Person(name, isAdmin, userType));
    }

    /**
     * Removes a Person from the simulation.
     *
     * @param user the name of the Person
     */
    public void removeUser(String user) {
        Person p = this.getUser(user);
        this.getUsersRoom(user).removePerson(p);
    }

    /**
     * Updates the user info for the following parameters. If the user does not
     * exist, creates a person with these attributes
     *
     * @param name The name of the Person
     * @param isAdmin if the Person is an administrator
     * @param userType the accessibility of the Person
     * @param room the room name the Person is in
     */
    public void updateUser(String name, boolean isAdmin, Person.UserTypes userType, String room) {
        Person p = this.getUser(name);
        if (p == null) {
            addNewUser(name, isAdmin, userType, room);
        } else {
            p.setAdmin(isAdmin);
            p.setUserType(userType);
            this.updateUserLocation(name, room);
        }
    }

    /**
     * Set the Person's administration rights.
     *
     * @param name the name of the Person
     * @param isAdmin if the Person is an administrator
     */
    public void setUserAdmin(String name, boolean isAdmin) {
        Person person = this.getUser(name);
        person.setAdmin(isAdmin);
    }

    /**
     * Set the accessibility of the Person.
     *
     * @param name the name of the Person
     * @param userType the accessibility of the Person
     */
    public void setUserType(String name, Person.UserTypes userType) {
        Person person = this.getUser(name);
        person.setUserType(userType);
    }

    /**
     * Update the room the Person is located in. Removes the person from the
     * previous room and inserts them into the new room.
     *
     * @param name the name of the Person
     * @param location the name of the room
     */
    public void updateUserLocation(String name, String location) {
        Room original = this.getRoom(this.getUserLocation(name));
        Room destination = this.getRoom(location);
        Person p = this.getUser(name);
        original.removePerson(p);
        destination.addPerson(p);
    }


}
