import java.awt.*;

public class LabelControl extends Control {
    Label label;
	
	//constructor
	LabelControl(String n, double initval) {
	  super(n,initval);
	  label = new Label("");
	  label.setText(name+" = "+value);//label.setText(name+" = "+(int)value);
	  
	  add(label);
	  resize(CONTROL_WIDTH,CONTROL_HEIGHT);
	}
	
	//methods
	
	public void setValue(double new_value) {
		value = new_value;
		label.setText(name+" = "+value);
	}
}