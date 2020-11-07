package gui;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.stage.WindowEvent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.Event;
import javafx.collections.FXCollections;

import HouseObjects.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import simulation.*;

/**
 * FXML Controller class
 *
 * @author d_ruiz-cigana
 */
public class EditFormController implements Initializable {

    @FXML
    Pane parentPane;

    @FXML
    Label title;
    @FXML
    TextField usernameInput;
    @FXML
    TextField passwordInput;
    @FXML
    CheckBox isAdmin;
    @FXML
    ComboBox accessibility;
    @FXML
    AnchorPane locationPane;
    @FXML
    ImageView userProfile;
    @FXML
    ComboBox location;
    @FXML
    Button saveButton;
    @FXML
    Button deleteButton;
    @FXML
    Label output;

    @FXML
    ComboBox moduleSelector;
    @FXML
    VBox modulePermissions;

    private String access;
    private boolean newUser = false;
    private String profilePic = "";
    private Person userPerson;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        accessibility.getItems().addAll(Arrays.asList(new String[]{"Adult (Family)", "Child (Family)", "Guest", "Stranger"}));
        moduleSelector.getItems().addAll(Driver.simulation.getModuleNames());
        userPerson = Driver.simulationController.editedUser;

        if (userPerson == null) {
            userPerson = new Person();
            title.setText("Create New User");
            newUser = true;
            location.setItems(FXCollections.observableArrayList(Driver.simulation.getRoomNames()));
            profilePic = AssetManager.DEFAULT_USER_IMAGE_URL;
            userProfile.setImage(new Image(profilePic));
            moduleSelector.setDisable(true);
        } else {
            userPerson = new Person(userPerson);
            usernameInput.setText(userPerson.getName());
            usernameInput.setDisable(true);
            passwordInput.setText(Driver.simulationController.accounts.get(userPerson.getName()).getPassword());
            for (int i = 0; i < accessibility.getItems().size(); i++) {
                String item = (String) accessibility.getItems().get(i);
                if (userPerson.getUserTypeAsString().equals(item)) {
                    accessibility.getSelectionModel().select(i);
                    access = (String) accessibility.getSelectionModel().getSelectedItem();
                    break;
                }
            }
            isAdmin.setSelected(userPerson.getIsAdmin());
            location.getItems().add(Driver.simulation.getUserLocation(userPerson.getName()));
            location.getSelectionModel().select(0);
            location.setDisable(true);
            profilePic = Driver.simulationController.accounts.get(userPerson.getName()).getImageURL();
            userProfile.setImage(new Image(profilePic));
        }
    }

    /**
     * Handles events that trigger to change the profile picture. The user must
     * double click for the event to trigger. If successful then a dialog
     * appears to select an image and the URL is saved to the User's account.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleChangeProfilePic(Event event) {
        MouseEvent e = (MouseEvent) event;
        if (e.getClickCount() == 2) {
            FileChooser fileChooserWindow = new FileChooser();
            fileChooserWindow.setTitle("Select User Image");
            fileChooserWindow.setInitialDirectory(new File(System.getProperty("user.dir")));
            fileChooserWindow.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("image", "*.jpg", "*.png", "*.gif", "*.bmp"), new FileChooser.ExtensionFilter("All Files", "*"));
            File chosenFile = fileChooserWindow.showOpenDialog(SimulationWindowController.editStage);
            // Informs the user that no file was selected.
            if (chosenFile == null || !chosenFile.isFile()) {
                output.setText("Profile Pic: No file was selected");
                // Informs the user that an incorrect file type was selected.
            } else {
                profilePic = "file:///" + chosenFile.getAbsolutePath();
                userProfile.setImage(new Image(profilePic));
            }
        }
    }

    /**
     * Handles events that trigger to save a user. Performs checks to verify
     * that all inputs have been filled. If creating a new user, adds the user
     * to the simulation in that location, otherwise updates the user profile
     * with the desired traits. If completed, it closes the window.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleSave(Event event) {
        if (usernameInput.getText().trim().equals("")) {
            output.setText("Cannot have an empty username");
            event.consume();
            return;
        }
        if (usernameInput.getText().trim().equals("[New User]")) {
            output.setText("Cannot have username be \"[New User]\" since it is a keyword");
            event.consume();
            return;
        }
        if (newUser && Driver.simulationController.accounts.containsKey(usernameInput.getText().trim())) {
            output.setText("Username is already taken");
            event.consume();
            return;
        }
        if (accessibility.getSelectionModel().isEmpty()) {
            output.setText("Must select an accessibility");
            event.consume();
            return;
        }
        if (newUser && location.getSelectionModel().isEmpty()) {
            output.setText("Must set location for new user");
            event.consume();
            return;
        }

        if (newUser && Driver.simulation.getRoom((String) location.getSelectionModel().getSelectedItem()) == null) {
            output.setText("ERROR: This location does not exists");
            event.consume();
            return;
        }

        if (newUser) {
            Driver.simulationController.usersList.getItems().add(Driver.simulationController.usersList.getItems().size() - 1, usernameInput.getText().trim());
            Driver.simulationController.usersList.getSelectionModel().selectLast();
            Driver.simulationController.usersList.getSelectionModel().selectPrevious();
        }
        Driver.simulation.updateUser(usernameInput.getText().trim(), isAdmin.isSelected(), Person.getUserType(access), (String) location.getSelectionModel().getSelectedItem());
        Driver.simulation.getUser(usernameInput.getText().trim()).setModulePermission(userPerson.getPermissions());
        Driver.simulationController.accounts.put(usernameInput.getText().trim(), new SimulationWindowController.Account(usernameInput.getText().trim(), passwordInput.getText(), profilePic));
        SimulationWindowController.editStage.fireEvent(new WindowEvent(SimulationWindowController.editStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     * Handles events that trigger to delete a user. Verifies that not creating
     * a new user. Removes the user from the simulation and its availability
     * from the GUI. Asks the user if they are sure to delete this user from a
     * confirmation window. If completed, it closes the window.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleDelete(Event event) {
        if (newUser) {
            output.setText("Cannot delete a user not create yet");
            event.consume();
            return;
        }

        Alert continueWindow = new Alert(Alert.AlertType.CONFIRMATION);
        continueWindow.setTitle("Delete User?");
        continueWindow.setHeaderText("Are you sure you wish to delete this user?");
        continueWindow.setContentText("Once you delete this user, they will be completely removed.");

        continueWindow.getButtonTypes().removeAll(continueWindow.getButtonTypes());
        continueWindow.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        continueWindow.showAndWait();
        if (continueWindow.getResult().equals(ButtonType.NO)) {
            event.consume();
            return;
        }
        Driver.simulation.removeUser(usernameInput.getText().trim());
        Driver.simulationController.accounts.remove(usernameInput.getText().trim());
        Driver.simulationController.usersList.getItems().remove(usernameInput.getText().trim());
        SimulationWindowController.editStage.fireEvent(new WindowEvent(SimulationWindowController.editStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     * Handles events that trigger to select a module. Gets the module commands
     * from the current selection of the module selector and updates the
     * permissions of the commands based on the user's attributes.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleSelectModule(Event event) {
        if (access == null) {
            return;
        }
        String moduleName = (String) moduleSelector.getSelectionModel().getSelectedItem();
        ArrayList<String> commands = Driver.simulation.getModuleCommands(Driver.simulation.getModuleFromName(moduleName).getClass());
        modulePermissions.getChildren().remove(0, modulePermissions.getChildren().size());

        String label = "";
        if (moduleName.contains("SHC")) {
            if (access.equals("Adult (Family)") || access.equals("Stranger")) {
                label = "Permission does not depend on location";
            } else if (access.equals("Child (Family)") || access.equals("Guest")) {
                label = "Permission only applies to current location";
            }
        } else if (moduleName.contains("SHP")) {
        }

        Label descr = new Label(label);
        descr.setPrefHeight(25);
        descr.setWrapText(true);
        modulePermissions.getChildren().add(descr);
        for (String command : commands) {
            CheckBox cb = new CheckBox(command);
            cb.setSelected(userPerson.getModulePermission(Driver.simulation.getModuleFromName(moduleName).getClass(), command));
            cb.setOnAction((e) -> {
                userPerson.updateModulePermission(Driver.simulation.getModuleFromName(moduleName).getClass(), command, cb.isSelected());
            });
            modulePermissions.getChildren().add(cb);
        }
        modulePermissions.applyCss();
        modulePermissions.layout();
    }

    /**
     * Handles events that trigger to select a new accessibility.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleUpdateAccessibility(Event event) {
        access = (String) accessibility.getSelectionModel().getSelectedItem();
        userPerson.setUserType(Person.getUserType(access));
        handleUpdatePermission();
    }

    /**
     * Handles events that trigger to select a new location.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleUpdateLocation(Event event) {
        handleUpdatePermission();
    }

    /**
     * Updates the permission of the user with default permissions. The default
     * commands are the same as creating a new user with the given
     * accessibility, and location.
     */
    private void handleUpdatePermission() {
        if (access == null) {
            return;
        }
        moduleSelector.setDisable(false);

        for (String module : Driver.simulation.getModuleNames()) {
            Class moduleClass = Driver.simulation.getModuleFromName(module).getClass();
            userPerson.initializeDefaultPermissions(moduleClass);
        }

        updateModulePermissionDisplay();
    }

    /**
     * Checks the user's preference for module permissions to update the visual
     * of a module's permission.
     */
    private void updateModulePermissionDisplay() {
        String module = (String) moduleSelector.getSelectionModel().getSelectedItem();
        if (access == null || module == null) {
            return;
        }

        Class moduleClass = Driver.simulation.getModuleFromName(module).getClass();
        String label = "";
        if (moduleClass.equals(SHC_Module.class)) {
            if (access.equals("Adult (Family)") || access.equals("Stranger")) {
                label = "Permission does not depend on location";
            } else if (access.equals("Child (Family)") || access.equals("Guest")) {
                label = "Permission only applies to current location";
            }
        } else if (moduleClass.equals(SHP_Module.class)) {
        }

        for (Node node : modulePermissions.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox check = (CheckBox) node;
                check.setSelected(userPerson.getModulePermission(moduleClass, check.getText()));
            } else {
                ((Label) node).setText(label);
            }
        }
        modulePermissions.applyCss();
        modulePermissions.layout();
    }

}
