/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

/**
 *
 * @author Derek Ruiz-Cigana
 */
public class MissingRoomException extends Exception{

    public MissingRoomException() {
        this("Missing Rooms");
    }

    public MissingRoomException(String message) {
        super(message);
    }
    
    
}
