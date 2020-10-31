/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import HouseObjects.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author DRC
 */
public class SHC_Module extends Module {

    public SHC_Module() {
        super("SHC", new ArrayList<>());
        this.commands.addAll(Arrays.asList("Open/Close Windows", "Lock/Unlock Doors", "Open/Close Garage", "Turn On/Off Lights"));
    }

    public void closeAllWindows(ArrayList<Room> rooms) {
        for (Room room : rooms) {
            for (Window w : room.getWindows()) {
                w.setOpen(false);
            }
        }
    }

}
