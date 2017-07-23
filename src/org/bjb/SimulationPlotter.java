package org.bjb;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class SimulationPlotter extends JPanel {

//	private Color paleGreen = new Color(0x98fb98);
	static final Color paleGreen = new Color(0xccffcc);
	final private BufferedImage offscreen;
	final private Graphics2D og2;
	final private Point[] plotPts;
	private long rounds;
	private int width,height;
	private int bankroll;
	private int max,min;
	private int y0;
	private Player[] players;
	private Point[] pts;
	private static Color[] colors = { Color.blue,PlayingArea.lightGreen,Color.magenta,Color.cyan,Color.orange,Color.yellow,Color.pink };
	
	public SimulationPlotter(int width,int height,Player[] players,long rounds,int bankroll,SimulationPlayerInfo[] pInfos) {
		this.rounds = rounds;
		this.width = width;
		this.height = height;
		this.players = players;
		this.bankroll = bankroll;
		max = bankroll + bankroll * 3 / 2;
		min = bankroll - max;
		y0 = height / 2;
		setPreferredSize(new Dimension(width,height));
		setBackground(Color.white);
		offscreen = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		og2 = offscreen.createGraphics();
		og2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);	
		og2.setColor(paleGreen);
		og2.fillRect(0,0,width,height);
		og2.setColor(Color.black);
		og2.drawLine(0,height/2,width,height/2);
		og2.setColor(Color.lightGray);
		int x5000 = toX(5000);
		og2.drawLine(x5000,0,x5000,height);
		int x10000 = toX(10000);
		og2.drawLine(x10000,0,x10000,height);
		int x15000 = toX(15000);
		og2.drawLine(x15000,0,x15000,height);
		int x20000 = toX(20000);
		og2.drawLine(x20000,0,x20000,height);
		og2.setColor(Color.black);
/*
		og2.drawString("1000", 10, height/2+15);	// TODO determine string based on actual initial bankroll
		og2.drawString("2000",10,toY(2000));
		og2.drawString("2500",10,toY(max-1)+15);
		og2.drawString("0",10,toY(0));
		og2.drawString("-500",10,height-15);
*/
		og2.drawString(new Integer(bankroll).toString(), 10, height/2+15);	// TODO determine string based on actual initial bankroll
		og2.drawString(new Integer(bankroll*2).toString(),10,toY(bankroll*2));
		og2.drawString(new Integer(bankroll+bankroll*3/2).toString(),10,toY(max-1)+15);
		og2.drawString("0",10,toY(0));
		og2.drawString(new Integer(bankroll-bankroll*3/2).toString(),10,height-15);
		og2.drawString("Rd.",x5000-20,15);
		og2.drawString("5k", x5000+2,15);
		og2.drawString("Rd.",x10000-20,15);
		og2.drawString("10k", x10000+2, 15);
		og2.drawString("Rd.",x15000-20,15);
		og2.drawString("15k", x15000+2, 15);
		og2.drawString("Rd.",x20000-20,15);
		og2.drawString("20k", x20000+2, 15);
		plotPts = new Point[players.length];
		pts = new Point[players.length];
		for (int i=0;i<plotPts.length;i++) 
			plotPts[i] = new Point(0,y0);			
	}
	
	public void update(Player[] players,long round) {
		boolean updated = false;
		for (int i=0;i<players.length;i++) {
			pts[i] = new Point(toX(round),toY(players[i].getBalance()));
//			System.out.println("SP update plotPts (" + plotPts[i].x + "," + plotPts[i].y + "), pts (" + pts[i].x + "," + pts[i].y + ")");
			if (pts[i].x != plotPts[i].x || pts[i].y != plotPts[i].y) {
				og2.setColor(colors[i]);
				og2.drawLine(plotPts[i].x,plotPts[i].y,pts[i].x,pts[i].y);
				plotPts[i] = pts[i];
				updated = true;
			}
		}
		if (updated)
			repaint();
	}
	
	private int toX(long round) {
		return (int)(round*width/rounds);
	}
	
	private int toY(int balance) {
		int y = 0;
		if (balance >= 0) {
			if (balance >= max) y = 5;
			else {
//				y = y0 - ((balance*height)/(2*(max-bankroll)));
				if (max-bankroll == 0)
					throw new IllegalStateException("toY with max " + max + " equal to bankroll " + bankroll);
				y = y0 - (y0 * (balance-bankroll)) / (max-bankroll); 
			}
		}
		else {
			if (balance <= min) y = height - 5;
			if (max-bankroll == 0)
				throw new IllegalStateException("toY with max " + max + " equal to bankroll " + bankroll);
			y = y0 + (y0 * Math.abs(balance-bankroll)) / (max-bankroll);
		}
		return y;
	}
	
	public void reset(long rounds,int bankroll) {
		this.rounds = rounds;
		this.bankroll = bankroll;
		og2.setColor(paleGreen);
		og2.fillRect(0,0,width,height);
		og2.setColor(Color.lightGray);
		int x5000 = toX(5000);
		og2.drawLine(x5000,0,x5000,height);
		int x10000 = toX(10000);
		og2.drawLine(x10000,0,x10000,height);
		int x15000 = toX(15000);
		og2.drawLine(x15000,0,x15000,height);
		int x20000 = toX(20000);
		og2.drawLine(x20000,0,x20000,height);
		og2.setColor(Color.black);
		og2.drawLine(0,height/2,width,height/2);
/*
		og2.drawString("1000", 10, height/2+15);	// TODO determine string based on actual initial bankroll
		og2.drawString("2000",10,toY(2000));
		og2.drawString("2500",10,toY(max-1)+15);
		og2.drawString("0",10,toY(0));
		og2.drawString("-500",10,height-15);
*/
		og2.drawString(new Integer(bankroll).toString(), 10, height/2+15);	// TODO determine string based on actual initial bankroll
		og2.drawString(new Integer(bankroll*2).toString(),10,toY(bankroll*2));
		og2.drawString(new Integer(bankroll+bankroll*3/2).toString(),10,toY(max-1)+15);
		og2.drawString("0",10,toY(0));
		og2.drawString(new Integer(bankroll-bankroll*3/2).toString(),10,height-15);
		og2.drawString("Rd.",x5000-20,15);
		og2.drawString("5k", x5000+2,15);
		og2.drawString("Rd.",x10000-20,15);
		og2.drawString("10k", x10000+2, 15);
		og2.drawString("Rd.",x15000-20,15);
		og2.drawString("15k", x15000+2, 15);
		og2.drawString("Rd.",x20000-20,15);
		og2.drawString("20k", x20000+2, 15);
		pts = new Point[players.length];
		for (int i=0;i<plotPts.length;i++) 
			plotPts[i] = new Point(0,y0);			
	}
	
	public static Color getColor(int index) {
		return colors[index];
	}
	
	protected void paintComponent(Graphics g) {
		g.drawImage(offscreen,0,0,null);
	}
}
