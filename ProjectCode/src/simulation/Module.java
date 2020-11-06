/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import java.util.ArrayList;

/**
 *
 * @author DRC
 */
public abstract class Module {
    private String name;
    Simulation sim;
    
    protected static ArrayList<String> commands;

    public Module(String name, ArrayList<String> commands) {
        this.name = name;
        this.commands = commands;
    }
    
    public void attachSimulation(Simulation simulation){
        this.sim = simulation;
    }
    
    public void detachSimulation(){
        this.sim = null;
    }
    
    public abstract void update();
    
    public String getName(){
        return this.name;
    }
    
    public static ArrayList<String> getCommands(){
        return Module.commands;
    }
    

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Module)) {
            return false;
        }
        return this.getName().equals(((Module) obj).getName());
    }
    
    
    
}
