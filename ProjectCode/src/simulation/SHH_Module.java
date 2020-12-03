package simulation;

import java.util.ArrayList;

import HouseObjects.Room;
import java.time.LocalTime;
import java.util.Arrays;

/**
 * 
 * @author a_khalil, a_richard
 *
 */
public class SHH_Module extends Module {

    //the number or periods the user currently chooses to have, either 1, 2, or 3
    private int noPeriods;
    //boolean representing whether or not away mode in the SHP is on or off
    private boolean awayMode;
    //the simulation
    private ArrayList<Zone> zones;
    private ArrayList<Room> overriddenRooms;

    public SHH_Module() {
        super("SHH", new ArrayList<>(Arrays.asList("Manage Zones", "Manage Periods", "Change Temperatures")));
        this.noPeriods = 1;
        this.zones = new ArrayList<>();
        this.overriddenRooms = new ArrayList<>();
        this.awayMode = false;
    }

    @Override
    public void attachSimulation(Simulation simulation) {
        super.attachSimulation(simulation);
        Zone initZone = new Zone();
        initZone.setTemps(22);
        for (Room room : sim.getRooms()) {
            initZone.addRoom(room);
        }
        zones.add(initZone);
    }
    
    // returns target temp for zone
    public double getZoneTemp(Zone zone, int periodIndex){
        return zone.getTemp(periodIndex);
    }
    
    // sets target temp for zone
    public void setZoneTemp(Zone zone, int periodIndex, double temp){
        zone.setTemp(temp, periodIndex);
    }
    
    // removes room from old zone, then adds to new zone
    public void changeZone(Room room, Zone zone) {
        // remove room from current zone
    	for(Zone z : zones){
    		for(Room r : z.getRooms()){
    			if(r.equals(room)){
    				z.removeRoom(r);
    				break;
    			}
    		}
    	}
        // add room to new zone
    	zone.addRoom(room);
    }

    // finds zone by name
    public Zone getZone(String zoneName) {
    	for(Zone z : zones){
    		if(z.getName().equals(zoneName)){
    			return z;
    		}
    	}
    	//returns default if could not find
    	return zones.get(0);
    }

    // returns list of names of all zones
    public ArrayList<String> getZoneNames() {
        ArrayList<String> zoneNames = new ArrayList<>();
        for(Zone z: zones){
        	zoneNames.add(z.getName());
        }
    	return zoneNames;
    }

    ////---Need start and end time attributes?
    
    // TODO
    public String getPeriodStartTime(int periodIndex) {
        // String.matches("^\\d\\d:\\d\\d:\\d\\d$") // "HH:MM:SS"
        return "13:23:45";
    }

    // TODO
    public String getPeriodEndTime(int periodIndex) {
        // String.matches("^\\d\\d:\\d\\d:\\d\\d$") // "HH:MM:SS"
        return "13:23:45";
    }

    //TODO set start and end times
    public void addPeriod() {
        // increaseNumPeriod
        this.noPeriods++;
        // change time start and end for each period
    }

    // TODO set start and end times
    public void removePeriod() {
        // decreases num period
        this.noPeriods--;
        // change time start and end for each period
    }
    
    //TODO set start and end times
    public void setNoPeriods(int noPeriods){
    	this.noPeriods = noPeriods;
    }
    
    // returns names of over-ridden rooms
    public ArrayList<String> getOverriddenRooms(){
        ArrayList<String> roomNames = new ArrayList<>();
        for (Room r : overriddenRooms) {
            roomNames.add(r.getName());
        }
        return roomNames;
    }
    
    // adds room to list of overridden rooms
    public void addOverriddenRoom(String room){
        overriddenRooms.add(getRoom(room));
    }
    
    // removes room from list of overridden rooms
    public void removeOverridenRoom(String room){
        overriddenRooms.remove(getRoom(room));
    }
    

    public int getNoPeriods() {
        return this.noPeriods;
    }

    public Room getRoom(String name) {
        return sim.getRoom(name);
    }

    public double getRoomTemp(String name) {
        return sim.getRoom(name).getTemp();
    }

    public void setRoomTemp(String name, double temp) {
        Room r = sim.getRoom(name);
        r.setTemp(temp);
    }

    @Override
    public boolean update() {
        return false;
    }

    public class Zone {
        //the array of temperatures that are set for each period, so temps[0] will be what the zone is set to all day if there is one period, 
        //the first half of the day if there are two periods, and the the first third of the day if there are three periods

    	private String name;
        private double[] temps;
        //the list of rooms that are in this zone
        private ArrayList<Room> rooms;

        public Zone() {
            this.rooms = new ArrayList<Room>();
            this.temps = new double[3];
        }

        public Zone(double x, double y, double z) {
            this.setTemps(x, y, z);
            this.rooms = new ArrayList<Room>();
        }
        
        public void setName(String name){
        	this.name=name;
        }

        public void setTemps(double x) {
            this.temps[0] = x;

        }

        public void setTemps(double x, double y) {
            this.temps[0] = x;
            this.temps[1] = y;
        }

        public void setTemps(double x, double y, double z) {
            this.temps[0] = x;
            this.temps[1] = y;
            this.temps[2] = z;

        }

        /**
         * sets value of some value in temps array, given an index
         *
         * @param x temperature
         * @param i index
         */
        public void setTemp(double x, int i) {
            this.temps[i] = x;
        }
        
        public String getName(){
        	return name;
        }

        public double getTemp(int i) {
            return temps[i];
        }

        public double[] getTemps() {
            return this.temps;
        }

        public Room getRoom(int i) {
            return rooms.get(i);
        }

        public ArrayList<Room> getRooms() {
            return this.rooms;
        }

        public void setRooms(ArrayList<Room> r) {
            this.rooms = r;
        }

        public void addRoom(Room r) {
            this.rooms.add(r);
        }
        
        public void removeRoom(Room r){
        	this.rooms.remove(r);
        }

        public double getRoomTemp(int i) {
            return this.getRoom(i).getTemp();
        }

        /**
         * sets temperature of room in rooms ArrayList given index and
         * temperature
         *
         * @param i index
         * @param temp temperature
         */
        public void setRoomTemp(int i, double temp) {
            Room r = getRoom(i);
            r.setTemp(temp);
        }

        /**
         * sets all rooms in this zone to a given temperature in temps array,
         * given an index in temps array
         *
         * @param i index
         */
        public void setRoomsTemp(int i) {
            double temp = this.temps[i];
            for (int x = 0; x < rooms.size(); x++) {
                setRoomTemp(x, temp);
            }

        }

    }
    //End of inner class Zone
}
//End of class SHH
