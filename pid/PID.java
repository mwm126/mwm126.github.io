import java.awt.*;

public class PID extends java.applet.Applet {
    PID_Simulation pid_sim;
    LineGraph GPM;
    LineGraph V;
    LineGraph T;
    ButtonControls button_controls;
	
    //Button update_everything;
	
    public void init() {
	pid_sim = new PID_Simulation(this);
	GPM = new LineGraph("GPM", pid_sim.gpmcq, 0, 10);
	V = new LineGraph("V", pid_sim.vcq, 0, 1.5);
	T = new LineGraph("T", pid_sim.tcq, 130, 150);
		
	GPM.setBackground(Color.red);
	V.setBackground(Color.white);
	T.setBackground(Color.blue);

	button_controls = new ButtonControls(pid_sim);
	//update_everything = new Button("update everything");

	setBackground(Color.white);
	resize(600,480);
	pid_sim.update_everything();
	pid_sim.start();
		
	//layout
	Panel panel = new Panel();
	panel.setLayout(new GridLayout(3,1));
	panel.add(GPM);
	panel.add(V);
	panel.add(T);

	setLayout(new BorderLayout());
	add("Center",panel);
	Panel pan = new Panel();
	pan.add(button_controls);
	add("East", pan);
	//add(panel);
	//add(button_controls);
    }

    public void paint(Graphics g) {
	GPM.repaint();
	V.repaint();
	T.repaint();
    }
}


