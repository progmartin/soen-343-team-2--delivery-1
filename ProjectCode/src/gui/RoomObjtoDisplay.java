package gui;

import java.util.ArrayList;

import HouseObjects.*;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.*;

public class RoomObjtoDisplay {

    private static double orgSceneX;
    private static double orgSceneY;
    private static final double initOffsetX = 10.0;
    private static final double initOffsetY = 10.0;
    private static final double lengthOfRoom = 100.0;
    private static final double lenghtOfDoor = 20.0;
    private static final double angleOfDoorClosed = 20.0;
    private static final double angleOfDoorOpened = 90.0;
    private static final double lengthOfWindow = 20.0;
    private static final double thickOfWindow = 2.5;
    private static final double widthOfPerson = 20.0;
    private static final double heightOfPerson = 20.0;
    private static final double widthOfLight = 10.0;
    private static final double heightOfLight = 10.0;
    private static final double widthOfHAVC = 10.0;
    private static final double heightOfHAVC = 10.0;

    /**
     * Creates the house layout using the array of rooms on the pane. The house
     * layout is not a real representation of the house, rather an indication of
     * rooms that exist in the house and the objects that reside in the rooms.
     * This means that the visualization may not be accurate to a real scenario,
     * for example two adjacent rooms do not have a door in between them or two
     * adjacent rooms have a window between them. <br/><br/>
     * The array of rooms must have a backyard room as its first element. The
     * last element of the room array must be an outside room. The second to
     * last element must be an entrance room that connects to outside. Every
     * room after the first room will be added from left to right then top to
     * bottom (backyard to outside).<br/><br/>
     * The layout will have a margin around the edges between the border of the
     * pane and the house. The size of an indoor room is a square with a fixed
     * length, while the outdoor room are have a width the width of the house.
     * The width of the house will be the ceiling of the square root of the
     * number of indoor rooms up to 4, and the height is the remaining number of
     * rooms. If the height of the house surpasses the pane layout then the root
     * pane will expand to accommodate for the rooms up its max height.
     *
     * @param root the pane to add the house layout to
     * @param rooms array of rooms to be added
     * @return true if was able to create the house; false otherwise
     */
    static public boolean drawHouseLayout(Pane root, ArrayList<Room> rooms) {

        if (rooms.size() < 3) {
            return false;
        }

        int numOfRoomsWide = Math.min((int) Math.ceil(Math.sqrt(rooms.size())), 4);
        double widthOfHouse = numOfRoomsWide * lengthOfRoom;

        root.getChildren().clear();

        ArrayList<Color> colors = new ArrayList<>();//set random colors
        colors.add(Color.LAVENDER);
        colors.add(Color.AQUAMARINE);
        colors.add(Color.LIGHTPINK);
        colors.add(Color.WHEAT);
        colors.add(Color.BURLYWOOD);

        // Create backyard room
        Room backyard = rooms.get(0);
        Rectangle roomRect = new Rectangle(widthOfHouse, lengthOfRoom, Color.WHITE);
        Label text = new Label(backyard.getName());
        text.setLayoutX((widthOfHouse - 50) / 2);
        text.setLayoutY((lengthOfRoom - 20) / 2);
        // Adding Doors
        ArrayList<Arc> doors = new ArrayList<>();
        for (int doorIdx = 0; doorIdx < backyard.getDoors().size(); ++doorIdx) {
            double doorOffsetX = (roomRect.getWidth() / (backyard.getDoors().size() + 1)) * (doorIdx + 1);
            double doorAngle = (backyard.getDoors().get(doorIdx).getOpen() ? angleOfDoorOpened : angleOfDoorClosed);
            Arc door = new Arc(doorOffsetX, lengthOfRoom, lenghtOfDoor, lenghtOfDoor, 0.0, doorAngle);
            door.setType(ArcType.ROUND);
            door.setFill(Color.BROWN);
            doors.add(door);
        }
        // Adding People
        ArrayList<Rectangle> people = new ArrayList<>();
        for (int personIdx = 0; personIdx < backyard.getPeople().size(); ++personIdx) {
            double personOffsetX = ((roomRect.getWidth() / (backyard.getPeople().size() + 1)) - (widthOfPerson / 2)) * (personIdx + 1);
            Rectangle person = new Rectangle(widthOfPerson, heightOfPerson, new ImagePattern(AssetManager.getPersonImage()));
            person.setX(personOffsetX);
            person.setY(lengthOfRoom * 0.1);
            people.add(person);
        }
        // Adding Lights
        ArrayList<Rectangle> lights = new ArrayList<>();
        for (int lightIdx = 0; lightIdx < backyard.getLights().size(); ++lightIdx) {
            double lightOffsetX = ((roomRect.getWidth() / (backyard.getLights().size() + 1)) - (widthOfLight / 2)) * (lightIdx + 1);
            Rectangle light = new Rectangle(widthOfLight, heightOfLight, new ImagePattern((backyard.getLights().get(lightIdx).getIsOn() ? AssetManager.getLightOnImage() : AssetManager.getLightOffImage())));
            light.setX(lightOffsetX);
            light.setY((lengthOfRoom * 0.9) - heightOfLight );
            lights.add(light);
        }
        AnchorPane roomPane = new AnchorPane(roomRect, text);
        roomPane.getChildren().addAll(doors.toArray(new Arc[doors.size()]));
        roomPane.getChildren().addAll(people.toArray(new Rectangle[people.size()]));
        roomPane.getChildren().addAll(lights.toArray(new Rectangle[lights.size()]));
        roomPane.setLayoutX(initOffsetX);
        roomPane.setLayoutY(initOffsetY);
        root.getChildren().add(roomPane);

        // Create all indoor rooms
        for (int roomIdx = 1; roomIdx < rooms.size() - 1; roomIdx++) {
            Room room = rooms.get(roomIdx);
            roomRect = new Rectangle(lengthOfRoom, lengthOfRoom, colors.get((roomIdx - 1) % colors.size()));
            text = new Label(room.getName());
            text.setLayoutX((lengthOfRoom - 50) / 2);
            text.setLayoutY((lengthOfRoom - 20) / 2);

            // Adding Doors
            doors = new ArrayList<>();
            double[] offsetIntervals = {0, 0.5, 1, 0.5};
            for (int doorIdx = 0; doorIdx < room.getDoors().size(); ++doorIdx) {
                double doorOffsetX = (roomRect.getWidth() * offsetIntervals[(doorIdx + 1) % offsetIntervals.length]) - (doorIdx % 2 == 1 ? 0 : lenghtOfDoor / 2);
                double doorOffsetY = (roomRect.getHeight() * offsetIntervals[doorIdx % offsetIntervals.length]) - (doorIdx % 2 == 0 ? 0 : lenghtOfDoor / 2);
                double doorAngleStart = (doorIdx % 2 == 0 ? (doorIdx + 2) : doorIdx) * 90.0;
                double doorAngleEnd = (room.getDoors().get(doorIdx).getOpen() ? angleOfDoorOpened : angleOfDoorClosed);
                Arc door = new Arc(doorOffsetX, doorOffsetY, lenghtOfDoor, lenghtOfDoor, doorAngleStart, doorAngleEnd);
                door.setType(ArcType.ROUND);
                door.setFill(Color.BROWN);
                doors.add(door);
            }

            // Adding Windows
            ArrayList<Rectangle> windows = new ArrayList<>();
            offsetIntervals = new double[]{0, 0.75, 1, 0.25};
            for (int windowIdx = 0; windowIdx < room.getWindows().size(); ++windowIdx) {
                double windowOffsetX = (roomRect.getWidth() * offsetIntervals[(windowIdx + 1) % offsetIntervals.length]) - (windowIdx % 2 == 1 ? (windowIdx % 4 == 1 ? thickOfWindow : 0) : lengthOfWindow / 2);
                double windowOffsetY = (roomRect.getHeight() * offsetIntervals[windowIdx % offsetIntervals.length]) - (windowIdx % 2 == 0 ? (windowIdx % 4 == 0 ? 0 : thickOfWindow) : lengthOfWindow / 2);
                Rectangle window = new Rectangle((windowIdx % 2 == 1 ? thickOfWindow : lengthOfWindow), (windowIdx % 2 == 0 ? thickOfWindow : lengthOfWindow), (room.getWindows().get(windowIdx).getOpen() ? Color.GREEN : Color.DEEPSKYBLUE));
                window.setX(windowOffsetX);
                window.setY(windowOffsetY);
                windows.add(window);
            }

            // Adding People
            people = new ArrayList<>();
            for (int personIdx = 0; personIdx < room.getPeople().size(); ++personIdx) {
                double personOffsetX = ((roomRect.getWidth() - widthOfPerson) / 2) + widthOfPerson * Math.sin(((personIdx + 1) * Math.PI * 2) / room.getPeople().size());
                double personOffsetY = ((roomRect.getHeight() - heightOfPerson) / 2) + widthOfPerson * Math.cos(((personIdx + 1) * Math.PI * 2) / room.getPeople().size());
                Rectangle person = new Rectangle(widthOfPerson, heightOfPerson, new ImagePattern(AssetManager.getPersonImage()));
                person.setX(personOffsetX);
                person.setY(personOffsetY);
                people.add(person);
            }
            
            // Adding Lights
            lights = new ArrayList<>();
            for (int lightIdx = 0; lightIdx < room.getLights().size(); ++lightIdx) {
                double lightOffsetX = ((roomRect.getWidth() - widthOfLight) / 2) + widthOfLight * Math.sin(((lightIdx + 1) * Math.PI * 2) / room.getLights().size());
                double lightOffsetY = ((roomRect.getHeight() - heightOfLight) / 2) + widthOfLight * Math.cos(((lightIdx + 1) * Math.PI * 2) / room.getLights().size());
                Rectangle light = new Rectangle(widthOfLight, heightOfLight, new ImagePattern((room.getLights().get(lightIdx).getIsOn() ? AssetManager.getLightOnImage() : AssetManager.getLightOffImage())));
                light.setX(lightOffsetX);
                light.setY(lightOffsetY);
                lights.add(light);
            }
            
            // Adding Heater and AC
            ArrayList<Rectangle> havcs = new ArrayList<>();
            if (room.getAcOn() || room.getHeaterOn()){
                
                ImagePattern havcImage = null;
                if (room.getAcOn()) {
                    havcImage = new ImagePattern(AssetManager.getACImage());
                }else if (room.getHeaterOn()){
                    havcImage = new ImagePattern(AssetManager.getHeaterImage());
                }
                
                double havcOffsetX = ((roomRect.getWidth() - widthOfHAVC - 10));
                double havcOffsetY = ((roomRect.getHeight() - heightOfHAVC - 10));
                Rectangle havc = new Rectangle(widthOfHAVC, heightOfHAVC, havcImage);
                havc.setX(havcOffsetX);
                havc.setY(havcOffsetY);
                havcs.add(havc);
            }
            
            roomPane = new AnchorPane(roomRect, text);
            roomPane.getChildren().addAll(doors.toArray(new Arc[doors.size()]));
            roomPane.getChildren().addAll(people.toArray(new Rectangle[people.size()]));
            roomPane.getChildren().addAll(windows.toArray(new Rectangle[windows.size()]));
            roomPane.getChildren().addAll(lights.toArray(new Rectangle[lights.size()]));
            roomPane.getChildren().addAll(havcs.toArray(new Rectangle[havcs.size()]));
            roomPane.setLayoutX(initOffsetX + (((roomIdx - 1) % numOfRoomsWide) * lengthOfRoom));
            roomPane.setLayoutY(initOffsetY + ((Math.floor((roomIdx - 1) / numOfRoomsWide) + 1) * lengthOfRoom));
            root.getChildren().add(roomPane);
        }
        /*
        
         Entrance room is created as an indoor room. No need to create it again.
        
         // Create Entrance room
         Room entrance = rooms.get(rooms.size() - 2);
         roomRect = new Rectangle(lengthOfRoom, lengthOfRoom, colors.get((rooms.size() - 3) % colors.size()));
         text = new Label(entrance.getName());
         text.setLayoutX((lengthOfRoom - 50) / 2);
         text.setLayoutY((lengthOfRoom - 20) / 2);
         // Adding Doors
         doors = new ArrayList<>();
         double[] offsetIntervals = {1, 0.5, 0, 0.5};
         for (int doorIdx = 0; doorIdx < entrance.getDoors().size(); ++doorIdx) {
         double doorOffsetX = (roomRect.getWidth() * offsetIntervals[(doorIdx + 1) % offsetIntervals.length]) - (doorIdx % 2 == 1 ? 0 : lenghtOfDoor / 2);
         double doorOffsetY = (roomRect.getHeight() * offsetIntervals[doorIdx % offsetIntervals.length]) - (doorIdx % 2 == 0 ? 0 : lenghtOfDoor / 2);
         double doorAngleStart = (doorIdx % 2 == 0 ? (doorIdx + 2) : doorIdx) * 90.0;
         double doorAngleEnd = (entrance.getDoors().get(doorIdx).getOpen() ? angleOfDoorOpened : angleOfDoorClosed);
         Arc door = new Arc(doorOffsetX, doorOffsetY, lenghtOfDoor, lenghtOfDoor, doorAngleStart, doorAngleEnd);
         door.setType(ArcType.ROUND);
         door.setFill(Color.BROWN);
         doors.add(door);
         }
         // Adding Windows
         ArrayList<Rectangle> windows = new ArrayList<>();
         offsetIntervals = new double[]{1, 0.75, 0, 0.25};
         for (int windowIdx = 0; windowIdx < entrance.getWindows().size(); ++windowIdx) {
         double windowOffsetX = (roomRect.getWidth() * offsetIntervals[(windowIdx + 1) % offsetIntervals.length]) - (windowIdx % 2 == 1 ? thickOfWindow / 2 : lengthOfWindow / 2);
         double windowOffsetY = (roomRect.getHeight() * offsetIntervals[windowIdx % offsetIntervals.length]) - (windowIdx % 2 == 0 ? thickOfWindow / 2 : lengthOfWindow / 2);
         Rectangle window = new Rectangle((windowIdx % 2 == 1 ? thickOfWindow : lengthOfWindow), (windowIdx % 2 == 0 ? thickOfWindow : lengthOfWindow), (entrance.getWindows().get(windowIdx).getOpen() ? Color.GREEN : Color.DEEPSKYBLUE));
         window.setX(windowOffsetX);
         window.setY(windowOffsetY);
         windows.add(window);
         }
         // Adding People
         people = new ArrayList<>();
         for (int personIdx = 0; personIdx < entrance.getPeople().size(); ++personIdx) {
         double personOffsetX = ((roomRect.getWidth() - widthOfPerson) / 2) + widthOfPerson * Math.sin(((personIdx + 1) * Math.PI * 2) / entrance.getPeople().size());
         double personOffsetY = ((roomRect.getHeight() - heightOfPerson) / 2) + widthOfPerson * Math.cos(((personIdx + 1) * Math.PI * 2) / entrance.getPeople().size());
         Rectangle person = new Rectangle(widthOfPerson, heightOfPerson, new ImagePattern(AssetManager.getPersonImage()));
         person.setX(personOffsetX);
         person.setY(personOffsetY);
         people.add(person);
         }
         roomPane = new AnchorPane(roomRect, text);
         roomPane.getChildren().addAll(doors.toArray(new Arc[doors.size()]));
         roomPane.getChildren().addAll(people.toArray(new Rectangle[people.size()]));
         roomPane.getChildren().addAll(windows.toArray(new Rectangle[windows.size()]));
         roomPane.setLayoutX(initOffsetX + Math.min((((rooms.size() - 4) % numOfRoomsWide) * lengthOfRoom), (Math.floor(numOfRoomsWide / 2) * lengthOfRoom)));
         roomPane.setLayoutY(initOffsetY + ((Math.floor((rooms.size() - 4) / numOfRoomsWide) + 2) * lengthOfRoom));
         root.getChildren().add(roomPane);
         */
        // Create Outside room
        Room outside = rooms.get(rooms.size() - 1);
        roomRect = new Rectangle(widthOfHouse, lengthOfRoom, Color.WHITE);
        text = new Label(outside.getName());
        text.setLayoutX((widthOfHouse - 50) / 2);
        text.setLayoutY((lengthOfRoom - 20) / 2);
        // Adding Doors
        doors = new ArrayList<>();
        for (int doorIdx = 0; doorIdx < outside.getDoors().size(); ++doorIdx) {
            double doorOffsetX = (roomRect.getWidth() / (outside.getDoors().size() + 1)) * (doorIdx + 1);
            double doorAngle = (outside.getDoors().get(doorIdx).getOpen() ? angleOfDoorOpened : angleOfDoorClosed);
            Arc door = new Arc(doorOffsetX, lengthOfRoom, lenghtOfDoor, lenghtOfDoor, 180.0, doorAngle);
            door.setType(ArcType.ROUND);
            door.setFill(Color.BROWN);
            doors.add(door);
        }
        roomPane = new AnchorPane(roomRect, text);
        roomPane.getChildren().addAll(doors.toArray(new Arc[doors.size()]));
        roomPane.setLayoutX(initOffsetX);
        roomPane.setLayoutY(initOffsetY + ((Math.floor((rooms.size() - 4) / numOfRoomsWide) + 2) * lengthOfRoom));
        root.getChildren().add(roomPane);

        if (roomPane.getLayoutY() > root.getHeight() - (initOffsetY * 2)) {
            root.setPrefHeight(roomPane.getLayoutY() + (initOffsetY * 2));
        }

        return true;
    }
    //setting up a doorfunction to make code cleaner

}
