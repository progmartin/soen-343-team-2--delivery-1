package gui;

import java.io.IOException;
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
