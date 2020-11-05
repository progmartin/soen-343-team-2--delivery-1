package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ResourceBundle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import javafx.animation.AnimationTimer;
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

    VBox shcOpenClosePane;
    ListView shcItems;

    static Stage editStage;
    static Stage editHomeStage;

    protected HashMap<String, Account> accounts = new HashMap<>();
    protected Person editedUser = null;
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

        updateOutsideTemp(15);

        LocalDateTime today = LocalDateTime.now();
        updateTime(today.getHour(), today.getMinute(), today.getSecond());
        updateDate(today.toLocalDate());
        initializeClock();
        initializeControls();

        // Temporary DEFAULT USER for testing users //
        Driver.simulation.addNewUser("Default", true, Person.UserTypes.CHILD, Driver.simulation.getRoomNames().get(0));
        accounts.put("Default", new Account("Default", "", AssetManager.DEFAULT_USER_IMAGE_URL));
        // ************* //
        
        if (!RoomObjtoDisplay.drawHouseLayout(houseViewPane, Driver.simulation.getRooms())) {
            writeToConsole("[Initializer] Missing rooms, cannot display house layout");
        }
        
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
            for (Node node : shsControls) {
                node.setDisable(false);
            }
            for (Tab tab : moduleContainer.getTabs()) {
                if (!tab.getText().equals("SHS")) {
                    tab.setDisable(false);
                }
            }
            simulationClock.start();

            simulation.setText("ON");
            writeToConsole("[Simulation] Turned ON");

        } else {
            // Simulation was ON, turning OFF
            simulationClock.stop();
            moduleContainer.getSelectionModel().select(shsTab);
            for (Node node : shsControls) {
                node.setDisable(true);
            }
            for (Tab tab : moduleContainer.getTabs()) {
                if (!tab.getText().equals("SHS")) {
                    tab.setDisable(true);
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
                RoomObjtoDisplay.drawHouseLayout(houseViewPane, Driver.simulation.getRooms());
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
            updateDate(dateChooser.getValue());
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
            updateTime(hr, min, sec);
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
                RoomObjtoDisplay.drawHouseLayout(houseViewPane, Driver.simulation.getRooms());
            });
            editStage.show();
        } catch (IOException ex) {
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
                rootElement.appendChild(newUser);
            }
            doc.appendChild(rootElement);

            //for output to file, console
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            //write data
            transformer.transform(new DOMSource(doc), new StreamResult(chosenFile));
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
                    org.w3c.dom.Node node = xmlList.item(i);
                    Element element = (Element) node;
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
                }
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
        usersList.getItems().remove(loggedInUser);

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
        usersList.getItems().add(usersList.getItems().size() - 1, loggedInUser);
        userProfilePic.setImage(new Image(AssetManager.DEFAULT_USER_IMAGE_URL));
        userProfilePic.setDisable(true);
        loggedInUser = null;
        locationPane.getChildren().remove(locationOptions);

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
        String item = (String) shcItems.getSelectionModel().getSelectedItem();
        shcOpenClosePane.getChildren().removeAll(shcOpenClosePane.getChildren());

        if (item.equals("Windows")) {
            for (Window window : Driver.simulation.getWindows(locationLink.getText().trim())) {
                CheckBox windowCheck = new CheckBox(window.name);
                windowCheck.setSelected(window.getOpen());
                windowCheck.setLayoutX(15);
                windowCheck.setFocusTraversable(false);
                windowCheck.setOnAction((e) -> {
                    window.setOpen(windowCheck.isSelected());
                });
                shcOpenClosePane.getChildren().add(windowCheck);
            }
        } else if (item.equals("Doors")) {
            for (Door door : Driver.simulation.getDoors(locationLink.getText().trim())) {
                CheckBox doorCheck = new CheckBox(door.name);
                doorCheck.setSelected(door.getOpen());
                doorCheck.setLayoutX(15);
                doorCheck.setFocusTraversable(false);
                doorCheck.setOnAction((e) -> {
                    door.setOpen(doorCheck.isSelected());
                });
                shcOpenClosePane.getChildren().add(doorCheck);
            }

        } else if (item.equals("Lights")) {

        }
        shcOpenClosePane.applyCss();
        shcOpenClosePane.layout();
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
                    LocalTime currentTime = LocalTime.parse(getTime()).plusSeconds(1);
                    updateTime(currentTime.getHour(), currentTime.getMinute(), currentTime.getSecond());
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
        outsideTempDisplay.setText(String.format("Outside Temp. %.2f" + "\u00B0" + "C", temp));
    }

    private void updateInsideTemp(double temp) {
        insideTempDisplay.setText(String.format("Inside Temp. %.2f" + "\u00B0" + "C", temp));
    }

    /**
     * Updates the date display. Displays the date to the dashboard.
     *
     * @param date the date to be set
     */
    private void updateDate(LocalDate date) {
        String day = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.CANADA) + " " + date.getMonth().getDisplayName(TextStyle.SHORT, Locale.CANADA) + " " + date.getDayOfMonth() + " " + date.getYear();
        dateDisplay.setText(day);
    }

    /**
     * Updates the time display. Displays the time to the dashboard.
     *
     * @param hour the hour to be set
     * @param min the minute to be set
     * @param sec the second to be set
     */
    private void updateTime(int hour, int min, int sec) {
        String time = String.format("%02d:%02d:%02d", hour, min, sec);
        timeDisplay.setText(time);
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

            Label itemLabel = new Label("Item");
            itemLabel.setPrefSize(itemsPane.getWidth(), 20);
            itemLabel.getStyleClass().add("moduleItemTitle");
            itemLabel.setFocusTraversable(false);
            itemsPane.getChildren().add(itemLabel);

            shcItems = new ListView();
            shcItems.getItems().addAll(Arrays.asList(new String[]{"Windows", "Lights", "Doors"}));
            shcItems.setPrefSize(itemsPane.getWidth() - 5, itemsPane.getHeight() - 25);
            shcItems.setFocusTraversable(false);
            shcItems.setOnMouseClicked((event) -> {
                handleSelectSHCItem(event);
            });
            itemsPane.getChildren().add(shcItems);

            Label openCloseLabel = new Label("Open/Close");
            openCloseLabel.setPrefSize(topPane.getWidth(), 20);
            openCloseLabel.getStyleClass().add("moduleItemTitle");
            openCloseLabel.setFocusTraversable(false);
            topPane.getChildren().add(openCloseLabel);

            shcOpenClosePane = new VBox();
            shcOpenClosePane.getStyleClass().addAll("simulationSubItem", "moduleVBox");
            shcOpenClosePane.setPrefSize(itemsPane.getWidth(), 200);
            topPane.getChildren().add(shcOpenClosePane);

            moduleContainer.applyCss();
            moduleContainer.layout();
            itemsPane.setLayoutX(15);
            itemsPane.setLayoutY(30);
            itemLabel.setLayoutX(0);
            itemLabel.setLayoutY(0);
            shcItems.setLayoutX(2.5);
            shcItems.setLayoutY(20);
            openCloseLabel.setLayoutX(0);
            openCloseLabel.setLayoutY(itemsPane.getLayoutY() + itemsPane.getHeight() + 20);
            shcOpenClosePane.setLayoutX(15);
            shcOpenClosePane.setLayoutY(openCloseLabel.getLayoutY() + openCloseLabel.getHeight() + 10);
        }

        Button close = new Button("Close Module");
        close.setFocusTraversable(false);
        close.setOnAction((event) -> {
            Button moduleButton;
            switch (module.getText()) {
                case "SHC":
                    moduleButton = shcModuleCreator;
                    break;
                case "SHP":
                    moduleButton = shpModuleCreator;
                    break;
                case "SHH":
                    moduleButton = shhModuleCreator;
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
