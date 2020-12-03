package simulation;

import java.util.ArrayList;

import HouseObjects.Room;
import HouseObjects.Window;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 
 * @author a_khalil, a_richard, d_ruiz
 *
 */
public class SHH_Module extends Module {

    //the number or periods the user currently chooses to have, either 1, 2, or 3
    private int noPeriods;
    
    //period start and end times
    private String p1start;
    private String p1end;
    private String p2start;
    private String p2end;
    private String p3start;
    private String p3end;
    
    //boolean representing whether or not away mode in the SHP is on or off
    private boolean awayMode;
    
    private boolean havcOn;
    
    //the simulation
    private ArrayList<Zone> zones;
    private HashMap<Room, Double> overriddenRooms;
    
    //summer and winter default temperatures
    private double winterDefault;
    private double summerDefault;
    
    //threshhold temperatures
    private double lowThresh;
    private double highThresh;
    
    DateTimeFormatter parser = DateTimeFormatter.ofPattern("HH:MM:SS");

    public SHH_Module() {
        super("SHH", new ArrayList<>(Arrays.asList("Manage Zones", "Manage Periods", "Change Temperatures")));
        this.noPeriods = 1;
        this.zones = new ArrayList<>();
        this.overriddenRooms = new HashMap<Room, Double>();
        this.awayMode = false;
        this.winterDefault = 22;
        this.summerDefault = 20;
        this.havcOn = false;
        this.lowThresh = 0;
        this.highThresh = 40;
    }
    
    //does not currently account for open windows
    @Override
    public boolean update(){
    	boolean updateGUI = false;
    	double targetTemp = 22;
    	
    	for(Room r: overriddenRooms.keySet()){
    		this.adjustOverrideTemp(r, overriddenRooms.get(r));
    	}
    	
    	if(awayMode&&this.isSummer(sim)){
    		targetTemp = summerDefault;
    		for(Zone z: zones){
    			this.adjustTemp(z, targetTemp);
    		}
    	}
    	else if(awayMode&&this.isWinter(sim)){
    		targetTemp = winterDefault;
    		for(Zone z: zones){
    			this.adjustTemp(z, targetTemp);
    		}
    	}
    	else{
    		for(Zone z : zones){
    			if(sim.getSimulationTime().isBefore(LocalDateTime.parse(p1end, parser))){
    				targetTemp = z.getTemp(0);
    			}
    			else if(sim.getSimulationTime().isBefore(LocalDateTime.parse(p2end, parser))){
    				targetTemp = z.getTemp(1);
    			}
    			else if(sim.getSimulationTime().isBefore(LocalDateTime.parse(p3end, parser))){
    				targetTemp = z.getTemp(2);
    			}
    			this.adjustTemp(z, targetTemp);
    		}
    	}
    	
    	for(Zone z: zones){
    		for(Room r: z.getRooms()){
    			this.checkThreshold(r);
    		}
    	}
    	
    	updateGUI = true;
    	return updateGUI;
    }
    
    //adjusts the temperature of the house according to appropriate method
    public void adjustTemp(Zone z, double targetTemp){
    	if(!havcOn){
    			for(Room r: z.getRooms()){
    				if(r.getTemp()>=targetTemp+1||r.getTemp()<=targetTemp-1){
    					this.havcOn = true;
    					break;
    				}
    				else{
    					if(r.getTemp()>targetTemp && !overriddenRooms.containsKey(r)){
    						r.setTemp(r.getTemp()-0.05);
    					}
    					else if(r.getTemp()<targetTemp && !overriddenRooms.containsKey(r)){
    						r.setTemp(r.getTemp()+0.05);
    					}
    				}
    			}
    	}
    	else{
    		for(Room r: z.getRooms()){
				if(r.getTemp()>=targetTemp+0.25 && !overriddenRooms.containsKey(r)){
					if(this.isSummer(sim)&&!awayMode&&sim.getRoom("Outside").getTemp()<r.getTemp()){
						for(Window w: r.getWindows()){
							if(!w.getBlocked()) w.setOpen(true);
						}
						r.setHeaterOn(false);
						r.setAcOn(false);
					}
					else{
						for(Window w:r.getWindows()){
							if(!w.getBlocked()) w.setOpen(false);
						}
						r.setHeaterOn(false);
						r.setAcOn(true);
					}
					r.setTemp(r.getTemp()-0.1);
				}
				else if(r.getTemp()<=targetTemp && !overriddenRooms.containsKey(r)){
					r.setHeaterOn(true);
					r.setAcOn(false);
					r.setTemp(r.getTemp()+0.1);
				}
				else if(!overriddenRooms.containsKey(r)){
					r.setHeaterOn(false);
					r.setAcOn(false);
				}
			}
    	}
    }
    
    //adjusts temperature of overridden room
    public void adjustOverrideTemp(Room r, double targetTemp){
    	if(!havcOn){
			if(r.getTemp()>=targetTemp+1||r.getTemp()<=targetTemp-1){
				this.havcOn = true;
			}
			else{
				if(r.getTemp()>targetTemp){
					r.setTemp(r.getTemp()-0.05);
				}
				else if(r.getTemp()<targetTemp){
					r.setTemp(r.getTemp()+0.05);
				}
			}
    	}
    	else{
			if(r.getTemp()>=targetTemp+0.25){
				if(this.isSummer(sim)&&!awayMode&&sim.getRoom("Outside").getTemp()<r.getTemp()){
					for(Window w: r.getWindows()){
						if(!w.getBlocked()) w.setOpen(true);
					}
					r.setHeaterOn(false);
					r.setAcOn(false);
				}
				else{
					for(Window w: r.getWindows()){
						if(!w.getBlocked()) w.setOpen(false);
					}
					r.setHeaterOn(false);
					r.setAcOn(true);
				}
				r.setTemp(r.getTemp()-0.1);
			}
			else if(r.getTemp()<=targetTemp){
				r.setHeaterOn(true);
				r.setAcOn(false);
				r.setTemp(r.getTemp()+0.1);
			}
			else{
				r.setHeaterOn(false);
				r.setAcOn(false);
			}
    	}
    }
    
    public int checkThreshold(Room r){
    	if(r.getTemp()<=this.lowThresh){
    		return -1;
    	}
    	else if(r.getTemp()>=this.highThresh){
    		return 1;
    	}
    	else{
    		return 0;
    	}
    }
    
    //returns true if it is summer
    public boolean isSummer(Simulation sim){
    	if(sim.getSimulationTime().getMonth().equals("JUNE")||sim.getSimulationTime().getMonth().equals("JULY")||sim.getSimulationTime().getMonth().equals("AUGUST")){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    
    //returns true if it is winter
    public boolean isWinter(Simulation sim){
    	if(sim.getSimulationTime().getMonth().equals("NOVEMBER")||sim.getSimulationTime().getMonth().equals("DECEMBER")||
    			sim.getSimulationTime().getMonth().equals("JANUARY")||sim.getSimulationTime().getMonth().equals("FEBRUARY")||sim.getSimulationTime().getMonth().equals("MARCH")){
    		return true;
    	}
    	else{
    		return false;
    	}
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
    
    // returns start times of requested period
    //if invalid input, returns 00:00:00
    public String getPeriodStartTime(int periodIndex) {
        if(periodIndex==0){
        	return p1start;
        }
        else if(periodIndex==1){
        	return p2start;
        }
        else if(periodIndex==2){
        	return p3start;
        }
        else{
        	return "00:00:00";
        }
    }

    // returns end times of requested period
    //if invalid, returns 00:00:00
    public String getPeriodEndTime(int periodIndex) {
    	if(periodIndex==0){
        	return p1end;
        }
        else if(periodIndex==1){
        	return p2end;
        }
        else if(periodIndex==2){
        	return p3end;
        }
        else{
        	return "00:00:00";
        }
    }

    //adds a new period to the day and changes start and end times
    public void addPeriod() {
        // increaseNumPeriod
        this.noPeriods++;
        //change start and end time for each period
        if(this.noPeriods==1){
        	p1start = "00:00:00";
        	p1end = "23:59:59";
        }
        else if(this.noPeriods==2){
        	p1start = "00:00:00";
        	p1end = "11:59:59";
        	p2start = "12:00:00";
        	p2end = "23:59:59";
        }
        else if(this.noPeriods==3){
        	p1start = "00:00:00";
        	p1end = "07:59:59";
        	p2start = "08:00:00";
        	p2end = "15:59:59";
        	p3start = "16:00:00";
        	p3end = "23:59:59";
        }
    }

    // removes a period from the day and changes start and end times
    public void removePeriod() {
        // decreases num period
        this.noPeriods--;
        // change time start and end for each period
        if(this.noPeriods==1){
        	p1start = "00:00:00";
        	p1end = "23:59:59";
        }
        else if(this.noPeriods==2){
        	p1start = "00:00:00";
        	p1end = "11:59:59";
        	p2start = "12:00:00";
        	p2end = "23:59:59";
        }
        else if(this.noPeriods==3){
        	p1start = "00:00:00";
        	p1end = "07:59:59";
        	p2start = "08:00:00";
        	p2end = "15:59:59";
        	p3start = "16:00:00";
        	p3end = "23:59:59";
        }
    }
    
    //sets number of periods in a day and changes start and end times
    public void setNoPeriods(int noPeriods){
    	this.noPeriods = noPeriods;
    	if(this.noPeriods==1){
        	p1start = "00:00:00";
        	p1end = "23:59:59";
        }
        else if(this.noPeriods==2){
        	p1start = "00:00:00";
        	p1end = "11:59:59";
        	p2start = "12:00:00";
        	p2end = "23:59:59";
        }
        else if(this.noPeriods==3){
        	p1start = "00:00:00";
        	p1end = "07:59:59";
        	p2start = "08:00:00";
        	p2end = "15:59:59";
        	p3start = "16:00:00";
        	p3end = "23:59:59";
        }
    }
    
    // returns names of over-ridden rooms
    public ArrayList<String> getOverriddenRooms(){
        ArrayList<String> roomNames = new ArrayList<>();
        for (Room r : overriddenRooms.keySet()) {
            roomNames.add(r.getName());
        }
        return roomNames;
    }
    
    // adds room to list of overridden rooms
    public void addOverriddenRoom(String room, double temp){
        overriddenRooms.put(getRoom(room), temp);
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
    
    public void setAwayMode(boolean awayMode){
    	this.awayMode = awayMode;
    }
    
    public boolean getAwayMode(){
    	return this.awayMode;
    }
    
    public void setWinterDefault(double winterDefault){
    	this.winterDefault = winterDefault;
    }
    
    public double getWinterDefault(){
    	return winterDefault;
    }
    
    public void setSummerDefault(double summerDefault){
    	this.summerDefault = summerDefault;
    }
    
    public boolean getHavcOn(){
    	return this.havcOn;
    }
    
    public void setHavcOn(boolean havcOn){
    	this.havcOn = havcOn;
    }
    
    public void setLowThresh(double lowThresh){
    	this.lowThresh = lowThresh;
    }
    
    public double getLowThresh(){
    	return this.getLowThresh();
    }
    
    public void setHighThresh(double highThresh){
    	this.highThresh = highThresh;
    }
    
    public double getHighThresh(){
    	return this.highThresh;
    }

    /**
     * 
     * @author a_khalil
     *
     */
    public class Zone {
        //the array of temperatures that are set for each period, so temps[0] will be what the zone is set to all day if there is one period, 
        //the first half of the day if there are two periods, and the the first third of the day if there are three periods

    	private String name;
        private double[] temps;
        //the list of rooms that are in this zone
        private ArrayList<Room> rooms;

        public Zone() {
        	this.name = "Default Zone";
            this.rooms = new ArrayList<Room>();
            this.temps = new double[3];
        }

        public Zone(double x, double y, double z) {
        	this.name = "New Zone";
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
