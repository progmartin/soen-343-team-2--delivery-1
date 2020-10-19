/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import HouseObjects.Room;
import java.util.ArrayList;

/**
 *
 * @author DRC
 */
public class Simulation {
    
    private ArrayList<Room> rooms;

    public Simulation() {
        setRooms(new ArrayList<>());
    }
    
    
    
    public String getUserLocation(String user){
        return "location";
    }
    
    public void setUserLocation(String user, String location){
        
    }
    
    public ArrayList getDoors(){
        for (Room r : getRooms()){
            //r.getDoors();
        }
        return new ArrayList<>();
    }
    
    
    public ArrayList<Room> getRooms(){
        return rooms;
    }



	public void setRooms(ArrayList<Room> rooms) {
		this.rooms = rooms;
	}
}
