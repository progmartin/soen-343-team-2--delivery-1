package gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.*;
import javafx.stage.Stage;
import simulation.Simulation;
import java.io.File;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.control.*;
import HouseObjects.*;

/**
 *
 * @author d_ruiz-cigana
 */
public class Driver extends Application {

    static Stage mainStage;
    static Scene simulationScene = null;
    static SimulationWindowController simulationController = null;
    public static Simulation simulation;

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {

            FileChooser fileChooserWindow = new FileChooser();
            fileChooserWindow.setTitle("Open House Layout File");
            fileChooserWindow.setInitialDirectory(new File(System.getProperty("user.dir")));
            fileChooserWindow.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("text", "*.txt"), new FileChooser.ExtensionFilter("All Files", "*"));
            File chosenFile = fileChooserWindow.showOpenDialog(Driver.mainStage);
            // Informs the user that no file was selected.
            if (chosenFile == null || !chosenFile.isFile()) {
                System.out.println("No House Layout File was selected try again");
                System.exit(1);
            }

            //ArrayList of rooms
            ArrayList<Room> roomArray = readFile(chosenFile.getPath());
            
            Driver.simulation = new Simulation(roomArray);
            // Set the stage/window to later reference if needed.
            Driver.mainStage = primaryStage;
            
            // Set the simulation scene to swap between scenes if needed.
            Pane root = FXMLLoader.load(getClass().getResource("SimulationWindow.fxml"));
           
            Scene scene = new Scene(root);
            
            scene.getRoot().requestFocus();
            
            Driver.simulationScene = scene;
            
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

    //Passes file name as parameters
    /**
     * Read House Layout file
     *
     * @param f the file name
     * @return a list of Rooms
     */
    public static ArrayList<Room> readFile(String f) {

        ArrayList<Room> rooms = new ArrayList<>();
        //new scanner
        Scanner input = null;

        try {
            //create file input stream
            input = new Scanner(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            //if issue creating stream
            System.out.println("Error reading.");
            System.exit(0);
        }

        int roomCount = 0;
        int doorID = 1;
        int windowID = 1;
        //create room array
        while (input.hasNext()) {

            if (input.next().equals("Room:")) {
                input.next();
                //room name
                String name = input.next() + " ";
                double temp = -1;
                //takes into consideration rooms with names more than one word
                while (temp < 0) {
                    String s = input.next();
                    if (s.equals("Temperature:")) {
                        //room temperature
                        temp = input.nextDouble();
                        break;
                    } else {
                        name = name + s + " ";
                    }
                }

                //create new room object
                rooms.add(new Room(name.trim(), temp));
                input.next();

                //add doors to array in room object
                int nbDoors = input.nextInt();
                for (int d = 0; d < nbDoors; d++) {
                    rooms.get(roomCount).addDoor(new Door(doorID++, false, name.trim() + "-" + doorID));
                }
                input.next();

                //add windows to array in room object
                int nbWindows = input.nextInt();
                for (int w = 0; w < nbWindows; w++) {
                    rooms.get(roomCount).addWindow(new Window(windowID++, false, false));
                }
                roomCount++;
            }
        }
        input.close();
        return rooms;
    }
    //End of readFile

}
