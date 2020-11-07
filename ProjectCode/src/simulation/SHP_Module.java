/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author DRC
 */
public class SHP_Module extends Module {

    boolean awayMode;
    //List of Light IDs that should be turned onn when away mode is set to on
    ArrayList<Integer> lights;
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

    public LocalTime[] getTimeLightsOn() {
		return timeLightsOn;
	}
	
	public boolean getContactAuthorities() {
		return contactAuthorities;
	}
	public int getAlertTime() {
		return alertTime;
	}
	public void setLights(ArrayList<Integer> lights) {
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
    }

    
    public ArrayList<Integer> getLights() {
        return lights;
    }
    /**
     * a method for setting the range of times in which the lights that are set to be on during away mode should be on
     * @param start hour
     * @param start minute
     * @param end hour
     * @param end minute
     */
    public void setTimeLightsOn(int a, int b, int c, int d) {
    	LocalTime x =LocalTime.of(a, b);
    	LocalTime y = LocalTime.of(c,d);
    	
    	LocalTime[] z = {x,y};
    	this.timeLightsOn=z;
    	
    }

    public void addLight(int lightID) {
        if (!lights.contains(lightID)) {
            lights.add(lightID);
        }
    }
    
    public void removeLight(int lightID){
        lights.remove(lightID);
    }

    public boolean getAwayMode() {
        return awayMode;
    }

  
    /**
     * if away mode is currently off, using SHC it will lock all doors and close all windows
     * for lights, it will go through all rooms, and all lights, and check if a light is within the permanent lights list, and if it the simulation time is within the timeLightsOn range
     * if so, it will turn the light on
     * if not, it will turn the light off
     * it will then set awayMode to true
     * if away mode is already on, it will do nothing
     * 
     * 
     * @return true if away mode is currently off, and false is away mode is currently on
     */
    public boolean setAwayOn() {
    	if(awayMode)
    		return false;
    	
    	for(int x=0;x<sim.getRooms().size();x++) {
    		if(sim.getRooms().get(x).getPeople().size() != 0) {
    			return false;
    		}
    	}
    	SHC_Module shc= (SHC_Module) sim.getModuleOfType(SHC_Module.class);
    	shc.closeAllWindows();
    	shc.lockAllDoors();
    	for(int x=0;x<sim.getRooms().size();x++) {
    		for (int y =0;y<sim.getRooms().get(x).getLights().size();y++) {
    			if(sim.getSimulationTime().toLocalTime().isAfter(timeLightsOn[0])&&sim.getSimulationTime().toLocalTime().isBefore(timeLightsOn[1])) {
    				if(lights.contains(sim.getRooms().get(x).getLights().get(y).getID()))
    					shc.turnOnLight(sim.getRooms().get(x).getName(), sim.getRooms().get(x).getLights().get(y).getID());
    			}
    			else
    				shc.turnOffLight(sim.getRooms().get(x).getName(), sim.getRooms().get(x).getLights().get(y).getID());
    		}
    	}
    	awayMode=true;
    	return true;
    	
    	
    	
    }
    /**
     * sets away mode to false, doesn't really do anything else.
     * @return if away mode is already off, it will return false, otherwise, it will return true
     */
    public boolean setAwayOff() {
    	if(this.awayMode) {
    		this.awayMode=false;
    		return true;
    	}
    	else
    		return false;
    }
/**
 * sets time to wait after intruder is detected to call authorities
 * @param integer in minutes
 */
    public void setAlertTime(int x) {
       this.alertTime=x;
        
        
    }

    /**
     * if contactAuthorities is equal to true, will change intruder detected back to false, and contact authorities back to false, assuming authorities will arrive successfully
     * this method will have to change if keeping track of authorities arriving and apprehending intruder becomes necessary functionality of the software
     * @return
     */
    public String contactAuthorities() {
        if(contactAuthorities) {
        this.intruderDetected=false;
		this.contactAuthorities=false;
		return "Contacting Authorities";
        }
        else return "";
    }
 
    /**
     * if away mode is on, and there is currently no intruder detected, it will check rooms for potential intruder
     * if intruder is found, it will set intruderDetected to true, and will set intruderDetectedTime to the current simulation time
     * if away mode is on, and an intruder has already been detected, it will check if enough time has elapsed to call the authorities
     * if so, contactAuthorities will be set to true.
     * if away mode is false, it will set intruder detected to false, and contact authorities to false as well. 
     * the only scenario to not contact authorities after an intruder is detected is to set away mode to off within the alertTime duration
     */
    public void update() {
    	if(awayMode==true) {
    		if(intruderDetected==false) {
    		int y=0;
    	for (int x =0; x<sim.getRooms().size();x++) {
    		if(sim.getRooms().get(x).numberOfPeople() != 0)
    		{
    			y++;
    			break;
    		}
    	}
    	if (y==0)
    		intruderDetected=false;
    	else {
    		intruderDetected=true;
    		intruderDetectedTime=sim.getSimulationTime().toLocalTime();
    	
    	}
    		}
    	if(intruderDetected=true&&intruderDetectedTime.isAfter(sim.getSimulationTime().toLocalTime().plusMinutes(alertTime)))
    		this.contactAuthorities=true;
    	
    	}
    	
    	else {
    		this.intruderDetected=false;
    		this.contactAuthorities=false;
    	}
    }

}
