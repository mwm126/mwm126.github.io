import java.awt.*;

public class SliderControl extends Control {
	Point range;
	double step;
	
	Callback callback;
	
	//constructor
	SliderControl(String n, double initval, double s, Point r,Callback cb) {
		super(n,s);
		callback = cb;
		step = s;
		range = r;
		resize(CONTROL_WIDTH,CONTROL_HEIGHT);
	}

	//methods
	public boolean mouseDown(Event evt, int x, int y) {
	  int new_value = range.x + (getHeight()-y)*(range.y-range.x)/getHeight();
	  setValue(new_value);
	  callback.callback(name);
	  repaint();
	  return true;
	}
	
	public void paint(Graphics g) {
	  g.setColor(Color.black);
	  g.fillRect(0,0,4,getHeight());
	  g.fillRect(20,0,4,getHeight());
	  g.drawString(name,60,10);
	  for (int i=0; i<=getHeight(); i+=getHeight()/4) {
	    g.drawString(""+i*100/getHeight()+"%",24,10+getHeight()-i);
	  }

	  g.setColor(Color.gray);
	  int height = (int)((value-range.x)*getHeight()/(range.y-range.x));
	  g.fillRect(4,getHeight()-height,16,height);
	}
}