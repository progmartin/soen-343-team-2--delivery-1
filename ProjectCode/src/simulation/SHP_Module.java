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
    public SHP_Module() {
        super("SHP", new ArrayList<>());
        this.commands.addAll(Arrays.asList("Away Mode","Permanent Lights"));
    }
}
