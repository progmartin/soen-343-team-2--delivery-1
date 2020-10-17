/**
 * <pre>
 * TODO:
 * </pre>
 */
package gui;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.*;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author DRC
 */
public class EditFormController implements Initializable {

    @FXML
    Pane parentPane;

    @FXML
    Label values;
    @FXML
    Label title;
    @FXML
    TextField usernameInput;
    @FXML
    TextField passwordInput;
    @FXML
    ComboBox accessibility;
    @FXML
    Button saveButton;
    @FXML
    Label output;

    private double mouseX;
    private double mouseY;

    private boolean isShiftKeyDown;
    private boolean isCtrlKeyDown;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mouseX = 0.0;
        mouseY = 0.0;

        accessibility.getItems().addAll(Arrays.asList(new String[]{"Adult (Family)", "Child (Family)", "Guest", "Stranger"}));

        String[] input = Driver.simulationController.editedUser.split(",");
        if (input.length == 1) {
            title.setText("Create New User");
        } else {
            usernameInput.setText(input[0]);
            usernameInput.setDisable(true);
            passwordInput.setText(input[1]);
            for (int i = 0; i < accessibility.getItems().size(); i++) {
                String item = (String) accessibility.getItems().get(i);
                if (input[2].equals(item)) {
                    accessibility.getSelectionModel().select(i);
                }
            }
        }

    }

    @FXML
    private void handleSave(Event e) {
        if (usernameInput.getText().trim().equals("")) {
            output.setText("Cannot have an empty username");
        }
        if (accessibility.getValue().equals("")) {
            output.setText("Must select an accessibility");
        }
        Driver.simulationController.accounts.put(usernameInput.getText().trim(), new String[]{passwordInput.getText(), (String) accessibility.getSelectionModel().getSelectedItem()});
        if (!usernameInput.isDisabled()) {
            Driver.simulationController.usersList.getItems().add(Driver.simulationController.usersList.getItems().size() - 1, usernameInput.getText().trim());
            Driver.simulationController.usersList.getSelectionModel().selectLast();
            Driver.simulationController.usersList.getSelectionModel().selectPrevious();
        }
        SimulationWindowController.editStage.fireEvent(new WindowEvent(SimulationWindowController.editStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        
    }

    @FXML
    private void handleMouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @FXML
    private void handleKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.SHIFT) {
            isShiftKeyDown = true;
        }
        if (e.getCode() == KeyCode.CONTROL) {
            isCtrlKeyDown = true;
        }
    }

    @FXML
    private void handleKeyReleased(KeyEvent e) {
        if (e.getCode() == KeyCode.SHIFT) {
            isShiftKeyDown = false;
        }
        if (e.getCode() == KeyCode.CONTROL) {
            isCtrlKeyDown = false;
        }
    }

}
