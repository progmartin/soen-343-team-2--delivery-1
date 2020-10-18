/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import HouseObjects.*;
import java.util.ArrayList;

/**
 *
 * @author DRC
 */
public class Simulation {

    private ArrayList<Room> rooms;

    public Simulation() {
        rooms = new ArrayList<>();

    }

    public Simulation(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public Room findRoom(String name) {
        for (Room r : this.rooms) {
            if (r.getName().equals(name)) {
                return r;
            }
        }
        return null;
    }
    
    private Room getUsersRoom(String user){
        for (Room r : this.rooms){
            for (Person p : r.getPeople()){
                if (p.getName().equals(user)){
                    return r;
                }
            }
        }
        return null;
    }
    
    public Person getUser(String user){
        for (Room r : this.rooms){
            for (Person p : r.getPeople()){
                if (p.getName().equals(user)){
                    return p;
                }
            }
        }
        return null;
    }

    public void addNewUser(String name, boolean isAdmin, Person.UserTypes userType, String room) {
        Room r = this.findRoom(room);
        r.addPerson(new Person(name, isAdmin, userType));

    }
    
    /**
     * Updates the user info for the following parameters.
     * If the user does not exist, creates a person with these attributes
     * @param name The user's name
     * @param isAdmin
     * @param userType
     * @param room 
     */
    public void updateUser(String name, boolean isAdmin, Person.UserTypes userType, String room){
        Person p = this.getUser(name);
        if (p == null){
            addNewUser(name, isAdmin, userType, room);
        }else{
            p.setAdmin(isAdmin);
            p.setUserType(userType);
            this.changeUserLocation(name, room);
        }
    }

    public void changeUserLocation(String user, String location) {
        Room original = this.findRoom(this.getUserLocation(user));
        Room destination = this.findRoom(location);
        original.removePerson(this.getUser(user));
        destination.addPerson(this.getUser(user));
    }

    public void changeUserAdmin(String user, boolean isAdmin) {
        Person person = this.getUser(user);
        person.setAdmin(isAdmin);
    }

    public ArrayList<Door> getDoors(String roomName) {
        for (Room r : rooms) {
            if (r.getName().equals(roomName)) {
                return r.getDoors();
            }
        }
        return new ArrayList<>();
    }

    public ArrayList<Window> getWindows(String roomName) {
        for (Room r : rooms) {
            if (r.getName().equals(roomName)) {
                return r.getWindows();
            }
        }
        return new ArrayList<>();
    }

    public ArrayList<String> getRoomNames() {
        ArrayList<String> roomNames = new ArrayList<>();
        for (Room r : rooms) {
            roomNames.add(r.getName());
        }
        return roomNames;
    }

    public ArrayList<Room> getRooms() {
        return this.rooms;
    } 

    public String getUserLocation(String user) {
        for (Room r : this.rooms) {
            for (Person p : r.getPeople()) {
                if (p.getName().equals(user)) {
                    return r.getName();
                }
            }
        }
        return null;
    }
    
    public ArrayList<String> getAllUserNames(){
        ArrayList<String> people = new ArrayList<>();
        for (Room r : rooms) {
            for (Person p : r.getPeople()){
                people.add(p.getName());
            }
        }
        return people;
    }
}
