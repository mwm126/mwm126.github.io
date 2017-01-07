import java.awt.*;

public class FlowControl extends Control {
	Point range;
	double step;
	
	Callback callback;
	
	//constructor
	FlowControl(String n, double initval, double s, Point r,Callback cb) {
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
	  g.setColor(Color.orange);
	  int height = (int)(value*(getHeight()/2)/(range.y-range.x));
	  if (height>0) {
	    g.fillRect(8,getHeight()/2 - height,4,height);
		g.drawLine(0,getHeight()/2 - height+10,10,getHeight()/2 - height);
		g.drawLine(20,getHeight()/2 - height+10,10,getHeight()/2 - height);
	  } else {
	    height = -height;
	    g.fillRect(8,getHeight()/2,4,height);
		g.drawLine(0,getHeight()/2 + height-10,10,getHeight()/2 + height);
		g.drawLine(20,getHeight()/2 + height-10,10,getHeight()/2 + height);
	  }
	  g.setColor(Color.black);
	  g.drawString(name+"="+value,15,10);
	}
}