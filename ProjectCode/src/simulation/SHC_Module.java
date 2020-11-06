package simulation;

import HouseObjects.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author DRC, a_richard
 */
public class SHC_Module extends Module {
	
	/**
	 * Auto mode will turn on lights in a room automatically if someone is in a room, and turn them off if they leave a room.
	 * By default, auto mode is set to off.
	 */
	boolean autoMode = false;

    public SHC_Module() {
        super("SHC", new ArrayList<>(Arrays.asList("Open/Close Windows", "Lock/Unlock Doors", "Open/Close Garage", "Turn On/Off Lights")));
    }

    /**
     * Updates the simulation.
     * 
     * If auto mode is on, this will turn on lights in a room that has people in it.
     * It will also turn off lights in a room where there is no one.
     */
    @Override
    public void update() {
    	//Auto mode functionality
    	//turns lights on if people in room
    	//turns lights off if no one in room
    	if(autoMode){
    		ArrayList<Room> rooms = sim.getRooms();
    		for(Room room : rooms){
    			if(room.numberOfPeople()>0){
    				for(Light l : room.getLights()){
    					l.setIsOn(true);
    				}
    			}
    			else if(room.numberOfPeople()==0){
    				for(Light l : room.getLights()){
    					l.setIsOn(false);
    				}
    			}
    		}
    	}
    }
    
    /**
     * A method to return the status of autoMode
     * @return true if auto mode is on
     */
    public boolean getAutoMode(){
    	return autoMode;
    }
    
    /**
     * Method for turning auto mode on and off
     * @param autoMode new status of auto mode
     */
    public void setAutoMode(boolean autoMode){
    	this.autoMode = autoMode;
    }

    /**
     * A method for closing all of the windows in the house. Will not close
     * windows that are being blocked
     *
     * @return ArrayList of windows that could not be closed
     */
    public ArrayList<Window> closeAllWindows() {
        ArrayList<Room> rooms = sim.getRooms();
        ArrayList<Window> unclosed = new ArrayList<Window>();
        for (Room room : rooms) {
            for (Window w : room.getWindows()) {
                if (!w.getBlocked()) {
                    w.setOpen(false);
                } else {
                    unclosed.add(w);
                }
            }
        }
        return unclosed;
    }

    /**
     * A method for opening all of the windows in the house. Will not open
     * windows that are being blocked
     *
     * @return ArrayList of windows that could not be opened
     */
    public ArrayList<Window> openAllWindows() {
        ArrayList<Room> rooms = sim.getRooms();
        ArrayList<Window> unopened = new ArrayList<Window>();
        for (Room room : rooms) {

            for (Window w : room.getWindows()) {
                if (!w.getBlocked()) {
                    w.setOpen(true);
                } else {
                    unopened.add(w);
                }
            }
        }
        return unopened;
    }

    /**
     * A method for closing a specific window in the house Will not close the
     * window if it is being blocked
     *
     * @param room the room that the window is in
     * @param window the window you want to close
     * @return true if successful
     */
<<<<<<< HEAD
    public boolean closeThisWindow(String room, int window){
    	if(sim.getRoom(room).getWindows().get(window-1).getBlocked()){
    		return false;
    	} else {
    		sim.getRoom(room).getWindows().get(window-1).setOpen(false);
    		return true;
    	}
=======
    public boolean closeThisWindow(int room, int window) {
        ArrayList<Room> rooms = sim.getRooms();
        if (rooms.get(room).getWindows().get(window - 1).getBlocked()) {
            return false;
        } else {
            rooms.get(room).getWindows().get(window - 1).setOpen(false);
            return true;
        }
>>>>>>> b5587ebbda35b18e7b72df75334534fdc707f08a
    }

    /**
     * A method for opening a specific window in the house. Will not open a
     * window that is being blocked.
     *
     * @param room the room that the window is in
     * @param window the window you want to close
     * @return true if successful
     */
<<<<<<< HEAD
    public boolean openThisWindow(String room, int window){
    	if(sim.getRoom(room).getWindows().get(window-1).getBlocked()){
    		return false;
    	} else {
    		sim.getRoom(room).getWindows().get(window-1).setOpen(true);
    		return true;
    	}
=======
    public boolean openThisWindow(int room, int window) {
        ArrayList<Room> rooms = sim.getRooms();
        if (rooms.get(room).getWindows().get(window - 1).getBlocked()) {
            return false;
        } else {
            rooms.get(room).getWindows().get(window - 1).setOpen(true);
            return true;
        }
>>>>>>> b5587ebbda35b18e7b72df75334534fdc707f08a
    }

    /**
     * A method for locking all doors in the house. Will not lock any doors that
     * were already locked
     *
     * @return ArrayList of doors that were already locked
     */
    public ArrayList<Door> lockAllDoors() {
        ArrayList<Room> rooms = sim.getRooms();

        ArrayList<Door> alreadyLocked = new ArrayList<Door>();
        for (Room room : rooms) {
            for (Door d : room.getDoors()) {
                if (!d.getLocked()) {
                    d.setLocked(true);
                } else {
                    alreadyLocked.add(d);
                }
            }
        }
        return alreadyLocked;
    }

    /**
     * A method for unlocking all doors in the house. Will not unlock any doors
     * that were already unlocked.
     *
     * @return ArrayList of doors that were already unlocked
     */
    public ArrayList<Door> unlockAllDoors() {
        ArrayList<Room> rooms = sim.getRooms();

        ArrayList<Door> alreadyUnlocked = new ArrayList<Door>();
        for (Room room : rooms) {
            for (Door d : room.getDoors()) {
                if (d.getLocked()) {
                    d.setLocked(false);
                } else {
                    alreadyUnlocked.add(d);
                }
            }
        }
        return alreadyUnlocked;
    }

    /**
     * Method for locking a specific door in the house. Will not lock the door
     * if it is already locked.
     *
     * @param room the room that the door is in
     * @param door the specific door you want to lock
     * @return true if successful
     */
<<<<<<< HEAD
    public boolean lockThisDoor(String room, int door){
    	if(sim.getRoom(room).getDoors().get(door-1).getLocked()){
    		return false;
    		//if door already locked
    	} else {
    		sim.getRoom(room).getDoors().get(door-1).setLocked(true);
    		return true;
    	}
=======
    public boolean lockThisDoor(int room, int door) {
        ArrayList<Room> rooms = sim.getRooms();
        if (rooms.get(room).getDoors().get(door - 1).getLocked()) {
            return false;
            //if door already locked
        } else {
            rooms.get(room).getDoors().get(door - 1).setLocked(true);
            return true;
        }
>>>>>>> b5587ebbda35b18e7b72df75334534fdc707f08a
    }

    /**
     * Method for unlocking a specific door in the house. Will not lock the
     * unlock the door if it is already unlocked
     *
     * @param room the room the door is in
     * @param door the door you want to close
     * @return true if successful
     */
<<<<<<< HEAD
    public boolean unlockThisDoor(String room, int door){
    	if(sim.getRoom(room).getDoors().get(door-1).getLocked()){
    		return false;
    		//if door already locked
    	} else {
    		sim.getRoom(room).getDoors().get(door-1).setLocked(true);
    		return true;
    	}
=======
    public boolean unlockThisDoor(int room, int door) {
        ArrayList<Room> rooms = sim.getRooms();
        if (rooms.get(room).getDoors().get(door - 1).getLocked()) {
            return false;
            //if door already locked
        } else {
            rooms.get(room).getDoors().get(door - 1).setLocked(true);
            return true;
        }
>>>>>>> b5587ebbda35b18e7b72df75334534fdc707f08a
    }

    /**
     * A method for opening the garage door. Will not open if the garage door is
     * locked.
     *
     * @param garage the room where the door is located
     * @param garageDoor the door to be opened
     * @return true if successful
     */
<<<<<<< HEAD
    public boolean openGarage(String garage, int garageDoor){
    	if(sim.getRoom(garage).getDoors().get(garageDoor-1).getLocked()){
    		return false;
    		//if door locked
    	} else {
    		sim.getRoom(garage).getDoors().get(garageDoor-1).setOpen(true);
    		return true;
    	}
=======
    public boolean openGarage(int garage, int garageDoor) {
        ArrayList<Room> rooms = sim.getRooms();
        if (rooms.get(garage).getDoors().get(garageDoor - 1).getLocked()) {
            return false;
            //if door locked
        } else {
            rooms.get(garage).getDoors().get(garageDoor - 1).setOpen(true);
            return true;
        }
>>>>>>> b5587ebbda35b18e7b72df75334534fdc707f08a
    }

    /**
     * A method for closing the garage door. Will not close the door if it is
     * locked.
     *
     * @param garage the room where the door is located
     * @param garageDoor the door to be opened
     * @return true if successful
     */
<<<<<<< HEAD
    public boolean closeGarage(String garage, int garageDoor){
    	if(sim.getRoom(garage).getDoors().get(garageDoor-1).getLocked()){
    		return false;
    		//if door locked
    	} else {
    		sim.getRoom(garage).getDoors().get(garageDoor-1).setOpen(false);
    		return true;
    	}
=======
    public boolean closeGarage(int garage, int garageDoor) {
        ArrayList<Room> rooms = sim.getRooms();
        if (rooms.get(garage).getDoors().get(garageDoor - 1).getLocked()) {
            return false;
            //if door locked
        } else {
            rooms.get(garage).getDoors().get(garageDoor - 1).setOpen(false);
            return true;
        }
>>>>>>> b5587ebbda35b18e7b72df75334534fdc707f08a
    }

    /**
     * A method for turning on a specific light in the house. Will not turn on
     * the light if it is already on.
     *
     * @param room the room where the light is located
     * @param light the light to be turned on
     * @return true if successful
     */
<<<<<<< HEAD
    public boolean turnOnLight(String room, int light){
    	if(sim.getRoom(room).getLights().get(light-1).getIsOn()){
    		return false;
    		//if light already on
    	} else {
    		sim.getRoom(room).getLights().get(light-1).setIsOn(true);
    		return true;
    	}
=======
    public boolean turnOnLight(int room, int light) {
        ArrayList<Room> rooms = sim.getRooms();
        if (rooms.get(room).getLights().get(light - 1).getIsOn()) {
            return false;
            //if light already on
        } else {
            rooms.get(room).getLights().get(light - 1).setIsOn(true);
            return true;
        }
>>>>>>> b5587ebbda35b18e7b72df75334534fdc707f08a
    }

    /**
     * A method for turning off a specific light in the house. Will not turn off
     * the light if it is already off.
     *
     * @param room the room where the light is located
     * @param light the light to be turned off
     * @return true if successful
     */
<<<<<<< HEAD
    public boolean turnOffLight(String room, int light){
    	if(!sim.getRoom(room).getLights().get(light-1).getIsOn()){
    		return false;
    		//if light already off
    	} else {
    		sim.getRoom(room).getLights().get(light-1).setIsOn(false);
    		return true;
    	}
=======
    public boolean turnOffLight(int room, int light) {
        ArrayList<Room> rooms = sim.getRooms();
        if (!rooms.get(room).getLights().get(light - 1).getIsOn()) {
            return false;
            //if light already off
        } else {
            rooms.get(room).getLights().get(light - 1).setIsOn(false);
            return true;
        }
>>>>>>> b5587ebbda35b18e7b72df75334534fdc707f08a
    }

}
