import java.awt.*;

public class Splitter extends HVAComponent implements MultipleOutputs {
	private subSplitter in_one;
	private subSplitter in_two;
  private double fraction;
	Splitter(double f) {
    fraction = f;
    in_one = new subSplitter(this,fraction);
    in_two = new subSplitter(this,1-fraction);
	}
  public HVACInput getOne() {
    return in_one;
  }
  public HVACInput getTwo() {
    return in_two;
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
    int r = MainCanvas.radius;
    in_one.setLoc(new Point(p.x+r,p.y-r));
    in_two.setLoc(new Point(p.x+r,p.y+r));
  }
	public void paint(Graphics g) {
    g.setColor(Color.black);
    if (hvac_input!=null) {
      g.drawLine(getLoc().x,getLoc().y,hvac_input.getLoc().x,hvac_input.getLoc().y);
    } else {
      g.drawLine(getLoc().x,getLoc().y,getLoc().x-50,getLoc().y);
    }
    int r = MainCanvas.radius;
    if (HVAC_Legos.use_images) {
      g.drawImage(HVAC_Legos.splitter_image,getLoc().x-r,getLoc().y-r,new Panel());
    } else {
      g.drawLine(getLoc().x - r,getLoc().y - r,getLoc().x + r,getLoc().y - r);
      g.drawLine(getLoc().x - r,getLoc().y - r,getLoc().x - r,getLoc().y + r);
      g.drawLine(getLoc().x - r,getLoc().y + r,getLoc().x + r,getLoc().y + r);
    }
	}
}

class subSplitter implements HVACInput {
  //fields
  private double fraction;
  private Splitter splitter;
  private Point loc;
  //constructor
  subSplitter(Splitter spl, double f) {
    splitter = spl;
    fraction = f;
  }
  //methods
  void setLoc(Point p) {
    loc = new Point(p.x,p.y);
  }
  public Point getLoc() {
    return new Point(loc.x,loc.y);
  }
	public double getFlow() {
    if (splitter.getInput()!=null)
      return fraction*splitter.getInput().getFlow();
    else
      return 0;
	}
	public double getPres() {
    if (splitter.getInput()!=null)
      return splitter.getInput().getPres();
    else
      return 0;
	}
	public double getTemp() {
    if (splitter.getInput()!=null)
      return splitter.getInput().getTemp();
    else
      return 0;
	}
}

interface MultipleOutputs {
  public HVACInput getOne();
  public HVACInput getTwo();
}