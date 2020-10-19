package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import simulation.Simulation;

public class EditHomeFormController implements Initializable {
	
	
	@FXML
	ComboBox rooms;
	@FXML
	Label title;
	@FXML
	Label selectedRoom;
	@FXML 
	Label titleSelectedRoom;
	@FXML 
	ListView accounts;
	@FXML
	ListView windows;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		String[] roomss= new String[Driver.simulation.getRooms().size()];
		for(int i=0;i<roomss.length;i++)
		{
			roomss[i]= Driver.simulation.getRooms().get(i).getName();
		}
		 
		rooms.getItems().addAll(Arrays.asList(roomss));
		selectedRoom.setOpacity(0);
		titleSelectedRoom.setOpacity(0);
		ArrayList<String[]> peopleKeys= new ArrayList<String[]>(Driver.simulationController.accounts.values());
		ArrayList<String> people= new ArrayList<String>();
		for(int i=0;i<people.size();i++)
		{
			people.set(i,peopleKeys.get(i)[0]);
		}
		accounts.setItems(FXCollections.observableArrayList(people));
		System.out.println(people.get(0));
		
		
		
		
		
	}
	
	public void handleSelectRoom(Event e)
	{
		selectedRoom.setText((String) rooms.getSelectionModel().getSelectedItem());
		selectedRoom.setOpacity(1);
		titleSelectedRoom.setOpacity(1);
		
		
	}
	
	
		
	}


