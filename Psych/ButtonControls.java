import java.awt.*;

class ButtonControls extends Panel {
	//fields
	AHU_Simulation ahu_sim;
	
	ButtonControl OA_percent;
	ButtonControl SA_temperature;
	ButtonControl CFM;
	
	ButtonControl OA_temperature;
	ButtonControl OA_WB;
	ButtonControl TONS;
	ButtonControl SHF;
	
	ButtonControl speed;
	
	//constructor
	ButtonControls(AHU_Simulation ahu) {
		ahu_sim = ahu;
		ahu_sim.button_controls = this;
		
			OA_percent = new ButtonControl("OA%", 5, new OAPerc(ahu_sim));
			SA_temperature = new ButtonControl("SA T", 1, new SupplyT(ahu_sim));
			CFM = new ButtonControl("CFM", 1000, new SaCfm(ahu_sim));
	
			OA_temperature = new ButtonControl("OA T", 1, new OATemp(ahu_sim));
			OA_WB = new ButtonControl("OA WB", 1, new TWB(ahu_sim));
			TONS = new ButtonControl("Tons", 5, new kyoo(ahu_sim));
			SHF = new ButtonControl("SHF", 0.05, new EssAcheEff(ahu_sim));		
			speed = new ButtonControl("sim speed", 1, new SimSpeed(ahu_sim));//fifty milliseconds
			
			//speed.setValue(5);
			//speed.callback.setValue(200);

		//OA_percent.setBackground(Color.);
		SA_temperature.setForeground(Color.blue);
		SA_temperature.setBackground(inverse(Color.blue));
		CFM.setForeground(Color.blue);
		CFM.setBackground(inverse(Color.blue));
		OA_temperature.setForeground(Color.red);
		OA_temperature.setBackground(inverse(Color.red));
		OA_WB.setForeground(Color.red);
		OA_WB.setBackground(inverse(Color.red));
		TONS.setBackground(Color.lightGray);
		TONS.setForeground(inverse(Color.lightGray));
		SHF.setBackground(Color.lightGray);
		SHF.setForeground(inverse(Color.lightGray));
		
		speed.setBackground(Color.magenta);
		speed.setForeground(inverse(Color.magenta));
		
		Label label = new Label("Adjust system");
		
		Panel p = new Panel();
		p.setLayout(new GridLayout(6,1));
		p.add(new Label(""));
		p.add(new Label(""));
		p.add(label);
		p.add(OA_percent);
		p.add(SA_temperature);
		p.add(CFM);
		
		Label label2 = new Label("Adjust weather");
		Label label3 = new Label("Adjust load");
		
		Panel p2 = new Panel();
		p2.setLayout(new GridLayout(7,1));
		p2.add(label2);
		p2.add(OA_temperature);
		p2.add(OA_WB);
		p2.add(label3);
		p2.add(TONS);
		p2.add(SHF);
		p2.add(speed);
		
		setLayout(new GridLayout(2,1,4,4));
		add(p);
		add(p2);
	}

	Color inverse(Color c) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		int sum = r+g+b;

		//382==(3/2)*255
		if (sum > 382) {
			r -= (382*r)/sum;
			g -= (382*g)/sum;
			b -= (382*b)/sum;
		} else {
			r += (382*(255-r))/(3*255 - sum);
			g += (382*(255-g))/(3*255 - sum);
			b += (382*(255-b))/(3*255 - sum);
		}
		return new Color(r,g,b);
	}
	
	//methods
	public void update_controls() {
		OA_percent.setValue(ahu_sim.OA_percent);
		SA_temperature.setValue(ahu_sim.supply_T);
		CFM.setValue(ahu_sim.sa_cfm);
		OA_temperature.setValue(ahu_sim.outside_T);
		OA_WB.setValue(ahu_sim.Twb);
		TONS.setValue(ahu_sim.Q);
		SHF.setValue(ahu_sim.SHF);
		speed.setValue(ahu_sim.SPEED);
		repaint();
	}
}

class OAPerc implements Callback {
	AHU_Simulation ahu_sim;
	OAPerc(AHU_Simulation ahu) {
		ahu_sim = ahu;
	}
	public void setValue(double val) {
		if (val<20) AHU.print("You need at least 20% fresh, oxygenated outside air or people suffocate.");
		if (val>100) AHU.print("Gosh, using 100% outside air is an expensive way to run this place!");
		val = Math.max(val, 20);
		val = Math.min(val, 100);
		ahu_sim.OA_percent = val;
		ahu_sim.update_everything();
	}
}

class SupplyT implements Callback {
	AHU_Simulation ahu_sim;
	SupplyT(AHU_Simulation ahu) {
		ahu_sim = ahu;
	}
	public void setValue(double val) {
		if (val>ahu_sim.room_T) AHU.print("You know, adding heat to the building is not a good way to air condition?");
		if (val<42) AHU.print("Our chiller can't cool air below 42 degrees.");
		val = Math.max(val, 42);
		val = Math.min(val, ahu_sim.room_T);
		ahu_sim.supply_T = val;
		ahu_sim.update_everything();
	}
}

class SaCfm implements Callback {
	AHU_Simulation ahu_sim;
	SaCfm(AHU_Simulation ahu) {
		ahu_sim = ahu;
	}
	public void setValue(double val) {
		if (val>40000) AHU.print("With that much air rushing through the ducts, it sounds like a PennState football game!");
		if (val<20000) AHU.print("Without at least 20000 cubic feet per minute of air flow, we get poor distribution.");
		val = Math.max(val, 20000);
		val = Math.min(val, 40000);
		ahu_sim.sa_cfm = val;
		ahu_sim.update_everything();
	}
}

class OATemp implements Callback {
	AHU_Simulation ahu_sim;
	OATemp(AHU_Simulation ahu) {
		ahu_sim = ahu;
	}
	public void setValue(double val) {
		if (val<ahu_sim.room_T) AHU.print("Wouldn't it be more efficient to go to an econmizer cycle now?");
		if (val>94) AHU.print("It's a hot day outside, but it's not the Sahara desert!");
		val = Math.max(val, ahu_sim.room_T);
		val = Math.min(val, 94);
		ahu_sim.outside_T = val;
		ahu_sim.update_everything();
	}
}

class TWB implements Callback {
	AHU_Simulation ahu_sim;
	TWB(AHU_Simulation ahu) {
		ahu_sim = ahu;
	}
	public void setValue(double val) {
		if (val<65) AHU.print("Wouldn't it be more efficient to go to an econmizer cycle now?");
		if (val>ahu_sim.outside_T) AHU.print("It feels like a rainy day in Miami right now!");
		val = Math.max(val, 65);
		val = Math.min(val, ahu_sim.outside_T);
		ahu_sim.Twb = val;
		ahu_sim.update_everything();
	}
}

class kyoo implements Callback {
	AHU_Simulation ahu_sim;
	kyoo(AHU_Simulation ahu) {
		ahu_sim = ahu;
	}
	public void setValue(double val) {
		if (val<40) AHU.print("If you go below 40 Tons, the chiller will go into surge...and that's not good");
		if (val>100) AHU.print("If you want more cooling, ya shoulda bought a bigger chiller.");
		val = Math.max(val, 40);
		val = Math.min(val, 100);
		ahu_sim.Q = val;
		ahu_sim.update_everything();
	}
}

class EssAcheEff implements Callback {
	AHU_Simulation ahu_sim;
	EssAcheEff(AHU_Simulation ahu) {
		ahu_sim = ahu;
	}
	public void setValue(double val) {
		if (val<0.5) AHU.print("With an SHF of 0.5, you must be trying to condition a sauna!");
		if (val>1.0) AHU.print("Are you positive there is no humidity load right now?"); 
		val = Math.max(val, 0.5);
		val = Math.min(val, 1.0);
		ahu_sim.SHF = val;
		ahu_sim.update_everything();
	}
}

class SimSpeed implements Callback {
	AHU_Simulation ahu_sim;
	SimSpeed(AHU_Simulation ahu) {
		ahu_sim = ahu;
	}
	public void setValue(double val) {
		if (val<1) AHU.print("Increase the SimSpeed to make things change faster");
		if (val>30) AHU.print("I'm moving as fast as I can!!");
		val = Math.max(val, 1);
		val = Math.min(val, 30);
		ahu_sim.SPEED = val;
		ahu_sim.update_everything();
	}
}

interface Callback {
	public void setValue(double value);
}

class ButtonControl extends Panel implements Callback {
	Button dec = new Button("<");
	Label label = new Label();
	Button inc = new Button(">");
	String name;
	
	double step;
	
	double value;
	
	Callback callback;
	
	//constructor
	ButtonControl(String n, double s, Callback cb) {
		name = n;
		step = s;
		callback = cb;
		setValue(42);
		setLayout(new BorderLayout());
		add("West", dec);
		add("Center", label);
		add("East", inc);
	}
	
	//methods
	public void setValue(double v) {
		value = v;
		label.setText(name+" "+AHU.format(value,2));
		repaint();
	}

	public boolean action(Event evt, Object what) {
		if (evt.target==dec) {
			setValue(value - step);
			callback.setValue(value);
		} else if (evt.target==inc) {
			setValue(value + step);
			callback.setValue(value);
		}
		return true;
	}
}
