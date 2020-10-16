package gui;

import java.io.IOException;
import java.util.ArrayList;

import HouseObjects.Door;
import HouseObjects.Room;
import HouseObjects.Window;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;
import javafx.stage.Stage;
import gui.AssetManager;


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
            Pane root = FXMLLoader.load(getClass().getResource("SimulationWindow.fxml"));
            Scene scene = new Scene(root);
            scene.getRoot().requestFocus();
            Driver.simulationScene = scene;
            
            //Martin-> adding visual buttons   
         // circles
            //Rectangle redCircle = RectangleRoom.createRectangle(root, 50, 50,  Color.RED);
          //  redCircle.setX(550);
           // redCircle.setY(100);
            
            //creating test rooms and doors and windows
            Room r1 = new Room();
            Window w1 = new Window();
            Door d1 = new Door();
            r1.addWindow(w1);
            r1.addDoor(d1);
            
            Room r2 = new Room();
            Window w2 = new Window();
            r2.addWindow(w2);
            r2.addDoor(d1);
            
            Room r3 = new Room();
            Window w3 = new Window();
            Door d3 = new Door();
            r3.addWindow(w3);
            r3.addDoor(d3);
            
            
            ArrayList<Room> roomArray = new ArrayList<Room>();// creating temp roomArrayList
            roomArray.add(r1);//adding all rooms to temp arraylist of room
            roomArray.add(r2);
            roomArray.add(r3);        
            RectangleRoom.createRectangle( root,roomArray);
      	  
            
           

            
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

}
