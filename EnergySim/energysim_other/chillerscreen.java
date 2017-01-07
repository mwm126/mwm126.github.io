import java.awt.*;

public class ChillerScreen extends Screen {
  final int CONTROL_WIDTH = 90;
  final int CONTROL_HEIGHT = 60;
  ButtonControl chiller_control = new ButtonControl("chiller",40,5,new Point(30,102),this,true);
  ButtonControl chiller2_control = new ButtonControl("chiller2",0,5,new Point(30,102),this,true);
  ButtonControl CTST_control = new ButtonControl("CTST",75,1,new Point(50,88),this,false);
  ButtonControl CTflow_control = new ButtonControl("CT flow",90,10,new Point(0,600),this,false);
  //CT flow is in gpm
  ButtonControl CWST_control = new ButtonControl("CWST",44,1,new Point(40,55),this,true);
  ButtonControl tons_control = new ButtonControl("tons",54,1,new Point(50,65),this,false);
  LabelControl CTRT_label = new LabelControl("CTRT",54);
  LabelControl CWRT_label = new LabelControl("CWRT",54);

  ChillerScreen(Image bg) {
    super(bg);
	add(chiller_control);
	add(chiller2_control);
	add(CTST_control);
	add(CTflow_control);
	add(CWST_control);
	add(tons_control);
	add(CTRT_label);
	add(CWRT_label);
	switch_to_manual();
  }
  public void callback(String name) {
    //if in_training_mode (haven't considered manual yet)
	if (name.equals("chiller")) {
	  //change CWST
	} else if (name.equals("chiller2")) {
	  //change CWST
	} else if (name.equals("CTST")) {
	  //change chillers keep everything else constant
	} else if (name.equals("CT flow")) {
	} else if (name.equals("CWST")) {
	  //change chillers keep everything else constant
	} else if (name.equals("tons")) {
	  //if chiller.isAuto() or chiller2.isAuto()??
	}
	CTRT_label.setValue(CTRT());
	CWRT_label.setValue(CWRT());
  }
  
  void switch_to_training() {
    CTST_control.show(true);
	CTflow_control.show(true);
	tons_control.show(true);
	repaint();
  }
  void switch_to_manual() {
    CTST_control.show(false);
	CTflow_control.show(false);
	tons_control.show(false);
	repaint();
  }
  
  private boolean laidOut = false;
  
  public void paint(Graphics g) {
    super.paint(g);
	if (!laidOut) {
	  Insets insets = insets();
	  chiller_control.reshape(300 + insets.left, 5 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  chiller2_control.reshape(300 + insets.left, 300 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  CTST_control.reshape(150 + insets.left, 250 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  CTflow_control.reshape(100 + insets.left, 350 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  CWST_control.reshape(400 + insets.left, 250 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  CTRT_label.reshape(100 + insets.left, 450 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  CWRT_label.reshape(450 + insets.left, 450 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  tons_control.reshape(400 + insets.left, 350 + insets.top, CONTROL_WIDTH, CONTROL_HEIGHT);
	  laidOut = true;
	}
	g.drawString("CoolingTower",200,200);
  }

  double CTRT() {
    double CAP = 102.2+1.65*(CWST_control.getValue()-44)-0.4*(CTST_control.getValue()-75);
    double KW = 82.7 + 0.3625*(CWST_control.getValue()-44)-0.4*(CTST_control.getValue()-75);
	double X = chiller_control.getValue()/CAP;
	double X2 = chiller2_control.getValue()/CAP;
	double Y = 0.27*Math.pow(X-0.3,1.65)+(X-0.3)/4;
	double Y2 = 0.27*Math.pow(X2-0.3,1.65)+(X2-0.3)/4;
	double AKW = (Y+Y2)*KW;
	double QCT = (X+X2)*CAP*12000+AKW*3413;
    return CTST_control.getValue() + QCT/(500*CTflow_control.getValue());
  }
  
  double CWRT() {
    double gpm = 0;
	if (chiller_control.getValue()>32)
	  gpm += 200;
	if (chiller2_control.getValue()>32)
	  gpm += 200;
    return CWST_control.getValue() + tons_control.getValue()*12000/
	  (500*gpm);
  }
}