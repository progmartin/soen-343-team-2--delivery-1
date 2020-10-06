/**
 * <pre>
 * TODO:
 * - Fix for JAR files:
 * </pre>
 */
package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import gui.AssetManager;
import simulation.Simulation;

/**
 * FXML Controller class
 *
 * @author d_ruizci
 */
public class SimulationWindowController implements Initializable {
    
    // Parent class of all pane types
    @FXML
    Pane parentPane;
    
    @FXML
    Label outputConsole;
    @FXML
    ImageView userProfilePic;

    static Stage editStage;
    private Simulation simulation;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        editStage = null;
        
        // New Simulation or load it
        simulation = loadSimulation();
    }

    @FXML
    private void handleLoadSimulation(Event e) {
        this.simulation = loadSimulation();
    }
    
    @FXML
    private void handleDisplayEditForm(Event e) throws IOException{
        // If edit stage is already exists
        if (editStage != null) {
            // Do nothing with this event
            e.consume();
            return;
        }
        
        // Create a new stage/window
        editStage = new Stage();
        
        try {
            // Load the scene from the fxml file
            Parent root = FXMLLoader.load(getClass().getResource("EditForm.fxml"));
            Scene scene = new Scene(root);
            scene.getRoot().requestFocus();

            // Change the attributes if the window
            editStage.setTitle("Create Custom Level");
            while (!editStage.getIcons().isEmpty()) {
                editStage.getIcons().remove(0);
            }
            editStage.getIcons().add(AssetManager.EXAMPLE_IMAGE);
            editStage.setMaxHeight(525.0);
            editStage.setScene(scene);
            editStage.centerOnScreen();
            editStage.setResizable(false);
            
            // Set event if user closes the window (clicks on X)
            editStage.setOnCloseRequest((event) -> {
                // Set the edit stage as removed
                editStage = null;
            });
            
            // Display the stage/window to the user
            editStage.show();
        } catch (IOException ex) {
            System.out.println("Some error caused the document not to load");
            throw ex;
        }
    }
    
    @FXML
    private void handleEditProfilePic(Event e){
        FileChooser fileChooserWindow = new FileChooser();
        fileChooserWindow.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("jpeg", "*.jpg"), new FileChooser.ExtensionFilter("All Files", "*.*"));
        fileChooserWindow.setInitialDirectory(new File("levels"));
        File chosenFile = fileChooserWindow.showOpenDialog(Driver.mainStage);
        // Informs the user that no file was selected.
        if (chosenFile == null || !chosenFile.isFile()) {
            outputConsole.setText("No file was selected");
            // Informs the user that an incorrect file type was selected.
        } else {
            userProfilePic.setImage(null);
        }
    }

    
    // --- HELPER METHODS --- //
    
    private Simulation loadSimulation(){
        return new Simulation();
    }

}
