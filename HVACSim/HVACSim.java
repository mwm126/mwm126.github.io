import java.awt.*;
import java.awt.event.*;

public class HVACSim extends java.applet.Applet {
    Room[] rooms = new Room[3];
	
    MainCanvas the_canvas = new MainCanvas();
       
    Checkbox manual_checkbox;
    Checkbox automatic_checkbox;
    {
	CheckboxGroup cbg = new CheckboxGroup();
	manual_checkbox = new Checkbox("manual mode",cbg,true);
	automatic_checkbox = new Checkbox("automatic mode",cbg,false);
    }

    Panel manauto;
    public void init() {
	manual_checkbox.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    if (manual_checkbox.getState()) {
			manauto.removeAll();
			manauto.add(the_canvas.the_fan.faster_button);
			manauto.add(the_canvas.the_fan.slower_button);
			manauto.validate();
		    }
		}
	    });
	automatic_checkbox.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    if (automatic_checkbox.getState()) {
			manauto.removeAll();
			manauto.add(the_canvas.the_fan.fan_gauges.press_button);
			manauto.add(the_canvas.the_fan.fan_gauges.depress_button);
			manauto.validate();
		    }
		}
	    });
	Panel p = new Panel();
	p.setLayout(new GridLayout(1,rooms.length+1));
		
	Panel radio_buttons = new Panel();
	radio_buttons.setLayout(new GridLayout(2,1));
	radio_buttons.add(manual_checkbox);
	radio_buttons.add(automatic_checkbox);
	//p.add(radio_buttons);
		
	manauto = new Panel();
	manauto.setLayout(new GridLayout(2,1));
	manauto.add(the_canvas.the_fan.faster_button);
	manauto.add(the_canvas.the_fan.slower_button);
	p.add(manauto);

	for (int i=0; i<rooms.length; i++) {
	    rooms[i] = new Room(i,new Point(150*(2+i),the_canvas.the_fan.rpm_gauge.height));
	    Panel pan = new Panel();
	    pan.setLayout(new GridLayout(2,1));
	    pan.add(rooms[i].open_button);
	    pan.add(rooms[i].close_button);
	    p.add(pan);
	}
		
	setLayout(new BorderLayout());
	add("Center",the_canvas);
	add("South",p);
    }
	
    public void repaint() {
	the_canvas.repaint();
    }
	
    class Room {
	class Valve {
	    int fraction_open = 100;//between zero and one hundred
	
	    Point location;
	
	    int height = 100;
	    int width = 40;
	
	    public Valve(Point loc) {
		location = loc;
	    }
	
	    public void paint(Graphics g) {
		g.setColor(Color.blue);
		g.drawLine(location.x,location.y,location.x,location.y+height);
		g.drawLine(location.x+width,location.y,location.x+width,location.y+height);
		double angle = fraction_open*Math.PI/200;
		g.drawLine(location.x+width/2+(int)(0.5*width*Math.cos(angle)),
			   location.y+height/2+(int)(0.5*width*Math.sin(angle)), 
			   location.x+width/2-(int)(0.5*width*Math.cos(angle)),
			   location.y+height/2-(int)(0.5*width*Math.sin(angle)));
		g.drawString(""+fraction_open + "%",location.x+height/2,location.y+height/2);
	    }

	    public void moveTo(int x, int y) {
		location.x = x;
		location.y = y;
	    }
	}

	public int index;
	Button open_button = new Button("open valve");
	Button close_button = new Button("close valve");	
	
	FlowGauge flow_gauge;
	Valve valve;

	public Room(int i, Point loc) {
	    index = i;
	    valve = new Valve(new Point(loc.x, loc.y));
	    flow_gauge = new FlowGauge(new Point(loc.x,loc.y+100));
	    open_button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			if (valve.fraction_open<100)
			    valve.fraction_open += 10;
			update_everything();
		    }
		});
	    close_button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			if (valve.fraction_open>0)
			    valve.fraction_open -= 10;
			update_everything();
		    }
		});
	}

	public void update_everything() {		
	    flow_gauge.flow = (the_canvas.the_fan.rpm_gauge.RPM/the_canvas.the_fan.rpm_gauge.MAX_RPM)*
		Math.sqrt(the_canvas.the_fan.fan_gauges.pressure_gauge.pressure/2)*valve.fraction_open;
	    the_canvas.the_fan.updateAirFlow();
	    the_canvas.the_fan.fan_gauges.pressure_gauge.pressure = (7 - the_canvas.the_fan.fan_gauges.flow_gauge.flow/60);
	    for (int i=0; i<rooms.length; i++) {
		rooms[i].flow_gauge.flow = (
					    the_canvas.the_fan.rpm_gauge.RPM/the_canvas.the_fan.rpm_gauge.MAX_RPM*
					    Math.sqrt(the_canvas.the_fan.fan_gauges.pressure_gauge.pressure/2)*
					    rooms[i].valve.fraction_open);
	    }
	    repaint();
	}
	public void paint(Graphics g) {
	    flow_gauge.paint(g);
	    valve.paint(g);
	    g.setColor(Color.black);
	    g.setColor(Color.magenta);
	    g.fillOval(flow_gauge.location.x-5,flow_gauge.location.y-5,10,10);		
	    g.drawLine(flow_gauge.location.x,flow_gauge.location.y,
		       valve.location.x + valve.width/2,valve.location.y + valve.height/2);
	}
	
	public void moveTo(int x, int y) {
	    flow_gauge.moveTo(x,y+100);
	    valve.moveTo(x,y);
	}
    }

    class Fan {
	class PressureGauge {
	    double pressure = 2;//in inches of H2O
	    double MAX_PRESSURE = 8;
	    double MIN_PRESSURE = 2;
	    final int GAUGE_RADIUS = 25;
	
	    Point location = new Point(100,100);
	
	    public PressureGauge(Point loc) {
		location = loc;
	    }

	    public void paint(Graphics g) {
		g.setColor(Color.black);
		g.fillOval(location.x,location.y,2*GAUGE_RADIUS,2*GAUGE_RADIUS);
		int angle = (int)(480*(pressure-MIN_PRESSURE)/(MAX_PRESSURE-MIN_PRESSURE));
		angle = Math.min(angle,240);
		angle = Math.max(angle,0);
		g.setColor(Color.red);
		fillArc(g,location.x,location.y,2*GAUGE_RADIUS,210-angle,210);
		g.setColor(Color.red);
		g.drawString("Pressure",location.x,location.y+GAUGE_RADIUS);
		g.drawString(""+HVACSim.format(pressure,2),location.x+GAUGE_RADIUS/2,location.y+GAUGE_RADIUS*3/2);
	    }

	    private void fillArc(Graphics g, int x, int y, int r, int a_one, int a_two) {
		if (a_one <= 180) {
		    if (a_two <= 180) {
			if (a_one <= a_two) {
			    g.fillArc(x,y,r,r,a_one,a_two-a_one);
			} else {
			    g.fillArc(x,y,r,r,a_one,180-a_one);
			    g.fillArc(x,y,r,r,0,a_two);
			    g.fillArc(x,y,r,r,180,180);
			}
		    } else {
			g.fillArc(x,y,r,r,a_one,a_two-a_one);
		    }
		} else {
		    if (a_two <=180) {
			g.fillArc(x,y,r,r,0,a_two);
			g.fillArc(x,y,r,r,a_one,360-a_one);
		    } else {
			if (a_one <= a_two) {
			    g.fillArc(x,y,r,r,a_one,a_two-a_one);
			} else {
			    g.fillArc(x,y,r,r,0,180);
			    g.fillArc(x,y,r,r,a_two,360 - a_two);
			    g.fillArc(x,y,r,r,180,a_one - 180);
			}
		    }
		}
	    }
	
	    public void moveTo(int x, int y) {
		location.x = x;
		location.y = y;
	    }
	}

	class RPMGauge {
	    int height = 100;
	    int width = 100;
	
	    double RPM = 1000;//
	    double MAX_RPM = 1000;
	    final int GAUGE_RADIUS = 50;
	
	    Point location = new Point(100,100);
	
	    public RPMGauge(Point loc) {
		location = loc;
	    }
	
	    public void paint(Graphics g) {
		g.setColor(Color.black);
		g.fillOval(location.x,location.y,2*GAUGE_RADIUS,2*GAUGE_RADIUS);
		int angle = (int)(360*RPM/MAX_RPM);
		g.setColor(Color.lightGray);
		g.fillArc(location.x,location.y,2*GAUGE_RADIUS,2*GAUGE_RADIUS,360-angle,angle);
		g.setColor(Color.red);
		g.drawString("Fan RPM",location.x+GAUGE_RADIUS/2,location.y+GAUGE_RADIUS/2);
		g.drawString(""+HVACSim.format(RPM,2),location.x+GAUGE_RADIUS/2,location.y+GAUGE_RADIUS*3/2);
	    }
	
	    public void moveTo(int x, int y) {
		location.x = x;
		location.y = y;
	    }
	}

	class FanGauges {
	    PressureGauge pressure_gauge;
	    FlowGauge flow_gauge;
	
	    Point location;
	
	    Button press_button = new Button("increase pressure");
	    Button depress_button = new Button("decrease pressure");

	    public FanGauges(Point loc) {
		/*		press_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    if (valve.fraction_open<100)
				valve.fraction_open += 10;
			    update_everything();
			}
		    });
		depress_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    if (valve.fraction_open>0)
				valve.fraction_open -= 10;
			    update_everything();
			}
		    });
		*/
		location = loc;
		pressure_gauge = new PressureGauge(new Point(location.x,location.y));
		flow_gauge = new FlowGauge(new Point(location.x,location.y+100));
	    }

	    public void paint(Graphics g) {
		pressure_gauge.paint(g);
		flow_gauge.paint(g);
	    }
	
	    public void updateAirFlow() {
		double new_total_flow = 0;
		for (int i=0; i<rooms.length; i++) {
		    new_total_flow = new_total_flow + rooms[i].flow_gauge.flow;
		}
		flow_gauge.flow = (new_total_flow);
	    }
	
	    public void update_everything() {
		//pressure_gauge.pressure = (7*Math.pow(rpm_gauge.RPM/rpm_gauge.MAX_RPM,2) - flow_gauge.flow/60);
		for (int i=0; i<rooms.length; i++) {
		    /*rooms[i].flow_gauge.flow = (
		      (rpm_gauge.RPM/rpm_gauge.MAX_RPM)*
		      Math.sqrt(Math.abs(pressure_gauge.pressure/2))*
		      rooms[i].valve.fraction_open);*/
		}//end for loop
		updateAirFlow();
	    }
	
	    public void moveTo(int x, int y) {
		location.x = x;
		location.y = y;
		pressure_gauge.moveTo(x,y);
		flow_gauge.moveTo(x,y+100);
		//rpm_gauge.moveTo(x+50,y+50);
	    }
	}

	FanGauges fan_gauges;

	Point location;

	RPMGauge rpm_gauge;

	Button faster_button = new Button("increase RPM");
	Button slower_button = new Button("decrease RPM");

	public Fan(Point loc) {
	    location = loc;
	    fan_gauges = new FanGauges(new Point(loc.x, loc.y + 100));
	    rpm_gauge = new RPMGauge(new Point(location.x,location.y));
	    faster_button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			if (rpm_gauge.RPM<1000)
			    rpm_gauge.RPM += 100;
			update_everything();
		    }});
	    slower_button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			if (rpm_gauge.RPM>0)
			    rpm_gauge.RPM -= 100;
			update_everything();
		    }});
	}

	public void paint(Graphics g) {
	    rpm_gauge.paint(g);
	    fan_gauges.paint(g);
	    g.setColor(Color.magenta);
	    g.fillOval(fan_gauges.location.x-5,fan_gauges.location.y-5,10,10);
	    g.drawLine(fan_gauges.location.x,fan_gauges.location.y,location.x + rpm_gauge.width/2,location.y + rpm_gauge.height/2);
	}

	public void updateAirFlow() {
	    double new_total_flow = 0;
	    for (int i=0; i<rooms.length; i++) {
		new_total_flow = new_total_flow + rooms[i].flow_gauge.flow;
	    }
	    fan_gauges.flow_gauge.flow = (new_total_flow);
	}

	public void update_everything() {
	    fan_gauges.pressure_gauge.pressure = (7*Math.pow(rpm_gauge.RPM/rpm_gauge.MAX_RPM,2) - fan_gauges.flow_gauge.flow/60);
	    for (int i=0; i<rooms.length; i++) {
		rooms[i].flow_gauge.flow = (
					    (rpm_gauge.RPM/rpm_gauge.MAX_RPM)*
					    Math.sqrt(Math.abs(fan_gauges.pressure_gauge.pressure/2))*
					    rooms[i].valve.fraction_open);
	    }
	    updateAirFlow();
	    repaint();
	}
    }

    class MainCanvas extends Canvas {
	Fan the_fan;
	String selection;
	
	MainCanvas() {		setBackground(Color.white);
	    the_fan = new Fan(new Point(10,10));
	    addMouseListener(new MouseAdapter() {
		    public void mouseReleased(MouseEvent e) {
			selection = "none";
		    }
		    public void mouseExited(MouseEvent e) {
			selection = "none";
		    }
		    public void mousePressed(MouseEvent e) {
			double fangauges_distance = Math.sqrt(Math.pow(the_fan.fan_gauges.location.x-e.getX(),2)+Math.pow(the_fan.fan_gauges.location.y-e.getY(),2));
			if (fangauges_distance<10) {
			    selection="fan gauges";
			    return;
			}
			double room_distance;
			for (int i=0; i<rooms.length; i++) {
			    room_distance = Math.sqrt(Math.pow(rooms[i].flow_gauge.location.x-e.getX(),2)+Math.pow(rooms[i].flow_gauge.location.y-e.getY(),2));
			    if (room_distance<10) {
				selection="flow gauge"+i;
				return;
			    }
			}
		    }
		});

	    addMouseMotionListener(new MouseMotionAdapter() {
		    public void mouseDragged(MouseEvent e) {
			if (selection=="fan gauges") {
			    the_fan.fan_gauges.moveTo(e.getX(),e.getY());
			    MainCanvas.this.repaint();
			}
			for (int i=0; i< rooms.length; i++) {
			    if (selection.equals("flow gauge"+i)) {
				rooms[i].flow_gauge.moveTo(e.getX(),e.getY());
				MainCanvas.this.repaint();
			    }
			}
		    }
		});
	}

	public void paint(Graphics g) {
	    int x0 = rooms[0].valve.location.x;
	    int x1 = rooms[1].valve.location.x;
	    int x2 = rooms[2].valve.location.x;
	    int y0 = rooms[0].valve.location.y;
	    int y1 = rooms[1].valve.location.y;
	    int y2 = rooms[2].valve.location.y;
	    int w0 = rooms[0].valve.width;
	    int w1 = rooms[1].valve.width;
	    int w2 = rooms[2].valve.width;
		
	    int fx = the_canvas.the_fan.location.x;
	    int fy = the_canvas.the_fan.location.y;
	    //int fw = the_fan.rpm_gauge.width;
	    int fh = the_canvas.the_fan.rpm_gauge.height;

	    int MH = 3*fh/5;//mainheight
		
	    int top = fy + fh/5;
	    int bot = fy + 4*fh/5;
		
	    //vertical
	    g.drawLine(x0, bot, x0, y0);
	    g.drawLine(x1, bot, x1, y1);
	    g.drawLine(x2, bot, x2, y2);
		
	    //more vertical
	    g.drawLine(x0 + w0, bot, x0 + w0, y0);
	    g.drawLine(x1 + w1, bot, x1 + w0, y1);
	    g.drawLine(x2 + w2, top, x2 + w0, y2);
		
	    //horizontal
	    g.drawLine(x0 - 50, bot, x0, bot);
	    g.drawLine(x0 + w0, bot, x1, bot);
	    g.drawLine(x1 + w1, bot, x2, bot);
		
	    //top right
	    g.drawLine(x2 + w2, top, x0 - 50, top);
		
	    //middle
	    g.drawLine(x0 - 50, top, x0 - 50 - (bot - top)/3, 2*top/3 + bot/3);		
	    g.drawLine(x0 - 50, bot, x0 - 50 - (bot - top)/3, 2*bot/3 + top/3);
	    g.drawLine(x0 - 50 - (bot - top)/3, 2*top/3 + bot/3, fx + 3*fh/2 + (bot - top)/3, 2*top/3 + bot/3);
	    g.drawLine(x0 - 50 - (bot - top)/3, 2*bot/3 + top/3, fx + 3*fh/2 + (bot - top)/3, 2*bot/3 + top/3);
	    g.drawLine(fx + 3*fh/2, top, fx + 3*fh/2 + (bot - top)/3, 2*top/3 + bot/3);
	    g.drawLine(fx + 3*fh/2, bot, fx + 3*fh/2 + (bot - top)/3, 2*bot/3 + top/3);
		
	    //around the fan
	    g.drawLine(fx, top, fx + 3*fh/2, top);
	    g.drawLine(fx, bot, fx + 3*fh/2, bot);
		
	    the_canvas.the_fan.paint(g);
	    for (int i=0; i<rooms.length; i++) {
		rooms[i].paint(g);
	    }
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



    class FlowGauge {
	double flow = 100;//in cubic feet per minute
	
	int MAX_FLOW = 100;
	
	final int GAUGE_RADIUS = 25;
	
	public Point location;
	
	public FlowGauge(Point loc) {
	    location = loc;
	}
	
	public void paint(Graphics g) {
	    g.setColor(Color.black);
	    g.fillOval(location.x,location.y,2*GAUGE_RADIUS,2*GAUGE_RADIUS);
	    int angle = (int)(360*flow/MAX_FLOW);
	    g.setColor(Color.green);
	    g.fillArc(location.x,location.y,2*GAUGE_RADIUS,2*GAUGE_RADIUS,360-angle,angle);
	    g.setColor(Color.red);
	    g.drawString("Flow",location.x+GAUGE_RADIUS/2,location.y+GAUGE_RADIUS/2);
	    g.drawString(""+HVACSim.format(flow,2),location.x+GAUGE_RADIUS/2,location.y+GAUGE_RADIUS*3/2);
	}
	public void moveTo(int x, int y) {
	    location.x = x;
	    location.y = y;
	}
    }
}
