import java.awt.*;
import java.awt.event.*;

public class AHU extends java.applet.Applet {
    AHU_Simulation ahu_sim;
    FlowChart flow_chart = new FlowChart();
    PsychrometricChart psych_chart = new PsychrometricChart();
    ButtonControls button_controls = new ButtonControls();
	
    static public Label status_bar = new Label("                                          ");
	
    static public void print(String s) {
	status_bar.setText(s);
    }
	
    public void init() {
	ahu_sim = new AHU_Simulation(this);
	flow_chart.setSize(170,180);
		
	setBackground(Color.white);
	resize(600,480);
	ahu_sim.update_everything();
	ahu_sim.start();
		
	//layout
	Panel p = new Panel();
	p.setLayout(new BorderLayout());
	p.add("North",flow_chart);
	p.add("South",button_controls);
	setLayout(new BorderLayout());
	add("West",p);
	add("Center",psych_chart);
	add("South",status_bar);
    }

    public void paint(Graphics g) {
	psych_chart.repaint();
	if (flow_chart!=null)
	    flow_chart.repaint();
    }

    public void update_everything() {
	button_controls.update_controls();
	repaint();
    }

    class ButtonControl extends Panel {
	Button dec = new Button("<");
	Label label = new Label();
	Button inc = new Button(">");
	String name;
	
	double step;
	
	double value;
	
	//constructor
	ButtonControl(String n, double s) {
	    name = n;
	    step = s;
	    setLayout(new BorderLayout());
	    dec.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			setValue(value - step);
			update_everything();
		    }
		});
	    inc.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			setValue(value + step);
			update_everything();
		    }
		});
	    add("West", dec);
	    add("Center", label);
	    add("East", inc);
	}
	
	//methods
	public void setValue(double v) {
	    value = v;
	    label.setText(name+" "+format(value,2));
	}
    }

    class ButtonControls extends Panel {
	//fields
	ButtonControl OA_percent;
	ButtonControl SA_temperature;
	ButtonControl CFM;
	
	ButtonControl OA_temperature;
	ButtonControl OA_WB;
	ButtonControl TONS;
	ButtonControl SHF;
	
	ButtonControl speed;

	//constructor
	ButtonControls() {
	    OA_percent = new ButtonControl("OA%", 5) {
		    public void setValue(double val) {
			if (val<20) AHU.print("You need at least 20% fresh, oxygenated outside air or people suffocate.");
			if (val>100) AHU.print("Gosh, using 100% outside air is an expensive way to run this place!");
			val = Math.max(val, 20);
			val = Math.min(val, 100);
			ahu_sim.OA_percent = val;
			super.setValue(val);
		    }
	    };
	    SA_temperature = new ButtonControl("SA T", 1) {
		    public void setValue(double val) {
			if (val>ahu_sim.room_T) AHU.print("You know, adding heat to the building is not a good way to air condition?");
			if (val<42) AHU.print("Our chiller can't cool air below 42 degrees.");
			val = Math.max(val, 42);
			val = Math.min(val, ahu_sim.room_T);
			ahu_sim.supply_T = val;
			super.setValue(val);
	    }};
	    CFM = new ButtonControl("CFM", 1000) {
		    public void setValue(double val) {
			if (val>40000) AHU.print("With that much air rushing through the ducts, it sounds like a Penn State football game!");
			if (val<20000) AHU.print("Without at least 20000 cubic feet per minute of air flow, we get poor distribution.");
			val = Math.max(val, 20000);
			val = Math.min(val, 40000);
			ahu_sim.sa_cfm = val;
			super.setValue(val);
	    }};
	    OA_temperature = new ButtonControl("OA T", 1) {
		    public void setValue(double val) {
			if (val<ahu_sim.room_T) AHU.print("Wouldn't it be more efficient to go to an econmizer cycle now?");
			if (val>94) AHU.print("It's a hot day outside, but it's not the Sahara desert!");
			val = Math.max(val, ahu_sim.room_T);
			val = Math.min(val, 94);
			ahu_sim.outside_T = val;
			super.setValue(val);
	    }};
	    OA_WB = new ButtonControl("OA WB", 1) {
		    public void setValue(double val) {
			if (val<65) AHU.print("Wouldn't it be more efficient to go to an econmizer cycle now?");
			if (val>ahu_sim.outside_T) AHU.print("It feels like a rainy day in Miami right now!");
			val = Math.max(val, 65);
			val = Math.min(val, ahu_sim.outside_T);
			ahu_sim.Twb = val;
			super.setValue(val);
		    }
		};
	    TONS = new ButtonControl("Tons", 5) {
		    public void setValue(double val) {
			if (val<40) AHU.print("If you go below 40 Tons, the chiller will go into surge...and that's not good");
			if (val>100) AHU.print("If you want more cooling, ya shoulda bought a bigger chiller.");
			val = Math.max(val, 40);
			val = Math.min(val, 100);
			ahu_sim.Q = val;
			super.setValue(val);
		    }
		};
	    SHF = new ButtonControl("SHF", 0.05) {
		    public void setValue(double val) {
			if (val<0.5) AHU.print("With an SHF of 0.5, you must be trying to condition a sauna!");
			if (val>1.0) AHU.print("Are you positive there is no humidity load right now?"); 
			val = Math.max(val, 0.5);
			val = Math.min(val, 1.0);
			ahu_sim.SHF = val;
			super.setValue(val);
		    }
		};
	    speed = new ButtonControl("sim speed", 1) {//fifty milliseconds
		    public void setValue(double val) {
			if (val<1) AHU.print("Increase the SimSpeed to make things change faster");
			if (val>30) AHU.print("I'm moving as fast as I can!!");
			val = Math.max(val, 1);
			val = Math.min(val, 30);
			ahu_sim.SPEED = val;
			super.setValue(val);
		    }
		};

	    Color lightBlue = new Color(200,200,255);
	    Color lightRed = new Color(255,200,200);
	    SA_temperature.setForeground(Color.blue);
	    SA_temperature.setBackground(lightBlue);
	    CFM.setForeground(Color.blue);
	    CFM.setBackground(lightBlue);
	    OA_temperature.setForeground(Color.red);
	    OA_temperature.setBackground(lightRed);
	    OA_WB.setForeground(Color.red);
	    OA_WB.setBackground(lightRed);
	    TONS.setBackground(Color.lightGray);
	    SHF.setBackground(Color.lightGray);
		
	    speed.setBackground(Color.magenta);
		
	    Label label = new Label("Adjust system");
		
	    Panel p = new Panel();
	    p.setLayout(new GridLayout(6,1));
	    p.add(new Label(""));
	    p.add(new Label(""));
	    p.add(label);
	    p.add(OA_percent);
	    p.add(SA_temperature);
	    p.add(CFM);
		
	    Label label2 = new Label("Adjust weather");
	    Label label3 = new Label("Adjust load");
		
	    Panel p2 = new Panel();
	    p2.setLayout(new GridLayout(7,1));
	    p2.add(label2);
	    p2.add(OA_temperature);
	    p2.add(OA_WB);
	    p2.add(label3);
	    p2.add(TONS);
	    p2.add(SHF);
	    p2.add(speed);
		
	    setLayout(new GridLayout(2,1,4,4));
	    add(p);
	    add(p2);
	}
	
	//methods
	public void update_controls() {
	    OA_percent.setValue(ahu_sim.OA_percent);
	    SA_temperature.setValue(ahu_sim.supply_T);
	    CFM.setValue(ahu_sim.sa_cfm);
	    OA_temperature.setValue(ahu_sim.outside_T);
	    OA_WB.setValue(ahu_sim.Twb);
	    TONS.setValue(ahu_sim.Q);
	    SHF.setValue(ahu_sim.SHF);
	    speed.setValue(ahu_sim.SPEED);
	    repaint();
	}
    }

    class FlowChart extends Canvas {
	//constants
	final int TOP = 30;
	final int BOTTOM = 80;
	final int RIGHT = 140;
	final int MIDDLE = 30;
	final int LEFT = 0;
	
	final int GREEN_R = Color.green.getRed();
	final int GREEN_G = Color.green.getGreen();
	final int GREEN_B = Color.green.getBlue();
	final int BLUE_R = Color.blue.getRed();
	final int BLUE_G = Color.blue.getGreen();
	final int BLUE_B = Color.blue.getBlue();
	final int RED_R = Color.red.getRed();
	final int RED_G = Color.red.getGreen();
	final int RED_B = Color.red.getBlue();
	final int ORANGE_R = Color.orange.getRed();
	final int ORANGE_G = Color.orange.getGreen();
	final int ORANGE_B = Color.orange.getBlue();
	
	final float GREEN_H = Color.RGBtoHSB(GREEN_R,GREEN_G,GREEN_B,null)[0];
	final float GREEN_S = Color.RGBtoHSB(GREEN_R,GREEN_G,GREEN_B,null)[1];
	final float RED_H = Color.RGBtoHSB(RED_R,RED_G,RED_B,null)[0];
	final float RED_S = Color.RGBtoHSB(RED_R,RED_G,RED_B,null)[1];
	final float BLUE_H = Color.RGBtoHSB(BLUE_R,BLUE_G,BLUE_B,null)[0];
	final float BLUE_S = Color.RGBtoHSB(BLUE_R,BLUE_G,BLUE_B,null)[1];
	final float ORANGE_H = Color.RGBtoHSB(ORANGE_R,ORANGE_G,ORANGE_B,null)[0];
	final float ORANGE_S = Color.RGBtoHSB(ORANGE_R,ORANGE_G,ORANGE_B,null)[1];
	
	final float BRIGHT = 1;
	
	//methods
	
	public void paint(Graphics g) {
	    g.setColor(Color.black);
	    Rectangle r = getBounds();
	    g.drawRect(0,0,r.width-1,r.height-1);
		
	    //OUTLINE
	    g.drawLine(0,TOP,RIGHT,TOP);
	    g.drawRect(0,TOP+20,MIDDLE,BOTTOM-TOP-20);
	    g.drawRect(MIDDLE+20,TOP+20,RIGHT-MIDDLE-20,BOTTOM-TOP-20);
	    g.drawLine(0,BOTTOM+20,RIGHT,BOTTOM+20);

	    //HOUSE BODY
	    g.setColor(Color.black);
	    g.fillRect(RIGHT,BOTTOM-20,20,20);
		
	    if (ahu_sim.section.equals("top")) {
		g.setColor(Color.green);
		int X = RIGHT - (int)((RIGHT - MIDDLE)*ahu_sim.counter/ahu_sim.STEPS);
		g.fillArc(X,TOP,20,20,(int)(ahu_sim.OA_percent*3.6)+90,360 - (int)(ahu_sim.OA_percent*3.6));
		int X2 = RIGHT - (int)((RIGHT - LEFT)*ahu_sim.counter/ahu_sim.STEPS);
		g.fillArc(X2,TOP,20,20,90,(int)(ahu_sim.OA_percent*3.6));
	    } else if (ahu_sim.section.equals("left")) {
		g.setColor(new Color(Color.HSBtoRGB((float)(GREEN_H+(ORANGE_H-GREEN_H)*ahu_sim.counter/ahu_sim.STEPS),
						    (float)(GREEN_S+(ORANGE_S-GREEN_S)*ahu_sim.counter/ahu_sim.STEPS),
						    BRIGHT)));
		int Y = TOP - (int)((TOP - BOTTOM)*ahu_sim.counter/ahu_sim.STEPS);
		g.fillArc(MIDDLE,Y,20,20,(int)(ahu_sim.OA_percent*3.6)+90,360 - (int)(ahu_sim.OA_percent*3.6));
		g.setColor(new Color(Color.HSBtoRGB((float)(RED_H+(ORANGE_H-RED_H)*ahu_sim.counter/ahu_sim.STEPS),
						    (float)(RED_S+(ORANGE_S-RED_S)*ahu_sim.counter/ahu_sim.STEPS),
						    BRIGHT)));
		int X = LEFT + (int)((MIDDLE - LEFT)*ahu_sim.counter/ahu_sim.STEPS);
		g.fillArc(X,BOTTOM,20,20,90,(int)(ahu_sim.OA_percent*3.6));
	    } else if (ahu_sim.section.equals("bottom")) {
		int X = MIDDLE + (int)((RIGHT - MIDDLE)*ahu_sim.counter/ahu_sim.STEPS);
		g.setColor(new Color(Color.HSBtoRGB((float)(ORANGE_H+(BLUE_H-ORANGE_H)*ahu_sim.counter/ahu_sim.STEPS),
						    (float)(ORANGE_S+(BLUE_S-ORANGE_S)*ahu_sim.counter/ahu_sim.STEPS),
						    BRIGHT)));
		//g.setColor(Color.red);
		g.fillArc(X,BOTTOM,20,20,(int)(ahu_sim.OA_percent*3.6)+90,360 - (int)(ahu_sim.OA_percent*3.6));
		//g.setColor(Color.blue);
		g.fillArc(X,BOTTOM,20,20,90,(int)(ahu_sim.OA_percent*3.6));
	    } else if (ahu_sim.section.equals("right")) {
		int Y = BOTTOM - (int)((BOTTOM - TOP)*ahu_sim.counter/ahu_sim.STEPS);
		g.setColor(new Color(Color.HSBtoRGB((float)(BLUE_H+(GREEN_H-BLUE_H)*ahu_sim.counter/ahu_sim.STEPS),
						    (float)(BLUE_S+(GREEN_S-BLUE_S)*ahu_sim.counter/ahu_sim.STEPS),
						    BRIGHT)));
		//g.setColor(Color.red);
		g.fillArc(RIGHT,Y,20,20,(int)(ahu_sim.OA_percent*3.6)+90,360 - (int)(ahu_sim.OA_percent*3.6));
		//g.setColor(Color.blue);
		g.fillArc(RIGHT,Y,20,20,90,(int)(ahu_sim.OA_percent*3.6));
	    }
	
	    //COIL
	    g.setColor(Color.blue);
	    for (int i=5; i<=25; i+=5) {
		g.drawLine((MIDDLE+RIGHT)/2+i,BOTTOM-4,(MIDDLE+RIGHT)/2+i,BOTTOM+24);
	    }
	    g.drawString("coil",(MIDDLE+RIGHT)/2+5,BOTTOM+35);
	
	    //HOUSE ROOF
	    g.setColor(Color.black);
	    int[] xtemp = {RIGHT-5,RIGHT+10,RIGHT+25};
	    int[] ytemp = {BOTTOM-17,BOTTOM-30,BOTTOM-17};
	    g.fillPolygon(xtemp,ytemp,3);
	}//end paint method
    }//end FlowChart class


    class PsychrometricChart extends Canvas {

	//constants
	final int LEFT_SHIFT = 150;
	final int UP_SHIFT = 200;
        
	final double MIN_TEMP = 70;
	final double MAX_TEMP = 80;
	final double MIN_HUMIDITY = 0.006;
	final double MAX_HUMIDITY = 0.013;
        
	final double SATURATION_LINE[][] = {
	    {40, 0.0052},
	    {45, 0.0063},
	    {50, 0.0076},
	    {55, 0.0092},
	    {60, 0.0112}, 
	    {65, 0.0132},
	    {70, 0.0158},
	    {75, 0.0188},
	    {80, 0.0223},
	    {85, 0.0264},
	    {90, 0.029},
	    {95,0.029}
	};
        
	//methods
        
	public void paint(Graphics g) {
	    Rectangle r = getBounds();
	    g.drawLine(r.width - 10, 0, r.width - 10, r.height - 1);
	    g.drawLine(0, r.height - 1, r.width - 10, r.height - 1);
	    //g.drawArc(-100, -100, 200, 200, 270, 90);
	    for (int i=0; i<SATURATION_LINE.length-1; i++) {
		g.drawLine(
			   Xconvert(SATURATION_LINE[i][0]),
			   Yconvert(SATURATION_LINE[i][1]),
			   Xconvert(SATURATION_LINE[i+1][0]),
			   Yconvert(SATURATION_LINE[i+1][1]));
	    }
	    g.setColor(Color.lightGray);
	    for (int i=0; i<SATURATION_LINE.length-1; i++) {
		g.drawLine(
			   Xconvert(SATURATION_LINE[i][0]),
			   Yconvert(SATURATION_LINE[i][1]),
			   Xconvert(SATURATION_LINE[i][0]),
			   r.height);
		g.drawLine(
			   Xconvert(SATURATION_LINE[i][0]),
			   Yconvert(SATURATION_LINE[i][1]),
			   r.width,
			   Yconvert(SATURATION_LINE[i][1]));                       
	    }
                
	    int X,Y;
                
	    //room air state
	    g.setColor(Color.green);
	    X = Xconvert(ahu_sim.room_T);
	    Y = Yconvert(ahu_sim.room_w);
	    g.fillOval(X - 5, Y - 5, 10, 10);
	    g.drawString("room",X+10,Y+4);
	    //g.drawString("room_T = "+ahu_sim.room_T+"  room_w = "+ahu_sim.room_w, 100,100);
                
	    //outside air state
	    g.setColor(Color.red);
	    X = Xconvert(ahu_sim.outside_T);
	    Y = Yconvert(ahu_sim.outside_w);
	    g.fillOval(X - 5,Y - 5, 10, 10);
	    g.drawString("outside",X+10,Y+4);
	    //g.drawString("outside_T = "+ahu_sim.outside_T+"  outside_w = "+ahu_sim.outside_w, 100,120);
                
	    //mixed air state
	    g.setColor(Color.orange);
	    X = Xconvert(ahu_sim.mixed_T);
	    Y = Yconvert(ahu_sim.mixed_w);
	    g.fillOval(X - 5,Y - 5, 10, 10);
	    g.drawString("mixed",X+10,Y+4);
	    //g.drawString("mixed_T = "+ahu_sim.mixed_T+"  mixed_w = "+ahu_sim.mixed_w, 100,140);
                
	    //supply air state
	    g.setColor(Color.blue);
	    X = Xconvert(ahu_sim.supply_T);
	    Y = Yconvert(ahu_sim.supply_w);
	    g.fillOval(X - 5, Y - 5, 10, 10);
	    g.drawString("supply",X+10,Y+4);
	    //g.drawString("supply_T = "+ahu_sim.supply_T+"  supply_w = "+ahu_sim.supply_w, 100,160);
                
	    //flow air state
	    g.setColor(Color.black);
	    X = Xconvert(ahu_sim.flow_T);
	    Y = Yconvert(ahu_sim.flow_w);
	    g.drawOval(X - 5, Y - 5, 10, 10);
	    g.drawString("OA",X+10,Y-6);
	    //g.drawString("flow_T = "+ahu_sim.flow_T+"  flow_w = "+ahu_sim.flow_w, 100, 180);
                
	    //recirc air state
	    g.setColor(Color.black);
	    X = Xconvert(ahu_sim.recirc_T);
	    Y = Yconvert(ahu_sim.recirc_w);         
	    g.drawOval(X - 7,Y - 7, 14, 14);
	    g.drawString("recirc",X+10,Y+14);
	    //g.drawString("recirc_T = "+ahu_sim.recirc_T+"  recirc_w = "+ahu_sim.recirc_w, 100, 200);
                
	    //arrow graphix
                
	    g.setColor(Color.black);
	    //box
	    g.drawLine(Xconvert(MIN_TEMP) - LEFT_SHIFT, Yconvert(MIN_HUMIDITY) - UP_SHIFT, Xconvert(MAX_TEMP) - LEFT_SHIFT, Yconvert(MIN_HUMIDITY) - UP_SHIFT);
	    g.drawLine(Xconvert(MIN_TEMP) - LEFT_SHIFT, Yconvert(MAX_HUMIDITY) - UP_SHIFT, Xconvert(MAX_TEMP) - LEFT_SHIFT, Yconvert(MAX_HUMIDITY) - UP_SHIFT);
	    g.drawLine(Xconvert(MIN_TEMP) - LEFT_SHIFT, Yconvert(MIN_HUMIDITY) - UP_SHIFT, Xconvert(MIN_TEMP) - LEFT_SHIFT, Yconvert(MAX_HUMIDITY) - UP_SHIFT);
	    g.drawLine(Xconvert(MAX_TEMP) - LEFT_SHIFT, Yconvert(MIN_HUMIDITY) - UP_SHIFT, Xconvert(MAX_TEMP) - LEFT_SHIFT, Yconvert(MAX_HUMIDITY) - UP_SHIFT);
                
	    g.drawLine(Xconvert(MIN_TEMP), Yconvert(MIN_HUMIDITY), Xconvert(MAX_TEMP), Yconvert(MIN_HUMIDITY));
	    g.drawLine(Xconvert(MIN_TEMP), Yconvert(MAX_HUMIDITY), Xconvert(MAX_TEMP), Yconvert(MAX_HUMIDITY));
	    g.drawLine(Xconvert(MIN_TEMP), Yconvert(MIN_HUMIDITY), Xconvert(MIN_TEMP), Yconvert(MAX_HUMIDITY));
	    g.drawLine(Xconvert(MAX_TEMP), Yconvert(MIN_HUMIDITY), Xconvert(MAX_TEMP), Yconvert(MAX_HUMIDITY));             
                
	    int tailX = Xconvert(ahu_sim.room_T);
	    int tailY = Yconvert(ahu_sim.room_w);
	    int headX = Xconvert(ahu_sim.room_T-ahu_sim.Q*Math.sin(ahu_sim.SHF*Math.PI/2)/10);
	    int headY = Yconvert(ahu_sim.room_w-ahu_sim.Q*Math.cos(ahu_sim.SHF*Math.PI/2)/(3*12000));
                
	    //load
	    g.drawString("load",headX - 8 - LEFT_SHIFT-25, headY - 8 - UP_SHIFT+3);
	    g.drawLine(tailX - LEFT_SHIFT, tailY - UP_SHIFT, headX - LEFT_SHIFT, headY - UP_SHIFT);
	    g.drawOval(headX - 8 - LEFT_SHIFT, headY - 8 - UP_SHIFT, 16, 16);               
	    //protractor
	    int r_temp=(int)Math.sqrt((tailX - headX)*(tailX - headX)+(tailY - headY)*(tailY - headY));
	    g.drawLine(tailX - LEFT_SHIFT-r_temp,
		       tailY - UP_SHIFT,
		       r_temp + tailX - LEFT_SHIFT,
		       tailY - UP_SHIFT);
	    g.drawArc(tailX - LEFT_SHIFT-r_temp,
		      tailY - UP_SHIFT-r_temp,
		      2*r_temp,
		      2*r_temp,
		      180,180);
                
	    //room
	    g.setColor(Color.green);
	    g.drawString("room",tailX - LEFT_SHIFT - 5+10,tailY - UP_SHIFT - 5+3);
	    g.fillOval(tailX - LEFT_SHIFT - 5, tailY - UP_SHIFT - 5, 10,10);
                
	    headX = Xconvert(ahu_sim.room_T-ahu_sim.Q_in*Math.sin(ahu_sim.SHF_in*Math.PI/2)/10);
	    headY = Yconvert(ahu_sim.room_w-ahu_sim.Q_in*Math.cos(ahu_sim.SHF_in*Math.PI/2)/(3*12000));
                
	    //supply
	    g.setColor(Color.blue);
	    g.drawString("supply",headX - 5 - LEFT_SHIFT+10, headY - 5 - UP_SHIFT+15);
	    g.fillOval(headX - 5 - LEFT_SHIFT, headY - 5 - UP_SHIFT, 10, 10);
	    g.drawLine(tailX - LEFT_SHIFT, tailY - UP_SHIFT, headX - LEFT_SHIFT, headY - UP_SHIFT);
                
	    //schematic
	    g.setColor(Color.black);
	    g.drawString("Cooling Vectors (BTU/Hr)",Xconvert(MIN_TEMP) - LEFT_SHIFT, Yconvert(MAX_HUMIDITY) - UP_SHIFT);
	}
        
	int Xconvert(double temperature) {
	    Rectangle r = getBounds();
	    int returnThis = (int)((temperature - 40)/60*(r.width-1));
	    returnThis = Math.max(returnThis,0);
	    returnThis = Math.min(returnThis,r.width - 1);
	    return returnThis;
	}
        
	int Yconvert(double humidity) {
	    Rectangle r = getBounds();
	    int returnThis = (int)((0.03 - humidity)*40*(r.height-1));
	    returnThis = Math.max(returnThis,0);
	    returnThis = Math.min(returnThis, r.height - 1);
	    return returnThis;
	}
    }



	
    //formats a double the RIGHT way
    public static String format(double d,int chars_after_decimal) {
	if (Math.abs(d)>1000.0)
	    return ""+(int)d;
	if (Math.abs(d-1)<0.001)
	    return ""+1.0;
	String sign;
	if (d<0.0) sign="-"; else sign="";
	d = Math.abs(d);
	int logg = (int)Math.ceil(Math.log(d)/Math.log(10));
	int mantissa = (int)(Math.pow(10,(chars_after_decimal-logg))*d);

	if (logg==0)
	    return sign+"0."+mantissa;
	else if (logg<4 && logg>0)
	    return sign+(int)d+"."+(int)((d - (int)d)*Math.pow(10,chars_after_decimal) + 0.5);
	else if (logg==-1)
	    return sign+"0.0"+mantissa;
	else
	    return sign+"0."+mantissa+"E"+logg;
    }
}
