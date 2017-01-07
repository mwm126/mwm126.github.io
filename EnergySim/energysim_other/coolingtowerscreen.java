import java.awt.*;

public class CoolingTowerScreen extends Screen {
  //CONSTANTS
  final int CONTROL_WIDTH = 90;
  final int CONTROL_HEIGHT = 60;
  final double A = 0.91875;
  final double B = 0.3;
  final double C = 1.7925;
  final double D = -0.01625;
  final double E = 0.4167;
  final double F = -0.03275;
  final double G = 13;
  final double n = 0.6;
  ButtonControl CT_control = new ButtonControl("CT",30,5,new Point(0,100),this,true);
  ButtonControl pump_control = new ButtonControl("pump",3,0.5,new Point(0,5),this,true);
  ButtonControl CTST_control = new ButtonControl("CTST",80,1,new Point(70,100),this,true);
  ButtonControl wet_bulb_control = new ButtonControl("wet bulb",70,2,new Point(50,100),this,false);
  ButtonControl tons_control = new ButtonControl("tons",100,5,new Point(25,150),this,false);
  LabelControl CTRT_label = new LabelControl("CTRT",90);
  
  CoolingTowerScreen(Image bg) {
    super(bg);
	add(CT_control);
	add(pump_control);
	add(CTST_control);
	add(CTRT_label);	
	add(wet_bulb_control);	
	add(tons_control);
	switch_to_manual();
  }

  public void callback(String name) {
    if (name.equals("CT")) {
	  pump_control.setValue(Math.max(pump_control.range.x,Math.min(pump_control.range.y,pump())));
	  if (pump_control.outOfRange()) {
	    CTST_control.setValue(CTST());
	  }
	} else if (name.equals("pump")) {
	  CT_control.setValue(Math.max(CT_control.range.x,Math.min(CT_control.range.y,CT())));
	  if (CT_control.outOfRange()) {
	    CTST_control.setValue(CTST());
	  }
	} else if (name.equals("CTST")) {
	  CT_control.setValue(Math.max(CT_control.range.x,Math.min(CT_control.range.y,CT())));
	  if (CT_control.outOfRange()) {
	    pump_control.setValue(pump());
	  }
	} else if (name.equals("wet bulb")) {
	  CTST_control.setValue(CTST());
	} else if (name.equals("tons")) {
	  CTST_control.setValue(CTST());
	}
	CTRT_label.setValue(CTST_control.getValue()+delta_T());
  }
  
  void switch_to_training() {
    wet_bulb_control.show(true);
	tons_control.show(true);
	repaint();
  }
  void switch_to_manual() {
    wet_bulb_control.show(false);
	tons_control.show(false);
	repaint();
  }
  
  private boolean laidOut = false;
  
  public void paint(Graphics g) {
    super.paint(g);
	if (!laidOut) {
	  Insets insets = insets();
	  CT_control.reshape(20 + insets.left, 5 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  pump_control.reshape(300 + insets.left, 300 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  CTST_control.reshape(150 + insets.left, 300 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  CTRT_label.reshape(300 + insets.left, 30 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  wet_bulb_control.reshape(20 + insets.left, 200 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  tons_control.reshape(500 + insets.left, 200 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  laidOut = true;
	}
	g.drawString("CoolingTower",200,200);
  }
  
  double delta_T() {
    return tons_control.getValue()*12000/(500*80*pump_control.getValue());
  }
  
  double CTST() {
	return A*wet_bulb_control.getValue()
	  +B*delta_T()
	  +C*pump_control.getValue()
	  +D*wet_bulb_control.getValue()*delta_T()
	  +E*pump_control.getValue()*delta_T()
	  +F*pump_control.getValue()*wet_bulb_control.getValue()
	  +G;
  }
  double CT() {
    return Math.pow(pump_control.getValue(),1-1/n);
  }
  double pump() {
    return Math.pow(CT_control.getValue(),n/(n-1));
  }
}