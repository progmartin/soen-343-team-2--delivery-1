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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.collections.FXCollections;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
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
    ImageView userProfilePic;
    @FXML
    Label usernameDisplay;
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


    static Stage editStage;
    private Simulation simulation;

    protected static HashMap<String, String[]> accounts = new HashMap<>();
    protected String editedUser = "";

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
        simulation = loadSimulation();
        accounts.put("Default User", new String[]{"", "Adult (Family)"});

        updateOutsideTemp(15);
        updateInsideTemp(21);

        LocalDateTime today = LocalDateTime.now();
        updateTime(today.getHour(), today.getMinute(), today.getSecond());
        updateDate(today.toLocalDate());

        initializeSHS();

    }

    @FXML
    private void handleLoadSimulation(Event e) {
        this.simulation = loadSimulation();
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
            if (input.getId().equals(insideTempInput.getId())) {
                updateInsideTemp(Integer.parseInt(input.getText()));
            } else if (input.getId().equals(outsideTempInput.getId())) {
                updateOutsideTemp(Integer.parseInt(input.getText()));
            }
        } catch (NumberFormatException ex) {
        }
    }

    @FXML
    private void handleSelectUser(Event e) {
        ComboBox users = (ComboBox) e.getSource();

        // If edit stage is already exists
        if (editStage != null) {
            // Do nothing with this event
            e.consume();
            return;
        }

        // Create a new stage/window
        editStage = new Stage();
        System.out.println(users.getSelectionModel().getSelectedItem());
        if (((String) users.getSelectionModel().getSelectedItem()).equals("New User")) {
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
    private void handleLogin(Event e) {
        
        if (!accounts.containsKey(usernameInput.getText().trim())){
            writeToConsole("[SHS] Username or password is incorrect");
            e.consume();
        }
        if (!accounts.get(usernameInput.getText().trim())[0].equals(passwordInput.getText())){
            writeToConsole("[SHS] Username or password is incorrect");
            e.consume();
        }
        
        usernameDisplay.setText(usernameInput.getText().trim());

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
        usernameDisplay.setText("Not Logged In");
        
        usernameTag.setVisible(true);
        usernameInput.setVisible(true);
        passwordTag.setVisible(true);
        passwordInput.setVisible(true);
        loginButton.setVisible(true);
        logoutButton.setVisible(false);
    }
    
    @FXML
    private void handleNewModule(Event e){
        Button module = (Button) e.getSource();
        String moduleStr = null;
        switch (module.getText()){
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
        }
        Tab moduleTab = new Tab(moduleStr);
        moduleContainer.getTabs().add(moduleContainer.getTabs().size()-1,moduleTab);
        createModule(moduleTab);
        
        module.setVisible(false);
        module.setManaged(false);
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

        usersList.getItems().addAll(Arrays.asList(new String[]{"Default User", "New User"}));
    }

    private void writeToConsole(String text) {
        String[] times = getTime().split(":");
        String time = "[" + times[0] + ":" + times[1] + "] ";
        outputConsole.setText(outputConsole.getText() + "\n" + time + text);
    }

    private void updateOutsideTemp(int temp) {
        outsideTempDisplay.setText("Outside Temp. " + temp + "\u00B0" + "C");
    }

    private void updateInsideTemp(int temp) {
        insideTempDisplay.setText("Inside Temp. " + temp + "\u00B0" + "C");
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
    
    private void createModule(Tab module){
        if (module.getText().equals("SHC")){
            Button close = new Button("Close Module");
        }
        Button close = new Button("Close Module");
        close.setOnAction((event) -> {
            Button moduleButton = null;
            switch (module.getText()){
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
            }
            moduleButton.setManaged(true);
            moduleButton.setVisible(true);
            moduleContainer.getTabs().remove(module);
        });
        AnchorPane topPane = new AnchorPane(close);
        module.setContent(topPane);
    }

}
