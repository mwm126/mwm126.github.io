import java.awt.*;

class Graph extends Canvas {
	//data variables
	CircularQueue cq;
	String label;
	
	double minn, maxx;

	void addDatum(double d) {
		cq.addItem(d);
		repaint();
	}
	
	//constructor
	Graph(String ell, double mn, double mx) {
		System.out.println("new graph here!");
		label = ell;
		cq = new CircularQueue(100);
		resize(200, 200);
		minn = mn;
		maxx = mx;
	}
	//methods
	public void paint(Graphics g) {
		g.setColor(Color.red);
		
		//draw label
		g.drawString(label,80,15);
		
		//draw border
		g.drawLine(0,0,size().width-1,0);
		g.drawLine(0,0,0,size().height-1);
		g.drawLine(size().width-1,0,size().width-1,size().height-1);
		g.drawLine(0,size().height-1,size().width-1,size().height-1);
		
		//draw axes
		g.drawLine(0, size().height - 10, size().width, size().height - 10);
		g.drawLine(10, 0, 10, size().height);

		//draw vertical notches
		g.drawLine(10, 10, 20, 10);
		g.drawLine(10, size().height/2, 20, size().height/2);
		g.setColor(getBackground());
		g.drawLine(20, size().height - 10, 50, size().height - 10);
		
		//draw horizontal notches
		g.drawLine(size().width - 70, size().height - 10, size().width, size().height - 10);
		
		g.setColor(Color.black);
		
		//draw numbers
		g.drawString(""+minn, 20, size().height - 5);
		g.drawString(""+maxx, 20, 15);
		g.drawString(""+(maxx+minn)/2, 20, size().height/2 + 5);
		
		//draw curve
		for(int i=1; i<cq.getSize(); i++) {
			g.drawLine(
			 (int)(size().width*(i-1)/cq.getSize()),
			 (int)((size().height - 20)*(maxx - cq.getItem(i-1))/(maxx - minn)) + 10,
			 (int)(size().width*i/cq.getSize()),
			 (int)((size().height - 20)*(maxx - cq.getItem(i))/(maxx - minn)) + 10
			);
		}
	}
}

class CircularQueue {
	//data fields
	private double[] items;
	private int pointer;//points to "first" item

	//constructor
	CircularQueue(int init_size) {
		items = new double[init_size];
		pointer = init_size - 1;
	}
	//methods
	public int getSize() {
		return items.length;
	}
	
	public void addItem(double dub) {
		pointer--;
		pointer = (pointer + items.length)%items.length;
		try {
		items[pointer] = dub;
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			System.out.println("!@#!@#!@#ArrayIndexOutOfBoundsException, pointer = "+pointer+" , items.length = "+items.length);
		}
	}
	
	public double getItem(int location) {
		location = Math.max(location,0);
		location = Math.min(location,items.length-1);
		location = (location + pointer) % items.length;
		try {
			return items[location];
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			System.out.println("!@#!@#!@#ArrayIndexOutOfBoundsException, pointer = "+pointer+" , items.length = "+items.length);
		}
		return 42;
	}
}
