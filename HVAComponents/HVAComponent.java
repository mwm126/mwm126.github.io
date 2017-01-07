import java.awt.*;

public abstract class HVAComponent implements HVACInput {
	//fields
	protected HVACInput hvac_input;
	//private HVAComponent hvacomponent2;//not implemented yet

	protected Point loc;//location
	//constructor
	HVAComponent() {
		loc = new Point(10,10);
	}
	//methods

	public Point getLoc() {
		return new Point(loc.x,loc.y);
	}

	public abstract void setLoc(Point new_loc);

	public void setInput(HVACInput hvc) {
		hvac_input = hvc;
	}
  public HVACInput getInput() {
    return hvac_input;
  }
	public abstract void paint(Graphics g);
}

interface HVACInput {
	public double getFlow();
	public double getPres();
	public double getTemp();
  public Point getLoc();
}
