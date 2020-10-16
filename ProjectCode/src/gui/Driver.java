package gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import HouseObjects.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;

import gui.AssetManager;
import java.io.File;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

/**
 *
 * @author d_ruizci
 */
public class Driver extends Application {

    static Stage mainStage;
    static Scene simulationScene = null;
    static Rectangle2D screen = Screen.getPrimary().getVisualBounds();
    public static double screenHeight = Driver.screen.getHeight() - 30.0;
    public static double screenWidth = Driver.screen.getWidth();

    @Override
    public void start(Stage primaryStage) throws IOException{
        try {
            // Set the stage/window to later reference if needed.
            Driver.mainStage = primaryStage;
            
            // Set the simulation scene to swap between scenes if needed.
            Parent root = FXMLLoader.load(getClass().getResource("SimulationWindow.fxml"));
            Scene scene = new Scene(root);
            scene.getRoot().requestFocus();
            Driver.simulationScene = scene;
            
            //ArrayList of rooms
            ArrayList<Room> roomArray = new ArrayList<Room>();
            FileChooser fileChooserWindow = new FileChooser();
            fileChooserWindow.setTitle("Open House Layout File");
            fileChooserWindow.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("text", "*.txt"), new FileChooser.ExtensionFilter("All Files", "*"));
            File chosenFile = fileChooserWindow.showOpenDialog(Driver.mainStage);
            // Informs the user that no file was selected.
            if (chosenFile == null || !chosenFile.isFile()) {
                System.out.println("No House Layout File was selected try again");
                System.exit(1);
                // Informs the user that an incorrect file type was selected.
            }

            String f = "HouseLayout.txt";
            readFile(chosenFile.getPath(), roomArray);
            
            // Change the attributes if the window
            primaryStage.setTitle("Smart Home Simulator");
            while (!primaryStage.getIcons().isEmpty()) {
                primaryStage.getIcons().remove(0);
            }
            //primaryStage.getIcons().add(AssetManager.DEFAULT_USER_IMAGE);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.setResizable(false);
            
            // Set event if user closes the window (clicks on X)
            primaryStage.setOnCloseRequest((event) -> {
                Alert continueWindow = new Alert(Alert.AlertType.CONFIRMATION);
                continueWindow.setTitle("Confirmation");
                continueWindow.setHeaderText("Do you wish to close the simulation?");
                continueWindow.setContentText("All unsaved data will be lost");

                continueWindow.getButtonTypes().remove(0, continueWindow.getButtonTypes().size());
                continueWindow.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
                continueWindow.showAndWait();
                // If the use selects to not close;
                if (continueWindow.getResult().equals(ButtonType.NO)) {
                    event.consume();
                }
            });

            // Display the UI to the user
            primaryStage.show();
        } catch (IOException ex) {
            System.out.println("Some error caused the document not to load");
            throw ex;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    //READ HOUSE LAYOUT FILE
    //Passes file name and room array as parameters
    /**
     * Read House Layout file
     * @param f
     * @param rooms
     */
    public static void readFile(String f, ArrayList<Room> rooms){
    	
    	//new scanner
    	Scanner input = null;
    	
		try{
			//create file input stream
			input = new Scanner(new FileInputStream(f));
		}
		catch(FileNotFoundException e){
			//if issue creating stream
			System.out.println("Error reading.");
			System.exit(0);
		}
		
		int roomCount = 0;
		int doorID = 1;
		int windowID = 1;
		//create room array
		while(input.hasNext()){
			
			if(input.next().equals("Room:")){
				input.next();
				//room name
				String name = input.next() + " ";
				int temp = -1;
				//takes into consideration rooms with names more than one word
				while(temp < 0){
					String s = input.next();
					if(s.equals("Temperature:")){
						//room temperature
						temp = input.nextInt();
						break;
					}
					else{
						name = name + s + " ";
					}
				}
				
				//create new room object
				rooms.add(new Room(name, temp));
				input.next();
				
				//add doors to array in room object
				int nbDoors = input.nextInt();
				for(int d=0;d<nbDoors;d++){
					rooms.get(roomCount).addDoor(new Door(doorID++, false));
				}
				input.next();
				
				//add windows to array in room object
				int nbWindows = input.nextInt();
				for(int w=0;w<nbWindows;w++){
					rooms.get(roomCount).addWindow(new Window(windowID++, false, false));
				}
				roomCount++;
			}
		}
		input.close();
    }
    //End of readFile

}
