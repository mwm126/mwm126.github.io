import java.awt.*;
import java.util.Vector;

public class MainCanvas extends Canvas {
	//fields
  static final int radius = 12;
	int selection = -1;
	int over = -1;
	Vector hvacomponents;

	//constructor
	MainCanvas(HVAC_Legos hvc_leg) {
    setBackground(Color.white);
		hvacomponents = hvc_leg.hvacomponents;//for convenience
	}

	//methods
	public void paint(Graphics g) {
		HVAComponent hvc;
		for (int i=0; i<hvacomponents.size(); i++) {
			hvc = (HVAComponent)hvacomponents.elementAt(i);
			hvc.paint(g);
		}
		if (over==-1) return;
		hvc = (HVAComponent)hvacomponents.elementAt(over);
		g.setColor(Color.pink);
		g.drawString("flow="+hvc.getFlow(),hvc.getLoc().x,hvc.getLoc().y);
		g.drawString("pres="+hvc.getPres(),hvc.getLoc().x,hvc.getLoc().y+10);
		g.drawString("temp="+hvc.getTemp(),hvc.getLoc().x,hvc.getLoc().y+20);
	}

	public boolean mouseDown(Event evt, int x, int y) {
		double distance;
		HVAComponent hvc;
		for (int i=0; i<hvacomponents.size(); i++) {
			hvc = (HVAComponent)hvacomponents.elementAt(i);
			distance = Math.sqrt(Math.pow(hvc.getLoc().x-x,2)+Math.pow(hvc.getLoc().y-y,2));
			if (distance<radius) {
				if (evt.metaDown()) {//meta means right click
					selection=-1;
					hvc.hvac_input = null;
					repaint();
				} else {
					selection=i;
				}
				return true;
			}
		}
		return true;
	}

	public boolean mouseUp(Event evt, int x, int y) {
		if (selection==-1) return true;

		HVAComponent selected_hvc = (HVAComponent)hvacomponents.elementAt(selection);
		double distance,distance2;
		HVAComponent hvc;

		for (int i=0; i<hvacomponents.size(); i++) {
			hvc = (HVAComponent)hvacomponents.elementAt(i);
      if (selected_hvc instanceof HX) {
        HX hx = (HX)selected_hvc;
        distance = Math.sqrt(Math.pow(hvc.getLoc().x+50-x,2)+Math.pow(hvc.getLoc().y-y-MainCanvas.radius,2));
        distance2 = Math.sqrt(Math.pow(hvc.getLoc().x+50-x,2)+Math.pow(hvc.getLoc().y-y+MainCanvas.radius,2));
        if (distance<radius/2) {
          hx.hvac_input = hvc;
          hx.setLoc(new Point(hvc.getLoc().x+50, hvc.getLoc().y));
          return true;
        }
        if (distance2<radius/2) {
          hx.hvac_input2 = hvc;
          hx.setLoc(new Point(hvc.getLoc().x+50, hvc.getLoc().y));
          return true;
        }
      } else {
			  distance = Math.sqrt(Math.pow(hvc.getLoc().x+50-x,2)+Math.pow(hvc.getLoc().y-y,2));
			  if (distance<radius) {
          if (hvc instanceof MultipleOutputs) {
            MultipleOutputs split = (MultipleOutputs)hvc;
            if (hvc.getLoc().y>y) {
              selected_hvc.setInput(split.getOne());
              selected_hvc.setLoc(new Point(hvc.getLoc().x+50, hvc.getLoc().y-radius));
            }
            else {
              selected_hvc.setInput(split.getTwo());
              selected_hvc.setLoc(new Point(hvc.getLoc().x+50, hvc.getLoc().y+radius));
            }
          } else {
            selected_hvc.setInput(hvc);
            selected_hvc.setLoc(new Point(hvc.getLoc().x+50, hvc.getLoc().y));
          }
				  return true;
			  }
		  }
    }
    return true;
	}

	public boolean mouseDrag(Event evt, int x, int y) {
		HVAComponent hvc;
		for (int i=0; i<hvacomponents.size(); i++) {
			hvc = (HVAComponent)hvacomponents.elementAt(i);
			if (selection==i) {
				hvc.setLoc(new Point(x,y));
				repaint();
				return true;//don't need to finish for loop
			}
		}
		return true;
	}

	public boolean mouseMove(Event evt, int x, int y) {
		double distance;
		HVAComponent hvc;
		for (int i=0; i<hvacomponents.size(); i++) {
			hvc = (HVAComponent)hvacomponents.elementAt(i);
			distance = Math.sqrt(Math.pow(hvc.getLoc().x-x,2)+Math.pow(hvc.getLoc().y-y,2));
			if (distance<radius) {
				over = i;
				repaint();
				return true;
			}
		}
		over=-1;
		return true;
	}

	public boolean mouseExit(Event evt, int x, int y) {
		selection = -1;
		over=-1;
		return true;
	}

}
