import java.awt.*;

public class PumpScreen extends Screen {
  //CONSTANTS
  final int CONTROL_WIDTH = 90;
  final int CONTROL_HEIGHT = 60;
  
  ButtonControl CWST_control = new ButtonControl("CWST",44,1,new Point(40,55),this,false);
  LabelControl CWRT_label = new LabelControl("CWRT",54);
  ButtonControl CWflow_control = new ButtonControl("CW flow",90,10,new Point(0,600),this,false);
  ButtonControl pump_control = new ButtonControl("pump",3,0.5,new Point(0,5),this,true);
  ButtonControl tons_control = new ButtonControl("tons",54,1,new Point(50,65),this,false);
  ButtonControl AHUST_control = new ButtonControl("AHU ST",44,1,new Point(40,55),this,true);
  LabelControl AHURT_label = new LabelControl("AHU RT",54);
  
  PumpScreen(Image bg) {
    super(bg);
	add(CWST_control);
	add(CWRT_label);
	add(CWflow_control);
	add(pump_control);
	add(tons_control);
	add(AHUST_control);
	add(AHURT_label);
	switch_to_manual();
  }
  
  public void callback(String name) {
    if (name.equals("CWST")) {
	} else if (name.equals("CWRT")) {
	} else if (name.equals("CW flow")) {
	} else if (name.equals("pump")) {
	} else if (name.equals("tons")) {
	} else if (name.equals("AHUST")) {
	} else if (name.equals("AHURT")) {
	}
  }
  
  void switch_to_training() {
    CWST_control.show(true);
	CWflow_control.show(true);
	tons_control.show(true);
	repaint();
  }
  void switch_to_manual() {
    CWST_control.show(false);
	CWflow_control.show(false);
	tons_control.show(false);
	repaint();  
  }
  
  private boolean laidOut = false;
  
  public void paint(Graphics g) {
    super.paint(g);
	if (!laidOut) {
	  Insets insets = insets();
	  pump_control.reshape(300 + insets.left, 5 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  AHURT_label.reshape(400 + insets.left, 350 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  CWST_control.reshape(150 + insets.left, 250 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  CWflow_control.reshape(100 + insets.left, 350 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  tons_control.reshape(400 + insets.left, 250 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  CWRT_label.reshape(150 + insets.left, 450 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  AHUST_control.reshape(400 + insets.left, 50 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  laidOut = true;
	}
  }
  /*
  double CWRT() {
      double gpm = 0;
  	if (chiller_control.getValue()>32)
  	  gpm += 200;
  	if (chiller2_control.getValue()>32)
  	  gpm += 200;
      return CWST_control.getValue() + tons_control.getValue()*12000/
  	  (500*gpm);
  }*/
}