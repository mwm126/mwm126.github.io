import java.awt.*;

public class Control extends Panel {
  //CONSTANTS	
  public static final int CONTROL_WIDTH = 90;
  public static final int CONTROL_HEIGHT = 60;

  //common implementation
  String name;
  double value;
  
  Control(String n, double initval) {
    name = n;
    value = initval;
	setBackground(Color.white);
  }
  
  public double getValue() {
    return value;
  }
  public void setValue(double d) {
    value = d;
  }
}