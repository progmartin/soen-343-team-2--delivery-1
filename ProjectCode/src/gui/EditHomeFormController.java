package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import HouseObjects.Window;
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
	ComboBox people;
	@FXML
	ComboBox windows;
	@FXML
	Label peopleTitle;
	@FXML 
	Label windowTitle;
	@FXML 
	ListView peopleInRoom;
	@FXML
	ListView blockedWindows;
	@FXML 
	Label peopleInRoomLabel;
	@FXML
	Label blockedWindowsLabel;

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
		people.setOpacity(0);
		windows.setOpacity(0);
		selectedRoom.setOpacity(0);
		titleSelectedRoom.setOpacity(0);
		peopleInRoom.setOpacity(0);
		blockedWindows.setOpacity(0);
		peopleTitle.setOpacity(0);
		windowTitle.setOpacity(0);
		blockedWindowsLabel.setOpacity(0);
		peopleInRoomLabel.setOpacity(0);
		
		
		
		
		
		
		
		
		
	}
	
	public void handleSelectRoom(Event e)
	{ 
		peopleInRoom.getItems().clear();
		
		people.getItems().clear();
		 
		windows.getItems().clear();
		
		
		blockedWindows.getItems().clear();
		 
		selectedRoom.setText((String) rooms.getSelectionModel().getSelectedItem());
		
		people.getItems().addAll(Driver.simulation.getAllUserNames());
		 
		int[] windowss= new int[Driver.simulation.getWindows(selectedRoom.getText()).size()];
		
		ArrayList<String> peoplee = new ArrayList<String>();
		 
		for (String x : Driver.simulation.getAllUserNames())
		{
			
			if(Driver.simulation.getUserLocation(x)==selectedRoom.getText())
				peoplee.add(x);
			
		}
		 
		peopleInRoom.getItems().addAll(peoplee);
		 
ArrayList<Integer> windowsss = new ArrayList<Integer>();
		
		for (Window  x : Driver.simulation.getWindows(selectedRoom.getText()))
		{
			if(x.getBlocked())
				windowsss.add(x.getID());
			
		}
		ArrayList<Integer> windowssss = new ArrayList<Integer>();
		for(Window x :Driver.simulation.getWindows(selectedRoom.getText()) )
		{
			windowssss.add(x.getID());
		}
		 
		windows.getItems().addAll(windowssss);
		
		blockedWindows.getItems().addAll(windowsss);
		
		selectedRoom.setOpacity(1);
		 
		titleSelectedRoom.setOpacity(1);
		
		people.setOpacity(1);
		windows.setOpacity(1);
		selectedRoom.setOpacity(1);
		titleSelectedRoom.setOpacity(1);
		peopleInRoom.setOpacity(1);
		blockedWindows.setOpacity(1);
		peopleTitle.setOpacity(1);
		windowTitle.setOpacity(1);
		blockedWindowsLabel.setOpacity(1);
		peopleInRoomLabel.setOpacity(1);
	}
	
		
	
	
	public void handleMovePerson(Event e)
	{
		try {
		
		if(!Driver.simulation.getUserLocation((String)people.getSelectionModel().getSelectedItem()).equals(selectedRoom.getText()))
		Driver.simulation.updateUserLocation((String)people.getSelectionModel().getSelectedItem(), selectedRoom.getText());
		updatePeopleInRoom();
		}
		catch (Exception ex)
		{
			
		}
	}
	public void handleBlockWindow(Event e)
	{   try {
		if(Driver.simulation.getWindow((Integer) windows.getSelectionModel().getSelectedItem()).getBlocked()) {
			
			Driver.simulation.getWindow((Integer) windows.getSelectionModel().getSelectedItem()).setBlocked(false);
			
			
		}
		else
			{
			Driver.simulation.getWindow((Integer) windows.getSelectionModel().getSelectedItem()).setBlocked(true);
			
			}
		
			updateBlockedWindows();
			
	}
	catch  (Exception ex){
		
	}
			
	
	}
	
	public void updatePeopleInRoom() {
		peopleInRoom.getItems().clear();
ArrayList<String> peoplee = new ArrayList<String>();
		
		for (String x : Driver.simulation.getAllUserNames())
		{
			if(Driver.simulation.getUserLocation(x)==selectedRoom.getText())
				peoplee.add(x);
				
		}
		peopleInRoom.getItems().addAll(peoplee);
	}
	
	public void updateBlockedWindows() {
		
		blockedWindows.getItems().clear();
		
ArrayList<Integer> windowsss = new ArrayList<Integer>();
		
		for (Window  x : Driver.simulation.getWindows(selectedRoom.getText()))
		{
			if(x.getBlocked())
				windowsss.add(x.getID());
				
		}
		
		
		blockedWindows.getItems().addAll(windowsss);
		
		
	}
	
	public void removeWindowSelection(Event e)
	{
		windows.getSelectionModel().clearSelection();
	}
		
	}


