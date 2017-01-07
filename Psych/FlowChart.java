import java.awt.*;
import java.net.URL;

class FlowChart extends Canvas {
	//constants
	final int TOP = 30;
	final int BOTTOM = 80;
	final int RIGHT = 140;
	final int MIDDLE = 30;
	final int LEFT = 0;
	
	final int GREEN_R = Color.green.getRed();
	final int GREEN_G = Color.green.getGreen();
	final int GREEN_B = Color.green.getBlue();
	final int BLUE_R = Color.blue.getRed();
	final int BLUE_G = Color.blue.getGreen();
	final int BLUE_B = Color.blue.getBlue();
	final int RED_R = Color.red.getRed();
	final int RED_G = Color.red.getGreen();
	final int RED_B = Color.red.getBlue();
	final int ORANGE_R = Color.orange.getRed();
	final int ORANGE_G = Color.orange.getGreen();
	final int ORANGE_B = Color.orange.getBlue();
	
	final float GREEN_H = Color.RGBtoHSB(GREEN_R,GREEN_G,GREEN_B,null)[0];
	final float GREEN_S = Color.RGBtoHSB(GREEN_R,GREEN_G,GREEN_B,null)[1];
	final float RED_H = Color.RGBtoHSB(RED_R,RED_G,RED_B,null)[0];
	final float RED_S = Color.RGBtoHSB(RED_R,RED_G,RED_B,null)[1];
	final float BLUE_H = Color.RGBtoHSB(BLUE_R,BLUE_G,BLUE_B,null)[0];
	final float BLUE_S = Color.RGBtoHSB(BLUE_R,BLUE_G,BLUE_B,null)[1];
	final float ORANGE_H = Color.RGBtoHSB(ORANGE_R,ORANGE_G,ORANGE_B,null)[0];
	final float ORANGE_S = Color.RGBtoHSB(ORANGE_R,ORANGE_G,ORANGE_B,null)[1];
	
	final float BRIGHT = 1;
	
	//fields
	AHU ahu;
	AHU_Simulation ahu_sim;
	
	//constructor
	FlowChart(AHU a) {
		ahu = a;
		ahu_sim = ahu.ahu_sim;
	}
	
	//methods
	
	public void paint(Graphics g) {
		g.setColor(Color.black);
		Dimension d = size();
		g.drawRect(0,0,d.width-1,d.height-1);
		
		//OUTLINE
		g.drawLine(0,TOP,RIGHT,TOP);
		g.drawLine(0,BOTTOM+20,RIGHT,BOTTOM+20);
		g.drawLine(0,TOP+20,MIDDLE,TOP+20);
		g.drawLine(0,BOTTOM,MIDDLE,BOTTOM);
		g.drawLine(MIDDLE+20,TOP+20,RIGHT,TOP+20);
		g.drawLine(MIDDLE+20,BOTTOM,RIGHT,BOTTOM);
		g.drawLine(MIDDLE,TOP+20,MIDDLE,BOTTOM);
		g.drawLine(MIDDLE+20,TOP+20,MIDDLE+20,BOTTOM);

		//HOUSE BODY
		g.setColor(Color.black);
		g.fillRect(RIGHT,BOTTOM-20,20,20);
		
		if (ahu_sim.section.equals("top")) {
			g.setColor(Color.green);
			int X = RIGHT - (int)((RIGHT - MIDDLE)*ahu_sim.counter/ahu_sim.STEPS);
			g.fillArc(X,TOP,20,20,(int)(ahu_sim.OA_percent*3.6)+90,360 - (int)(ahu_sim.OA_percent*3.6));
			int X2 = RIGHT - (int)((RIGHT - LEFT)*ahu_sim.counter/ahu_sim.STEPS);
			g.fillArc(X2,TOP,20,20,90,(int)(ahu_sim.OA_percent*3.6));
		} else if (ahu_sim.section.equals("left")) {
			g.setColor(new Color(Color.HSBtoRGB((float)(GREEN_H+(ORANGE_H-GREEN_H)*ahu_sim.counter/ahu_sim.STEPS),
				(float)(GREEN_S+(ORANGE_S-GREEN_S)*ahu_sim.counter/ahu_sim.STEPS),
					BRIGHT)));
			int Y = TOP - (int)((TOP - BOTTOM)*ahu_sim.counter/ahu_sim.STEPS);
			g.fillArc(MIDDLE,Y,20,20,(int)(ahu_sim.OA_percent*3.6)+90,360 - (int)(ahu_sim.OA_percent*3.6));
			g.setColor(new Color(Color.HSBtoRGB((float)(RED_H+(ORANGE_H-RED_H)*ahu_sim.counter/ahu_sim.STEPS),
				(float)(RED_S+(ORANGE_S-RED_S)*ahu_sim.counter/ahu_sim.STEPS),
				BRIGHT)));
			int X = LEFT + (int)((MIDDLE - LEFT)*ahu_sim.counter/ahu_sim.STEPS);
			g.fillArc(X,BOTTOM,20,20,90,(int)(ahu_sim.OA_percent*3.6));
		} else if (ahu_sim.section.equals("bottom")) {
			int X = MIDDLE + (int)((RIGHT - MIDDLE)*ahu_sim.counter/ahu_sim.STEPS);
			g.setColor(new Color(Color.HSBtoRGB((float)(ORANGE_H+(BLUE_H-ORANGE_H)*ahu_sim.counter/ahu_sim.STEPS),
				(float)(ORANGE_S+(BLUE_S-ORANGE_S)*ahu_sim.counter/ahu_sim.STEPS),
				BRIGHT)));
			//g.setColor(Color.red);
			g.fillArc(X,BOTTOM,20,20,(int)(ahu_sim.OA_percent*3.6)+90,360 - (int)(ahu_sim.OA_percent*3.6));
			//g.setColor(Color.blue);
			g.fillArc(X,BOTTOM,20,20,90,(int)(ahu_sim.OA_percent*3.6));
		} else if (ahu_sim.section.equals("right")) {
			int Y = BOTTOM - (int)((BOTTOM - TOP)*ahu_sim.counter/ahu_sim.STEPS);
			g.setColor(new Color(Color.HSBtoRGB((float)(BLUE_H+(GREEN_H-BLUE_H)*ahu_sim.counter/ahu_sim.STEPS),
				(float)(BLUE_S+(GREEN_S-BLUE_S)*ahu_sim.counter/ahu_sim.STEPS),
				BRIGHT)));
			//g.setColor(Color.red);
			g.fillArc(RIGHT,Y,20,20,(int)(ahu_sim.OA_percent*3.6)+90,360 - (int)(ahu_sim.OA_percent*3.6));
			//g.setColor(Color.blue);
			g.fillArc(RIGHT,Y,20,20,90,(int)(ahu_sim.OA_percent*3.6));
		}
	
		//COIL
		g.setColor(Color.blue);
		g.drawLine((MIDDLE+RIGHT)/2+5,BOTTOM-4,(MIDDLE+RIGHT)/2+5,BOTTOM+24);
		g.drawLine((MIDDLE+RIGHT)/2+10,BOTTOM-4,(MIDDLE+RIGHT)/2+10,BOTTOM+24);
		g.drawLine((MIDDLE+RIGHT)/2+15,BOTTOM-4,(MIDDLE+RIGHT)/2+15,BOTTOM+24);
		g.drawLine((MIDDLE+RIGHT)/2+20,BOTTOM-4,(MIDDLE+RIGHT)/2+20,BOTTOM+24);
		g.drawString("coil",(MIDDLE+RIGHT)/2+5,BOTTOM+35);
	
		//HOUSE ROOF
		g.setColor(Color.black);
		int[] xtemp = {RIGHT-5,RIGHT+10,RIGHT+25};
		int[] ytemp = {BOTTOM-17,BOTTOM-30,BOTTOM-17};
		g.fillPolygon(xtemp,ytemp,3);
		//g.drawLine(RIGHT+10,BOTTOM,RIGHT+10,BOTTOM-10);
		//g.drawLine(RIGHT+20,BOTTOM,RIGHT+20,BOTTOM-10);
		//g.drawLine(RIGHT+10,BOTTOM,RIGHT+20,BOTTOM);
		//g.drawLine(RIGHT+8,BOTTOM-20,RIGHT+15,BOTTOM-10);
		//g.drawLine(RIGHT+22,BOTTOM-20,RIGHT+15,BOTTOM-10);
	}//end paint method
}//end FlowChart class