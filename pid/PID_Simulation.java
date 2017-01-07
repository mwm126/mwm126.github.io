public class PID_Simulation extends Thread {
    //****************************************************************
    //constants
    //****************************************************************
    final double CAP = 100;//volume of tank in gallons
    final double DT = 0.01;//whatever this is
    final double TS = 140;//thermostat setting
    final double T_initial = 50;//temperature of incoming water	
    //****************************************************************
    //data variables
    //****************************************************************
	
	//"input" variables
    double PB;//temperature in Farenheit
    double GAIN;//temperature in Farenheit
    //double D = 0;//temperature in Farenheit
	
    //"output" variables
    double GPM;//varies from 0 to 6 every five seconds
    double V;
    double T = 135;//temperature of water in tank
	
    double QIN;
    double Q2;
	
    double I = 0;//whatever this is
	
    double D = 0;//not used at all yet
	
    CircularQueue gpmcq;
    CircularQueue vcq;
    CircularQueue tcq;

    int counter = 0;
	
    //other variables
    ButtonControls button_controls;
	
    PID pid;
	
	//****************************************************************
	//constructor
	//****************************************************************
    PID_Simulation(PID the_pid) {
	super();
	pid = the_pid;
	gpmcq = new CircularQueue(50);
	vcq = new CircularQueue(50);
	tcq = new CircularQueue(50);
    }
	
    //constants
    final int BREAK = 100;
	
    //****************************************************************
    //special run method
    //****************************************************************
    public void run() {
	while(true) {
	    try {
		sleep(BREAK);//sleep for one second
	    } catch (InterruptedException ie) {}

	    update_everything();
	    counter++;

	    pid.repaint();
	}//end infinite while loop
    }//end run()
	
    //****************************************************************
    //methods
    //****************************************************************
	
    void updateGPM() {
	if ((counter % (5000/BREAK))<(2500/BREAK)) {
	    GPM = 2;
	} else {
	    GPM = 8;
	}
	gpmcq.addItem(GPM);
    }
	
    void updateV() {
	double P = (TS - T)/PB;
	I += GAIN*(TS-T);
		
	V = P+I;//+D?
	V = Math.min(V,1);
	V = Math.max(V,0);
	vcq.addItem(V);
    }
	
    void updateT() {
	QIN = V*574*950*DT;
	Q2 = GPM*500*(T - T_initial)*DT;
	T = (CAP*8.3*T + QIN - Q2)/(CAP*8.3);
	tcq.addItem(T);
    }
	
    public void update_everything() {
	updateGPM();
	updateV();
	updateT();
	//print_controls();
	//print_everything();
	button_controls.update_controls();
	pid.repaint();
    }
}
