package simulation;

import HouseObjects.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author DRC, a_richard
 */
public class SHC_Module extends Module {

    public SHC_Module() {
        super("SHC", new ArrayList<>());
        this.commands.addAll(Arrays.asList("Open/Close Windows", "Lock/Unlock Doors", "Open/Close Garage", "Turn On/Off Lights"));
    }

    @Override
    public void update() {
    }

    public ArrayList<Window> closeAllWindows() {
    	ArrayList<Room> rooms = sim.getRooms();
        ArrayList<Window> unclosed = new ArrayList<Window>();
    	for (Room room : rooms) {
            for (Window w : room.getWindows()) {
                if(!w.getBlocked()){
                	w.setOpen(false);
                } else {
                	unclosed.add(w);
                }
            }
        }
        return unclosed;
    }
    
    public ArrayList<Window> openAllWindows(){
    	ArrayList<Room> rooms = sim.getRooms();
    	ArrayList<Window> unopened = new ArrayList<Window>();
    	for (Room room : rooms) {
            for (Window w : room.getWindows()) {
                if(!w.getBlocked()){
                	w.setOpen(true);
                } else {
                	unopened.add(w);
                }
            }
        }
        return unopened;
    }

    public boolean closeThisWindow(int room, int window){
    	ArrayList<Room> rooms = sim.getRooms();
    	if(rooms.get(room).getWindows().get(window-1).getBlocked()){
    		return false;
    	} else {
    		rooms.get(room).getWindows().get(window-1).setOpen(false);
    		return true;
    	}
    }

    public boolean openThisWindow(int room, int window){
    	ArrayList<Room> rooms = sim.getRooms();
    	if(rooms.get(room).getWindows().get(window-1).getBlocked()){
    		return false;
    	} else {
    		rooms.get(room).getWindows().get(window-1).setOpen(true);
    		return true;
    	}
    }

    //This method will return array of doors already locked
    public ArrayList<Door> lockAllDoors(){
    	ArrayList<Room> rooms = sim.getRooms();
        ArrayList<Door> alreadyLocked = new ArrayList<Door>();
    	for (Room room : rooms) {
            for (Door d : room.getDoors()) {
                if(!d.getLocked()){
                	d.setLocked(true);
                } else {
                	alreadyLocked.add(d);
                }
            }
        }
        return alreadyLocked;
    }

    //This method will return array of doors already unlocked
    public ArrayList<Door> unlockAllDoors(){
    	ArrayList<Room> rooms = sim.getRooms();
        ArrayList<Door> alreadyUnlocked = new ArrayList<Door>();
    	for (Room room : rooms) {
            for (Door d : room.getDoors()) {
                if(d.getLocked()){
                	d.setLocked(false);
                } else {
                	alreadyUnlocked.add(d);
                }
            }
        }
        return alreadyUnlocked;
    }

    public boolean lockThisDoor(int room, int door){
    	ArrayList<Room> rooms = sim.getRooms();
    	if(rooms.get(room).getDoors().get(door-1).getLocked()){
    		return false;
    		//if door already locked
    	} else {
    		rooms.get(room).getDoors().get(door-1).setLocked(true);
    		return true;
    	}
    }

    public boolean unlockThisDoor(int room, int door){
    	ArrayList<Room> rooms = sim.getRooms();
    	if(rooms.get(room).getDoors().get(door-1).getLocked()){
    		return false;
    		//if door already locked
    	} else {
    		rooms.get(room).getDoors().get(door-1).setLocked(true);
    		return true;
    	}
    }

    public boolean openGarage(int garage, int garageDoor){
    	ArrayList<Room> rooms = sim.getRooms();
    	if(rooms.get(garage).getDoors().get(garageDoor-1).getLocked()){
    		return false;
    		//if door locked
    	} else {
    		rooms.get(garage).getDoors().get(garageDoor-1).setOpen(true);
    		return true;
    	}
    }

    public boolean closeGarage(int garage, int garageDoor){
    	ArrayList<Room> rooms = sim.getRooms();
    	if(rooms.get(garage).getDoors().get(garageDoor-1).getLocked()){
    		return false;
    		//if door locked
    	} else {
    		rooms.get(garage).getDoors().get(garageDoor-1).setOpen(false);
    		return true;
    	}
    }

    public boolean turnOnLight(int room, int light){
    	ArrayList<Room> rooms = sim.getRooms();
    	if(rooms.get(room).getLights().get(light-1).getIsOn()){
    		return false;
    		//if light already on
    	} else {
    		rooms.get(room).getLights().get(light-1).setIsOn(true);
    		return true;
    	}
    }

    public boolean turnOffLight(int room, int light){
    	ArrayList<Room> rooms = sim.getRooms();
    	if(!rooms.get(room).getLights().get(light-1).getIsOn()){
    		return false;
    		//if light already off
    	} else {
    		rooms.get(room).getLights().get(light-1).setIsOn(false);
    		return true;
    	}
    }

}
