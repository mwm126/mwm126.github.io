import java.awt.*;

public class PumpScreen extends Screen {  
  ButtonControl CWST_control = new ButtonControl("CWST",44,1,new Point(40,55),this,false);
  LabelControl CWRT_label = new LabelControl("CWRT",54);
  ButtonControl CWflow_control = new ButtonControl("CW flow",90,10,new Point(0,600),this,false);
  ButtonControl pump_control = new ButtonControl("pump",90,10,new Point(0,600),this,true);
  ButtonControl tons_control = new ButtonControl("tons",54,1,new Point(50,65),this,false);
  LabelControl AHUST_label = new LabelControl("AHU ST",44);
  ButtonControl AHURT_control = new ButtonControl("AHU RT",54,1,new Point(50,65),this,false);
  FlowControl crossflow_label = new FlowControl("cross flow",45,1,new Point(0,600),this);
  
  PumpScreen(Image bg) {
    super(bg);
	add(CWST_control);
	add(CWRT_label);
	add(CWflow_control);
	add(pump_control);
	add(tons_control);
	add(AHUST_label);
	add(AHURT_control);
	add(crossflow_label);
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
	} else if (name.equals("cross flow")) {
	}
	crossflow_label.setValue(crossflow());
	CWRT_label.setValue(CWRT());
	AHUST_label.setValue(AHUST());
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
	  pump_control.reshape(300 + insets.left, 5 + insets.top, ButtonControl.CONTROL_WIDTH, ButtonControl.CONTROL_HEIGHT);
	  CWST_control.reshape(150 + insets.left, 55 + insets.top, ButtonControl.CONTROL_WIDTH, ButtonControl.CONTROL_HEIGHT);
	  CWflow_control.reshape(100 + insets.left, 155 + insets.top, ButtonControl.CONTROL_WIDTH, ButtonControl.CONTROL_HEIGHT);
	  CWRT_label.reshape(150 + insets.left, 250 + insets.top, ButtonControl.CONTROL_WIDTH, ButtonControl.CONTROL_HEIGHT);
	  
	  tons_control.reshape(420 + insets.left, 155 + insets.top, ButtonControl.CONTROL_WIDTH, ButtonControl.CONTROL_HEIGHT);
	  AHUST_label.reshape(400 + insets.left, 45 + insets.top, ButtonControl.CONTROL_WIDTH, ButtonControl.CONTROL_HEIGHT);
	  AHURT_control.reshape(400 + insets.left, 250 + insets.top, ButtonControl.CONTROL_WIDTH, ButtonControl.CONTROL_HEIGHT);
	  crossflow_label.reshape(200 + insets.left, 155 + insets.top, ButtonControl.CONTROL_WIDTH, ButtonControl.CONTROL_HEIGHT);
	  laidOut = true;
	}
  }
  double AHURT() {
    return AHUST_label.getValue() + tons_control.getValue()*12000/(500*pump_control.getValue());
  }
  
  double crossflow() {
    return pump_control.getValue() - CWflow_control.getValue();
  }
  
  double AHUST() {
    if (crossflow()>0) {
    return AHURT_control.getValue() + 
	  (CWST_control.getValue()-AHURT_control.getValue())*CWflow_control.getValue()/pump_control.getValue();
	} else {
    return CWST_control.getValue();	
	}
  }
 
  double CWRT() {
    if (crossflow()>0) {
	  return AHURT_control.getValue();
	} else {
    return CWST_control.getValue() + 
	  (AHURT_control.getValue()-CWST_control.getValue())*pump_control.getValue()/CWflow_control.getValue();
	}
  }  
}