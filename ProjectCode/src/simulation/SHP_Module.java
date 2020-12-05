/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import HouseObjects.Light;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author DRC
 */
public class SHP_Module extends Module {

    boolean awayMode;
    //List of Light IDs that should be turned on when away mode is set to on
    ArrayList<Light> lights;
    //array of size 2 defining when lights should be on, index 0 is start time in index 1 is end time
    LocalTime[] timeLightsOn;
    //time in minutes after intruder is detected to call authorities
    int alertTime;
    //boolean representing whether or not an intruder is detected
    boolean intruderDetected;
    //LocalTime representing time when intruder was detected
    LocalTime intruderDetectedTime;
    //boolean representing whether or not enough time has elapsed since an intruder was detected to call authorities
    boolean contactAuthorities;

    public void resetTimeLightsOn() {
        timeLightsOn = null;
    }

    public LocalTime[] getTimeLightsOn() {
        return timeLightsOn;
    }

    public boolean getContactAuthorities() {
        return contactAuthorities;
    }

    public void resetAlertTime(){
        alertTime = 0;
    }
    public int getAlertTime() {
        return alertTime;
    }

    public void setLights(ArrayList<Light> lights) {
        this.lights = lights;
    }

    public boolean getIntruderDetected() {
        return intruderDetected;
    }

    public LocalTime getIntruderDetectedTime() {
        return intruderDetectedTime;
    }

    public SHP_Module() {
        super("SHP", new ArrayList<>(Arrays.asList("Away Mode", "Lights On When Away", "Set Alert Time")));
        lights = new ArrayList<>();
        alertTime = 0;
        intruderDetected = false;
        contactAuthorities = false;
    }

    public ArrayList<Light> getLights() {
        return lights;
    }

    /**
     * a method for setting the range of times in which the lights that are set
     * to be on during away mode should be on
     *
     * @param start start time
     * @param end end time
     */
    public void setTimeLightsOn(LocalTime start, LocalTime end) {
        LocalTime x = start;
        LocalTime y = end;

        LocalTime[] z = {x, y};
        this.timeLightsOn = z;

    }

    public void addLight(Light light) {
        if (!lights.contains(light)) {
            lights.add(light);
        }
    }

    public void removeLight(Light light) {
        lights.remove(light);
    }

    public boolean getAwayMode() {
        return awayMode;
    }

    /**
     * if away mode is currently off, using SHC it will lock all doors and close
     * all windows for lights, it will go through all rooms, and all lights, and
     * check if a light is within the permanent lights list, and if it the
     * simulation time is within the timeLightsOn range if so, it will turn the
     * light on if not, it will turn the light off it will then set awayMode to
     * true if away mode is already on, it will do nothing
     *
     *
     * @return true if away mode is currently off, and false is away mode is
     * currently on
     */
    public boolean setAwayOn() {
        if (awayMode) {
            return false;
        }
        
        for (int x = 0; x < sim.getRooms().size(); x++) {
            if (this.isRoomEmpty(x)) {
                return false;
            }
        }
        SHC_Module shc = (SHC_Module) sim.getModuleOfType(SHC_Module.class);
        shc.closeAllWindows();
        shc.lockAllDoors();

        // Determines if lights should be on based on if the time set for lights being off.
        boolean withinTimeRange;
        if (timeLightsOn == null) {
            withinTimeRange = true;
        } else {
            // If the first time is earlier than the second time (5am - 10am), then sim time must be between this time,
            // If the first time is later than the second time (10pm - 3am), then sim time must be after second time and before first time.
            withinTimeRange = (timeLightsOn[0].compareTo(timeLightsOn[1]) < 0
                    ? sim.getSimulationTime().toLocalTime().isAfter(timeLightsOn[0]) && sim.getSimulationTime().toLocalTime().isBefore(timeLightsOn[1])
                    : sim.getSimulationTime().toLocalTime().isAfter(timeLightsOn[1]) || sim.getSimulationTime().toLocalTime().isBefore(timeLightsOn[0]));
        }

        for (int x = 0; x < sim.getRooms().size(); x++) {
            for (int y = 0; y < sim.getRooms().get(x).getLights().size(); y++) {
                if (withinTimeRange) {
                    if (lights.contains(sim.getRooms().get(x).getLights().get(y))) {
                        shc.turnOnLight(sim.getRooms().get(x).getName(), sim.getRooms().get(x).getLights().get(y).getID());
                    }
                } else {
                    shc.turnOffLight(sim.getRooms().get(x).getName(), sim.getRooms().get(x).getLights().get(y).getID());
                }
            }
        }
        awayMode = true;
        return true;

    }
    
    /**
     * A method for determining if a room is empty
     * @param x The index of the room
     * @return true if empty, false if occupied
     */
    private boolean isRoomEmpty(int x){
    	if(!sim.getRooms().get(x).getName().equalsIgnoreCase("Outside") && !sim.getRooms().get(x).getPeople().isEmpty()){
    		return true;
    	}
    	return false;
    }

    /**
     * sets away mode to false, doesn't really do anything else.
     *
     * @return if away mode is already off, it will return false, otherwise, it
     * will return true
     */
    public boolean setAwayOff() {
        if (this.awayMode) {
            this.awayMode = false;
            return true;
        } else {
            return false;
        }
    }

    /**
     * sets time to wait after intruder is detected to call authorities
     *
     * @param x in seconds
     */
    public void setAlertTime(int x) {
        this.alertTime = x;

    }

    /**
     * if contactAuthorities is equal to true, will change intruder detected
     * back to false, and contact authorities back to false, assuming
     * authorities will arrive successfully this method will have to change if
     * keeping track of authorities arriving and apprehending intruder becomes
     * necessary functionality of the software
     *
     * @return
     */
    public String contactAuthorities() {
        if (contactAuthorities) {
            this.intruderDetected = false;
            this.contactAuthorities = false;
            return "Contacting Authorities";
        } else {
            return "";
        }
    }

    /**
     * if away mode is on, and there is currently no intruder detected, it will
     * check rooms for potential intruder if intruder is found, it will set
     * intruderDetected to true, and will set intruderDetectedTime to the
     * current simulation time if away mode is on, and an intruder has already
     * been detected, it will check if enough time has elapsed to call the
     * authorities if so, contactAuthorities will be set to true. if away mode
     * is false, it will set intruder detected to false, and contact authorities
     * to false as well. the only scenario to not contact authorities after an
     * intruder is detected is to set away mode to off within the alertTime
     * duration
     *
     * @return true if the simulation was updated and the GUI needs to update
     * display
     */
    @Override
    public boolean update() {
        boolean updateGUI = false;
        if (awayMode == true) {
            if (intruderDetected == false) {
                int y = 0;
                for (int x = 0; x < sim.getRooms().size(); x++) {
                    if (!sim.getRooms().get(x).getName().equalsIgnoreCase("Outside") && sim.getRooms().get(x).numberOfPeople() != 0) {
                        y++;
                        break;
                    }
                }
                if (y == 0) {
                    intruderDetected = false;
                } else {
                    intruderDetected = true;
                    intruderDetectedTime = sim.getSimulationTime().toLocalTime();

                }
            }
            
            if (intruderDetected && intruderDetectedTime.plusSeconds(alertTime).isBefore(sim.getSimulationTime().toLocalTime())) {
                this.contactAuthorities = true;
            }
            updateGUI = true;
        } else {
            this.intruderDetected = false;
            this.contactAuthorities = false;
        }
        return updateGUI;
    }

}
