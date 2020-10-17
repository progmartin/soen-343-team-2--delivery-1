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
    
    ArrayList<Room> rooms;

    public Simulation() {
        rooms = new ArrayList<>();
    }
    
    
    
    public String getUserLocation(String user){
        return "location";
    }
    
    public void setUserLocation(String user, String location){
        
    }
    
    public ArrayList getDoors(){
        for (Room r : rooms){
            //r.getDoors();
        }
        return new ArrayList<>();
    }
    
    
    public ArrayList<Room> getRooms(){
        return rooms;
    }
}
