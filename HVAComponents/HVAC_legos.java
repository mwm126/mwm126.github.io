import java.awt.*;
import java.util.Vector;
import java.net.URL;

public class HVAC_Legos extends java.applet.Applet {
public static void main(String[] arg) {
	Frame f = new Frame();
	f.add(new HVAC_Legos());
	f.show();
}
//fields
static final boolean use_images = false;
static Image fan_image;
static Image heater_image;
static Image source_image;
static Image splitter_image ;
Vector hvacomponents = new Vector();

MainCanvas main_canvas = new MainCanvas(this);
Button newFan = new Button("new Fan");
Button newHeater = new Button("new Heater");
Button newSource = new Button("new Source");
Button newSplitter = new Button("new Splitter");
Button newLoad = new Button("new Load");
Button newHX = new Button("new HX");
ThreadCanvas newThreadCanvas = new ThreadCanvas();

//methods
	public void init() {
		resize(600,400);
    setBackground(Color.white);
    fan_image = getImage(getCodeBase(),"Fan.gif");
    heater_image = getImage(getCodeBase(),"Heater.gif");
    source_image = getImage(getCodeBase(),"Source.gif");
    splitter_image = getImage(getCodeBase(),"Splitter.gif");
		//construct panel controls
		Panel p = new Panel();
		p.setLayout(new GridLayout(10,1));
		p.add(newFan);
		p.add(newHeater);
		p.add(newSource);
    p.add(newSplitter);
    p.add(newLoad);
    p.add(newHX);
    Panel p2 = new Panel();
    p2.setLayout(new BorderLayout());
    p2.add("North",p);
    p2.add("Center",newThreadCanvas);
		//construct main canvas
		setLayout(new BorderLayout());
		add("Center",main_canvas);
		add("East",p2);
	}

	public boolean action(Event evt, Object what) {
		if (evt.target==newFan) {
			Fan fan = new Fan();
			hvacomponents.addElement(fan);
		} else if (evt.target==newHeater) {
			Heater heater = new Heater();
			hvacomponents.addElement(heater);
		} else if (evt.target==newSource) {
			Source source = new Source(100, 100000, 300);
			hvacomponents.addElement(source);
		} else if (evt.target==newSplitter) {
      Splitter splitter = new Splitter(0.5);
      hvacomponents.addElement(splitter);
    } else if (evt.target==newLoad) {
      Load load = new Load();
      hvacomponents.addElement(load);
    } else if (evt.target==newHX) {
      HX hx = new HX(0.7);
      hvacomponents.addElement(hx);
    }
    main_canvas.repaint();
		return true;
	}

}//end class HVAC_Legos

class ThreadCanvas extends Canvas implements Runnable {
  Thread t;

  ThreadCanvas() {
    setBackground(Color.white);
    t = new Thread(this);
    t.start();
  }

  private int theta=0;//used in run method
  private double r(double theta) {
    return (20+10*Math.sin(2*3.141592*theta/100)+5*Math.sin(4*3.141592*theta/100));
  }
  public void run() {
    while (true) {
      try {
      t.sleep(100);
      theta++;
      theta %= 100;
      if (getGraphics()!=null)
        paint(getGraphics());
      } catch (InterruptedException ix) {}
    }
  }
  public void paint(Graphics g) {
    g.setColor(Color.white);
    g.drawLine(30,
      30,
      30+(int)(r(theta-1)*Math.cos(2*3.141592*(theta-1)/100)),
      30+(int)(r(theta-1)*Math.sin(2*3.141592*(theta-1)/100)));
    g.setColor(Color.red);
    g.drawLine(
      30+(int)(r(theta-1)*Math.cos(2*3.141592*(theta-1)/100)),
      30+(int)(r(theta-1)*Math.sin(2*3.141592*(theta-1)/100)),
      30+(int)(r(theta)*Math.cos(2*3.141592*theta/100)),
      30+(int)(r(theta)*Math.sin(2*3.141592*theta/100)));
    g.drawLine(30,
      30,
      30+(int)(r(theta)*Math.cos(2*3.141592*theta/100)),
      30+(int)(r(theta)*Math.sin(2*3.141592*theta/100)));
  }
}
