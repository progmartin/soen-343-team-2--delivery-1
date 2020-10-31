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
    
    protected ArrayList<String> commands;

    public Module(String name, ArrayList<String> commands) {
        this.name = name;
        this.commands = commands;
    }
    
    
    
    public String getName(){
        return this.name;
    }
    
    public ArrayList<String> getCommands(){
        return this.commands;
    }
    

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Module)) {
            return false;
        }
        return this.getName().equals(((Module) obj).getName());
    }
    
    
    
}
