import java.awt.*;
import java.awt.event.*;

public class ButtonControls extends Panel {
    //fields
    PID_Simulation pid_sim;
	
    ButtonControl pb;
    ButtonControl gain;
    //ButtonControl D;
	
    //constructor
    ButtonControls(PID_Simulation pid) {
	pid_sim = pid;
	pid_sim.button_controls = this;
		
	pb = new ButtonControl("PB", 0.5) {
		public void setValue(double val) {
		    val = Math.max(val, 0);
		    val = Math.min(val, 10);
		    pid_sim.PB = val;
		    super.setValue(val);
		}
	    };
	gain = new ButtonControl("GAIN", 0.01) {
		public void setValue(double val) {
		    val = Math.max(val, 0);
		    val = Math.min(val, 0.1);
		    pid_sim.GAIN = val;
		    super.setValue(val);
		}
	    };
	/*D = new ButtonControl("D", 1, new Dee(pid_sim)) {    public void setValue(double val) {
	  val = Math.max(val, 0);
	  val = Math.min(val, 10);
	  pid_sim.D = val;
	  super.setValue(val);
	  }};*/

	//OA_percent.setBackground(Color.);
	pb.setBackground(Color.yellow);
	gain.setBackground(Color.green);
	//D.setBackground(Color.blue);
		
	//Panel p = new Panel();
	setLayout(new GridLayout(2,1));
	add(pb);
	add(gain);
	//p.add(D);

	//add(p);
    }
	
    //methods
    public void update_controls() {
	pb.setValue(pid_sim.PB);
	gain.setValue(pid_sim.GAIN);
	//D.setValue(pid_sim.D);
	repaint();
    }
}

class ButtonControl extends Panel {
    Button dec = new Button("<");
    chunk can = new chunk();
    Button inc = new Button(">");
    String name;
	
    double step;
	
    double value;
	
    //constructor
    ButtonControl(String n, double s) {
	name = n;
	step = s;
	can.setSize(100,50);
	setLayout(new BorderLayout());
	dec.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setValue(value - step);
		}
	    });
	inc.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setValue(value + step);
		}
	    });
	add("West", dec);
	add("Center", can);
	add("East", inc);
    }
	
    //methods
    public void setValue(double v) {
	value = v;
	//label.setText(name+" "+value);
	can.setText(name+" "+value);
	repaint();
    }
}

class chunk extends Canvas {
    private String label;
    public void setText(String s) {
	label = s;
	repaint();
    }
    public void paint(Graphics g) {
	g.drawString(label, 10, 25);
    }
}
