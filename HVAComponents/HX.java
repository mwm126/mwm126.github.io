import java.awt.*;

public class HX extends HVAComponent implements MultipleOutputs {
  HVACInput hvac_input;
  HVACInput hvac_input2;
	private subHX out_one;
	private subHX out_two;

  private double epsilon;

	HX(double ep) {
    epsilon = ep;
    out_one = new subHX(this,1,epsilon);
    out_two = new subHX(this,2,epsilon);
	}
  public HVACInput getOne() {
    return out_one;
  }
  public HVACInput getTwo() {
    return out_two;
  }
  //dummy methods; ain't gonna be used, don't do nuthin'
	public double getFlow() {
    return 0;
	}
	public double getPres() {
    return 0;
	}
	public double getTemp() {
    return 0;
	}
  public void setLoc(Point p) {
    loc = new Point(p.x,p.y);
  }
	public void paint(Graphics g) {
    int r = MainCanvas.radius;
    g.setColor(Color.black);
    if (hvac_input!=null) {
      g.drawLine(getLoc().x - r,getLoc().y - r,hvac_input.getLoc().x,hvac_input.getLoc().y);
    } else {
      g.drawLine(getLoc().x - r,getLoc().y - r,getLoc().x-50,getLoc().y - r);
    }
    if (hvac_input2!=null) {
      g.drawLine(getLoc().x - r,getLoc().y + r,hvac_input2.getLoc().x,hvac_input2.getLoc().y);
    } else {
      g.drawLine(getLoc().x - r,getLoc().y + r,getLoc().x-50,getLoc().y + r);
    }
    if (HVAC_Legos.use_images) {
      g.drawImage(HVAC_Legos.splitter_image,getLoc().x-r,getLoc().y-r,new Panel());
    } else {
      g.drawLine(getLoc().x - r,getLoc().y - r,getLoc().x + r,getLoc().y - r);
      g.drawLine(getLoc().x,getLoc().y - r,getLoc().x,getLoc().y + r);
      g.drawLine(getLoc().x - r,getLoc().y + r,getLoc().x + r,getLoc().y + r);
    }
	}
}

class subHX implements HVACInput {
  private double epsilon;
  private HX hx;
  private int number;//takes value of either one or two
  subHX(HX h_x, int n, double ep) {
    hx = h_x;
    number = n;
    epsilon = ep;
  }
	public double getFlow() {
    if (number == 1) {
      if (hx.hvac_input!=null) {
        return hx.hvac_input.getFlow();
      } else return 0;
    } else {
      if (hx.hvac_input2!=null) {
        return hx.hvac_input2.getFlow();
      } else return 0;
    }
	}
	public double getPres() {
    if (number == 1) {
      if (hx.hvac_input!=null) {
        return (2/3)*hx.hvac_input.getPres();
      } else return 0;
    } else {
      if (hx.hvac_input2!=null) {
        return (2/3)*hx.hvac_input2.getPres();
      } else return 0;
    }
	}
	public double getTemp() {
    if (hx.hvac_input!=null && hx.hvac_input2!=null) {
      if (number == 1) {
        if (hx.hvac_input.getFlow()<hx.hvac_input2.getFlow()) {
          return epsilon*Math.abs(hx.hvac_input2.getTemp()-hx.hvac_input.getTemp());
        } else {
          return (hx.hvac_input.getFlow()/hx.hvac_input2.getFlow())*epsilon*Math.abs(hx.hvac_input.getTemp()-hx.hvac_input2.getTemp());
        }
      } else {
        if (hx.hvac_input.getFlow()<hx.hvac_input2.getFlow()) {
          return (hx.hvac_input2.getFlow()/hx.hvac_input.getFlow())*epsilon*Math.abs(hx.hvac_input2.getTemp()-hx.hvac_input.getTemp());
        } else {
          return epsilon*Math.abs(hx.hvac_input.getTemp()-hx.hvac_input2.getTemp());
        }
      }
    } else return 0;
	}
  public Point getLoc() {
    int r = MainCanvas.radius;
    if (number==1) {
      return new Point(hx.getLoc().x + r, hx.getLoc().y - r);
    } else {
      return new Point(hx.getLoc().x + r, hx.getLoc().y + r);
    }
  }
}
