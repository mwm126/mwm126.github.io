import java.awt.*;

public class FanScreen extends Screen {
  FanScreen(Image bg) {
    super(bg);
  }

  public void callback(String name) {
    if (name.equals("CT")) {
	  //respond to new CT value
	} else if (name.equals("pump")) {
	  //respond to new pump value
	}
  }
  
  	void switch_to_training() {
	}
	void switch_to_manual() {
	}  
  
  public void paint(Graphics g) {
    super.paint(g);
    g.drawString("Fan",100,100);
  }
}