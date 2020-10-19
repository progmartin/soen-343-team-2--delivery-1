/**
 * <pre>
 * TODO:
 * - Fix for JAR files:
 * </pre>
 */
package gui;

import HouseObjects.Door;
import HouseObjects.Room;
import HouseObjects.Window;
import com.sun.javafx.scene.control.skin.ScrollPaneSkin;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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
    Pane dashboardPane;

    @FXML
    ScrollPane outputPane;
    @FXML
    Text outputConsole;

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
    Label dateTimeDisplay;

    @FXML
    TabPane moduleContainer;

    @FXML
    ComboBox hour;
    @FXML
    ComboBox minute;
    @FXML
    ComboBox second;
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
    Button shcModuleCreator;
    @FXML
    Button shpModuleCreator;
    @FXML
    Button shhModuleCreator;
    @FXML
    Button editHome;

    VBox shcOpenClosePane;
    ListView shcItems;
    ArrayList<Window> shcWindows;
    ArrayList<Door> shcDoors;
    //ArrayList<Light> shcLights;

    static Stage editStage;
    static Stage editHomeStage;
    private Simulation simulation;

    protected static HashMap<String, String[]> accounts = new HashMap<>();
    protected String editedUser = "";
    private String loggedInUser;

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
        
        // New Simulation or load it
        setSimulation(loadSimulation());
        accounts.put("Default User", new String[]{"", "Adult (Family)"});
        loggedInUser = null;

        updateOutsideTemp(15);
        //updateInsideTemp(21);

        LocalDateTime today = LocalDateTime.now();
        updateTime(today.getHour(), today.getMinute(), today.getSecond());
        updateDate(today.toLocalDate());

        initializeSHS();
        initializeSHC();
       

    }

    @FXML
    private void handleLoadSimulation(Event e) {
        this.setSimulation(loadSimulation());
    }

    @FXML
    private void handleToggleSimulation(Event e) {
        ToggleButton simulation = (ToggleButton) e.getSource();
        if (simulation.isSelected()) {
            // Simulation is ON
            simulation.setText("ON");
            writeToConsole("[Simulation] Turned ON");
        } else {
            // Simulation is OFF
            simulation.setText("OFF");
            writeToConsole("[Simulation] Turned OFF");
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
    private void handleChangeLocation(Event e) {

        writeToConsole("Change Location does nothing until simulation contains rooms");
        ArrayList<String> locations = new ArrayList<>();
        for (Room r : getSimulation().getRooms()) {
            locations.add(r.getName());
        }
        locations.add("TEMP");
        locations.add("Not Real");
        locations.add("location");
        locations.add("[CANCEL]");
        locations.remove(locationLink.getText());

        locationOptions = new ComboBox(FXCollections.observableArrayList(locations));
        locationOptions.setVisibleRowCount(4);
        locationOptions.setPromptText("Select Location");
        locationOptions.setLayoutX(locationLink.getLayoutX());
        locationOptions.setLayoutY(locationLink.getLayoutY());
        locationOptions.setPrefSize(locationLink.getWidth(), locationLink.getHeight());
        locationOptions.setOnAction((event) -> {
            String loc = (String) locationOptions.getSelectionModel().getSelectedItem();
            if (!loc.equals("[CANCEL]")) {
                getSimulation().setUserLocation(loggedInUser, loc);
                locationLink.setText(loc);
            }
            locationPane.getChildren().remove(locationOptions);
            locationOptions = null;
        });
        locationPane.getChildren().add(locationOptions);
        locationPane.applyCss();
        locationPane.layout();
    }

    @FXML
    private void handleChooseDate(Event e) {
        DatePicker dateChooser = (DatePicker) e.getSource();
        try {
            updateDate(dateChooser.getValue());
        } catch (NullPointerException ex) {
        }
    }

    @FXML
    private void handleChooseTime(Event e) {
        try {
            int hr = (int) this.hour.getValue();
            int min = (int) this.minute.getValue();
            int sec = (int) this.second.getValue();
            updateTime(hr, min, sec);
        } catch (NullPointerException ex) {
        }

    }

    @FXML
    private void handleChangeTemp(Event e) {
        TextField input = (TextField) e.getSource();
        try {
            if (input.getId().equals(outsideTempInput.getId())) {
                updateOutsideTemp(Double.parseDouble(input.getText()));
            }
        } catch (NumberFormatException ex) {
        }
    }

    @FXML
    private void handleSelectUser(Event e) {
        ComboBox users = (ComboBox) e.getSource();

        // If edit stage is already exists
        if (editStage != null) {
            e.consume();
            return;
        }
        if (users.getSelectionModel().isEmpty()) {
            e.consume();
            return;
        }

        // Create a new stage/window
        editStage = new Stage();
        if (((String) users.getSelectionModel().getSelectedItem()).equals("[New User]")) {
            editedUser = ((String) users.getValue());
        } else {
            editedUser = ((String) users.getValue() + "," + this.accounts.get((String) users.getValue())[0] + "," + this.accounts.get((String) users.getValue())[1]);
        }

        try {
            // Load the scene from the fxml file
            Parent root = FXMLLoader.load(getClass().getResource("EditForm.fxml"));
            Scene scene = new Scene(root);
            scene.getRoot().requestFocus();

            // Change the attributes if the window
            editStage.setTitle("Edit User");
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
            editStage = null;
            e.consume();
        }

    }
 @FXML   
private void handleEditHome(Event e) {
	 if (editHomeStage != null) {
         e.consume();
         return;
     }
	 
	 editHomeStage=new Stage();
	 
	 try {
         // Load the scene from the fxml file
         Parent root = FXMLLoader.load(getClass().getResource("EditHomeForm.fxml"));
         Scene scene = new Scene(root);
         scene.getRoot().requestFocus();

         // Change the attributes if the window
         editHomeStage.setTitle("Edit Home Status");
         editHomeStage.setMaxHeight(525.0);
         editHomeStage.setScene(scene);
         editHomeStage.centerOnScreen();
         editHomeStage.setResizable(false);

         // Set event if user closes the window (clicks on X)
         editHomeStage.setOnCloseRequest((event) -> {
             // Set the edit stage as removed
             editHomeStage = null;
         });

         // Display the stage/window to the user
         editHomeStage.show();
     } catch (IOException ex) {
    	 ex.printStackTrace();
         editHomeStage = null;
         e.consume();
     }
    }

    @FXML
    private void handleLogin(Event e) {
        if (usernameInput.getText().trim().equals("")) {
            e.consume();
            return;
        }
        if (!accounts.containsKey(usernameInput.getText().trim())) {
            writeToConsole("[SHS] Username or password is incorrect");
            e.consume();
            return;
        }
        if (!accounts.get(usernameInput.getText().trim())[0].equals(passwordInput.getText())) {
            writeToConsole("[SHS] Username or password is incorrect");
            e.consume();
            return;
        }

        usersList.getSelectionModel().clearSelection();
        loggedInUser = usernameInput.getText().trim();
        usernameDisplay.setText(loggedInUser);
        usersList.getItems().remove(loggedInUser);

        locationPane.setVisible(true);
        String location = getSimulation().getUserLocation(loggedInUser);
        locationLink.setText(location);

        usernameInput.setText("");
        passwordInput.setText("");

        usernameTag.setVisible(false);
        usernameInput.setVisible(false);
        passwordTag.setVisible(false);
        passwordInput.setVisible(false);
        loginButton.setVisible(false);
        logoutButton.setVisible(true);
    }

    @FXML
    private void handleLogout(Event e) {
        if (loggedInUser == null) {
            e.consume();
            return;
        }

        usernameDisplay.setText("Not Logged In");
        usersList.getItems().add(usersList.getItems().size() - 1, loggedInUser);
        loggedInUser = null;
        locationPane.getChildren().remove(locationOptions);

        usernameTag.setVisible(true);
        usernameInput.setVisible(true);
        passwordTag.setVisible(true);
        passwordInput.setVisible(true);
        loginButton.setVisible(true);
        logoutButton.setVisible(false);
        locationPane.setVisible(false);
    }

    @FXML
    private void handleNewModule(Event e) {
        Button module = (Button) e.getSource();
        String moduleStr = null;
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
                e.consume();
                return;
        }
        Tab moduleTab = new Tab(moduleStr);
        moduleContainer.getTabs().add(moduleContainer.getTabs().size() - 1, moduleTab);
        createModule(moduleTab);

        module.setVisible(false);
        module.setManaged(false);
    }

    @FXML
    private void handleSelectSHCItem(Event e) {
        String item = (String) shcItems.getSelectionModel().getSelectedItem();
        shcOpenClosePane.getChildren().removeAll(shcOpenClosePane.getChildren());

        if (item.equals("Windows")) {
            for (Window window : shcWindows) {
                CheckBox windowCheck = new CheckBox(window.name);
                windowCheck.setSelected(window.getOpen());
                windowCheck.setLayoutX(15);
                windowCheck.setOnAction((event) -> {
                    window.setOpen(windowCheck.isSelected());
                });
                shcOpenClosePane.getChildren().add(windowCheck);
            }
        } else if (item.equals("Doors")) {
            for (Door door : shcDoors) {
                CheckBox doorCheck = new CheckBox(door.name);
                doorCheck.setSelected(door.getOpen());
                doorCheck.setLayoutX(15);
                doorCheck.setOnAction((event) -> {
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
    private Simulation loadSimulation() {
        return new Simulation();
    }

    private void initializeSHS() {
        List<Integer> times = IntStream.of(IntStream.range(0, 24).toArray()).boxed().collect(Collectors.toList());
        hour.getItems().addAll(times);
        times = IntStream.of(IntStream.range(0, 60).toArray()).boxed().collect(Collectors.toList());
        minute.getItems().addAll(FXCollections.observableArrayList(times));
        second.getItems().addAll(FXCollections.observableArrayList(times));

        usersList.getItems().addAll(Arrays.asList(new String[]{"Default User", "[New User]"}));
        locationPane.setVisible(false);
    }

    private void initializeSHC() {

        shcWindows = new ArrayList<>();
        shcDoors = getSimulation().getDoors();
        //shcLights = new ArrayList<>();

        // EXAMPLE
        Window win = new Window();
        win.name = "kitchen";
        shcWindows.add(win);
        win.name = "Bedroom 1";
        shcWindows.add(win);
        win = new Window(1, true, true);
        win.name = "Living Room";
        shcWindows.add(win);
        win.name = "Bathroom";
        shcWindows.add(win);

        Door temp = new Door(0, true);
        temp.name = "Main";
        shcDoors.add(temp);
        temp = new Door();
        temp.name = "Garage";
        shcDoors.add(temp);
        temp = new Door(0, true);
        temp.name = "Backyard";
        shcDoors.add(temp);

        // For each room in simulation
        //      if room has window
        //          shcWindows.add(Window)
    }

    private void writeToConsole(String text) {
        String[] times = getTime().split(":");
        String time = "[" + times[0] + ":" + times[1] + "] ";
        outputConsole.setText(outputConsole.getText() + "\n" + time + text);
        outputPane.setVvalue(1);
    }

    private void updateOutsideTemp(double temp) {
        outsideTempDisplay.setText(String.format("Outside Temp. %.2f" + "\u00B0" + "C", temp));
    }

    private void updateInsideTemp(double temp) {
        insideTempDisplay.setText(String.format("Inside Temp. %.2f" + "\u00B0" + "C", temp));
    }

    private void updateDate(LocalDate date) {
        String day = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.CANADA) + " " + date.getMonth().getDisplayName(TextStyle.SHORT, Locale.CANADA) + " " + date.getDayOfMonth() + " " + date.getYear();
        dateTimeDisplay.setText(day + "\n" + getTime());
    }

    private void updateTime(int hour, int min, int sec) {
        String time = String.format("%02d:%02d:%02d", hour, min, sec);
        dateTimeDisplay.setText(getDate() + "\n" + time);
    }

    private String getTime() {
        String text = dateTimeDisplay.getText();
        return text.split("\n")[1];
    }

    private String getDate() {
        String text = dateTimeDisplay.getText();
        return text.split("\n")[0];
    }

    private void createModule(Tab module) {
        AnchorPane topPane = new AnchorPane();
        module.setContent(topPane);
        moduleContainer.applyCss();
        moduleContainer.layout();
        //topPane.resize(moduleContainer.getWidth(), moduleContainer.getHeight());

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
            itemsPane.getChildren().add(itemLabel);

            shcItems = new ListView();
            shcItems.getItems().addAll(Arrays.asList(new String[]{"Windows", "Lights", "Doors"}));
            shcItems.setPrefSize(itemsPane.getWidth() - 5, itemsPane.getHeight() - 25);
            shcItems.setOnMouseClicked((event) -> {
                handleSelectSHCItem(event);
            });
            itemsPane.getChildren().add(shcItems);

            Label openCloseLabel = new Label("Open/Close");
            openCloseLabel.setPrefSize(topPane.getWidth(), 20);
            openCloseLabel.getStyleClass().add("moduleItemTitle");
            topPane.getChildren().add(openCloseLabel);

            shcOpenClosePane = new VBox();
            shcOpenClosePane.getStyleClass().add("simulationSubItem");
            shcOpenClosePane.setPrefSize(itemsPane.getWidth(), 200);
            shcOpenClosePane.setSpacing(10);
            shcOpenClosePane.setPadding(new Insets(10, 0, 0, 10));
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
        close.setOnAction((event) -> {
            Button moduleButton = null;
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

	public Simulation getSimulation() {
		return simulation;
	}

	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
	}

}
