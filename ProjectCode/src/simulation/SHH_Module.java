package simulation;

import java.util.ArrayList;

import HouseObjects.Room;
import HouseObjects.Window;
import gui.Driver;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author a_khalil, a_richard, d_ruiz-cigana
 *
 */
public class SHH_Module extends Module {

    /**
     * the number or periods the user currently chooses to have, either 1, 2, or
     * 3
     */
    private int noPeriods;

    //Period start and end times, one variable for each period
    /**
     * Period 1 start
     */
    private String p1start;
    /**
     * Period 1 end
     */
    private String p1end;
    /**
     * period 2 start
     */
    private String p2start;
    /**
     * period 2 end
     */
    private String p2end;
    /**
     * period 3 start
     */
    private String p3start;
    /**
     * period 3 end
     */
    private String p3end;

    /**
     * boolean representing whether or not the HAVC is on
     */
    private boolean havcOn;

    //the simulation
    /**
     * ArrayList of zones in the simulation
     */
    private ArrayList<Zone> zones;
    /**
     * HashMap of Overridden rooms in the simulation
     */
    private HashMap<Room, Double> overriddenRooms;

    /**
     * ArrayList of rooms that are affected by the HAVC system.
     */
    private ArrayList<Room> affectedRooms;

    //summer and winter default temperatures
    /**
     * Winter default temperature
     */
    private double winterDefault;
    /**
     * Summer Default temperature
     */
    private double summerDefault;

    //threshhold temperatures
    /**
     * Lower threshold temperature
     */
    private double lowThresh;
    /**
     * Higher threshold temperature
     */
    private double highThresh;
    private int badTemp= 0;
    private boolean blockedWindow=false;

    /**
     * Used to format date and time
     */
    DateTimeFormatter parser = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Default constructor
     */
    public SHH_Module() {
        super("SHH", new ArrayList<>(Arrays.asList("Manage Zones", "Manage Periods", "Change Temperatures", "Monitor Temperatures")));
        this.noPeriods = 1;
        this.zones = new ArrayList<>();
        this.overriddenRooms = new HashMap<>();
        this.winterDefault = 22;
        this.summerDefault = 20;
        this.havcOn = false;
        this.lowThresh = 0;
        this.highThresh = 40;
        this.p1start = "00:00:00";
        this.p1end = "23:59:59";
    }

    /**
     * Update method. Called once per second
     */
    @Override
    public boolean update() {
        boolean updateGUI = false;
        double targetTemp = 22;
        boolean awayMode = ((SHP_Module) sim.getModuleOfType(SHP_Module.class)).getAwayMode();

        //adjust temperature in overridden rooms
        for (Room r : overriddenRooms.keySet()) {
            this.adjustOverrideTemp(r, overriddenRooms.get(r), awayMode);
        }

        //adjust temperature during summer and winter (away mode)
        if (awayMode && this.isSummer(sim)) {
            targetTemp = summerDefault;
            for (Zone z : zones) {
                this.adjustTemp(z, targetTemp, awayMode);
            }
        } else if (awayMode && this.isWinter(sim)) {
            targetTemp = winterDefault;
            for (Zone z : zones) {
                this.adjustTemp(z, targetTemp, awayMode);
            }
        } //adjust temperature per zone
        else {
            for (Zone z : zones) {
                if (sim.getSimulationTime().isBefore(sim.getSimulationTime().with(LocalTime.parse(p1end, parser)))) {
                    targetTemp = z.getTemp(0);
                } else if (sim.getSimulationTime().isBefore(sim.getSimulationTime().with(LocalTime.parse(p2end, parser)))) {
                    targetTemp = z.getTemp(1);
                } else if (sim.getSimulationTime().isBefore(sim.getSimulationTime().with(LocalTime.parse(p3end, parser)))) {
                    targetTemp = z.getTemp(2);
                }
                this.adjustTemp(z, targetTemp, awayMode);
            }
        }

        //verify thresholds
        for (Zone z : zones) {
            for (Room r : z.getRooms()) {
                badTemp = this.checkThreshold(r);
            }
        }

        updateGUI = true;
        return updateGUI;
    }

    /**
     * adjusts the temperature of the in the appropriate way
     *
     * @param z the current zone
     * @param targetTemp the temperature to be reached
     */
    private void adjustTemp(Zone z, double targetTemp, boolean awayMode) {
        //if havc is off
        if (!havcOn) {
            for (Room r : z.getRooms()) {
                //turn havc on
                if (r.getTemp() >= targetTemp + 1 || r.getTemp() <= targetTemp - 1) {
                    this.havcOn = true;
                    break;
                } //adjust temp to outside
                else {
                    if (r.getTemp() > sim.getRoom("Outside").getTemp() && !overriddenRooms.containsKey(r)) {
                        r.setTemp(r.getTemp() - 0.05);
                    } else if (r.getTemp() < sim.getRoom("Outside").getTemp() && !overriddenRooms.containsKey(r)) {
                        r.setTemp(r.getTemp() + 0.05);
                    }
                }
            }
        } //if havc is on
        else {
            for (Room r : z.getRooms()) {
                //if temp needs adjusting
                if (r.getTemp() >= targetTemp + 0.25 && !overriddenRooms.containsKey(r)) {
                    //adjust by open window
                    if (this.isSummer(sim) && !awayMode && sim.getRoom("Outside").getTemp() < r.getTemp()) {
                        for (Window w : r.getWindows()) {
                            if (!w.getBlocked()) {
                                w.setOpen(true);
                            }
                            else blockedWindow = true;
                        }
                        r.setHeaterOn(false);
                        r.setAcOn(false);
                    } //adjust by AC
                    else {
                        for (Window w : r.getWindows()) {
                            if (!w.getBlocked()) {
                                w.setOpen(false);
                            }
                        }
                        r.setHeaterOn(false);
                        r.setAcOn(true);
                    }
                    //adjust numbers
                    r.setTemp(r.getTemp() - 0.1);
                } //adjust by heater
                else if (r.getTemp() <= targetTemp - 0.25 && !overriddenRooms.containsKey(r)) {
                    r.setHeaterOn(true);
                    r.setAcOn(false);
                    r.setTemp(r.getTemp() + 0.1);
                } //adjust by outside temp
                else if (!overriddenRooms.containsKey(r)) {
                	for (Window w : r.getWindows()) {
                        if (!w.getBlocked()) {
                            w.setOpen(false);
                        }
                    }
                    r.setHeaterOn(false);
                    r.setAcOn(false);
                    if (r.getTemp() > sim.getRoom("Outside").getTemp()) {
                        r.setTemp(r.getTemp() - 0.05);
                    } else if (r.getTemp() < sim.getRoom("Outside").getTemp()) {
                        r.setTemp(r.getTemp() + 0.05);
                    }
                }
            }
        }
    }

    /**
     * adjusts temperature of overridden room
     *
     * @param r the current room
     * @param targetTemp the temperature the should should be
     */
    private void adjustOverrideTemp(Room r, double targetTemp, boolean awayMode) {
        //if havc is off
        if (!havcOn) {
            //turn havc on
            if (r.getTemp() >= targetTemp + 1 || r.getTemp() <= targetTemp - 1) {
                this.havcOn = true;
            } //adjust to outside temp
            else {
                if (r.getTemp() > sim.getRoom("Outside").getTemp()) {
                    r.setTemp(r.getTemp() - 0.05);
                } else if (r.getTemp() < sim.getRoom("Outside").getTemp()) {
                    r.setTemp(r.getTemp() + 0.05);
                }
            }
        } //if havc is on
        else {
            if (r.getTemp() >= targetTemp + 0.25) {
                //adjust temp by open windows
                if (this.isSummer(sim) && !awayMode && sim.getRoom("Outside").getTemp() < r.getTemp()) {
                    for (Window w : r.getWindows()) {
                        if (!w.getBlocked()) {
                            w.setOpen(true);
                        }
                        else blockedWindow = true;
                    }
                    r.setHeaterOn(false);
                    r.setAcOn(false);
                } //adjust temp by AC
                else {
                    for (Window w : r.getWindows()) {
                        if (!w.getBlocked()) {
                            w.setOpen(false);
                        }
                    }
                    r.setHeaterOn(false);
                    r.setAcOn(true);
                }
                r.setTemp(r.getTemp() - 0.1);
            } //adjust temp by heater
            else if (r.getTemp() <= targetTemp - 0.25) {
                r.setHeaterOn(true);
                r.setAcOn(false);
                r.setTemp(r.getTemp() + 0.1);
            } //adjust temp to outside
            else {
            	for (Window w : r.getWindows()) {
                    if (!w.getBlocked()) {
                        w.setOpen(false);
                    }
                }
                r.setHeaterOn(false);
                r.setAcOn(false);
                if (r.getTemp() > sim.getRoom("Outside").getTemp()) {
                    r.setTemp(r.getTemp() - 0.05);
                } else if (r.getTemp() < sim.getRoom("Outside").getTemp()) {
                    r.setTemp(r.getTemp() + 0.05);
                }
            }
        }
    }

    /**
     * A method for checking the temperatures of the room against safety
     * thresholds.
     *
     * @param r The room to be checked.
     * @return 0 if room is safe, -1 if too cold, 1 if too hot.
     */
    public int checkThreshold(Room r) {
        if (r.getTemp() <= this.lowThresh) {
            return -1;
        } else if (r.getTemp() >= this.highThresh) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * A method for determining if the season is Summer
     *
     * @param sim The simulation
     * @return true if it is summer.
     */
    private boolean isSummer(Simulation sim) {
        if (sim.getSimulationTime().getMonth().getValue()==6 || sim.getSimulationTime().getMonth().getValue()==7 || sim.getSimulationTime().getMonth().getValue()==8) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method for determining if the season is Winter
     *
     * @param sim The simulation.
     * @return true if it is Winter.
     */
    private boolean isWinter(Simulation sim) {
        if (sim.getSimulationTime().getMonth().getValue()==11 || sim.getSimulationTime().getMonth().getValue()==12
                || sim.getSimulationTime().getMonth().getValue()==1 || sim.getSimulationTime().getMonth().getValue()==2 || sim.getSimulationTime().getMonth().getValue()==3) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Connects to simulation.
     *
     * @param simulation The simulation to be connected to.
     */
    @Override
    public void attachSimulation(Simulation simulation) {
        super.attachSimulation(simulation);
        affectedRooms = new ArrayList<>(Driver.simulation.getRooms());
        affectedRooms.remove(Driver.simulation.getRoom("Outside"));
        affectedRooms.remove(Driver.simulation.getRoom("Backyard"));
        Zone z = new Zone();
        for (Room r : affectedRooms) {
            z.addRoom(r);
        }
        zones.add(z);
    }

    /**
     * A method for finding the target temperature of a specific zone.
     *
     * @param zone The current zone
     * @param periodIndex Which period of the day it is.
     * @return the target temperature
     */
    public double getZoneTemp(Zone zone, int periodIndex) {
        return zone.getTemp(periodIndex);
    }

    /**
     * A method for setting the target temperature for a zone
     *
     * @param zone the current zone
     * @param periodIndex the period of the day
     * @param temp the target temperature
     */
    public void setZoneTemp(Zone zone, int periodIndex, double temp) {
        zone.setTemp(temp, periodIndex);
    }

    /**
     * Changes to which zone a room belongs
     *
     * @param room the room to change
     * @param zone the new zone
     */
    public void changeZone(Room room, Zone zone) {
        // remove room from current zone
        for (Zone z : zones) {
            for (Room r : z.getRooms()) {
                if (r.equals(room)) {
                    z.removeRoom(r);
                    break;
                }
            }
        }
        // add room to new zone
        zone.addRoom(room);
    }

    /**
     * A method for adding a new zone to the ArrayList of zones.
     *
     * @param name The name of the new zone
     * @param temps an array of target temperature (1-3) for each period of the
     * day.
     */
    public void addNewZone(String name, double... temps) {
        Zone z = new Zone();
        z.setName(name);
        for (int i = 0; i < temps.length; i++) {
            z.setTemp(temps[i], i);
        }
        zones.add(z);
    }

    /**
     * A method for removing a zone from the simulation. Puts all rooms from
     * that zone into the default zone.
     *
     * @param name The name of the zone you wish to remove.
     */
    public void removeZone(String name) {
        Zone z = getZone(name);
        ArrayList<Room> roomsInZone = z.getRooms();
        zones.remove(z);
        zones.get(0).addAllRooms(roomsInZone);
    }

    /**
     * A method for finding a zone by its name.
     *
     * @param zoneName The name of the zone to be found
     * @return the found zone, default if could not find
     */
    public Zone getZone(String zoneName) {
        for (Zone z : zones) {
            if (z.getName().equals(zoneName)) {
                return z;
            }
        }
        //returns default if could not find
        return zones.get(0);
    }

    /**
     * Returns list of names of all zones.
     *
     * @return list of names
     */
    public ArrayList<String> getZoneNames() {
        ArrayList<String> zoneNames = new ArrayList<>();
        for (Zone z : zones) {
            zoneNames.add(z.getName());
        }
        return zoneNames;
    }

    /**
     * Returns start times of requested period. If invalid, will simply return
     * 00:00:00
     *
     * @param periodIndex the period of the day
     * @return the period's start time.
     */
    public String getPeriodStartTime(int periodIndex) {
        if (periodIndex == 0) {
            return p1start;
        } else if (periodIndex == 1) {
            return p2start;
        } else if (periodIndex == 2) {
            return p3start;
        } else {
            return "00:00:00";
        }
    }

    /**
     * Returns the end time of requested period. If invalid, will simply return
     * 00:00:00
     *
     * @param periodIndex the period of the day
     * @return the period's end time
     */
    public String getPeriodEndTime(int periodIndex) {
        if (periodIndex == 0) {
            return p1end;
        } else if (periodIndex == 1) {
            return p2end;
        } else if (periodIndex == 2) {
            return p3end;
        } else {
            return "00:00:00";
        }
    }

    /**
     * Adds a new period to the day and changes start and end times.
     */
    public void addPeriod() {
        // increaseNumPeriod
        this.noPeriods++;
        //change start and end time for each period
        if (this.noPeriods == 1) {
            p1start = "00:00:00";
            p1end = "23:59:59";
        } else if (this.noPeriods == 2) {
            p1start = "00:00:00";
            p1end = "11:59:59";
            p2start = "12:00:00";
            p2end = "23:59:59";
        } else if (this.noPeriods == 3) {
            p1start = "00:00:00";
            p1end = "07:59:59";
            p2start = "08:00:00";
            p2end = "15:59:59";
            p3start = "16:00:00";
            p3end = "23:59:59";
        }
    }

    /**
     * Removes a period from the day and changes start and end times
     */
    public void removePeriod() {
        // decreases num period
        this.noPeriods--;
        // change time start and end for each period
        if (this.noPeriods == 1) {
            p1start = "00:00:00";
            p1end = "23:59:59";
        } else if (this.noPeriods == 2) {
            p1start = "00:00:00";
            p1end = "11:59:59";
            p2start = "12:00:00";
            p2end = "23:59:59";
        } else if (this.noPeriods == 3) {
            p1start = "00:00:00";
            p1end = "07:59:59";
            p2start = "08:00:00";
            p2end = "15:59:59";
            p3start = "16:00:00";
            p3end = "23:59:59";
        }
    }

    /**
     * Sets number of periods in a day and changes start and end times
     *
     * @param noPeriods the new number of periods to have.
     */
    public void setNoPeriods(int noPeriods) {
        this.noPeriods = noPeriods;
        if (this.noPeriods == 1) {
            p1start = "00:00:00";
            p1end = "23:59:59";
        } else if (this.noPeriods == 2) {
            p1start = "00:00:00";
            p1end = "11:59:59";
            p2start = "12:00:00";
            p2end = "23:59:59";
        } else if (this.noPeriods == 3) {
            p1start = "00:00:00";
            p1end = "07:59:59";
            p2start = "08:00:00";
            p2end = "15:59:59";
            p3start = "16:00:00";
            p3end = "23:59:59";
        }
    }

    /**
     * Returns names of overridden rooms
     *
     * @return A list of room names that have been overridden.
     */
    public ArrayList<String> getOverriddenRooms() {
        ArrayList<String> roomNames = new ArrayList<>();
        for (Room r : overriddenRooms.keySet()) {
            roomNames.add(r.getName());
        }
        return roomNames;
    }

    /**
     * Returns the desired temperature of the overridden room.
     *
     * @param room the room temperature
     * @return the desired temperature of the room
     */
    public double getOverriddenRoomTemp(String room) {
        return overriddenRooms.get(getRoom(room));
    }

    /**
     * Adds room to list of overridden rooms
     *
     * @param room The room to add.
     * @param temp The target temperature of the room
     */
    public void addOverriddenRoom(String room, double temp) {
        overriddenRooms.put(getRoom(room), temp);
    }

    /**
     * Removes room from list of overridden rooms.
     *
     * @param room the room to be removed.
     */
    public void removeOverridenRoom(String room) {
        overriddenRooms.remove(getRoom(room));
    }

    /**
     * Returns the number of periods in a day
     *
     * @return Number of periods.
     */
    public int getNoPeriods() {
        return this.noPeriods;
    }

    public ArrayList<String> getRoomNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Room r : affectedRooms) {
            names.add(r.getName());
        }
        return names;
    }

    /**
     * Gets a specific room.
     *
     * @param name The name of the room to get
     * @return The requested room.
     */
    public Room getRoom(String name) {
        return sim.getRoom(name);
    }

    public Zone getRoomZone(Room room) {
        for (Zone z : zones) {
            if (z.getRooms().contains(room)) {
                return z;
            }
        }
        return null;
    }

    /**
     * Gets the temperature of a room.
     *
     * @param name The name of the room to get
     * @return The temperature of the room.
     */
    public double getRoomTemp(String name) {
        return sim.getRoom(name).getTemp();
    }

    /**
     * Sets the temperature of a room
     *
     * @param name The room to set
     * @param temp The target temperature
     */
    public void setRoomTemp(String name, double temp) {
        Room r = sim.getRoom(name);
        r.setTemp(temp);
    }

    /**
     * A method for setting the default Winter temperature.
     *
     * @param winterDefault the target temperature.
     */
    public void setWinterDefault(double winterDefault) {
        this.winterDefault = winterDefault;
    }

    /**
     * A method for getting the default Winter temperature.
     *
     * @return the current Winter default temperature.
     */
    public double getWinterDefault() {
        return winterDefault;
    }

    /**
     * A method for setting the default Summer temperature.
     *
     * @param summerDefault the target default Summer temperature.
     */
    public void setSummerDefault(double summerDefault) {
        this.summerDefault = summerDefault;
    }

    /**
     * A method for getting the default Summer temperature.
     *
     * @return the current Summer default temperature.
     */
    public double getSummerDefault() {
        return this.summerDefault;
    }

    /**
     * A method that shows whether or not the HAVC is on
     *
     * @return true if havc is on.
     */
    public boolean getHavcOn() {
        return this.havcOn;
    }

    /**
     * A method for turning on/off the HAVC
     *
     * @param havcOn true if turning on, false if turning off.
     */
    public void setHavcOn(boolean havcOn) {
        this.havcOn = havcOn;
    }

    /**
     * A method for setting the cold threshold for the house.
     *
     * @param lowThresh The temperature representing an unsafe cold temperature.
     */
    public void setLowThresh(double lowThresh) {
        this.lowThresh = lowThresh;
    }

    /**
     * A method that gets the cold threshold of the house.
     *
     * @return the unsafe cold temperature.
     */
    public double getLowThresh() {
        return this.lowThresh;
    }

    /**
     * A method that sets the hot threshold for the house.
     *
     * @param highThresh The temperature considered unsafe if too hot.
     */
    public void setHighThresh(double highThresh) {
        this.highThresh = highThresh;
    }

    /**
     * A method for returning the hot threshold temperature.
     *
     * @return The temperature that is considered too hot.
     */
    public double getHighThresh() {
        return this.highThresh;
    }
    
    /**
     * Contact the user if there is a dangerous temperature in the house.
     * @return The message to be sent to the user.
     */
    public String contactUser(){
    	if(badTemp<0){
    		badTemp = 0;
    		return "It is too cold in the house, pipes could burst!";
    	}
    	else if (badTemp>0){
    		badTemp = 0;
    		return "It's too hot in your house! That's dangerous.";
    	}
    	else return "";
    }
    
    /**
     * Let's the user know if a window cannot be opened because it is blocked
     * @return Message to be sent to the user
     */
    public String noWindow(){
    	if(blockedWindow){
    		blockedWindow = false;
    		return "A window is blocked and could not be opened.";
    	}
    	else return "";
    }

    /**
     * An inner class to to SHH. Each zone represents a target temperature for a
     * group of rooms.
     *
     * @author a_khalil
     *
     */
    public class Zone {
        //the array of temperatures that are set for each period, so temps[0] will be what the zone is set to all day if there is one period, 
        //the first half of the day if there are two periods, and the the first third of the day if there are three periods

        /**
         * The zone's name.
         */
        private String name;
        /**
         * An array of temperatures for each possible period dring the day.
         */
        private double[] temps = new double[3];
        ;
        /**
         * The list of rooms that are in this zone
         */
        private ArrayList<Room> rooms;

        /**
         * Default constructor
         */
        public Zone() {
            this.name = "Default Zone";
            this.setTemps(22);
            this.rooms = new ArrayList<>();
        }

        /**
         * Parametrized constructor.
         *
         * @param x Period 1 target temperature.
         * @param y Period 2 target temperature.
         * @param z Period 3 target temperature.
         */
        public Zone(double x, double y, double z) {
            this.name = "New Zone";
            this.setTemps(x, y, z);
            this.rooms = new ArrayList<Room>();
        }

        /**
         * A method for setting the name of a zone.
         *
         * @param name The new name for the zone.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * A method for setting the temperature for period 1
         *
         * @param x The target temperature.
         */
        public void setTemps(double x) {
            this.temps[0] = x;

        }

        /**
         * A method for settings the temperatures for periods 1 and 2
         *
         * @param x Target temperature for period 1
         * @param y Target temperature for period 2
         */
        public void setTemps(double x, double y) {
            this.temps[0] = x;
            this.temps[1] = y;
        }

        /**
         * A method for setting the temperatures for all periods in the day.
         *
         * @param x Target temperature for period 1
         * @param y Target temperature for period 2
         * @param z Target temperature for period 3
         */
        public void setTemps(double x, double y, double z) {
            this.temps[0] = x;
            this.temps[1] = y;
            this.temps[2] = z;

        }

        /**
         * Sets the target temperature for a specific period in the day.
         *
         * @param x Target temperature
         * @param i Period index
         */
        public void setTemp(double x, int i) {
            this.temps[i] = x;
        }

        /**
         * A method for getting the name of the zone.
         *
         * @return The name of the zone.
         */
        public String getName() {
            return name;
        }

        /**
         * A method for getting the Temperature of the zone for a specific
         * period
         *
         * @param i The index od the period
         * @return The target temperature for that period.
         */
        public double getTemp(int i) {
            return temps[i];
        }

        /**
         * Returns a list of all temperatures for the day.
         *
         * @return Array of temperatures.
         */
        public double[] getTemps() {
            return this.temps;
        }

        /**
         * A method for getting a room by index
         *
         * @param i index of the desired room
         * @return the desired room
         */
        public Room getRoom(int i) {
            return rooms.get(i);
        }

        /**
         * A method for returning a list of rooms in the zone.
         *
         * @return The list of rooms in the zone.
         */
        public ArrayList<Room> getRooms() {
            return this.rooms;
        }

        /**
         *
         * @param r
         */
        public void setRooms(ArrayList<Room> r) {
            this.rooms = r;
        }

        /**
         * A method for adding a room to the zone.
         *
         * @param r The room to be added.
         */
        public void addRoom(Room r) {
            this.rooms.add(r);
        }

        /**
         * A method for adding all rooms to the zone.
         *
         * @param rooms the collection of rooms to be added.
         */
        public void addAllRooms(Collection<Room> rooms) {
            this.rooms.addAll(rooms);
        }

        /**
         * A method for removing a room from the zone.
         *
         * @param r The room to be removed..
         */
        public void removeRoom(Room r) {
            this.rooms.remove(r);
        }

        /**
         * A method for retrieving the current temperature of a room.
         *
         * @param i the index of the room
         * @return The temperature of the room.
         */
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
