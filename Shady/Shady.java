import java.awt.*;
import java.awt.event.*;

public class Shady extends java.applet.Applet implements Runnable {
    HouseGraphic house_graphic = new HouseGraphic();
    HouseSide house_side = new HouseSide();
    DirectionGraphic direction_graphic = new DirectionGraphic();
    ClockControl clock_control = new ClockControl();
    MonthControl month_control = new MonthControl();
    TextField latitude_tf = new TextField();
    Button run_button;
    Checkbox run_day;
    Checkbox run_year;
    double latitude = 40*Math.PI/180;//latitude in degrees
    boolean is_running=false;

    public void repaint() {
		clock_control.repaint();
        house_side.repaint();
        house_graphic.repaint();
        direction_graphic.repaint();
    }

    public void init() {
        run_button = new Button("Run");
        CheckboxGroup cbg = new CheckboxGroup();
        run_day = new Checkbox("Run day",cbg,true);
        run_year = new Checkbox("Run year",cbg,false);
                
        setBackground(Color.white);
                
        //layout
        Panel p3 = new Panel();
        p3.setLayout(new GridLayout(1,2));
        p3.add(month_control);
        p3.add(clock_control);
                
        Panel p4 = new Panel();
        p4.setLayout(new GridLayout(1,2));
        p4.add(run_year);
        p4.add(run_day);
                
        Panel p2 = new Panel();
        p2.setLayout(new BorderLayout());
        p2.add("North",p3);
        p2.add("Center",p4);
        p2.add("South",run_button);

        Panel p5 = new Panel();
        p5.setLayout(new GridLayout(1,2));
		Label lab = new Label("Latitude");
		lab.setAlignment(Label.RIGHT);
        p5.add(lab);
        p5.add(latitude_tf);

        Panel p = new Panel();
        p.setLayout(new BorderLayout());
        p.add("Center",direction_graphic);
        p.add("South",p5);
        /*
        Panel p6 = new Panel();
        p6.setLayout(new BorderLayout());
        p6.add("West",p);
        p6.add("Center",p2);
                
        setLayout(new BorderLayout());
        add("Center",house_graphic);
        add("East",house_side);
                
        add("South",p6);*/
		setLayout(new GridLayout(2,2));
		add(house_graphic);
		add(house_side);
		add(p);
		add(p2);
        run_button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    is_running = !is_running;
                    if (is_running) {
                        run_button.setLabel("Stop");
                    } else {
                        run_button.setLabel("Run");
                    }
                    repaint();
                }
            });
        
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
		house_graphic.update_shadow();
        while(true) {
            while(is_running) {
                long now=System.currentTimeMillis();
                if (run_year.getState()) {
                    while(System.currentTimeMillis()<now+1000);
					month_control.incMonth();
                } else {
                    while(System.currentTimeMillis()<now+100);
					clock_control.incTime();
                }
				house_graphic.update_shadow();
            }
        }
    }

    public class MonthControl extends Panel {
		int month=0;//month ranging from 0 to 11
        CheckboxGroup cbg = new CheckboxGroup();
        Checkbox[] checkboxes = new Checkbox[12];
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun",
                           "Jul","Aug","Sep","Oct","Nov","Dec"};
        class monthclick implements ItemListener {
            int index;
            monthclick(int i) {
                index = i;
            }
            public void itemStateChanged(ItemEvent e) {
                month=index;
                house_graphic.update_shadow();
            }
        }
		public void incMonth() {
			month++;
			month %= 12;
			checkboxes[month].setState(true);
		}
        public MonthControl() {
            for (int i=0; i<12; i++) {
                checkboxes[i] = new Checkbox(months[i],cbg,false);
            }
            setLayout(new GridLayout(6,2));
            add(checkboxes[5]);
            add(checkboxes[6]);
            add(checkboxes[4]);
            add(checkboxes[7]);
            add(checkboxes[3]);
            add(checkboxes[8]);
            add(checkboxes[2]);
            add(checkboxes[9]);
            add(checkboxes[1]);
            add(checkboxes[10]);
            add(checkboxes[0]);
            add(checkboxes[11]);
            checkboxes[0].setState(true);//start with January checked!
            for (int i=0; i<12; i++) {
                checkboxes[i].addItemListener(new monthclick(i));
            }         
        }
    }

    public class ClockControl extends Canvas {
		final int CLOCK_RADIUS=50;
		final int AMPM_X=10;
		final int AMPM_Y=10;
		final int AMPM_WIDTH=20;
		final int AMPM_HEIGHT=15;
		double time=0;//time ranging from 0 to 23 = #hrs
		public void paint(Graphics g) {
			Rectangle R = ClockControl.this.getBounds();
			int W = R.width/2;
			int H = R.height/2;
			
			//draw AM/PM
			FontMetrics fm = g.getFontMetrics();
			g.setColor(Color.green);
			if (time<12) {
				g.fillRect(AMPM_X,AMPM_Y,AMPM_WIDTH,AMPM_HEIGHT);
			} else {
				g.fillRect(W*2-AMPM_X-AMPM_WIDTH,AMPM_Y,AMPM_WIDTH,AMPM_HEIGHT);
			}
			g.setColor(Color.black);
			g.drawRect(AMPM_X,AMPM_Y,AMPM_WIDTH,AMPM_HEIGHT);
			g.drawString("AM",AMPM_X+5,AMPM_Y+fm.getHeight()-5);
			g.drawRect(W*2-AMPM_X-AMPM_WIDTH,AMPM_Y,AMPM_WIDTH,AMPM_HEIGHT);
			g.drawString("PM",W*2-AMPM_X-AMPM_WIDTH+5,AMPM_Y+fm.getHeight()-5);
			
			g.drawOval(W-CLOCK_RADIUS,H-CLOCK_RADIUS,2*CLOCK_RADIUS,2*CLOCK_RADIUS);
			g.drawString("12",W-4,H-CLOCK_RADIUS+fm.getHeight()+10);
			g.drawString("3",W+CLOCK_RADIUS-20,H+5);
			g.drawString("6",W,H+CLOCK_RADIUS-10);
			g.drawString("9",W-CLOCK_RADIUS+10,H+5);
			for (int i=0; i<12; i++) {
				g.drawLine((int)(W+(CLOCK_RADIUS-10)*Math.cos(i*Math.PI/6)),(int)(H+(CLOCK_RADIUS-10)*Math.sin(i*Math.PI/6)),
						   (int)(W+CLOCK_RADIUS*Math.cos(i*Math.PI/6)),(int)(H+CLOCK_RADIUS*Math.sin(i*Math.PI/6)));
			}
			g.drawLine(W,H,
					   (int)(W+CLOCK_RADIUS*Math.cos((time-3)*Math.PI/6)),
					   (int)(H+CLOCK_RADIUS*Math.sin((time-3)*Math.PI/6)));
		}
		public void incTime() {
			time += 0.1;
			time %= 24;
		}
        ClockControl() {
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					Rectangle R = ClockControl.this.getBounds();
					int W = R.width/2;
					int H = R.height/2;
					int exx = e.getX();
					int wye = e.getY();
					if (AMPM_X<exx && exx<AMPM_X+AMPM_WIDTH
					 && AMPM_Y<wye && wye<AMPM_Y+AMPM_HEIGHT) {
						//switch to am
						time %= 12;
					} else if (W*2-AMPM_X-AMPM_WIDTH<exx && exx<W*2-AMPM_X
					        && AMPM_Y<wye && wye<AMPM_Y+AMPM_HEIGHT) {
						//switch to pm
						time = (time%12)+12;
					} else {
						if (time<12) {
							time = 3+6*Math.atan2(e.getY()-H,e.getX()-W)/Math.PI;
							time %= 12;
						} else {
							time = 3+6*Math.atan2(e.getY()-H,e.getX()-W)/Math.PI;
							time = (time%12)+12;
						}
					}
					house_graphic.update_shadow();
				}
			});
        }
    }
	
	static final int ROOF_HEIGHT = 40;
    static final int WALL_HEIGHT=100;
    static final double DEGREES=180/Math.PI;

    public class HouseGraphic extends Canvas {
		static final int WINDOW_WIDTH=40;
		static final int WINDOW_HEIGHT=30;
		static final int OVER_HANG = 20;
		static final int WALL_WIDTH=200;
		double delta;
		double beta;
		double phi;
		double gamma;
		double theta_v;
		double shadowheight;
		double wingshadowwidth;
		void update_shadow() {
			try {
				latitude=Double.valueOf(latitude_tf.getText()).doubleValue()*Math.PI/180;
			} catch (Exception e) {latitude_tf.setText(""+latitude*180/Math.PI);}
			//compute shadowline from latitude
			delta = 23.45*Math.sin(2*Math.PI*(month_control.month-2)/12)*Math.PI/180;
			beta = Math.asin(Math.cos(latitude)*Math.cos(delta)*Math.cos((clock_control.time-12)*60*Math.PI/(180*4))+Math.sin(latitude)*Math.sin(delta));
			phi = Math.acos((Math.sin(beta)*Math.sin(latitude)-Math.sin(delta))/(Math.cos(beta)*Math.cos(latitude)));
			phi *= (clock_control.time>12?-1:1);
			gamma = phi-direction_graphic.direction-Math.PI/2;
			theta_v = Math.acos(Math.cos(gamma)*Math.cos(beta));
			theta_v *= (clock_control.time<0?-1:1);
			shadowheight = (int)(house_side.side*Math.tan(theta_v));
			wingshadowwidth = (int)(house_side.wing_width*Math.tan(gamma));
			Shady.this.repaint();
		}

        Point window = new Point(50+WALL_WIDTH/4,50+WALL_HEIGHT/2);
		Point window2 = new Point(50+3*WALL_WIDTH/4,50+WALL_HEIGHT/2);
        public HouseGraphic() {
            setSize(200+WALL_WIDTH,200+WALL_HEIGHT);
            addMouseListener(new MouseAdapter() {          
                    public void mousePressed(MouseEvent e) {
						Rectangle R = HouseGraphic.this.getBounds();
						int W = R.width/2;
						int H = R.height/2;
						if (e.getX()<W) {
							window.x = Math.max(W-WALL_WIDTH/2+WINDOW_WIDTH/2,Math.min(W-WINDOW_WIDTH/2,e.getX()));
							window.y = Math.max(H-WALL_HEIGHT/2+WINDOW_HEIGHT/2,Math.min(H+WALL_HEIGHT/2-WINDOW_HEIGHT/2,e.getY()));
						} else {
							window2.x = Math.max(W+WINDOW_WIDTH/2,Math.min(W+WALL_WIDTH/2-WINDOW_WIDTH/2,e.getX()));
							window2.y = Math.max(H-WALL_HEIGHT/2+WINDOW_HEIGHT/2,Math.min(H+WALL_HEIGHT/2-WINDOW_HEIGHT/2,e.getY()));
						}
                        HouseGraphic.this.repaint();
                    }
                });
        }
        public void paint(Graphics g) {
			Rectangle R = HouseGraphic.this.getBounds();
			int W = R.width/2;
			int H = R.height/2;
            if (beta<0) {
				//nighttime
                g.setColor(Color.blue);
                g.fillRect(0,0,1000,1000);
			} else {
				//overhang shadow
				g.setColor(Color.gray);
				g.fillRect(W-WALL_WIDTH/2,H-WALL_HEIGHT/2,WALL_WIDTH,(int)(shadowheight<0?WALL_HEIGHT:Math.min(WALL_HEIGHT,shadowheight)));
				
				//wingwall shadow
				if (Math.abs(gamma)<Math.PI/2) {
					g.fillRect((int)(wingshadowwidth<0?W:W-Math.min(WALL_WIDTH/2,Math.abs(wingshadowwidth))),H-WALL_HEIGHT/2,
							   Math.min(WALL_WIDTH/2,(int)Math.abs(wingshadowwidth)),WALL_HEIGHT);
				} else {
					//entire wall in in shadow of the house!
					g.setColor(Color.darkGray);
					g.fillRect(W-WALL_WIDTH/2,H-WALL_HEIGHT/2,WALL_WIDTH,WALL_HEIGHT);
				}
			}
            /**debugging info***
			g.setColor(Color.red);
            g.drawString("delta = "+delta*DEGREES,50,10);
            g.drawString("beta = "+beta*DEGREES,50,20);
            g.drawString("phi = "+phi*DEGREES,50,30);
            g.drawString("gamma = "+gamma*DEGREES,50,40);
            g.drawString("theta_v = "+theta_v*DEGREES,50,50);
            g.drawString("shadowheight = "+shadowheight,50,60);
			g.drawString("wingshadowwidth = "+wingshadowwidth,50,70);
			*/

            //front wall
            g.setColor(Color.black);
            g.drawRect(W-WALL_WIDTH/2,H-WALL_HEIGHT/2,WALL_WIDTH,WALL_HEIGHT);
            //roof
            g.drawRect(W-WALL_WIDTH/2-OVER_HANG,H-WALL_HEIGHT/2-ROOF_HEIGHT,WALL_WIDTH+2*OVER_HANG,ROOF_HEIGHT);

            //window
            g.setColor(new Color(0x52,0x2a,0x2a));
            g.drawRect(window.x-WINDOW_WIDTH/2,window.y-WINDOW_HEIGHT/2,WINDOW_WIDTH,WINDOW_HEIGHT);
			g.drawRect(window.x-WINDOW_WIDTH/2-1,window.y-WINDOW_HEIGHT/2-1,WINDOW_WIDTH+2,WINDOW_HEIGHT+2);
			g.fillRect(window.x-WINDOW_WIDTH/2,window.y-2,WINDOW_WIDTH,4);
			g.fillRect(window.x-2,window.y-WINDOW_HEIGHT/2,4,WINDOW_HEIGHT);

            //window2
            g.drawRect(window2.x-WINDOW_WIDTH/2,window2.y-WINDOW_HEIGHT/2,WINDOW_WIDTH,WINDOW_HEIGHT);
			g.drawRect(window2.x-WINDOW_WIDTH/2-1,window2.y-WINDOW_HEIGHT/2-1,WINDOW_WIDTH+2,WINDOW_HEIGHT+2);
			g.fillRect(window2.x-WINDOW_WIDTH/2,window2.y-2,WINDOW_WIDTH,4);
			g.fillRect(window2.x-2,window2.y-WINDOW_HEIGHT/2,4,WINDOW_HEIGHT);
			
			//draw dimensions
			g.drawLine(2*W-20,H-WALL_HEIGHT/2,2*W-20,H-10);
			g.drawString("100",2*W-30,H);
			g.drawLine(2*W-20,H,2*W-20,H+WALL_HEIGHT/2);
			
			//draw border
			g.setColor(Color.red);
			for (int i=0; i<11; i +=2) {
				g.fillRect(W-2,H-WALL_HEIGHT/2+i*WALL_HEIGHT/11,4,WALL_HEIGHT/11);
			}
        }
    }
    public class HouseSide extends Canvas {
		static final int SIDE_WALL_WIDTH=100;
        int side=25;
		int wing_width=20;
        public HouseSide() {
            setSize(100+SIDE_WALL_WIDTH+2*side,300);
            this.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
						Rectangle R = HouseSide.this.getBounds();
						int W = R.width/2;
						int H = R.height/2;
						if (e.getX()<W) {
							wing_width = Math.max(0,(Math.abs(e.getX()-W)-SIDE_WALL_WIDTH/2)/2);
						} else {
							side = Math.max(0,(Math.abs(e.getX()-W)-SIDE_WALL_WIDTH/2)/2);
						}
                        house_graphic.update_shadow();
                    }
                });
        }
        public void paint(Graphics g) {
			Rectangle R = HouseSide.this.getBounds();
			int W = R.width/2;
			int H = R.height/2;
            if (house_graphic.beta<0) {
                g.setColor(Color.blue);
                g.fillRect(0,0,1000,1000);
                g.setColor(Color.white);
                g.drawString("Nighttime",10,30);
            } else {
                int[] shadow_x = {W+SIDE_WALL_WIDTH/2+side,W-SIDE_WALL_WIDTH/2-side,
                                  (int)(W-SIDE_WALL_WIDTH/2-side+WALL_HEIGHT/Math.tan(house_graphic.theta_v)),(int)(W+SIDE_WALL_WIDTH/2+side+WALL_HEIGHT/Math.tan(house_graphic.theta_v))};
                int[] shadow_y = {H-WALL_HEIGHT/2,H-WALL_HEIGHT/2,H+WALL_HEIGHT/2,H+WALL_HEIGHT/2};
                g.setColor(Color.gray);
                g.fillPolygon(shadow_x,shadow_y,4);
            }
            g.setColor(Color.black);
            g.drawRect(W-SIDE_WALL_WIDTH/2,H-WALL_HEIGHT/2,SIDE_WALL_WIDTH,WALL_HEIGHT);
            int[] roof_x = {W-SIDE_WALL_WIDTH/2+SIDE_WALL_WIDTH/2,W-SIDE_WALL_WIDTH/2-side,W-SIDE_WALL_WIDTH/2+SIDE_WALL_WIDTH+side};
            int[] roof_y = {H-WALL_HEIGHT/2-ROOF_HEIGHT,H-WALL_HEIGHT/2,H-WALL_HEIGHT/2};
            g.drawPolygon(roof_x,roof_y,3);
			
			//draw wing wall
			g.setColor(Color.red);
			g.fillRect(W-SIDE_WALL_WIDTH/2-wing_width,H-WALL_HEIGHT/2,wing_width,WALL_HEIGHT);
            /***debugging info*** 
            g.setColor(Color.red);
            g.drawString("side = "+side,10,10);
			*/
			
			//draw dimensions
			g.setColor(Color.black);
			g.drawLine(W+SIDE_WALL_WIDTH/2,H+WALL_HEIGHT/2,W+SIDE_WALL_WIDTH/2,H+WALL_HEIGHT/2+10);
			g.drawLine(W+SIDE_WALL_WIDTH/2+side,H+WALL_HEIGHT/2,W+SIDE_WALL_WIDTH/2+side,H+WALL_HEIGHT/2+10);
			g.drawLine(W+SIDE_WALL_WIDTH/2,H+WALL_HEIGHT/2+10,W+SIDE_WALL_WIDTH/2+side,H+WALL_HEIGHT/2+10);
			g.drawString(""+side,W+SIDE_WALL_WIDTH/2+side/2-5,H+WALL_HEIGHT/2+10);
			
			g.drawLine(W-SIDE_WALL_WIDTH/2,H+WALL_HEIGHT/2,W-SIDE_WALL_WIDTH/2,H+WALL_HEIGHT/2+10);
			g.drawLine(W-SIDE_WALL_WIDTH/2-wing_width,H+WALL_HEIGHT/2,W-SIDE_WALL_WIDTH/2-wing_width,H+WALL_HEIGHT/2+10);
			g.drawLine(W-SIDE_WALL_WIDTH/2,H+WALL_HEIGHT/2+10,W-SIDE_WALL_WIDTH/2-wing_width,H+WALL_HEIGHT/2+10);
			g.drawString(""+wing_width,W-SIDE_WALL_WIDTH/2-wing_width/2-5,H+WALL_HEIGHT/2+10);
        }
    }

    public class DirectionGraphic extends Canvas {
		final int COMPASS_RADIUS = 50;
		double direction=-Math.PI/2;//direction the house is facing in degrees CCW from North
        public DirectionGraphic() {
            setSize(150,150);
            this.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
						Rectangle R = DirectionGraphic.this.getBounds();
						int W = R.width/2;
						int H = R.height/2;
                        direction = Math.atan2(e.getY()-H,e.getX()-W);
                        house_graphic.update_shadow();
                    }
                });
        }
        public void paint(Graphics g) {
			Rectangle R = DirectionGraphic.this.getBounds();
			int W = R.width/2;
			int H = R.height/2;
			FontMetrics fm = g.getFontMetrics();
            g.setColor(Color.black);
            g.drawOval(W-COMPASS_RADIUS,H-COMPASS_RADIUS,2*COMPASS_RADIUS,2*COMPASS_RADIUS);
            g.drawString("N",(int)(W+(COMPASS_RADIUS-15)*Math.cos(direction)),(int)(H+fm.getHeight()/2+(COMPASS_RADIUS-15)*Math.sin(direction)));
            g.drawString("E",(int)(W+(COMPASS_RADIUS-15)*Math.cos(direction+Math.PI/2)),(int)(H+fm.getHeight()/2+(COMPASS_RADIUS-15)*Math.sin(direction+Math.PI/2)));
            g.drawString("S",(int)(W+(COMPASS_RADIUS-15)*Math.cos(direction+Math.PI)),(int)(H+fm.getHeight()/2+(COMPASS_RADIUS-15)*Math.sin(direction+Math.PI)));
            g.drawString("W",(int)(W+(COMPASS_RADIUS-15)*Math.cos(direction+3*Math.PI/2)),(int)(H+fm.getHeight()/2+(COMPASS_RADIUS-15)*Math.sin(direction+3*Math.PI/2)));
            g.drawLine(W,H,(int)(W+COMPASS_RADIUS*Math.cos(direction)),(int)(H+COMPASS_RADIUS*Math.sin(direction)));

            String where;
            if (direction<-7*Math.PI/8) {
                where = "west";
            } else if (direction<-5*Math.PI/8) {
                where = "southwest";
            } else if (direction<-3*Math.PI/8) {
                where = "south";
            } else if (direction<-Math.PI/8) {
                where = "southeast";
            } else if (direction<Math.PI/8) {
                where = "east";
            } else if (direction<3*Math.PI/8) {
                where = "northeast";
            } else if (direction<5*Math.PI/8) {
                where = "north";
            } else if (direction<7*Math.PI/8) {
                where = "northwest";
            } else {
                where = "west";
            }
            g.drawString("Wall facing "+where,W-30,30);
            /***debugging info*** 
            g.setColor(Color.red);
            g.drawString("direction = "+direction*DEGREES,10,10);
            */
        }
    }
}
