class EnergySimLogic {	
	//******************************************************
	//PUBLIC VALUES VIEWED FROM OUTSIDE
	//******************************************************
	
	//supply air wet bulb temperature
	//DOES NOT DEPEND ON OTHER FUNCTIONS IN EnergySimLogic
	public double twb(double t) {
		return 72+0.5*(tdb(t)-72);
	}
	
	//supply air dry bulb temperature
	//DOES NOT DEPEND ON OTHER FUNCTIONS IN EnergySimLogic
	public double tdb(double t) {
		return 83.0-10.0*Math.cos(t/(24*2*Math.PI));
	}
	
	//building load (heat transfer needed)
	static double MIN_LOAD = 8;
	//DOES NOT DEPEND ON OTHER FUNCTIONS IN EnergySimLogic
	public double need(double t) {
		t -= ((int)t)/24;
		if (t<6 || t>17)
			return MIN_LOAD;
		double ld = 15.0;
		double dl = 200.0;
		double ar = 1.0;
		double x = ar*2.0*Math.cos((t-15)*5*Math.PI/12)
			+ ld*Math.cos((t-13)*1.17*Math.PI/12);
		return x*dl/(ar+ld);
	}
	
	//operating cooling capacity---"efficiency"
	//DOES NOT DEPEND ON OTHER FUNCTIONS IN EnergySimLogic
	public double cap(double t,double cwt,int numChillers,int numCTFans) {
		System.out.print("");
		return numChillers*(102.2+1.65*(cwt-44)-0.4*(teetee-75));
	}
	
	//air temperature entering coil
	//DOES NOT DEPEND ON OTHER FUNCTIONS IN EnergySimLogic
	public double ta() {
		return 77.0;
	}	
	
	private double teetee;
	
	//temperature of water into chiller from cooling tower
	public double tt(double t, double cwt,int numChillers,int numCTFans) {
		//correction for condensor performance
		double del = x(t,cwt,numChillers,numCTFans)
			*cap(t,cwt,numChillers,numCTFans)*12000
			+akw(t,cwt,numChillers,numCTFans)*3413
				-nt[numCTFans]*(tdb(t)-twb(t));//end expression for del
		if (del>0)
			teetee = twb(t)+del/((50000+nt[numCTFans]*60000)*0.074*60);
		else
			teetee = twb(t);
		return teetee;
	}
	
	//temperature of air leaving coil
	private static double CFM = 72000;
	public double lat(double t, double cwt,int numChillers,int numCTFans) {
		double da = x(t,cwt,numChillers,numCTFans)*cap(t,cwt,numChillers,numCTFans)*12000/(1.09*CFM);
		return ta()-da;
	}
	
	//actual power off chiller
	public double akw(double t,double cwt,int numChillers,int numCTFans) {
		//kilowatts chillers are using
		double kw = (82.7+0.3625*(cwt-44)-0.4*(teetee-75))*numChillers;
		return kw*y(t,cwt,numChillers,numCTFans);//return kw times an "efficiency?" factor.
	}
	
	//total kilowatts
	public double tkw(double t,double cwt,int numChillers,int numCTFans,int numCWPumps,int numSAFans) {
		return akw(t,cwt,numChillers,numCTFans) 
			+ ctkw[numCTFans] + wp[numCWPumps] + fa[numSAFans];
	}
	
	//******************************************************
	//PRIVATE FUNCTIONS ONLY USED WITHIN "EnergySimLogic"
	//******************************************************
	
	//fraction limit of cap
	//if x>0.9, tell user to add another chiller
	private double x(double t, double cwt,int numChillers,int numCTFans) {
		double returnThis = need(t)/(cap(t,cwt,numChillers,numCTFans)+0.0001);
		if (returnThis>1.1)
			return 1.1;
		else if (returnThis<0.3)
			return 0.3;
		return returnThis;
	}

	//kilowatt correction factor
	private double y(double t, double cwt,int numChillers,int numCTFans) {
		return 0.27+Math.pow((x(t,cwt,numChillers,numCTFans)-0.3),1.65)
			+(x(t,cwt,numChillers,numCTFans)-0.3)/4;
	}
	
	//******************************************************
	//PRIVATE ARRAYS USED LIKE SIMPLE FUNCTIONS TO TRANSLATE INPUT INTO PHYSICAL VALUES
	//******************************************************

	private static final double[] nt = {
		3000*0.00025*0.74*60,//low
		8000*0.00025*0.74*60,//medium
		17000*0.00025*0.74*60//high
	};

	private static final double[] ctkw = {
		0,//low
		7.5,//medium
		22.4//high
	};

	//current to the supply air fans
	private static final double[] fa = {
		Double.NaN,//Nan is an error value, since java array indexing begins at zero
		28,//low
		40,//medium
		52//high
	};

	//water flow from chilled water pumps
	private static final double[] gpm = {
		Double.NaN,//Nan is an error value, since java array indexing begins at zero
		500,//low
		7.5,//medium
		22.4//high
	};

	//air flow from supply air fans
	private static final double[] cfm = {
		Double.NaN,//Nan is an error value, since java array indexing begins at zero
		70000,//low
		90000,//medium
		108000//high
	};

	//current to the chilled water pumps
	private static final double[] wp = {
		Double.NaN,//Nan is an error value, since java array indexing begins at zero
		9,//low
		13,//medium
		18//high
	};

	//private double dw(double t, double cwt,int numChillers,int numCTFans,int numCWPumps) {
//		return x(t,cwt,numChillers,numCTFans)*cap(t,cwt,numChillers,numCTFans)*12000/(500*gpm(numCWPumps));
	//}
}
