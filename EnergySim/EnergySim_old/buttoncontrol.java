import java.awt.*;

class ButtonControl extends Panel {
	Button dec = new Button("<");
	Label label;
	Button inc = new Button(">");
	String name;
	
	Point range;
	
	int step;
	
	int value;
	
	//constructor
	ButtonControl(String n, int initval, int s, Point r) {
		name = n;
		value = initval;
		step = s;
		range = r;
		label = new Label(name+" = "+stringform());
		setLayout(new BorderLayout());
		add("West", dec);
		add("Center", label);
		add("East", inc);
	}
	
	//methods
	public int getValue() {
		return value;
	}
	
	private String stringform() {
		if (range.y==2) {
			switch (value) {
				case 0:return "off";
				case 1:return "low";
				case 2:return "high";
				default: System.out.println("error-bad switch value");
			}
		} else if (range.y==3) {
			switch (value) {
				case 1:return "low";
				case 2:return "medium";
				case 3:return "high";
				default: System.out.println("error-bad switch value");
			}
		}
		return ""+value;
	}

	public boolean action(Event evt, Object what) {
		if (evt.target==dec) {
			value -= step;
			value = Math.max(value,range.x);
		} else if (evt.target==inc) {
			value += step;
			value = Math.min(value,range.y);
		}
		label.setText(name+" = "+stringform());
		repaint();
		return true;
	}
}
