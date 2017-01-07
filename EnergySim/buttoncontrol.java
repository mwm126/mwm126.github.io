import java.awt.*;

class ButtonControl extends Panel {
	Button dec = new Button("<");
	Button inc = new Button(">");
	Button min = new Button("<<");
	Button max = new Button(">>");
	Checkbox auto;
	Label label;
	String name;
	Point range;
	double step;
	double value;
	
	Callback callback;
	
	//constructor
	ButtonControl(String n, double initval, double s, Point r,Callback cb,boolean showauto) {
		callback = cb;
		name = n;
		value = initval;
		step = s;
		range = r;
		label = new Label(name+" = "+stringform());
		setLayout(new BorderLayout());
		add("North", label);

		auto = new Checkbox("Auto",null,true);
		if (showauto) add("Center",auto);
		
		Panel p = new Panel();
		p.setLayout(new GridLayout(1,4));
		p.add(min);
		p.add(dec);
		p.add(inc);
		p.add(max);
		add("South", p);
	}
	
	//methods
	public double getValue() {
		return value;
	}
	
	public void setValue(double new_value) {
		auto.setState(true);
		value = Math.max(range.x,Math.min(range.y,new_value));
		label.setText(name+" = "+stringform());
	}
	
	public boolean outOfRange() {
	  return (value<range.x||range.y<value);
	}
	
	private String stringform() {
		if (range.y==2) {
			switch ((int)value) {
				case 0:return "off";
				case 1:return "low";
				case 2:return "high";
				default: System.out.println("error--bad switch value");
			}
		} else if (range.y==3) {
			switch ((int)value) {
				case 1:return "low";
				case 2:return "medium";
				case 3:return "high";
				default: System.out.println("error---bad switch value");
			}
		}
		return ""+value;//""+(int)value;
	}

	public boolean action(Event evt, Object what) {
		if (evt.target==dec) {
			value -= step;
			value = Math.max(value,range.x);
			callback.callback(name);
			auto.setState(false);
		} else if (evt.target==inc) {
			value += step;
			value = Math.min(value,range.y);
			callback.callback(name);
			auto.setState(false);
		} else if (evt.target==min) {
			value = range.x;
			callback.callback(name);
			auto.setState(false);
		} else if (evt.target==max) {
			value = range.y;
			callback.callback(name);
			auto.setState(false);
		} else if (evt.target==auto) {
		  //don't need to do anything--yet.
		}
		label.setText(name+" = "+stringform());
		repaint();
		return true;
	}
}
