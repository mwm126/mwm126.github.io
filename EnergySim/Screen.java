import java.awt.*;

public abstract class Screen extends Panel implements Callback {
	Image background;
	
	Screen(Image bg) {
	    super();
	    setLayout(null);
	    //setFont(new Font("Helvetica", Font.PLAIN, 14));	
		background = bg;
	}

	abstract void switch_to_training();
	abstract void switch_to_manual();
	
	public void paint(Graphics g) {
		Dimension size = getSize();
		if (background!=null)
			g.drawImage(background,0,0,size.width,size.height,this);
		g.setColor(Color.blue);
		g.fillRect(0,0,size.width,10);
		g.fillRect(0,size.height-10,size.width,10);
		g.fillRect(0,0,10,size.height);
		g.fillRect(size.width-10,0,10,size.height);
	}
}