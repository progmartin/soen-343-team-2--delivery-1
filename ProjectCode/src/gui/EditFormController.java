/**
 * <pre>
 * TODO:
 * </pre>
 */
package gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.*;
import javafx.scene.control.Alert;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;

import gui.AssetManager;

/**
 * FXML Controller class
 *
 * @author DRC
 */
public class EditFormController implements Initializable {

    @FXML
    Pane parentPane;

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

    @FXML
    private void handleAbout(Event e) {
        Alert aboutWindow = new Alert(Alert.AlertType.INFORMATION);
        aboutWindow.setTitle("About");
        aboutWindow.setContentText("This is the custom level designer.\n\n"
                + "To place a cell on the grid, simply drag the desired image from the side panel and drop it in the cell grid.\n"
                + "If misplaced an image, click and drag the image to the new location, or to remove the image release over the side panel.\n"
                + "The grid will automatically expand when an image is dragged and dropped above an empty space.\n"
                + "To quick place a single image in multiple cells, hold down the shift button while dragging a desired image, "
                + "and every cell that is hovered over will turn into the chosen image.\n\n"
                + "The scroll wheel will scroll the grid up and down.\n"
                + "If holding down the control button and using the scroll wheel, then the grid will zoom in and out.\n"
                + "If holding down the control button, clicking and dragging while the mouse is on the visual panel, "
                + "will move the grid in the direction dragged.");

        aboutWindow.setWidth(200.0);
        aboutWindow.showAndWait();
    }



}
