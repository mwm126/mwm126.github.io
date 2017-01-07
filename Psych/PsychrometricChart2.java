import java.awt.*;
//import java.awt.image.*;

class PsychrometricChart extends Canvas {

	//constants
	final int LEFT_SHIFT = 150;
	final int UP_SHIFT = 200;
	
	final double MIN_TEMP = 70;
	final double MAX_TEMP = 80;
	final double MIN_HUMIDITY = 0.006;
	final double MAX_HUMIDITY = 0.013;
	
	final double SATURATION_LINE[][] = {
		{40, 0.0052},
		{45, 0.0063},
		{50, 0.0076},
		{55, 0.0092},
		{60, 0.0112}, 
		{65, 0.0132},
		{70, 0.0158},
		{75, 0.0188},
		{80, 0.0223},
		{85, 0.0264},
		{90, 0.029},
		{95,0.029}
	};

	//fields
	AHU_Simulation ahu_sim;
	
	Image buffer = createImage(400, 400);
	
	//constructor
	PsychrometricChart(AHU_Simulation as) {
		ahu_sim = as;
		resize(400,400);
		//MemoryImageSource source = new MemoryImageSource(400, 400, new int[160000], 0, 400);
		buffer = createImage(200, 200);
		System.out.println("WHAT THE FUCK!?!?!?!   buffer = "+ buffer);
	}
	
	//methods
	
	public void paint(Graphics g) {
		if (buffer!=null)
		System.out.println("drawImage returns : "+g.drawImage(buffer,0,0,this));
	}
	
	public void repaint() {
		paint();
	}
	
	public void paint() {
		if (buffer==null) {System.out.println("buffer = null");return;}
		Graphics g = buffer.getGraphics();
		if (g==null) {System.out.println("g = null");return;}
		g.drawLine(size().width - 10, 0, size().width - 10, size().height - 1);
		g.drawLine(0, size().height - 1, size().width - 10, size().height - 1);
		//g.drawArc(-100, -100, 200, 200, 270, 90);
		for (int i=0; i<SATURATION_LINE.length-1; i++) {
			g.drawLine(
				Xconvert(SATURATION_LINE[i][0]),
				Yconvert(SATURATION_LINE[i][1]),
				Xconvert(SATURATION_LINE[i+1][0]),
				Yconvert(SATURATION_LINE[i+1][1]));
		}
		g.setColor(Color.lightGray);
		for (int i=0; i<SATURATION_LINE.length-1; i++) {
			g.drawLine(
				Xconvert(SATURATION_LINE[i][0]),
				Yconvert(SATURATION_LINE[i][1]),
				Xconvert(SATURATION_LINE[i][0]),
				size().height);				
			g.drawLine(
				Xconvert(SATURATION_LINE[i][0]),
				Yconvert(SATURATION_LINE[i][1]),
				size().width,
				Yconvert(SATURATION_LINE[i][1]));			
		}
		
		int X,Y;
		
		//room air state
		g.setColor(Color.green);
		X = Xconvert(ahu_sim.room_T);
		Y = Yconvert(ahu_sim.room_w);
		g.fillOval(X - 5, Y - 5, 10, 10);
		g.drawString("room",X+10,Y+4);
		//g.drawString("room_T = "+ahu_sim.room_T+"  room_w = "+ahu_sim.room_w, 100,100);
		
		//outside air state
		g.setColor(Color.red);
		X = Xconvert(ahu_sim.outside_T);
		Y = Yconvert(ahu_sim.outside_w);
		g.fillOval(X - 5,Y - 5, 10, 10);
		g.drawString("outside",X+10,Y+4);
		//g.drawString("outside_T = "+ahu_sim.outside_T+"  outside_w = "+ahu_sim.outside_w, 100,120);
		
		//mixed air state
		g.setColor(Color.orange);
		X = Xconvert(ahu_sim.mixed_T);
		Y = Yconvert(ahu_sim.mixed_w);
		g.fillOval(X - 5,Y - 5, 10, 10);
		g.drawString("mixed",X+10,Y+4);
		//g.drawString("mixed_T = "+ahu_sim.mixed_T+"  mixed_w = "+ahu_sim.mixed_w, 100,140);
		
		//supply air state
		g.setColor(Color.blue);
		X = Xconvert(ahu_sim.supply_T);
		Y = Yconvert(ahu_sim.supply_w);
		g.fillOval(X - 5, Y - 5, 10, 10);
		g.drawString("supply",X+10,Y+4);
		//g.drawString("supply_T = "+ahu_sim.supply_T+"  supply_w = "+ahu_sim.supply_w, 100,160);
		
		//flow air state
		g.setColor(Color.black);
		X = Xconvert(ahu_sim.flow_T);
		Y = Yconvert(ahu_sim.flow_w);
		g.drawOval(X - 5, Y - 5, 10, 10);
		g.drawString("OA",X+10,Y-6);
		//g.drawString("flow_T = "+ahu_sim.flow_T+"  flow_w = "+ahu_sim.flow_w, 100, 180);
		
		//recirc air state
		g.setColor(Color.black);
		X = Xconvert(ahu_sim.recirc_T);
		Y = Yconvert(ahu_sim.recirc_w);		
		g.drawOval(X - 7,Y - 7, 14, 14);
		g.drawString("recirc",X+10,Y+14);
		//g.drawString("recirc_T = "+ahu_sim.recirc_T+"  recirc_w = "+ahu_sim.recirc_w, 100, 200);
		
		//arrow graphix
		
		g.setColor(Color.black);
		//box
		g.drawLine(Xconvert(MIN_TEMP) - LEFT_SHIFT, Yconvert(MIN_HUMIDITY) - UP_SHIFT, Xconvert(MAX_TEMP) - LEFT_SHIFT, Yconvert(MIN_HUMIDITY) - UP_SHIFT);
		g.drawLine(Xconvert(MIN_TEMP) - LEFT_SHIFT, Yconvert(MAX_HUMIDITY) - UP_SHIFT, Xconvert(MAX_TEMP) - LEFT_SHIFT, Yconvert(MAX_HUMIDITY) - UP_SHIFT);
		g.drawLine(Xconvert(MIN_TEMP) - LEFT_SHIFT, Yconvert(MIN_HUMIDITY) - UP_SHIFT, Xconvert(MIN_TEMP) - LEFT_SHIFT, Yconvert(MAX_HUMIDITY) - UP_SHIFT);
		g.drawLine(Xconvert(MAX_TEMP) - LEFT_SHIFT, Yconvert(MIN_HUMIDITY) - UP_SHIFT, Xconvert(MAX_TEMP) - LEFT_SHIFT, Yconvert(MAX_HUMIDITY) - UP_SHIFT);
		
		g.drawLine(Xconvert(MIN_TEMP), Yconvert(MIN_HUMIDITY), Xconvert(MAX_TEMP), Yconvert(MIN_HUMIDITY));
		g.drawLine(Xconvert(MIN_TEMP), Yconvert(MAX_HUMIDITY), Xconvert(MAX_TEMP), Yconvert(MAX_HUMIDITY));
		g.drawLine(Xconvert(MIN_TEMP), Yconvert(MIN_HUMIDITY), Xconvert(MIN_TEMP), Yconvert(MAX_HUMIDITY));
		g.drawLine(Xconvert(MAX_TEMP), Yconvert(MIN_HUMIDITY), Xconvert(MAX_TEMP), Yconvert(MAX_HUMIDITY));		
		
		int tailX = Xconvert(ahu_sim.room_T);
		int tailY = Yconvert(ahu_sim.room_w);
		int headX = Xconvert(ahu_sim.room_T-ahu_sim.Q*Math.sin(ahu_sim.SHF*Math.PI/2)/10);
		int headY = Yconvert(ahu_sim.room_w-ahu_sim.Q*Math.cos(ahu_sim.SHF*Math.PI/2)/(3*12000));
		
		//load
		g.drawString("load",headX - 8 - LEFT_SHIFT-25, headY - 8 - UP_SHIFT+3);
		g.drawLine(tailX - LEFT_SHIFT, tailY - UP_SHIFT, headX - LEFT_SHIFT, headY - UP_SHIFT);
		g.drawOval(headX - 8 - LEFT_SHIFT, headY - 8 - UP_SHIFT, 16, 16);		
		//protractor
		int r_temp=(int)Math.sqrt((tailX - headX)*(tailX - headX)+(tailY - headY)*(tailY - headY));
		g.drawLine(tailX - LEFT_SHIFT-r_temp,
			tailY - UP_SHIFT,
				r_temp + tailX - LEFT_SHIFT,
					tailY - UP_SHIFT);
		g.drawArc(tailX - LEFT_SHIFT-r_temp,
			tailY - UP_SHIFT-r_temp,
				2*r_temp,
					2*r_temp,
						180,180);
		
		//room
		g.setColor(Color.green);
		g.drawString("room",tailX - LEFT_SHIFT - 5+10,tailY - UP_SHIFT - 5+3);
		g.fillOval(tailX - LEFT_SHIFT - 5, tailY - UP_SHIFT - 5, 10,10);
		
		headX = Xconvert(ahu_sim.room_T-ahu_sim.Q_in*Math.sin(ahu_sim.SHF_in*Math.PI/2)/10);
		headY = Yconvert(ahu_sim.room_w-ahu_sim.Q_in*Math.cos(ahu_sim.SHF_in*Math.PI/2)/(3*12000));
		
		//supply
		g.setColor(Color.blue);
		g.drawString("supply",headX - 5 - LEFT_SHIFT+10, headY - 5 - UP_SHIFT+15);
		g.fillOval(headX - 5 - LEFT_SHIFT, headY - 5 - UP_SHIFT, 10, 10);
		g.drawLine(tailX - LEFT_SHIFT, tailY - UP_SHIFT, headX - LEFT_SHIFT, headY - UP_SHIFT);
		
		//schematic
		g.setColor(Color.black);
		g.drawString("Cooling Vectors (BTU/Hr)",Xconvert(MIN_TEMP) - LEFT_SHIFT, Yconvert(MAX_HUMIDITY) - UP_SHIFT);/*
		
		//load
		g.setColor(Color.black);
		g.drawLine(50, 80, 100, 50);
		g.drawOval(50-8, 80-8, 16, 16);		
		
		//room
		g.setColor(Color.green);
		g.fillOval(100-5, 50-5, 10,10);		
		
		//supply
		g.setColor(Color.blue);
		g.fillOval(50-5, 50-5, 10, 10);
		g.drawLine(50, 50, 100, 50);*/
		
	}
	
	int Xconvert(double temperature) {
		int returnThis = (int)((temperature - 40)/60*(size().width-1));
		returnThis = Math.max(returnThis,0);
		returnThis = Math.min(returnThis,size().width - 1);
		return returnThis;
	}
	
	int Yconvert(double humidity) {
		int returnThis = (int)((0.03 - humidity)*40*(size().height-1));
		returnThis = Math.max(returnThis,0);
		returnThis = Math.min(returnThis, size().height - 1);
		return returnThis;
	}
}
