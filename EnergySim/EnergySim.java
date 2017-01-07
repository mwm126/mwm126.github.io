import java.awt.*;

public class EnergySim extends java.applet.Applet {
	//CONTROLS
	CheckboxGroup cb = new CheckboxGroup();
	Checkbox cooling_tower_button = new Checkbox("Cooling Tower",cb,true);
	Checkbox chiller_button = new Checkbox("Chillers",cb,false);
	Checkbox pump_button = new Checkbox("Pumps",cb,false);
	Checkbox fan_button = new Checkbox("Fans",cb,false);
	
	CheckboxGroup cbg = new CheckboxGroup();
	Checkbox training = new Checkbox("Training mode",cbg,false);
	Checkbox manual = new Checkbox("Manual mode",cbg,true);
	
	//POSSIBLE SCREENS
	CoolingTowerScreen cooling_tower_screen;
	ChillerScreen chiller_screen;
	PumpScreen pump_screen;
	FanScreen fan_screen;
	
	//MAIN SCREEN (actually a panel holding the main screen)
	Panel current_screen;

	public void init() {
	  cooling_tower_screen = new CoolingTowerScreen(getImage(getCodeBase(), "cooling_tower_bg.gif"));
	  chiller_screen = new ChillerScreen(getImage(getCodeBase(), "chiller_bg.gif"));
	  pump_screen = new PumpScreen(getImage(getCodeBase(), "pump_bg.gif"));
	  fan_screen = new FanScreen(getImage(getCodeBase(), "fan_bg.gif"));
	
	  Panel radio = new Panel();
	  radio.setLayout(new GridLayout(2,1));
	  radio.add(training);
	  radio.add(manual);
	  
	  Panel screenbuttonz = new Panel();
	  screenbuttonz.setLayout(new GridLayout(1,4));
	  screenbuttonz.add(cooling_tower_button);
	  screenbuttonz.add(chiller_button);
	  screenbuttonz.add(pump_button);
	  screenbuttonz.add(fan_button);
	  
	  Panel controls = new Panel();
	  controls.add(radio);
	  controls.add(screenbuttonz);
	  
	  current_screen = new Panel();
	  current_screen.setLayout(new CardLayout());
	  current_screen.add("Cooling Tower",cooling_tower_screen);
	  current_screen.add("Chillers",chiller_screen);
	  current_screen.add("Pumps",pump_screen);
	  current_screen.add("Fans",fan_screen);

	  setLayout(new BorderLayout());
	  add("Center",current_screen);
	  add("South",controls);
	  resize(600,480);/*
	  Component[] c = current_screen.getComponents();
	  for (int i=0; i<c.length; i++) {
	    c[i].validate();
		Component[] c2=null;
		if (c[i] instanceof Container) {
	      c2 = ((Container)c[i]).getComponents();
		}
		if (c2!=null)
		for (int j=0; j<c2.length; j++) {
		  c2[j].validate();
		}
	  }*/
	  repaint();
	}

	public boolean action(Event evt, Object what) {
		if (evt.target==cooling_tower_button || evt.target==chiller_button || evt.target==pump_button || evt.target==fan_button) {
		  String change2 = "";
		  if (cooling_tower_button.getState()) {
		    change2 = "Cooling Tower";
		  } else if (chiller_button.getState()) {
		    change2 = "Chillers";
		  } else if (pump_button.getState()) {
		    change2 = "Pumps";
		  } else if (fan_button.getState()) {
		    change2 = "Fans";
		  }
		  ((CardLayout)current_screen.getLayout()).show(current_screen,change2);
		  current_screen.repaint();
		  return true;
		} else if (evt.target==training) {
		  //switch to training mode
		  Component[] c = current_screen.getComponents();
		  for (int i=0; i<c.length; i++) {
		    ((Screen)c[i]).switch_to_training();
		  }
		  current_screen.repaint();
		  return true;
		} else if (evt.target==manual) {
		  //switch to manual mode
		  Component[] c = current_screen.getComponents();
		  for (int i=0; i<c.length; i++) {
		    ((Screen)c[i]).switch_to_manual();
		  }
		  current_screen.repaint();
		  return true;
		}
		return false;
	}	
}