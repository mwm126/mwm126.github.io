import java.awt.*;

public class LabelControl extends Panel {
	Label label;
	String name;
	double value;
	
	//constructor
	LabelControl(String n, double initval) {
		name = n;
		value = initval;
		label = new Label("");
		setValue(initval);

		add(label);
	}
	
	//methods
	public double getValue() {
		return value;
	}
	
	public void setValue(double new_value) {
		value = new_value;
		label.setText(name+" = "+value);//label.setText(name+" = "+(int)value);
	}
}