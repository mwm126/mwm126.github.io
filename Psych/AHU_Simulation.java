class AHU_Simulation extends Thread {
	//data variables
	
	final double p = 14.696;//barometric pressure in psia
	final double cp = 1000;//specific heat of the room
	
	double room_T = 80;//temperature in Farenheit
	double supply_T = 56;//temperature in Farenheit
	double mixed_T = 83;//temperature in Farenheit
	double outside_T = 90;//temperature in Farenheit
	
	double room_rh = 50;//relative humidity, percentage
	double supply_rh = 95;//relative humidity, percentage
	double mixed_rh = 47;//relative humidity, percentage
	double Twb = 84;//outside wet bulb temperature {related to outside relative humidity}
	
	double room_w = 0.011;//pounds water vapor per pound dry air
	double supply_w = 0.0095;//pounds water vapor per pound dry air
	double mixed_w = 0.0152;//pounds water vapor per pound dry air
	double outside_w = 0.024;//pounds water vapor per pound dry air
	
	double room_h = 31.2;//enthalpy in BTUs per pound
	double supply_h = 23.7;//enthalpy in BTUs per pound
	double mixed_h = 36.3;//enthalpy in BTUs per pound
	double outside_h = 48.2;//enthalpy in BTUs per pound	
	
	double OA_percent = 30;//percent of outside air
	
	double sa_cfm = 30000;//supply air flow rate
	
	double Q = 80;//load in tons
	double SHF = 0.9;//sensible heat factor [dimensionless]
	
	double Q_in;
	double SHF_in;
	
	double flow_w;
	double flow_T;
	
	double recirc_w;
	double recirc_T;
	
	String section = "top";
	int counter = 0;
	
	AHU ahu;
	
	//constructor
	AHU_Simulation(AHU a) {
		super();
		ahu = a;
	}
	
	//constants
	double SPEED = 5;
	final int STEPS = 20;
	
	public void run() {
		while(true) {
			try {
				sleep((int)(1000/SPEED));//sleep for one second
			} catch (InterruptedException ie) {}
			updateRoomConditions();//update stuff, should be "synchronized"
			updateFlowConditions();
			counter++;
			if (counter==STEPS) {
				counter=0;
				if (section == "right") {
					section = "top";
				} else if (section == "top") {
					section = "left";
				} else if (section == "left") {
					section = "bottom";
				} else if (section == "bottom") {
					section = "right";
				}
			}
			ahu.repaint();
		}//end infinite while loop
	}//end run()
	
	//methods
	
	double findSatVaporPressure(double farenheit_temperature) {
		double T = farenheit_temperature + 459.67;//convert to Rankine
		return Math.exp(-10440.4/T - 11.29465 - 0.027022355*T + 1.289036E-5*T*T
			 - 2.4780681E-9*T*T*T+6.5459673*Math.log(T));//sat vapor pressure		
	}

	double findEnthalpy(double w, double T) {
		return ((0.24+0.444*w)*T + 1061*w);
	}
	
	double findHumidity(double pw, double p) {
		return 0.62198*(pw/(p - pw));
	}
	
	 void updateRoomConditions() {
		double pw = (room_rh/100)*findSatVaporPressure(room_T);

		double cool_supply = sa_cfm*60*0.241*(room_T - supply_T)/(13.2*12000);

		//double w_in = 42;//meaning of life, the universe, everything...
		
		Q_in = sa_cfm*60*(room_h - supply_h)/(13.2*12000);
		SHF_in = cool_supply/Q_in;
		
		room_T = room_T + (Q*SHF - cool_supply)/cp;
		
		long now = System.currentTimeMillis()/1000;
		//System.out.println("now = "+now);
		
		if (now%6==3) {
			//System.out.println("temp time");
			if (room_T>80)
				AHU.print("Whew!  It's too hot in here!");
			else if (room_T<70) 
				AHU.print("Brrr!  It's too cold in here!");
			else
				AHU.print("");
		}

		
		room_w = findHumidity(pw, p);
		room_h = findEnthalpy(room_w, room_T);
		

		room_rh = room_rh + 
		(Q*(1-SHF) - (room_w - supply_w)*sa_cfm*60*(1093+.444*supply_T)/(13.2*12000))/100;
		
		if (now%6==0) {
			if (room_rh>60)
				AHU.print("Ugh!  It's too humid.");
			else if (room_rh<30)
				AHU.print("It's too dry.");
			else
				AHU.print("");
		}

		
		updateMixedConditions();
	}
	
	void updateOutsideConditions() {
		//double pws = findSatVaporPressure(outside_T);
		double pw_star = findSatVaporPressure(Twb);
		double w_star = findHumidity(pw_star, p);
		outside_w = ((1093 - 0.556*Twb)*w_star - 0.24*(outside_T - Twb))/(1093 + 0.444*outside_T - Twb);
		outside_h = findEnthalpy(outside_w, outside_T);
	}
	
	void updateMixedConditions() {
		mixed_T = (OA_percent/100)*outside_T + (1-OA_percent/100)*room_T;
		mixed_w = (OA_percent/100)*outside_w + (1-OA_percent/100)*room_w;
		//double pws = findSatVaporPressure(mixed_T);
		mixed_h = findEnthalpy(mixed_w, mixed_T);
	}
	
	void updateSupplyConditions() {
		if (supply_T<60) {
			supply_rh = 95;
		} else {
			supply_rh = 95 - (supply_T - 60)*3;
		}
		double pw = (supply_rh/100)*findSatVaporPressure(supply_T);
		supply_w = findHumidity(pw, p);
		supply_h = findEnthalpy(supply_w, supply_T);
	}
	
	void updateFlowConditions() {
		if (section == "right") {
			flow_w = recirc_w = supply_w + (room_w - supply_w)*(counter*1.0/STEPS);//supply to room
			flow_T = recirc_T = supply_T + (room_T - supply_T)*(counter*1.0/STEPS);//supply to room
		} else if (section == "top") {
			flow_w = recirc_w = room_w;//stay at room
			flow_T = recirc_T = room_T;
		} else if (section == "left") {
			flow_w = outside_w + (mixed_w - outside_w)*(counter*1.0/STEPS);//outside to mixed
			flow_T = outside_T + (mixed_T - outside_T)*(counter*1.0/STEPS);
			recirc_w = room_w + (mixed_w - room_w)*(counter*1.0/STEPS);//room to mixed
			recirc_T = room_T + (mixed_T - room_T)*(counter*1.0/STEPS);
		} else if (section == "bottom") {
			flow_w = recirc_w = mixed_w + (supply_w - mixed_w)*Math.pow(counter*1.0/STEPS,2);//mixed to supply quadratically
			flow_T = recirc_T = mixed_T + (supply_T - mixed_T)*(counter*1.0/STEPS);
		}
	}
	
	public void update_everything() {
		updateOutsideConditions();
		updateMixedConditions();
		updateSupplyConditions();
		updateFlowConditions();
		//print_controls();
		//print_everything();
		ahu.update_everything();
	}
}
