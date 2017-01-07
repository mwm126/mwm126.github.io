import java.awt.*;

public class LineGraph extends Canvas {
    //data variables
    CircularQueue cq;
    String label;
	
    double minn, maxx;
	
    //constructor
    LineGraph(String ell, CircularQueue seekyoo, double mn, double mx) {
	label = ell;
	cq = seekyoo;
	setSize(200, 100);
	minn = mn;
	maxx = mx;
    }
    //methods
    public void paint(Graphics g) {
	Rectangle r = getBounds();
	double value = cq.getItem(0);
	g.setColor(Color.black);
		
	//draw label
	g.drawString(label,80,15);
		
	//draw border
	//g.drawLine(0,0,r.width,0);
	//g.drawLine(0,0,0,r.height);
	//g.drawLine(r.width,0,r.width,r.height);
	//g.drawLine(0,r.height,r.width,r.height);
		
	//draw axes
	g.drawLine(0, r.height - 10, r.width, r.height - 10);
	g.drawLine(10, 0, 10, r.height);

	//draw vertical notches
	g.drawLine(10, 10, 20, 10);
	g.drawLine(10, r.height/2, 20, r.height/2);
	g.setColor(getBackground());
	g.drawLine(20, r.height - 10, 50, r.height - 10);
		
	//draw horizontal notches
	g.drawLine(r.width - 70, r.height - 10, r.width, r.height - 10);
		
	g.setColor(Color.black);
		
	//draw numbers
	g.drawString(""+minn, 20, r.height - 5);
	g.drawString(""+maxx, 20, 15);
	g.drawString(""+(maxx+minn)/2, 20, r.height/2 + 5);
	g.drawString("5 sec ago", r.width - 70, r.height - 5);
		
	//draw curve
	for(int i=1; i<cq.getSize(); i++) {
	    g.drawLine(
		       (int)(r.width*(i-1)/cq.getSize()),
		       (int)((r.height - 20)*(maxx - cq.getItem(i-1))/(maxx - minn)) + 10,
		       (int)(r.width*i/cq.getSize()),
		       (int)((r.height - 20)*(maxx - cq.getItem(i))/(maxx - minn)) + 10
		       );
	}
    }
}
