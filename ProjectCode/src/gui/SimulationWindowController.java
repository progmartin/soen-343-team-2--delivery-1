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
import javafx.scene.text.Text;

import gui.AssetManager;
import java.util.GregorianCalendar;
import java.util.Locale;
import javafx.scene.image.Image;
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
    Text outputConsole;

    @FXML
    ToggleButton toggleSimulation;
    @FXML
    ImageView userProfilePic;
    @FXML
    Label outsideTemp;
    @FXML
    Label dateTime;

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
        updateOutsideTemp(15);
        updateDate(new GregorianCalendar());
        
    }

    @FXML
    private void handleLoadSimulation(Event e) {
        this.simulation = loadSimulation();
    }

    @FXML
    private void handleDisplayEditForm(Event e) throws IOException {
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
    private void handleEditProfilePic(Event e) {
        FileChooser fileChooserWindow = new FileChooser();
        fileChooserWindow.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("image", "*.jpg", "*.png", "*.gif", "*.bmp"), new FileChooser.ExtensionFilter("All Files", "*"));
        File chosenFile = fileChooserWindow.showOpenDialog(Driver.mainStage);
        // Informs the user that no file was selected.
        if (chosenFile == null || !chosenFile.isFile()) {
            writeToConsole("Profile Pic: No file was selected");
            // Informs the user that an incorrect file type was selected.
        } else {
            userProfilePic.setImage(new Image("file:///" + chosenFile.getPath()));
        }
    }

    @FXML
    private void handleToggleSimulation(Event e) {
        if (toggleSimulation.isSelected()) {
            // Simulation is ON
            toggleSimulation.setText("ON");
            writeToConsole("[Simulation] Turned ON");
        } else {
            // Simulation is OFF
            toggleSimulation.setText("OFF");
            writeToConsole("[Simulation] Turned OFF");
        }
    }

    // --- HELPER METHODS --- //
    private Simulation loadSimulation() {
        return new Simulation();
    }

    private void writeToConsole(String text) {
        String[] times = getTime().split(":");
        String time = "[" + times[0] + ":" + times[1] + "] ";
        outputConsole.setText(outputConsole.getText() + "\n" + time + text);
    }

    private void updateOutsideTemp(int temp) {
        outsideTemp.setText("Outside Temp. " + temp + "\u00B0" + "C");
    }

    private void updateDate(GregorianCalendar date) {
        String day = date.getDisplayName(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.SHORT_FORMAT, Locale.CANADA) + " " + date.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.SHORT_FORMAT, Locale.CANADA) + " " + date.get(GregorianCalendar.DAY_OF_MONTH) + " " + date.get(GregorianCalendar.YEAR);
        String time = String.format("%02d:%02d:%02d", date.get(GregorianCalendar.HOUR), date.get(GregorianCalendar.MINUTE), date.get(GregorianCalendar.SECOND));
        dateTime.setText(day + "\n" + time);
    }

    private String getTime() {
        String text = dateTime.getText();
        return text.split("\n")[1];
    }

}
