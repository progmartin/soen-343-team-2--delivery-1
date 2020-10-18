package gui;
import java.util.ArrayList;

import HouseObjects.Room;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;

public class RoomObjtoDisplay   {

  static double orgSceneX;
static double orgSceneY;

 static public void createRectangle(Pane root, ArrayList<Room> arr) {
	 	
	 ArrayList<Color> color = new ArrayList<Color>();//set random colors
	 color.add(Color.LAVENDER);
	 color.add(Color.AQUAMARINE);
	 color.add(Color.LIGHTPINK);
	 color.add(Color.WHEAT);
	 
	 //setting up the rectangles
	 int offsetx = 0;//increment by 50 pixels
	 int offsety = 0;//increment by 50 pixels
	 int it = 0;
	 for(int i = 0; i<arr.size();i++) {	 
		 Rectangle rec1 = new Rectangle(50,50,color.get(it));//create rectangle
		 rec1.setX(550+offsetx);//place the rectangle in place
		 rec1.setY(100+offsety);
	      root.getChildren().add(rec1);//add it to gui
	      offsetx+=50;//offset algorythm for each rectangle
	      it++;
	      if (offsetx==150) {
	    	  offsetx=0;
	    	  offsety+=50;
	      }
	      if(it==4) {
	    	  it=0;
	      }
	 }
	 
	 //seting up doors
	  	
	 	int offsetDoorx=0;//needed to replicate door setup to each room
	 	int offsetDoory=0;	 	
	 for(int i = 0; i<arr.size();i++) {	//for every room				 
			 
			 for(int k = 0; k<arr.get(i).numberOfDoors();k++) {	 // for every door in room
					
				 //door left
				 if (k==0) {
					 Arc rec1 = new Arc(550+offsetDoorx,130+offsetDoory,13,13,270,90);//create half arc 	 
					 rec1.setType(ArcType.ROUND);
					 rec1.setFill(Color.BROWN);
					 root.getChildren().add(rec1);//add it to gui
				 }
				
				 //door top
				 if (k==1) {
					 Arc rec1 = new Arc(556+offsetDoorx,100+offsetDoory,13,13,270,90);//create  half arc 	 
					 rec1.setType(ArcType.ROUND);
					 rec1.setFill(Color.BROWN);
					 rec1.setRotate(90);
					 root.getChildren().add(rec1);//add it to gui
				 }
				 
				 //door right
				 if (k==2) {
					 Arc rec1 = new Arc(587+offsetDoorx,110+offsetDoory,13,13,270,90);//create  half arc 	 
					 rec1.setType(ArcType.ROUND);
					 rec1.setFill(Color.BROWN);
					 rec1.setRotate(180);
					 root.getChildren().add(rec1);//add it to gui					 
				 }
				 
				 //door bot
				 if (k==3) {
					 Arc rec1 = new Arc(580+offsetDoorx,137+offsetDoory,13,13,270,90);//create  half arc 	 
					 rec1.setType(ArcType.ROUND);
					 rec1.setFill(Color.BROWN);
					 rec1.setRotate(270);
					 root.getChildren().add(rec1);//add it to gui						 

				 }			 
			 }
				offsetDoorx+=50;//needed to replicate door setup
				if (offsetDoorx==150) {
					offsetDoorx=0;
					offsetDoory+=50;
				}			 						 		 	 	 
	 }
	 
	 //setting up windows, algorythm similar to doors
	 	int offsetWindowx=0;//needed to replicate window set up to other rooms
	 	int offsetWindowy=0;	 	
	 for(int i = 0; i<arr.size();i++) {//for number of rooms	 				 
			 
			 for(int k = 0; k<arr.get(i).numberOfWindows();k++) {//for every window in room	 
					
				 //window left
				 if (k==0) {
					 Rectangle rec1 = new Rectangle(6,15,Color.DEEPSKYBLUE);//create rectangle 	 
					 rec1.setX(550+offsetWindowx);//place the rectangle in place
					 rec1.setY(110+offsetWindowy); 
					 root.getChildren().add(rec1);//add it to gui
				 }
				
				 //window top
				 if (k==1) {
					 Rectangle rec1 = new Rectangle(15,6,Color.DEEPSKYBLUE);//create rectangle 	 
					 rec1.setX(580+offsetWindowx);
					 rec1.setY(100+offsetWindowy);
					 root.getChildren().add(rec1);//add it to gui
				 }
				 
				 //window right
				 if (k==2) {
					 Rectangle rec1 = new Rectangle(6,15,Color.DEEPSKYBLUE);//create rectangle 	
					 rec1.setX(594+offsetWindowx);//place the rectangle in place
					 rec1.setY(129+offsetWindowy); 
					 root.getChildren().add(rec1);//add it to gui
				 }
				 
				 //window bot
				 if (k==3) {
					 Rectangle rec1 = new Rectangle(15,6,Color.DEEPSKYBLUE);//create rectangle 	
					 rec1.setX(560+offsetWindowx);
					 rec1.setY(144+offsetWindowy);
					 root.getChildren().add(rec1);//add it to gui

				 }			 
			 }
			 offsetWindowx+=50;//needed to replicate window set up to other rooms
				if (offsetWindowx==150) {
					offsetWindowx=0;
					offsetWindowy+=50;
				}			 						 		 	 	 
	 }	 
	  
  }
	//setting up a doorfunction to make code cleaner

}