import java.awt.*;

public class Source extends HVAComponent {
	private double flow;
	private double pres;
	private double temp;
	Source(double f, double p, double t) {
		flow = f;
		pres = p;
		temp = t;
	}
	public double getFlow() {
		return flow;
	}
	public double getPres() {
		return pres;
	}
	public double getTemp() {
		return temp;
	}
  public void setLoc(Point new_loc) {
		loc = new Point(new_loc.x,new_loc.y);
	}

	public void paint(Graphics g) {
    int r = MainCanvas.radius;
    if (HVAC_Legos.use_images) {
      g.drawImage(HVAC_Legos.source_image,getLoc().x-r,getLoc().y-r,new Panel());
    } else {
		  g.setColor(Color.green);
		  g.fillOval(getLoc().x - r,getLoc().y - r, 2*r, 2*r);
    }
	}
}

