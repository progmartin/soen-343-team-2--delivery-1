package simulation;

import java.util.ArrayList;
import HouseObjects.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

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
    private LocalDateTime simTime;

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
        this.simModules.addAll(Arrays.asList(new SHC_Module(),new SHP_Module()));
        this.simTime = LocalDateTime.now();
        for (Module mod : this.simModules) {
            mod.attachSimulation(this);
        }

    }

    /**
     * Notifies all modules that are listening for updates.
     */
    public void notifyAllModules() {
        for (Module mod : simModules) {
            mod.update();
        }
    }

    /**
     * Get the simulation time.
     *
     * @return the simulation time
     */
    public LocalDateTime getSimulationTime() {
        return this.simTime;
    }

    /**
     * Set the simulation date and time with the new datetime.
     *
     * @param newDateTime the new datetime
     */
    public void updateSimulationDateTime(LocalDateTime newDateTime) {
        this.simTime = newDateTime;
    }

    /**
     * Set the simulation date with the new date.
     *
     * @param newDate the new date
     */
    public void updateSimulationDate(LocalDate newDate) {
        this.simTime = this.simTime.withYear(newDate.getYear()).withMonth(newDate.getMonthValue()).withDayOfMonth(newDate.getDayOfMonth());
    }

    /**
     * Set the simulation time with the new time.
     *
     * @param newTime the new time
     */
    public void updateSimulationTime(LocalTime newTime) {
        this.simTime = this.simTime.withHour(newTime.getHour()).withMinute(newTime.getSecond()).withSecond(newTime.getSecond());
    }

    /**
     * Increments the simulation time by 1 second.
     */
    public void incrementSimulationTime() {
        this.simTime = this.simTime.plusSeconds(1);
    }

    /**
     * Returns the names of all the modules loaded in the simulation.
     *
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
     *
     * @param moduleName the module class to search for
     * @return an array of commands
     */
    public ArrayList<String> getModuleCommands(Class moduleName) {
        for (Module m : this.simModules) {
            if (m.getClass().equals(moduleName)) {
                return m.getCommands();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Returns the module of this simulation from the class type. If this
     * simulation does not contain a module of the given type, then null is
     * returned. <br/>
     * An example of how to get the SHC module is as follows:<br/>
     * SHC_Module module = (SHC_Module) getModuleOfType(SHC_Module.class);
     *
     * @param moduleName the module class to search for
     * @return one of the simulation's module, null if cannot be found
     */
    public Module getModuleOfType(Class moduleName) {
        for (Module m : this.simModules) {
            if (m.getClass() == moduleName) {
                return m;
            }
        }
        return null;
    }

    /**
     * Returns the module with the given name.
     *
     * @param moduleName the name of the module
     * @return a module from this simulation
     */
    public Module getModuleFromName(String moduleName) {
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

    /**
     * Returns a window with the given id.
     *
     * @param id the window id
     * @return the window with the id
     */
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
     * Returns an array list of windows in the room.
     *
     * @param roomName the name of the room to search
     * @return a list of windows
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
     * Returns an array of all windows in the simulation.
     *
     * @return a list of windows
     */
    public ArrayList<Window> getAllWindows() {
        ArrayList<Window> windows = new ArrayList<>();
        for (Room r : rooms) {
            windows.addAll(r.getWindows());
        }
        return windows;
    }

    /**
     * Returns a door with the given id.
     *
     * @param id the door id
     * @return the door with the id
     */
    public Door getDoor(int id) {
        for (Room r : this.rooms) {
            for (Door d : r.getDoors()) {
                if (d.getID() == id) {
                    return d;
                }
            }
        }
        return null;
    }

    /**
     * Returns an array list of doors in the room.
     *
     * @param roomName the name of the room to search
     * @return a list of doors
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
     * Returns an array of all doors in the simulation.
     *
     * @return a list of doors
     */
    public ArrayList<Door> getAllDoors() {
        ArrayList<Door> doors = new ArrayList<>();
        for (Room r : rooms) {
            doors.addAll(r.getDoors());
        }
        return doors;
    }

    /**
     * Returns a light with the given id.
     *
     * @param id the light id
     * @return the light with the id
     */
    public Light getLight(int id) {
        for (Room r : this.rooms) {
            for (Light l : r.getLights()) {
                if (l.getID() == id) {
                    return l;
                }
            }
        }
        return null;
    }

    /**
     * Returns an array list of lights in the room.
     *
     * @param roomName the name of the room to search
     * @return a list of lights
     */
    public ArrayList<Light> getLights(String roomName) {
        for (Room r : rooms) {
            if (r.getName().equals(roomName)) {
                return r.getLights();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Returns an array of all lights in the simulation.
     *
     * @return a list of lights
     */
    public ArrayList<Light> getAllLights() {
        ArrayList<Light> lights = new ArrayList<>();
        for (Room r : rooms) {
            lights.addAll(r.getLights());
        }
        return lights;
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
     * Gets all the Persons in the simulation.
     *
     * @return a list of people
     */
    public ArrayList<Person> getAllUsers() {
        ArrayList<Person> people = new ArrayList<>();
        for (Room r : rooms) {
            people.addAll(r.getPeople());
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
        notifyAllModules();
    }

    /**
     * Removes a Person from the simulation.
     *
     * @param user the name of the Person
     */
    public void removeUser(String user) {
        Person p = this.getUser(user);
        this.getUsersRoom(user).removePerson(p);
        notifyAllModules();
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
        notifyAllModules();
    }

}
