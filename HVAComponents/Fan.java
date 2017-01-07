import java.awt.*;

public class Fan extends HVAComponent {
	public double getFlow() {
		if (hvac_input==null) {
			return 0;
		} else {
			return hvac_input.getFlow()/2;
		}
	}
	public double getPres() {
		if (hvac_input==null) {
			return 0;
		} else {
			return hvac_input.getPres()*2;
		}
	}
	public double getTemp() {
		if (hvac_input==null) {
			return 0;
		} else {
			return hvac_input.getTemp();
		}
	}
  
  public void setLoc(Point new_loc) {
		loc = new Point(new_loc.x,new_loc.y);
	}

	public void paint(Graphics g) {
		g.setColor(Color.blue);
		if (hvac_input!=null) {
			g.drawLine(getLoc().x, getLoc().y, hvac_input.getLoc().x, hvac_input.getLoc().y);
		} else {
			g.drawLine(getLoc().x, getLoc().y, getLoc().x - 50, getLoc().y);
		}
    int r = MainCanvas.radius;
    if (HVAC_Legos.use_images) {
      g.drawImage(HVAC_Legos.fan_image,getLoc().x-r,getLoc().y-r,new Panel());
    } else {
      g.drawRect(getLoc().x - r, getLoc().y - r, 2*r, 2*r);
    }
	}
}

