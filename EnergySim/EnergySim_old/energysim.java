import java.awt.*;

public class EnergySim extends java.applet.Applet implements Runnable {
	//CONTROLS
	//ButtonControl(String n, double initval, double s, Pair range)
	ButtonControl numCoolingTowerFans;
	ButtonControl numChillers;
	
	ButtonControl numChilledWaterPumps;
	ButtonControl numSupplyFans;
	
	ButtonControl chilledWaterTemperature;
	ButtonControl speed;
	Button pause;
	Button reset;
	
	//LABEL OUTPUTS
	Label time;
	Label twb;
	Label tdb;
	Label tt;
	Label need;
	Label cap;
	Label lat;
	Label ta;
	Label akw;
	Label tkw;
	
	Graph graph;
	
	Image image;// = loadImage("image.gif");
	
	ImageCanvas imageCanvas;
	
	Thread thread;
	boolean stopped;
	
	//time
	double t;
	
	EnergySimLogic logic;
	
	Panel p;
	
	public void run() {
		while(true) {
			try {
				long dt;
				thread.sleep(100);//wait 100 milliseconds
				t += 0.0051*speed.getValue();//increase t by 0.25/speed hours
				if (t>24)
					t = t - 24;
	
				int cwt = chilledWaterTemperature.getValue();
				int nc = numChillers.getValue();
				int nf = numSupplyFans.getValue();
				int nctf = numCoolingTowerFans.getValue();
				int ncwp = numChilledWaterPumps.getValue();
				
				//int day = ((int)t)/24;
				int hour = ((int)t)%24;
				int minute = (int)(60*(t/* - 24*day*/ - hour));
				int second = (int)((t/* - 24*day */- hour - minute/60.0)*3600.0);
				time.setText("time = "+hour+":"+minute+":"+second);
				twb.setText("twb = "+format(logic.twb(t),2));
				tdb.setText("tdb = "+format(logic.tdb(t),2));
				tt.setText("tt = "+format(logic.tt(t,cwt,nc,nctf),2));
				need.setText("need = "+format(logic.need(t),2));
				cap.setText("cap = "+format(logic.cap(t,cwt,nc,nctf),2));
				lat.setText("lat = "+format(logic.lat(t,cwt,nc,nctf),2));
				ta.setText("ta = "+format(logic.ta(),2));
				akw.setText("akw = "+format(logic.akw(t,cwt,nc,nctf),2));
				tkw.setText("tkw = "+format(logic.tkw(t,cwt,nc,nctf,ncwp,nf),2));
				
				graph.addDatum(logic.need(t));
				
			} catch (InterruptedException iex) {System.out.println("excuse the interruption");}
		}
	}
	
	public void init() {
		t = 0;
		stopped = false;
		logic = new EnergySimLogic();
		
		time = new Label("time = ");
		twb = new Label("twb = ");
		tdb = new Label("tdb = ");
		tt = new Label("tt = ");
		need = new Label("need = ");
		cap = new Label("cap = ");
		lat = new Label("lat = ");
		ta = new Label("ta = ");
		akw = new Label("akw = ");
		tkw = new Label("tkw = ");
		
		//ButtonControl(String n, double initval, double s, Point range)
		numCoolingTowerFans = new ButtonControl("num cooling tower fans",0,1,new Point(0,2));
		numChillers = new ButtonControl("num chillers",0,1,new Point(0,2));
	
		numChilledWaterPumps = new ButtonControl("num chilled water pumps",1,1,new Point(1,3));
		numSupplyFans = new ButtonControl("num supply fans",1,1,new Point(1,3));
	
		chilledWaterTemperature = new ButtonControl("chilled water temp",44,1,new Point(34,54));
		speed = new ButtonControl("simulation speed",5,1,new Point(1,40));
		pause = new Button("pause");
		reset = new Button("reset");
	
		graph = new Graph("Load",0,200);
		//try and load image; if we fail just use a blue border
		imageCanvas = new ImageCanvas(getImage(getCodeBase(), "image.gif"));
		//imageCanvas = new ImageCanvas();
		setBackground(Color.white);
		resize(600,450);

		Panel labels = new Panel(); labels.setLayout(new GridLayout(10,1));
		labels.add(time);
		labels.add(tdb);
		labels.add(twb);
		labels.add(tt);
		labels.add(need);
		labels.add(cap);
		labels.add(ta);
		labels.add(lat);
		labels.add(akw);
		labels.add(tkw);
		
		Panel controls = new Panel(); controls.setLayout(new GridLayout(4,2));
		controls.add(numCoolingTowerFans);
		controls.add(numChillers);
		controls.add(numChilledWaterPumps);
		controls.add(numSupplyFans);
		controls.add(chilledWaterTemperature);
		controls.add(speed);
		controls.add(pause);
		controls.add(reset);
		
		p = new Panel(); p.setLayout(new BorderLayout());
		p.add("North",labels);
		p.add("Center",graph);
		
		Panel p2 = new Panel(); p2.setLayout(new BorderLayout());
		p2.add("Center",imageCanvas);
		p2.add("South",controls);
		
		setLayout(new BorderLayout());
		add("West",p);
		add("Center",p2);
		thread = new Thread(this);
		System.out.println("initialized succuessfylly");
		thread.start();
	}
	
	public boolean action(Event evt, Object what) {
		if (evt.target==pause) {
			if (stopped) {
				stopped = false;
				thread.resume();
				pause.setLabel("pause");
			} else {
				stopped = true;
				thread.suspend();
				pause.setLabel("resume");
			}
		} else if (evt.target==reset) {
			thread.stop();
			
			t = 0;
			stopped = false;
			pause.setLabel("pause");
			logic = new EnergySimLogic();
		
			time.setText("time = ");
			twb.setText("twb = ");
			tdb.setText("tdb = ");
			tt.setText("tt = ");
			need.setText("need = ");
			cap.setText("cap = ");
			lat.setText("lat = ");
			ta.setText("ta = ");
			akw.setText("akw = ");
			tkw.setText("tkw = ");
		
			//ButtonControl(String n, double initval, double s, Point range)
			numCoolingTowerFans = new ButtonControl("num cooling tower fans",0,1,new Point(0,2));
			numChillers = new ButtonControl("num chillers",0,1,new Point(0,2));
	
			numChilledWaterPumps = new ButtonControl("num chilled water pumps",1,1,new Point(1,3));
			numSupplyFans = new ButtonControl("num supply fans",1,1,new Point(1,3));
	
			chilledWaterTemperature = new ButtonControl("chilled water temp",44,1,new Point(34,54));
			speed = new ButtonControl("simulation speed",5,1,new Point(1,40));
	
			resize(600,450);
			
			p.remove(graph);
			graph = new Graph("Load",0,200);
			p.add("Center",graph);
			p.validate();

			thread = new Thread(this);
			System.out.println("initialized succuessfylly");
			thread.start();
		}
		return true;
	}
	
	//formats a double the RIGHT way
	public static String format(double d,int chars_after_decimal) {
		if (Math.abs(d)<1.0E-100)
			return "0.0";
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

class ImageCanvas extends Canvas {
	Image image;
	ImageCanvas(Image i) {
		image = i;
	}
	
	ImageCanvas() {
		resize(200,200);
	}
	
	public void paint(Graphics g) {
		Dimension size = getSize();
		if (image!=null)
			g.drawImage(image,0,0,size.width,size.height,this);
		g.setColor(Color.blue);
		g.fillRect(0,0,size.width,10);
		g.fillRect(0,size.height-10,size.width,10);
		g.fillRect(0,0,10,size.height);
		g.fillRect(size.width-10,0,10,size.height);
	}
}
