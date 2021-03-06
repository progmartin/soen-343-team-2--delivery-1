package gui;
/*
 Manage Zones: User can add/remove zones, and move rooms from zone to zone.
 Manage Periods: User can add/remove periods between 1 to 3.
 Adjust Temperature: User can change the desired temperature of a zone and its period. User can override a room from its zone and change its desired temperature. User can change the winter and summer away mode temperatures.
 MonitorTemperature: User can monitor the temperature of each room.
 */

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.fxml.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.Event;
import javafx.collections.FXCollections;

import HouseObjects.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import simulation.*;

/**
 * FXML Controller class
 *
 * @author d_ruiz-cigana
 */
public class SimulationWindowController implements Initializable {

    // Parent class of all pane types
    @FXML
    Pane parentPane;

    @FXML
    Pane dashboardPane;

    @FXML
    ScrollPane outputPane;
    @FXML
    Text outputConsole;

    @FXML
    Pane houseViewPane;

    @FXML
    ImageView userProfilePic;
    @FXML
    Label usernameDisplay;
    @FXML
    Label userAccessibilityDisplay;
    @FXML
    Pane locationPane;
    @FXML
    Hyperlink locationLink;
    ComboBox locationOptions = null;
    @FXML
    Label outsideTempDisplay;
    @FXML
    Label insideTempDisplay;
    @FXML
    Label dateDisplay;
    @FXML
    Label timeDisplay;
    @FXML
    Slider timeRateSlider;
    @FXML
    Button editHome;

    @FXML
    TabPane moduleContainer;

    @FXML
    Tab shsTab;
    @FXML
    DatePicker selectDate;
    @FXML
    ComboBox hour;
    @FXML
    ComboBox minute;
    @FXML
    ComboBox second;
    @FXML
    Button updateTime;
    @FXML
    TextField outsideTempInput;
    @FXML
    TextField insideTempInput;
    @FXML
    ComboBox usersList;
    @FXML
    TextField usernameInput;
    @FXML
    Label usernameTag;
    @FXML
    PasswordField passwordInput;
    @FXML
    Label passwordTag;
    @FXML
    Button loginButton;
    @FXML
    Button logoutButton;

    @FXML
    Tab addTab;
    @FXML
    Button shcModuleCreator;
    @FXML
    Button shpModuleCreator;
    @FXML
    Button shhModuleCreator;

    Label shcCommandOptionsLabel;
    VBox shcCommandOptionsPane = null;
    ListView<String> shcCommands = null;

    Label shpCommandOptionsLabel;
    VBox shpCommandOptionsPane = null;
    ListView<String> shpCommands = null;

    Label shhCommandOptionsLabel;
    VBox shhCommandOptionsPane = null;
    ListView<String> shhCommands = null;

    static Stage editStage;
    static Stage editHomeStage;

    protected HashMap<String, Account> accounts = new HashMap<>();
    protected Person editedUser = null;
    private boolean simulationOn;
    private boolean updateSHHOn = false;
    private String loggedInUser;
    private AnimationTimer simulationClock;
    private double timeSpeed;
    private ArrayList<Node> shsControls;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Driver.simulationController = this;
        editStage = null;
        simulationOn = true;

        updateOutsideTemp(15);

        initializeClock();
        initializeControls();

        // Temporary DEFAULT USER for testing users //
        Driver.simulation.addNewUser("Default", true, Person.UserTypes.CHILD, Driver.simulation.getRoomNames().get(0));
        accounts.put("Default", new SimulationWindowController.Account("Default", "", AssetManager.DEFAULT_USER_IMAGE_URL));
        // ************* //
        /*
         Driver.simulation.notifyAllModules();
         if (!RoomObjtoDisplay.drawHouseLayout(houseViewPane, Driver.simulation.getRooms())) {
         writeToConsole("[Initializer] Missing rooms, cannot display house layout");
         }*/

        initializeSHS();

    }

    /**
     * Handles events that trigger to toggle the simulation on and off.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleToggleSimulation(Event event) {
        ToggleButton simulation = (ToggleButton) event.getSource();
        if (simulation.isSelected()) {
            // Simulation was OFF, turning ON
            simulationOn = true;
            for (Node node : shsControls) {
                node.setDisable(false);
            }
            for (Tab tab : moduleContainer.getTabs()) {
                if (!tab.getText().equals("SHS")) {
                    if (tab.equals(addTab) && loggedInUser == null) {
                        continue;
                    }
                    tab.setDisable(false);
                }
            }
            simulationClock.start();

            simulation.setText("ON");
            writeToConsole("[Simulation] Turned ON");

        } else {
            // Simulation was ON, turning OFF
            simulationOn = false;
            simulationClock.stop();
            locationPane.getChildren().remove(locationOptions);
            for (Node node : shsControls) {
                node.setDisable(true);
            }
            for (Tab tab : moduleContainer.getTabs()) {
                if (!tab.equals(shsTab)) {
                    tab.setDisable(true);
                } else {
                    moduleContainer.getSelectionModel().select(tab);
                }
            }

            simulation.setText("OFF");
            writeToConsole("[Simulation] Turned OFF");
        }
    }

    /**
     * Handles events that trigger to edit the profile image. Allows the user to
     * select an image from their machine.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleEditProfilePic(Event event) {
        MouseEvent e = (MouseEvent) event;
        if (e.getClickCount() == 2) {
            FileChooser fileChooserWindow = new FileChooser();
            fileChooserWindow.setTitle("Select User Image");
            fileChooserWindow.setInitialDirectory(new File(System.getProperty("user.dir")));
            fileChooserWindow.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("image", "*.jpg", "*.png", "*.gif", "*.bmp"), new FileChooser.ExtensionFilter("All Files", "*"));
            File chosenFile = fileChooserWindow.showOpenDialog(Driver.mainStage);
            // Informs the user that no file was selected.
            if (chosenFile == null || !chosenFile.isFile()) {
                writeToConsole("[Profile Pic] No file was selected");
                // Informs the user that an incorrect file type was selected.
            } else {
                userProfilePic.setImage(new Image("file:///" + chosenFile.getPath()));
                accounts.get(loggedInUser).changeUserImage("file:///" + chosenFile.getPath());
            }
        }
    }

    /**
     * Handles events that trigger to change the location of the current logged
     * in user. Creates and displays a ComboBox containing the rooms of the
     * simulation excluding the room currently residing. When a choice is
     * selected or canceled, the current location is appropriately updated and
     * the ComboBox is removed.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleChangeLocation(Event event) {
        locationOptions = new ComboBox(FXCollections.observableArrayList(Driver.simulation.getRoomNames()));
        locationOptions.getItems().add("[CANCEL]");
        locationOptions.getItems().remove((String) locationLink.getText());
        locationOptions.setVisibleRowCount(4);
        locationOptions.setPromptText("Select Location");
        locationOptions.setLayoutX(locationLink.getLayoutX());
        locationOptions.setLayoutY(locationLink.getLayoutY());
        locationOptions.setPrefSize(locationLink.getWidth(), locationLink.getHeight());
        locationOptions.setOnAction((e) -> {
            String loc = (String) locationOptions.getSelectionModel().getSelectedItem();
            if (!loc.equals("[CANCEL]")) {
                Driver.simulation.updateUserLocation(loggedInUser, loc);
                locationLink.setText(loc);
            }
            locationPane.getChildren().remove(locationOptions);
            locationOptions = null;
            Driver.simulation.notifyAllModules();
            RoomObjtoDisplay.drawHouseLayout(houseViewPane, Driver.simulation.getRooms());
        });
        locationPane.getChildren().add(locationOptions);
        locationPane.applyCss();
        locationPane.layout();
    }

    /**
     * Handles event that trigger to change the rate at which the simulation
     * time increments. Gets the rate from the time speed slider.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleChangeTimeRate(Event event) {
        timeSpeed = Math.pow(10, 9 - timeRateSlider.getValue());
    }

    /**
     * Handles events that trigger to edit the simulation. Creates a new window
     * to edit the users location, and block windows.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleEditHome(Event event) {
        if (editHomeStage != null) {
            editHomeStage.requestFocus();
            event.consume();
            return;
        }

        editHomeStage = new Stage();
        editHomeStage.initOwner(Driver.mainStage);
        editHomeStage.initModality(Modality.APPLICATION_MODAL);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("EditHomeForm.fxml"));
            Scene scene = new Scene(root);
            scene.getRoot().requestFocus();
            editHomeStage.setTitle("Edit Home Status");
            editHomeStage.setMaxHeight(525.0);
            editHomeStage.setScene(scene);
            editHomeStage.centerOnScreen();
            editHomeStage.setResizable(false);
            editHomeStage.setOnCloseRequest((e) -> {
                // Set the edit stage as removed
                String location = Driver.simulation.getUserLocation(loggedInUser);
                locationLink.setText(location);
                editHomeStage = null;
                if (Driver.simulation.notifyAllModules()) {
                    RoomObjtoDisplay.drawHouseLayout(houseViewPane, Driver.simulation.getRooms());
                }
            });
            editHomeStage.show();
        } catch (IOException ex) {
            editHomeStage = null;
            event.consume();
        }
    }

    /**
     * Handles events that trigger to choose the date. Gets the date value from
     * the source of the event and calls the updateDate method.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleChooseDate(Event event) {
        DatePicker dateChooser = (DatePicker) event.getSource();
        try {
            Driver.simulation.updateSimulationDate(dateChooser.getValue());
            updateDate();
        } catch (NullPointerException ex) {
        }
    }

    /**
     * Handles events that trigger to choose the time. Gets the hour, minute,
     * and second from the fixed ComboBoxes and calls the updateTime method.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleChooseTime(Event event) {
        try {
            int hr = (int) this.hour.getValue();
            int min = (int) this.minute.getValue();
            int sec = (int) this.second.getValue();
            Driver.simulation.updateSimulationTime(LocalTime.of(hr, min, sec));
            updateTime();
        } catch (NullPointerException ex) {
        }

    }

    /**
     * Handles events that trigger to change the outside temperature. Gets the
     * temperature value from the source of the event and call the
     * updateOutsideTemp method.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleChangeTemp(Event event) {
        TextField input = (TextField) event.getSource();
        try {
            if (input.getId().equals(outsideTempInput.getId())) {
                updateOutsideTemp(Double.parseDouble(input.getText()));
            }
        } catch (NumberFormatException ex) {
        }
    }

    /**
     * Handles events that trigger to edit a user. Gets the selected user from
     * the source of the event and creates a new window to edit or create the
     * user.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleEditUser(Event event) {
        ComboBox users = (ComboBox) event.getSource();

        // If edit stage is already exists
        if (editStage != null) {
            editStage.requestFocus();
            event.consume();
            return;
        }
        if (users.getSelectionModel().isEmpty()) {
            event.consume();
            return;
        }

        editStage = new Stage();
        editStage.initOwner(Driver.mainStage);
        editStage.initModality(Modality.APPLICATION_MODAL);

        if (((String) users.getSelectionModel().getSelectedItem()).equals("[New User]")) {
            editedUser = null;
        } else {
            editedUser = Driver.simulation.getUser((String) users.getValue());
        }

        try {
            Parent root = FXMLLoader.load(getClass().getResource("EditForm.fxml"));
            Scene scene = new Scene(root);
            scene.getRoot().requestFocus();

            editStage.setTitle("Edit User");
            editStage.setMaxHeight(525.0);
            editStage.setScene(scene);
            editStage.centerOnScreen();
            editStage.setResizable(false);
            editStage.setOnCloseRequest((e) -> {
                usersList.getSelectionModel().clearSelection();
                editStage = null;
                Driver.simulation.notifyAllModules();
                RoomObjtoDisplay.drawHouseLayout(houseViewPane, Driver.simulation.getRooms());

            });
            editStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            editStage = null;
            event.consume();
        }

    }

    /**
     * Handles events that trigger to save all users to file. Gets all the users
     * from the user selector and their attributes and saves them to a file in
     * XML format. If the user does not select a file then the operation is
     * canceled.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleSaveUserToFile(Event event) {
        FileChooser fileChooserWindow = new FileChooser();
        fileChooserWindow.setTitle("Select User Profile File");
        fileChooserWindow.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooserWindow.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("xml", "*.xml"), new FileChooser.ExtensionFilter("All Files", "*"));
        File chosenFile = fileChooserWindow.showSaveDialog(Driver.mainStage);
        // Informs the user that no file was selected.
        if (chosenFile == null) {
            writeToConsole("[Save Users] No file was selected");
            event.consume();
            return;
        }

        // Building XML file
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            //add elements to Document
            Element rootElement = doc.createElement("Users");
            for (Map.Entry<String, Account> entry : accounts.entrySet()) {
                String username = entry.getKey();
                Account acc = entry.getValue();
                Person user = Driver.simulation.getUser(username);
                Element newUser = doc.createElement("User");
                newUser.appendChild(doc.createElement("username").appendChild(doc.createTextNode(user.getName())).getParentNode());
                newUser.appendChild(doc.createElement("usertype").appendChild(doc.createTextNode(user.getUserTypeAsString())).getParentNode());
                newUser.appendChild(doc.createElement("admin").appendChild(doc.createTextNode(Boolean.toString(user.getIsAdmin()))).getParentNode());
                newUser.appendChild(doc.createElement("room").appendChild(doc.createTextNode(Driver.simulation.getUserLocation(username))).getParentNode());
                newUser.appendChild(doc.createElement("password").appendChild(doc.createTextNode(acc.getPassword())).getParentNode());
                newUser.appendChild(doc.createElement("image").appendChild(doc.createTextNode(acc.getImageURL())).getParentNode());
                Element permissions = doc.createElement("permissions");
                for (Map.Entry<Class, HashMap<String, Boolean>> modulePerm : user.getPermissions().entrySet()) {
                    Element module = doc.createElement("commands");
                    module.setAttribute("module", modulePerm.getKey().getName());
                    for (Map.Entry<String, Boolean> perm : modulePerm.getValue().entrySet()) {
                        String commandName = perm.getKey().replace(" ", "_SPACE_").replace("/", "_SLASH_");
                        module.appendChild(doc.createElement(commandName).appendChild(doc.createTextNode(perm.getValue().toString())).getParentNode());
                    }
                    permissions.appendChild(module);
                }
                newUser.appendChild(permissions);
                rootElement.appendChild(newUser);
            }
            doc.appendChild(rootElement);

            //for output to file, console
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            //write data
            transformer.transform(new DOMSource(doc), new StreamResult(chosenFile));

            writeToConsole("[Save Users] Saving complete");
        } catch (Exception e) {
            writeToConsole("[Save Users] Error saving document");
        }
    }

    /**
     * Handles events that trigger to load all users from file. Gets all the
     * users in an XML formatted file and their attributes. If a user is already
     * in the system then the original user will be kept and it will inform the
     * user of such.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleLoadUserFromFile(Event event) {
        FileChooser fileChooserWindow = new FileChooser();
        fileChooserWindow.setTitle("Select User Profile File");
        fileChooserWindow.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooserWindow.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("xml", "*.xml"), new FileChooser.ExtensionFilter("All Files", "*"));
        File chosenFile = fileChooserWindow.showOpenDialog(Driver.mainStage);
        // Informs the user that no file was selected.
        if (chosenFile == null || !chosenFile.isFile()) {
            writeToConsole("[Load Users] No file was selected");
            // Informs the user that an incorrect file type was selected.
        } else {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                //an instance of builder to parse the specified xml file
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(chosenFile);
                doc.getDocumentElement().normalize();

                NodeList xmlList = doc.getDocumentElement().getElementsByTagName("User");
                for (int i = 0; i < xmlList.getLength(); ++i) {
                    Element element = (Element) xmlList.item(i);
                    String name = element.getElementsByTagName("username").item(0).getTextContent();
                    if (Driver.simulation.getAllUserNames().contains(name)) {
                        writeToConsole("[Load Users] User \"" + name + "\" already exits, keeping original");
                        continue;
                    }

                    Person.UserTypes usertype = Person.getUserType(element.getElementsByTagName("usertype").item(0).getTextContent());
                    boolean isadmin = Boolean.valueOf(element.getElementsByTagName("admin").item(0).getTextContent());
                    String room = element.getElementsByTagName("room").item(0).getTextContent();
                    String password = element.getElementsByTagName("password").item(0).getTextContent();
                    String imageURL = element.getElementsByTagName("image").item(0).getTextContent();
                    Driver.simulation.addNewUser(name, isadmin, usertype, room);
                    accounts.put(name, new Account(name, password, imageURL));
                    usersList.getItems().add(usersList.getItems().size() - 1, name);

                    Person user = Driver.simulation.getUser(name);
                    NodeList perms = element.getElementsByTagName("permissions").item(0).getChildNodes();
                    for (int j = 0; j < perms.getLength(); ++j) {
                        if (perms.item(j).getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {
                            continue;
                        }
                        Element commands = (Element) perms.item(j);
                        String module = commands.getAttribute("module");
                        NodeList coms = commands.getChildNodes();
                        for (int comIdx = 0; comIdx < coms.getLength(); ++comIdx) {
                            if (coms.item(comIdx).getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {
                                continue;
                            }
                            Element command = (Element) coms.item(comIdx);
                            user.updateModulePermission(Class.forName(module), command.getTagName().replace("_SPACE_", " ").replace("_SLASH_", "/"), Boolean.parseBoolean(command.getTextContent()));
                        }
                    }

                }
                writeToConsole("[Load Users] Loading complete");
            } catch (Exception e) {
                writeToConsole("[Load Users] Error loading document");
            }

        }
    }

    /**
     * Handles events that trigger to login a user. Gets the username and
     * password controls to get the values of the username and password. If the
     * username or password do match with an account cancel login, otherwise
     * login the user. If the user is logged in, hide the login function and
     * display the logout function and update the simulation dashboard.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleLogin(Event event) {
        if (usernameInput.getText().trim().equals("")) {
            event.consume();
            return;
        }
        if (!accounts.containsKey(usernameInput.getText().trim())) {
            writeToConsole("[SHS] Username or password is incorrect");
            event.consume();
            return;
        }
        if (!accounts.get(usernameInput.getText().trim()).getPassword().equals(passwordInput.getText())) {
            writeToConsole("[SHS] Username or password is incorrect");
            event.consume();
            return;
        }

        usersList.getSelectionModel().clearSelection();
        loggedInUser = usernameInput.getText().trim();
        userProfilePic.setImage(new Image(accounts.get(loggedInUser).getImageURL()));
        userProfilePic.setDisable(false);
        usernameDisplay.setText(loggedInUser);
        userAccessibilityDisplay.setText("(" + Driver.simulation.getUser(loggedInUser).getUserTypeAsString() + ")");
        usersList.getItems().remove(loggedInUser);
        if (simulationOn) {
            addTab.setDisable(false);
        }

        locationPane.setVisible(true);
        String location = Driver.simulation.getUserLocation(loggedInUser);
        locationLink.setText(location);

        usernameInput.setText("");
        passwordInput.setText("");

        usernameTag.setVisible(false);
        usernameInput.setVisible(false);
        usernameInput.setFocusTraversable(false);
        passwordTag.setVisible(false);
        passwordInput.setVisible(false);
        passwordInput.setFocusTraversable(false);
        loginButton.setVisible(false);
        loginButton.setFocusTraversable(false);
        logoutButton.setVisible(true);
        logoutButton.setFocusTraversable(true);
    }

    /**
     * Handles events that trigger to logout the user. If no user is logged in
     * cancel logout, otherwise logout the user. If the user is logged out, hide
     * the logout function and display the login function and update the
     * simulation dashboard.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleLogout(Event event) {
        if (loggedInUser == null) {
            event.consume();
            return;
        }

        usernameDisplay.setText("Not Logged In");
        userAccessibilityDisplay.setText("");
        usersList.getItems().add(usersList.getItems().size() - 1, loggedInUser);
        userProfilePic.setImage(new Image(AssetManager.DEFAULT_USER_IMAGE_URL));
        userProfilePic.setDisable(true);
        loggedInUser = null;
        locationPane.getChildren().remove(locationOptions);
        locationLink.setText("");
        addTab.setDisable(true);
        for (int i = 0; i < moduleContainer.getTabs().size(); ++i) {
            Tab tab = moduleContainer.getTabs().get(i);
            if (!tab.equals(shsTab) && !tab.equals(addTab)) {
                tab.setDisable(false);
                AnchorPane tabPane = (AnchorPane) tab.getContent();
                for (Node node : tabPane.getChildrenUnmodifiable()) {
                    if (node instanceof Button) {
                        Button b = (Button) node;
                        if (b.getText().equals("Close Module")) {
                            b.fire();
                            --i;
                            break;
                        }
                    }
                }
            }
        }

        usernameTag.setVisible(true);
        usernameInput.setVisible(true);
        usernameInput.setFocusTraversable(true);
        passwordTag.setVisible(true);
        passwordInput.setVisible(true);
        passwordInput.setFocusTraversable(true);
        loginButton.setVisible(true);
        loginButton.setFocusTraversable(true);
        logoutButton.setVisible(false);
        logoutButton.setFocusTraversable(false);
        locationPane.setVisible(false);
    }

    /**
     * Handle events that trigger to insert a new module. Gets the module name
     * from the source of the event, creates a new tab with the module name, and
     * calls createModule method to fill the tab.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleNewModule(Event event) {
        Button module = (Button) event.getSource();
        String moduleStr;
        switch (module.getText()) {
            case "Smart Home Core":
                moduleStr = "SHC";
                break;
            case "Smart Home Security":
                moduleStr = "SHP";
                break;
            case "Smart Home Heating":
                moduleStr = "SHH";
                break;
            default:
                event.consume();
                return;
        }
        Tab moduleTab = new Tab(moduleStr);
        createModule(moduleTab);

        module.setVisible(false);
        module.setManaged(false);
    }

    /**
     * Handles events that trigger to select a SHC item. Gets the room item name
     * from the shcItems ComboBox and determines which name was selected. Clears
     * and fills the selection pane with the available items taken from the
     * simulation.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleSelectSHCItem(Event event) {
        String command = shcCommands.getSelectionModel().getSelectedItem();
        if (command == null) {
            event.consume();
            return;
        }
        Person loggedPerson = Driver.simulation.getUser(loggedInUser);
        SHC_Module mod = (SHC_Module) Driver.simulation.getModuleOfType(SHC_Module.class);

        shcCommandOptionsPane.getChildren().removeAll(shcCommandOptionsPane.getChildren());
        String label = "";
        for (int i = 0; i < command.split(" ").length - 1; ++i) {
            label += " " + command.split(" ")[i];
        }
        shcCommandOptionsLabel.setText(label.trim());

        ArrayList<Room> rooms = new ArrayList<>();
        switch (loggedPerson.getUserType()) {
            case ADULT:
            case STRANGER:
                rooms = Driver.simulation.getRooms();
                break;
            case CHILD:
            case GUEST:
                rooms.add(Driver.simulation.getRoom(Driver.simulation.getUserLocation(loggedInUser)));
                break;
        }
        for (Room room : rooms) {
            if (command.contains("Windows")) {
                for (Window window : room.getWindows()) {
                    CheckBox windowCheck = new CheckBox(window.toString());
                    windowCheck.setSelected(window.getOpen());
                    windowCheck.setFocusTraversable(false);
                    windowCheck.setOnAction((e) -> {
                        if (windowCheck.isSelected()) {
                            if (!mod.openThisWindow(room.getName(), window.getID())) {
                                windowCheck.setSelected(false);
                                writeToConsole("[SHC] Could not open window " + window.toString());
                            }
                        } else {
                            if (!mod.closeThisWindow(room.getName(), window.getID())) {
                                windowCheck.setSelected(true);
                                writeToConsole("[SHC] Could not close window " + window.toString());
                            }
                        }
                        RoomObjtoDisplay.drawHouseLayout(houseViewPane, Driver.simulation.getRooms());
                    });
                    shcCommandOptionsPane.getChildren().add(windowCheck);
                }
            } else if (command.contains("Doors")) {
                for (Door door : room.getDoors()) {
                    CheckBox doorCheck = new CheckBox(door.toString());
                    doorCheck.setFocusTraversable(false);
                    if (command.contains("Lock")) {
                        doorCheck.setSelected(door.getLocked());
                        doorCheck.setOnAction((e) -> {
                            if (doorCheck.isSelected()) {
                                if (!mod.lockThisDoor(room.getName(), door.getID())) {
                                    doorCheck.setSelected(false);
                                    writeToConsole("[SHC] Could not lock door" + door.toString());
                                }
                            } else {
                                if (!mod.unlockThisDoor(room.getName(), door.getID())) {
                                    doorCheck.setSelected(true);
                                    writeToConsole("[SHC] Could not unlock door" + door.toString());
                                }
                            }
                            RoomObjtoDisplay.drawHouseLayout(houseViewPane, Driver.simulation.getRooms());
                        });
                    } else {
                        doorCheck.setSelected(door.getOpen());
                        doorCheck.setOnAction((e) -> {
                            if (doorCheck.isSelected()) {
                                if (!mod.openGarage(room.getName(), door.getID())) {
                                    doorCheck.setSelected(false);
                                    writeToConsole("[SHC] Could not open door" + door.toString());
                                }
                            } else {
                                if (!mod.closeGarage(room.getName(), door.getID())) {
                                    doorCheck.setSelected(true);
                                    writeToConsole("[SHC] Could not close door" + door.toString());
                                }
                            }
                            RoomObjtoDisplay.drawHouseLayout(houseViewPane, Driver.simulation.getRooms());
                        });
                    }
                    shcCommandOptionsPane.getChildren().add(doorCheck);
                }

            } else if (command.contains("Lights")) {
                CheckBox auto = new CheckBox("Auto Mode");
                auto.setSelected(mod.getAutoMode());
                auto.setOnAction((e) -> {
                    mod.setAutoMode(auto.isSelected());
                    if (Driver.simulation.notifyAllModules()) {
                        RoomObjtoDisplay.drawHouseLayout(houseViewPane, Driver.simulation.getRooms());
                    }
                    writeToConsole("[SHC] Auto Mode is turned " + (mod.getAutoMode() ? "on" : "off"));
                });
                shcCommandOptionsPane.getChildren().add(auto);
                for (Light light : room.getLights()) {
                    CheckBox lightCheck = new CheckBox(light.toString());
                    lightCheck.setSelected(light.getIsOn());
                    lightCheck.setFocusTraversable(false);
                    lightCheck.setOnAction((e) -> {
                        if (lightCheck.isSelected()) {
                            if (!mod.turnOnLight(room.getName(), light.getID())) {
                                lightCheck.setSelected(false);
                                writeToConsole("[SHC] Could not turn on light" + light.toString());
                            }
                        } else {
                            if (!mod.turnOffLight(room.getName(), light.getID())) {
                                lightCheck.setSelected(true);
                                writeToConsole("[SHC] Could not turn off light" + light.toString());
                            }
                        }
                        RoomObjtoDisplay.drawHouseLayout(houseViewPane, Driver.simulation.getRooms());
                    });
                    shcCommandOptionsPane.getChildren().add(lightCheck);
                }
            }
        }

        Hyperlink checkAll = new Hyperlink("All");
        checkAll.setOnAction((e) -> {
            for (Node node : shcCommandOptionsPane.getChildren()) {
                if (node instanceof CheckBox) {
                    CheckBox otherChecks = (CheckBox) node;
                    if (!otherChecks.isSelected() && !otherChecks.getText().equals("Auto Mode")) {
                        otherChecks.fire();
                    }
                }
            }
        });
        Hyperlink checkNone = new Hyperlink("None");
        checkNone.setOnAction((e) -> {
            for (Node node : shcCommandOptionsPane.getChildren()) {
                if (node instanceof CheckBox) {
                    CheckBox otherChecks = (CheckBox) node;
                    if (otherChecks.isSelected() && !otherChecks.getText().equals("Auto Mode")) {
                        otherChecks.fire();
                    }
                }
            }
        });
        HBox allNonePane = new HBox(checkAll, new Label(" / "), checkNone);
        shcCommandOptionsPane.getChildren().add(allNonePane);
        shcCommandOptionsPane.applyCss();
        shcCommandOptionsPane.layout();
    }

    /**
     * Handles events that trigger to select a SHP item. Gets the room item name
     * from the shcItems ComboBox and determines which name was selected. Clears
     * and fills the selection pane with the available items taken from the
     * simulation.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void handleSelectSHPItem(Event event) {
        String command = shpCommands.getSelectionModel().getSelectedItem();
        if (command == null) {
            event.consume();
            return;
        }
        shpCommandOptionsPane.getChildren().removeAll(shpCommandOptionsPane.getChildren());
        SHP_Module module = (SHP_Module) Driver.simulation.getModuleOfType(SHP_Module.class);

        if (command.contains("Away Mode")) {
            ToggleButton away = new ToggleButton(command);
            away.setPrefWidth(shpCommandOptionsPane.getWidth() - 25);
            away.setSelected(module.getAwayMode());
            away.setOnAction((e) -> {
                if (away.isSelected()) {
                    if (module.setAwayOn()) {
                        writeToConsole("[SHP] Away mode is turned on");
                    } else {
                        away.setSelected(false);
                        writeToConsole("[SHP] Cannot turn away mode on, people in the house");
                    }
                } else {
                    if (module.setAwayOff()) {
                        writeToConsole("[SHP] Away mode is turned off");
                    }
                }
                if (Driver.simulation.notifyAllModules()) {
                    RoomObjtoDisplay.drawHouseLayout(houseViewPane, Driver.simulation.getRooms());
                }
            });
            shpCommandOptionsPane.getChildren().add(away);

        } else if (command.contains("Light")) {
            TextField fromTimeText = new TextField();
            fromTimeText.setPromptText("ex: 14:23:34");
            VBox fromTimePane = new VBox(new Label("From"), fromTimeText);

            TextField toTimeText = new TextField();
            toTimeText.setPromptText("ex: 20:30:57");
            VBox toTimePane = new VBox(new Label("To"), toTimeText);
            HBox allTimePane = new HBox(fromTimePane, toTimePane);

            Button setTimebutton = new Button("Set Time");
            setTimebutton.setOnAction((e) -> {
                String fromTime = fromTimeText.getText().trim();
                String toTime = toTimeText.getText().trim();
                if (!fromTime.matches("^\\d\\d:\\d\\d:\\d\\d$")) {
                    writeToConsole("[SHP] From time of \"" + fromTime + "\" is not valid");
                    e.consume();
                    return;
                } else if (!toTime.matches("^\\d\\d:\\d\\d:\\d\\d$")) {
                    writeToConsole("[SHP] To time of \"" + toTime + "\" is not valid");
                    e.consume();
                    return;
                }
                try {
                    module.setTimeLightsOn(LocalTime.parse(fromTime), LocalTime.parse(toTime));
                    writeToConsole("[SHP] Time is set for lights in away mode");
                } catch (DateTimeParseException ex) {
                    writeToConsole("[SHP] Time range of \"" + fromTime + "\" to \"" + toTime + "\" is not valid");
                }
            });
            Button removeTimeButton = new Button("Remove Set Time");
            removeTimeButton.setOnAction((e) -> {
                module.resetTimeLightsOn();
                writeToConsole("[SHP] Time is removed for lights in away mode");
            });
            HBox buttons = new HBox(setTimebutton, removeTimeButton);
            VBox timePane = new VBox(new Label("Time Range"), allTimePane, buttons);
            shpCommandOptionsPane.getChildren().add(timePane);

            for (Light light : Driver.simulation.getLights(locationLink.getText().trim())) {
                CheckBox lightCheck = new CheckBox(light.toString());
                lightCheck.setSelected(module.getLights().contains(light));
                lightCheck.setFocusTraversable(false);
                lightCheck.setOnAction((e) -> {
                    if (lightCheck.isSelected()) {
                        module.addLight(light);
                    } else {
                        module.removeLight(light);
                    }
                    if (Driver.simulation.notifyAllModules()) {
                        RoomObjtoDisplay.drawHouseLayout(houseViewPane, Driver.simulation.getRooms());
                    }
                });
                shpCommandOptionsPane.getChildren().add(lightCheck);

            }
            Hyperlink checkAll = new Hyperlink("All");
            checkAll.setOnAction((e) -> {
                for (Node node : shpCommandOptionsPane.getChildren()) {
                    if (node instanceof CheckBox) {
                        CheckBox otherChecks = (CheckBox) node;
                        if (!otherChecks.isSelected()) {
                            otherChecks.fire();
                        }
                    }
                }
            });
            Hyperlink checkNone = new Hyperlink("None");
            checkNone.setOnAction((e) -> {
                for (Node node : shpCommandOptionsPane.getChildren()) {
                    if (node instanceof CheckBox) {
                        CheckBox otherChecks = (CheckBox) node;
                        if (otherChecks.isSelected()) {
                            otherChecks.fire();
                        }
                    }
                }
            });
            HBox allNonePane = new HBox(checkAll, new Label(" / "), checkNone);
            shpCommandOptionsPane.getChildren().add(allNonePane);
        } else if (command.contains("Alert")) {
            TextField timeDelayText = new TextField();
            timeDelayText.setPromptText("ex: 20:30:57");
            Button setTimeDelayButton = new Button("Set Delay");
            setTimeDelayButton.setOnAction((e) -> {
                String timeDelay = timeDelayText.getText().trim();
                if (!timeDelay.matches("^\\d\\d:\\d\\d:\\d\\d$")) {
                    writeToConsole("[SHP] Time delay of \"" + timeDelay + "\" is not valid");
                    e.consume();
                    return;
                }
                try {
                    LocalTime delay = LocalTime.parse(timeDelay);
                    module.setAlertTime((delay.getHour() * 3600) + (delay.getMinute() * 60) + delay.getSecond());
                    writeToConsole("[SHP] Time Delay is set for alerting authorities");
                } catch (DateTimeParseException ex) {
                    writeToConsole("[SHP] Time delay of \"" + timeDelay + "\" is not valid");
                }
            });
            Button resetTimeDelayButton = new Button("Remove Delay");
            resetTimeDelayButton.setOnAction((e) -> {
                module.resetAlertTime();
            });
            shpCommandOptionsPane.getChildren().addAll(new Label("Time Delay"), timeDelayText, setTimeDelayButton, resetTimeDelayButton);
        }

        shpCommandOptionsPane.applyCss();
        shpCommandOptionsPane.layout();
    }

    private void handleSelectSHHItem(Event event) {
        String command = shhCommands.getSelectionModel().getSelectedItem();
        if (command == null) {
            event.consume();
            return;
        }
        shhCommandOptionsPane.getChildren().removeAll(shhCommandOptionsPane.getChildren());
        SHH_Module module = (SHH_Module) Driver.simulation.getModuleOfType(SHH_Module.class);

        if (command.contains("Zones")) {
            Label roomLabel = new Label("Room");
            Label zoneLabel = new Label("Zone");
            HBox row = new HBox(roomLabel, zoneLabel);
            row.setSpacing(30);
            shhCommandOptionsPane.getChildren().add(row);

            for (String roomName : module.getRoomNames()) {
                roomLabel = new Label(roomName);
                ComboBox<String> zoneName = new ComboBox<>(FXCollections.observableArrayList(module.getZoneNames()));
                zoneName.getSelectionModel().select(module.getRoomZone(module.getRoom(roomName)).getName());
                zoneName.setOnAction((e) -> {
                    module.changeZone(module.getRoom(roomName), module.getZone(zoneName.getSelectionModel().getSelectedItem()));
                });
                row = new HBox(roomLabel, zoneName);
                row.setSpacing(10);
                shhCommandOptionsPane.getChildren().add(row);
            }

            Button add = new Button("Add");
            HBox addPane = new HBox(add);
            add.setOnAction((e) -> {
                add.setManaged(false);
                add.setVisible(false);

                VBox newZone = new VBox();

                TextField zoneName = new TextField();
                zoneName.setPromptText("Zone Name");
                newZone.getChildren().add(zoneName);

                TextField[] periods = new TextField[module.getNoPeriods()];
                for (int i = 0; i < module.getNoPeriods(); ++i) {
                    TextField periodTemp = new TextField();
                    periodTemp.setPromptText("Period " + (i + 1) + " Temp");
                    newZone.getChildren().add(periodTemp);
                    periods[i] = periodTemp;
                }

                Button addZone = new Button("Confirm");
                addZone.setOnAction((ev) -> {
                    // validate zone
                    boolean valid = true;
                    for (String zone : module.getZoneNames()) {
                        if (zoneName.getText().equals(zone)) {
                            valid = false;
                            zoneName.getStyleClass().add("redText");
                            zoneName.getStyleClass().remove("blackText");
                            break;
                        } else {
                            zoneName.getStyleClass().add("blackText");
                            zoneName.getStyleClass().remove("redText");
                        }
                    }

                    for (TextField period : periods) {
                        try {
                            Double.parseDouble(period.getText());
                            period.getStyleClass().add("blackText");
                            period.getStyleClass().remove("redText");
                        } catch (NumberFormatException ex) {
                            valid = false;
                            period.getStyleClass().add("redText");
                            period.getStyleClass().remove("blackText");
                            break;
                        }
                    }

                    if (valid) {
                        double[] temps = new double[periods.length];
                        for (int i = 0; i < periods.length; i++) {
                            temps[i] = Double.parseDouble(periods[i].getText());
                        }
                        module.addNewZone(zoneName.getText(), temps);
                        addPane.getChildren().remove(newZone);
                        handleSelectSHHItem(new ActionEvent(shhCommands, Event.NULL_SOURCE_TARGET));
                    }
                });

                Button cancel = new Button("Cancel");
                cancel.setOnAction((ev) -> {
                    addPane.getChildren().remove(newZone);
                    handleSelectSHHItem(new ActionEvent(shhCommands, Event.NULL_SOURCE_TARGET));
                });

                newZone.getChildren().addAll(addZone, cancel);
                addPane.getChildren().add(newZone);
            });
            shhCommandOptionsPane.getChildren().add(addPane);
            ComboBox<String> remove = new ComboBox<>(FXCollections.observableArrayList(module.getZoneNames()));
            if (remove.getItems().size() == 1) {
                remove.setDisable(true);
            }
            remove.setPromptText("Remove Zone");
            remove.setOnAction((ev) -> {
                module.removeZone(remove.getSelectionModel().getSelectedItem());
                handleSelectSHHItem(new ActionEvent(shhCommands, Event.NULL_SOURCE_TARGET));
            });
            shhCommandOptionsPane.getChildren().add(remove);
        } else if (command.contains("Periods")) {

            for (int i = 0; i < module.getNoPeriods(); i++) {
                int period = i;
                Label periodName = new Label("Period " + (period + 1));

                Label fromText = new Label(module.getPeriodStartTime(period));
                Label dash = new Label(" - ");
                Label toText = new Label(module.getPeriodEndTime(period));
                HBox timePeriod = new HBox(fromText, dash, toText);

                VBox periodBox = new VBox(periodName, timePeriod);
                periodBox.getStyleClass().addAll("simulationSubItem");
                shhCommandOptionsPane.getChildren().add(periodBox);
            }

            Button remove = new Button("Remove");
            Button add = new Button("Add");

            remove.setOnAction((e) -> {
                module.removePeriod();
                handleSelectSHHItem(new ActionEvent(shhCommands, Event.NULL_SOURCE_TARGET));
            });
            add.setOnAction((e) -> {
                module.addPeriod();
                handleSelectSHHItem(new ActionEvent(shhCommands, Event.NULL_SOURCE_TARGET));
            });

            if (module.getNoPeriods() == 1) {
                remove.setManaged(false);
                remove.setVisible(false);
            } else if (module.getNoPeriods() == 3) {
                add.setManaged(false);
                add.setVisible(false);
            }
            HBox row = new HBox(add, remove);
            row.setSpacing(10);
            shhCommandOptionsPane.getChildren().add(row);

        } else if (command.contains("Change Temp")) {
            Label zone = new Label("Zone");
            ComboBox<String> zoneSelecter = new ComboBox<>(FXCollections.observableArrayList(module.getZoneNames()));
            HBox row = new HBox(zone, zoneSelecter);
            row.setSpacing(10);
            shhCommandOptionsPane.getChildren().add(row);

            Label period = new Label("Period");
            ArrayList<String> numPeriods = new ArrayList<>();
            for (int i = 1; i <= module.getNoPeriods(); i++) {
                numPeriods.add(String.valueOf(i));
            }
            ComboBox<String> periodSelecter = new ComboBox<>(FXCollections.observableArrayList(numPeriods));
            row = new HBox(period, periodSelecter);
            row.setSpacing(10);
            shhCommandOptionsPane.getChildren().add(row);

            Label tempName = new Label("Temperature");
            TextField tempText = new TextField();
            tempText.setPrefWidth(75);
            tempText.setOnKeyReleased((e) -> {
                try {
                    String zoneName = zoneSelecter.getSelectionModel().getSelectedItem();
                    String periodName = periodSelecter.getSelectionModel().getSelectedItem();
                    if (zoneName != null && periodName != null) {
                        module.setZoneTemp(module.getZone(zoneName), Integer.parseInt(periodName) - 1, Double.parseDouble(tempText.getText()));
                    } else {
                    }
                } catch (NumberFormatException ex) {
                    tempText.getStyleClass().add("redText");
                    tempText.getStyleClass().remove("blackText");
                }
            });
            periodSelecter.setOnAction((e) -> {
                if (zoneSelecter.getSelectionModel().getSelectedItem() != null && periodSelecter.getSelectionModel().getSelectedItem() != null) {
                    tempText.setText(String.format("%.2f", module.getZoneTemp(module.getZone(zoneSelecter.getSelectionModel().getSelectedItem()), Integer.parseInt(periodSelecter.getSelectionModel().getSelectedItem()) - 1)));
                }
            });
            zoneSelecter.setOnAction((e) -> {
                if (zoneSelecter.getSelectionModel().getSelectedItem() != null && periodSelecter.getSelectionModel().getSelectedItem() != null) {
                    tempText.setText(String.format("%.2f", module.getZoneTemp(module.getZone(zoneSelecter.getSelectionModel().getSelectedItem()), Integer.parseInt(periodSelecter.getSelectionModel().getSelectedItem()) - 1)));
                }
            });

            row = new HBox(tempName, tempText);
            row.setSpacing(10);
            shhCommandOptionsPane.getChildren().add(row);

            Separator line = new Separator(Orientation.HORIZONTAL);
            shhCommandOptionsPane.getChildren().add(line);

            VBox overridenRooms = new VBox();
            /*ScrollPane scroll = new ScrollPane(overridenRooms);
             scroll.getStyleClass().add("simulationSubItem");*/
            shhCommandOptionsPane.getChildren().add(overridenRooms);

            Label overriden = new Label("Overridden Rooms");
            overridenRooms.getChildren().add(overriden);

            for (String roomName : module.getOverriddenRooms()) {
                Label roomLabel = new Label(roomName);
                TextField tempTexts = new TextField(String.format("%.2f", module.getOverriddenRoomTemp(roomName)));
                tempTexts.setPrefWidth(75);
                tempTexts.setOnKeyReleased((e) -> {
                    try {
                        module.addOverriddenRoom(roomName, Double.parseDouble(tempTexts.getText()));
                        tempTexts.getStyleClass().add("blackText");
                        tempTexts.getStyleClass().remove("redText");
                    } catch (NumberFormatException ex) {
                        tempTexts.getStyleClass().add("redText");
                        tempTexts.getStyleClass().remove("blackText");
                    }
                });
                Button remove = new Button("Remove");
                HBox newrow = new HBox(roomLabel, tempTexts, remove);

                remove.setOnAction((e) -> {
                    module.removeOverridenRoom(roomName);
                    overridenRooms.getChildren().remove(newrow);
                });
                overridenRooms.getChildren().add(newrow);
            }

            HBox newRoom = new HBox();
            Button add = new Button("Add");
            add.setOnAction((e) -> {
                add.setManaged(false);
                add.setVisible(false);
                ArrayList<String> allRooms = module.getRoomNames();
                allRooms.removeAll(module.getOverriddenRooms());
                ComboBox<String> rooms = new ComboBox<>(FXCollections.observableArrayList(allRooms));
                rooms.setPromptText("Rooms");
                rooms.setOnAction((ev) -> {
                    String roomName = rooms.getSelectionModel().getSelectedItem();
                    module.addOverriddenRoom(roomName, module.getRoomTemp(roomName));
                    overridenRooms.getChildren().remove(rooms);
                    handleSelectSHHItem(new ActionEvent(shhCommands, Event.NULL_SOURCE_TARGET));
                });
                overridenRooms.getChildren().add(rooms);
            });
            newRoom.getChildren().add(add);
            overridenRooms.getChildren().add(newRoom);

            line = new Separator(Orientation.HORIZONTAL);
            shhCommandOptionsPane.getChildren().add(line);

            Label winterLabel = new Label("Winter Temp");
            TextField winterText = new TextField(String.format("%.2f", module.getWinterDefault()));
            winterText.setPrefWidth(75);
            winterText.setOnKeyReleased((e) -> {
                try {
                    module.setWinterDefault(Double.parseDouble(winterText.getText()));
                    winterText.getStyleClass().add("blackText");
                    winterText.getStyleClass().remove("redText");
                } catch (NumberFormatException ex) {
                    winterText.getStyleClass().add("redText");
                    winterText.getStyleClass().remove("blackText");
                }
            });
            HBox winterRow = new HBox(winterLabel, winterText);

            Label summerLabel = new Label("Summer Temp");
            TextField summerText = new TextField(String.format("%.2f", module.getSummerDefault()));
            summerText.setPrefWidth(75);
            summerText.setOnKeyReleased((e) -> {
                try {
                    module.setSummerDefault(Double.parseDouble(summerText.getText()));
                    summerText.getStyleClass().add("blackText");
                    summerText.getStyleClass().remove("redText");
                } catch (NumberFormatException ex) {
                    summerText.getStyleClass().add("redText");
                    summerText.getStyleClass().remove("blackText");
                }
            });
            HBox summerRow = new HBox(summerLabel, summerText);

            shhCommandOptionsPane.getChildren().addAll(winterRow, summerRow);
            
            line = new Separator(Orientation.HORIZONTAL);
            shhCommandOptionsPane.getChildren().add(line);
            
            Label highLabel = new Label("Upper Threshold");
            TextField highText = new TextField(String.format("%.2f", module.getHighThresh()));
            highText.setPrefWidth(75);
            highText.setOnKeyReleased((e) -> {
                try {
                    module.setHighThresh(Double.parseDouble(highText.getText()));
                    highText.getStyleClass().add("blackText");
                    highText.getStyleClass().remove("redText");
                } catch (NumberFormatException ex) {
                    highText.getStyleClass().add("redText");
                    highText.getStyleClass().remove("blackText");
                }
            });
            HBox highRow = new HBox(highLabel, highText);

            Label lowLabel = new Label("Lower Threshold");
            TextField lowText = new TextField(String.format("%.2f", module.getLowThresh()));
            lowText.setPrefWidth(75);
            lowText.setOnKeyReleased((e) -> {
                try {
                    module.setLowThresh(Double.parseDouble(lowText.getText()));
                    lowText.getStyleClass().add("blackText");
                    lowText.getStyleClass().remove("redText");
                } catch (NumberFormatException ex) {
                    lowText.getStyleClass().add("redText");
                    lowText.getStyleClass().remove("blackText");
                }
            });
            HBox lowRow = new HBox(lowLabel, lowText);

            shhCommandOptionsPane.getChildren().addAll(highRow, lowRow);
            
            
        } else if (command.contains("Monitor Temp")) {

            final AnimationTimer updateSHHClock = new AnimationTimer() {
                SHH_Module module = (SHH_Module) Driver.simulation.getModuleOfType(SHH_Module.class);

                @Override
                public void handle(long now) {
                    try {
                        if (shhCommands.getSelectionModel().getSelectedItem().contains("Monitor Temp")) {
                            for (Node elem : shhCommandOptionsPane.getChildren()) {
                                if (elem instanceof HBox) {
                                    HBox check = (HBox) elem;
                                    if (check.getChildren().size() != 2) {
                                        continue;
                                    }
                                    try {
                                        Double.parseDouble(((Label) check.getChildren().get(1)).getText());
                                        ((Label) check.getChildren().get(1)).setText(String.format("%.2f", module.getRoomTemp(((Label) check.getChildren().get(0)).getText())));
                                    } catch (NumberFormatException ex) {
                                    }
                                }
                            }
                        } else {
                            throw new Exception();
                        }
                    } catch (Exception ex) {
                        this.stop();
                        updateSHHOn = false;
                    }
                }
            };

            Label roomLabel = new Label("Room");
            Label tempLabel = new Label("Temperature");
            HBox row = new HBox(roomLabel, tempLabel);
            row.setSpacing(30);
            shhCommandOptionsPane.getChildren().add(row);

            for (String roomName : module.getRoomNames()) {
                roomLabel = new Label(roomName);
                tempLabel = new Label(String.format("%.2f", module.getRoomTemp(roomName)));
                row = new HBox(roomLabel, tempLabel);
                row.setSpacing(10);
                shhCommandOptionsPane.getChildren().add(row);
            }

            Separator line = new Separator(Orientation.HORIZONTAL);
            shhCommandOptionsPane.getChildren().add(line);

            Button update = new Button("Update");
            update.setOnAction((e) -> {
                handleSelectSHHItem(new ActionEvent(shhCommands, Event.NULL_SOURCE_TARGET));
            });
            CheckBox auto = new CheckBox("Auto");
            auto.setSelected(updateSHHOn);
            auto.setOnAction((e) -> {
                if (auto.isSelected()) {
                    updateSHHClock.start();
                    updateSHHOn = true;
                } else {
                    updateSHHClock.stop();
                    updateSHHOn = false;
                }
            });
            shhCommandOptionsPane.getChildren().addAll(update, auto);
        }

        shhCommandOptionsPane.applyCss();
        shhCommandOptionsPane.layout();
    }

    // --- HELPER METHODS --- //
    /**
     * Initializes the simulation clock to update the time every second.
     */
    private void initializeClock() {
        this.timeSpeed = Math.pow(10, 9);
        simulationClock = new AnimationTimer() {
            long prev = 0;

            @Override
            public void handle(long now) {
                if ((long) (prev / timeSpeed) != (long) (now / timeSpeed)) {
                    Driver.simulation.incrementSimulationTime();
                    updateDate();
                    updateTime();
                    if (Driver.simulation.notifyAllModules()) {
                        RoomObjtoDisplay.drawHouseLayout(houseViewPane, Driver.simulation.getRooms());
                        SHP_Module mod = (SHP_Module) Driver.simulation.getModuleOfType(SHP_Module.class);
                        String contact = mod.contactAuthorities();
                        if (!contact.equals("")) {
                            writeToConsole("[SHP] " + contact);
                        }
                        SHH_Module shh = (SHH_Module) Driver.simulation.getModuleOfType(SHH_Module.class);
                        contact = shh.contactUser();
                        if (!contact.equals("")) {
                            writeToConsole("[SHH] " + contact);
                        }
                        contact = shh.noWindow();
                        if (!contact.equals("")) {
                            writeToConsole("[SHH] " + contact);
                        }
                        
                        
                    }
                }
                prev = now;
            }
        };
        simulationClock.start();
    }

    /**
     * Initializes list of controls to be disabled when simulation is stopped.
     */
    private void initializeControls() {
        shsControls = new ArrayList<>();
        Collections.addAll(shsControls, locationLink, editHome, selectDate, hour, minute, second, updateTime, outsideTempInput, usersList);
    }

    /**
     * Initializes the SHS tab. Fills all the ComboBoxes with their respective
     * values.
     */
    private void initializeSHS() {

        List<Integer> times = IntStream.of(IntStream.range(0, 24).toArray()).boxed().collect(Collectors.toList());
        hour.getItems().addAll(times);
        times = IntStream.of(IntStream.range(0, 60).toArray()).boxed().collect(Collectors.toList());
        minute.getItems().addAll(FXCollections.observableArrayList(times));
        second.getItems().addAll(FXCollections.observableArrayList(times));

        loggedInUser = null;
        usersList.getItems().addAll(FXCollections.observableArrayList(Driver.simulation.getAllUserNames()));
        usersList.getItems().add("[New User]");
        locationPane.setVisible(false);
        addTab.setDisable(true);
    }

    /**
     * Writes a message to the console for the user to see. Gets the current
     * time of the simulation and displays the message after it.
     *
     * @param text the text to be displayed on the console
     */
    private void writeToConsole(String text) {
        String[] times = getTime().split(":");
        String time = "[" + times[0] + ":" + times[1] + "] ";
        outputConsole.setText(outputConsole.getText() + "\n" + time + text);
        outputPane.setVvalue(1);
    }

    /**
     * Updates the outside temperature display. Displays the temperature to the
     * dashboard.
     *
     * @param temp the temperature in degrees Celsius
     */
    private void updateOutsideTemp(double temp) {
        Driver.simulation.getRoom("Outside").setTemp(temp);
        outsideTempDisplay.setText(String.format("Outside Temp. %.2f" + "\u00B0" + "C", temp));
    }

    /**
     * Updates the date display. Displays the date to the dashboard.
     *
     * @param date the date to be set
     */
    private void updateDate() {
        dateDisplay.setText(Driver.simulation.getSimulationTime().format(DateTimeFormatter.ofPattern("EEE MMM d YYY")));
    }

    /**
     * Updates the time display. Displays the time to the dashboard.
     *
     * @param hour the hour to be set
     * @param min the minute to be set
     * @param sec the second to be set
     */
    private void updateTime() {
        timeDisplay.setText(Driver.simulation.getSimulationTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    /**
     * Gets the simulation time from the dashboard in JDBC time escape format
     * "hr:min:sec". Where hr, min, and sec are integers.
     *
     * @return the time as a String
     */
    private String getTime() {
        return timeDisplay.getText().trim();
    }

    /**
     * Gets the simulation date from the dashboard in the form "DAY MONTH DATE
     * YEAR". Where DAY is the day of the week in short text style, MONTH is the
     * month name in short text style, DATE is the numerical day of the month,
     * and YEAR is the numerical year.
     *
     * @return the date as a String
     */
    private String getDate() {
        return dateDisplay.getText().trim();
    }

    /**
     * Fills the module tab with the specific controls. Determines which module
     * will be created from the name and inserts elements specific to that
     * module.
     *
     * @param module the module name to be created
     */
    private void createModule(Tab module) {
        Person loggedPerson = Driver.simulation.getUser(loggedInUser);
        moduleContainer.getTabs().add(moduleContainer.getTabs().size() - 1, module);

        AnchorPane topPane = new AnchorPane();
        module.setContent(topPane);
        moduleContainer.applyCss();
        moduleContainer.layout();
        if (module.getText().equals("SHC")) {
            AnchorPane itemsPane = new AnchorPane();
            itemsPane.getStyleClass().add("simulationSubItem");
            itemsPane.setPrefSize(topPane.getWidth() - 30, 120);
            topPane.getChildren().add(itemsPane);
            topPane.applyCss();
            topPane.layout();

            Label itemLabel = new Label("Commands");
            itemLabel.setPrefSize(itemsPane.getWidth(), 20);
            itemLabel.getStyleClass().add("moduleItemTitle");
            itemLabel.setFocusTraversable(false);
            itemsPane.getChildren().add(itemLabel);

            shcCommands = new ListView();
            for (String commands : Driver.simulation.getModuleCommands(SHC_Module.class)) {
                if (loggedPerson.getModulePermission(SHC_Module.class, commands)) {
                    shcCommands.getItems().add(commands);
                }
            }
            shcCommands.setPrefSize(itemsPane.getWidth() - 5, itemsPane.getHeight() - 25);
            shcCommands.setFocusTraversable(false);
            shcCommands.setOnMouseClicked((event) -> {
                handleSelectSHCItem(event);
            });
            itemsPane.getChildren().add(shcCommands);

            shcCommandOptionsLabel = new Label("Options");
            shcCommandOptionsLabel.setPrefSize(topPane.getWidth(), 20);
            shcCommandOptionsLabel.getStyleClass().add("moduleItemTitle");
            shcCommandOptionsLabel.setFocusTraversable(false);
            topPane.getChildren().add(shcCommandOptionsLabel);

            shcCommandOptionsPane = new VBox();
            shcCommandOptionsPane.getStyleClass().add("moduleVBox");
            shcCommandOptionsPane.setPrefSize(itemsPane.getWidth(), 180);
            ScrollPane scroll = new ScrollPane(shcCommandOptionsPane);
            scroll.getStyleClass().add("simulationSubItem");
            scroll.setPrefSize(itemsPane.getWidth(), 300);
            topPane.getChildren().add(scroll);

            moduleContainer.applyCss();
            moduleContainer.layout();
            itemsPane.setLayoutX(15);
            itemsPane.setLayoutY(30);
            itemLabel.setLayoutX(0);
            itemLabel.setLayoutY(0);
            shcCommands.setLayoutX(2.5);
            shcCommands.setLayoutY(20);
            shcCommandOptionsLabel.setLayoutX(0);
            shcCommandOptionsLabel.setLayoutY(itemsPane.getLayoutY() + itemsPane.getHeight() + 20);
            scroll.setLayoutX(15);
            scroll.setLayoutY(shcCommandOptionsLabel.getLayoutY() + shcCommandOptionsLabel.getHeight() + 10);

        } else if (module.getText().equals("SHP")) {
            AnchorPane itemsPane = new AnchorPane();
            itemsPane.getStyleClass().add("simulationSubItem");
            itemsPane.setPrefSize(topPane.getWidth() - 30, 120);
            topPane.getChildren().add(itemsPane);
            topPane.applyCss();
            topPane.layout();

            Label itemLabel = new Label("Commands");
            itemLabel.setPrefSize(itemsPane.getWidth(), 20);
            itemLabel.getStyleClass().add("moduleItemTitle");
            itemLabel.setFocusTraversable(false);
            itemsPane.getChildren().add(itemLabel);

            shpCommands = new ListView();
            for (String commands : Driver.simulation.getModuleCommands(SHP_Module.class)) {
                if (loggedPerson.getModulePermission(SHP_Module.class, commands)) {
                    shpCommands.getItems().add(commands);
                }
            }
            shpCommands.setPrefSize(itemsPane.getWidth() - 5, itemsPane.getHeight() - 25);
            shpCommands.setFocusTraversable(false);
            shpCommands.setOnMouseClicked((event) -> {
                handleSelectSHPItem(event);
            });
            itemsPane.getChildren().add(shpCommands);

            shpCommandOptionsLabel = new Label("Options");
            shpCommandOptionsLabel.setPrefSize(topPane.getWidth(), 20);
            shpCommandOptionsLabel.getStyleClass().add("moduleItemTitle");
            shpCommandOptionsLabel.setFocusTraversable(false);
            topPane.getChildren().add(shpCommandOptionsLabel);

            shpCommandOptionsPane = new VBox();
            shpCommandOptionsPane.getStyleClass().addAll("moduleVBox");
            shpCommandOptionsPane.setPrefSize(itemsPane.getWidth(), 180);
            ScrollPane scroll = new ScrollPane(shpCommandOptionsPane);
            scroll.getStyleClass().add("simulationSubItem");
            scroll.setPrefSize(itemsPane.getWidth(), 300);
            topPane.getChildren().add(scroll);

            moduleContainer.applyCss();
            moduleContainer.layout();
            itemsPane.setLayoutX(15);
            itemsPane.setLayoutY(30);
            itemLabel.setLayoutX(0);
            itemLabel.setLayoutY(0);
            shpCommands.setLayoutX(2.5);
            shpCommands.setLayoutY(20);
            shpCommandOptionsLabel.setLayoutX(0);
            shpCommandOptionsLabel.setLayoutY(itemsPane.getLayoutY() + itemsPane.getHeight() + 20);
            scroll.setLayoutX(15);
            scroll.setLayoutY(shpCommandOptionsLabel.getLayoutY() + shpCommandOptionsLabel.getHeight() + 10);
        } else if (module.getText().equals("SHH")) {
            AnchorPane itemsPane = new AnchorPane();
            itemsPane.getStyleClass().add("simulationSubItem");
            itemsPane.setPrefSize(topPane.getWidth() - 30, 120);
            topPane.getChildren().add(itemsPane);
            topPane.applyCss();
            topPane.layout();

            Label itemLabel = new Label("Commands");
            itemLabel.setPrefSize(itemsPane.getWidth(), 20);
            itemLabel.getStyleClass().add("moduleItemTitle");
            itemLabel.setFocusTraversable(false);
            itemsPane.getChildren().add(itemLabel);

            shhCommands = new ListView();
            for (String commands : Driver.simulation.getModuleCommands(SHH_Module.class)) {
                if (loggedPerson.getModulePermission(SHH_Module.class, commands)) {
                    shhCommands.getItems().add(commands);
                }
            }
            shhCommands.setPrefSize(itemsPane.getWidth() - 5, itemsPane.getHeight() - 25);
            shhCommands.setFocusTraversable(false);
            shhCommands.setOnMouseClicked((event) -> {
                handleSelectSHHItem(event);
            });
            itemsPane.getChildren().add(shhCommands);

            shhCommandOptionsLabel = new Label("Options");
            shhCommandOptionsLabel.setPrefSize(topPane.getWidth(), 20);
            shhCommandOptionsLabel.getStyleClass().add("moduleItemTitle");
            shhCommandOptionsLabel.setFocusTraversable(false);
            topPane.getChildren().add(shhCommandOptionsLabel);

            shhCommandOptionsPane = new VBox();
            shhCommandOptionsPane.getStyleClass().addAll("moduleVBox");
            shhCommandOptionsPane.setPrefSize(itemsPane.getWidth(), 180);
            ScrollPane scroll = new ScrollPane(shhCommandOptionsPane);
            scroll.getStyleClass().add("simulationSubItem");
            scroll.setPrefSize(itemsPane.getWidth(), 300);
            topPane.getChildren().add(scroll);

            moduleContainer.applyCss();
            moduleContainer.layout();
            itemsPane.setLayoutX(15);
            itemsPane.setLayoutY(30);
            itemLabel.setLayoutX(0);
            itemLabel.setLayoutY(0);
            shhCommands.setLayoutX(2.5);
            shhCommands.setLayoutY(20);
            shhCommandOptionsLabel.setLayoutX(0);
            shhCommandOptionsLabel.setLayoutY(itemsPane.getLayoutY() + itemsPane.getHeight() + 20);
            scroll.setLayoutX(15);
            scroll.setLayoutY(shhCommandOptionsLabel.getLayoutY() + shhCommandOptionsLabel.getHeight() + 10);
        }

        Button close = new Button("Close Module");
        close.setFocusTraversable(false);
        close.setOnAction((event) -> {
            Button moduleButton;
            switch (module.getText()) {
                case "SHC":
                    moduleButton = shcModuleCreator;
                    shcCommands = null;
                    shcCommandOptionsPane = null;
                    break;
                case "SHP":
                    moduleButton = shpModuleCreator;
                    shpCommands = null;
                    shpCommandOptionsPane = null;
                    break;
                case "SHH":
                    moduleButton = shhModuleCreator;
                    shhCommands = null;
                    shhCommandOptionsPane = null;
                    break;
                default:
                    event.consume();
                    return;
            }
            moduleButton.setManaged(true);
            moduleButton.setVisible(true);
            moduleContainer.getTabs().remove(module);
        });
        topPane.getChildren().add(close);

        moduleContainer.applyCss();
        moduleContainer.layout();
        close.setLayoutY(topPane.getHeight() - close.getHeight() - 20);
        close.setLayoutX(topPane.getWidth() / 2 - close.getWidth() / 2);

    }

    /**
     * A representation of a User's account.
     */
    public static class Account {

        private String username;
        private String password;
        private String imageURL;

        /**
         * Parameterized constructor.
         *
         * @param username name of the user for this account
         * @param password password for the account
         * @param imageURL image of the account
         */
        public Account(String username, String password, String imageURL) {
            this.username = username;
            this.password = password;
            this.imageURL = imageURL;
        }

        /**
         * Sets the password of the account to a new password.
         *
         * @param password the new password
         */
        public void changePassword(String password) {
            this.password = password;
        }

        /**
         * Sets the image of the account to a new image.
         *
         * @param imageURL the new image
         */
        public void changeUserImage(String imageURL) {
            this.imageURL = imageURL;
        }

        /**
         * Gets the password for the account.
         *
         * @return the password for the account
         */
        public String getPassword() {
            return this.password;
        }

        /**
         * Gets the image for the account.
         *
         * @return the image for the account
         */
        public String getImageURL() {
            return this.imageURL;
        }

        /**
         * Returns if two Accounts are equivalent. If the other object is not an
         * instance of type Account then it returns false. Two Accounts are
         * equivalent if and only if their usernames are equivalent.
         *
         * @param obj the other Account to be compared
         * @return true if the usernames are equivalent; false otherwise
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Account) {
                return this.username.equals(((Account) obj).username);
            }
            return false;
        }

    }

}
