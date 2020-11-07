/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;



import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author DRC
 */
public class SHP_Module extends Module{
	Simulation sim;
	boolean awayMode;
	//List of Light IDs
	ArrayList<Integer> lights;
	//array of size 2 defining when lights should be on, index 0 is start time in index 1 is end time
    int[] timeLightsOn;
    //time in minutes after intruder is detected to call authorities
     int alertTime;
     boolean intruderDetected;
	
    public SHP_Module() {
        super("SHP", new ArrayList<>());
        this.commands.addAll(Arrays.asList("Away Mode","Permanent Lights","Check for Intruders", "Set Alert Time"));
    }
   public boolean getAwayMode() {
    	return awayMode;
    }
    public boolean toggleAwayMode() {
    	awayMode =!(this.getAwayMode());
    	if(awayMode) {
    	SHC_Module shc = (SHC_Module) sim.getModuleOfType(SHC_Module.class);
    	//ArrayList<Integer> simLights = sim.getLights();
    	 shc.closeAllWindows(sim.getRooms());
    	// shc.lockAllDoors(sim.getRooms());
    	//for (int x=0; x<simLights.size();x++) {
    	//if(lights.contains.simLights.get(x)&& sim.getTime()<timeLightsOn[1] &&sim.getTime()>timeLightsOn[0]) {
    	//shc.turnOnLight(simLights.get(x));
    	//continue;
    	// 
    	//}
    	// shc.turnOffLight(simLights.get(x));
    	// }
    
    	}
    	return awayMode;
    	
    }
    
    public void setAlertTime(int x){
    	this.alertTime=x;
    }
    public void contactAuthorities() {
    	System.out.println("contacting authorities");
    }
 
    
    public void Update() {
    	if(awayMode==true) {
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
    	else
    		intruderDetected=true;
    	
    	
    	}
    }
    
    
    
}
