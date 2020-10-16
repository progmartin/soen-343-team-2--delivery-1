package gui;
import java.util.ArrayList;

import HouseObjects.Room;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RectangleRoom   {

  static double orgSceneX;
static double orgSceneY;

 static public void createRectangle(Pane root, ArrayList<Room> arr) {
	 
	 int offset = 0;//increment by 50 pixels
	 ArrayList<Color> color = new ArrayList<Color>();//set random colors
	 color.add(Color.CORNFLOWERBLUE);
	 color.add(Color.AQUAMARINE);
	 color.add(Color.CRIMSON);
	 color.add(Color.WHEAT);
	 for(int i = 0; i<arr.size();i++) {	 
		 Rectangle rec1 = new Rectangle(50,50,color.get(i));
		 rec1.setX(550+offset);
		 rec1.setY(100);
	      root.getChildren().add(rec1);
	      offset+=50;	 
	 }
	 
	  /*Rectangle redCircle = new Rectangle(50,50);
	  redCircle.setX(550);
      redCircle.setY(100);
      root.getChildren().add(redCircle);*/
	  
	  
	//dragable setup
	 /* rectangle.setCursor(Cursor.HAND);

	  rectangle.setOnMousePressed((t) -> {
      orgSceneX = t.getSceneX();
      orgSceneY = t.getSceneY();

      Rectangle rr = (Rectangle) (t.getSource());
      rr.toFront();
    });
	  rectangle.setOnMouseDragged((t) -> {
      double offsetX = t.getSceneX() - orgSceneX;
      double offsetY = t.getSceneY() - orgSceneY;

      Rectangle rr = (Rectangle) (t.getSource());

      rr.setX(rr.getX() + offsetX);
      rr.setY(rr.getY() + offsetY);

      orgSceneX = t.getSceneX();
      orgSceneY = t.getSceneY();
    });*/
  //  return rectangle;
  }
}