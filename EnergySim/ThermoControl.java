import java.awt.*;

public class ThermoControl extends Control {
	Point range;
	double step;
	
	Callback callback;
	
	//constructor
	ThermoControl(String n, double initval, double s, Point r,Callback cb) {
		super(n,s);
		callback = cb;
		step = s;
		range = r;
		resize(CONTROL_WIDTH,CONTROL_HEIGHT);
	}

  public void setValue(double d) {
    value = d;
	repaint();
  }	

	public void paint(Graphics g) {
	  g.setColor(Color.black);
	  g.fillRect(0,0,10,getHeight());
	  
	  g.setColor(Color.white);
	  g.fillRect(2,4,6,getHeight()-8);
	  
	  g.setColor(Color.red);
	  g.drawString(name+"="+value,15,10);
	  int height = (int)((value-range.x)*(getHeight()-8)*1.0/(range.y-range.x));
	  g.fillRect(2,getHeight()-height-4,6,height-8);
	}
}