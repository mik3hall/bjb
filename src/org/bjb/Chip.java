package org.bjb;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import javax.swing.Icon;

public class Chip implements Icon {
	final static Color purple = new Color(255,0,255);
	int bet = 0;
	
	public Chip(int bet) {
		this.bet = bet;
	}
	
	public void paintIcon( Component c, Graphics g, int x, int y ) {
		Font font = new Font("SanSerif", Font.BOLD, 14);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setFont(font);
		g2d.setStroke(new BasicStroke(2.0f));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		String s = new StringBuilder("$").append(new Integer(bet).toString()).toString();
  		FontMetrics fm = g.getFontMetrics(font);
  		int w = Math.max(fm.stringWidth(s)+10,Hand.back.getIconWidth()*4/5);
  		int h = w;
  		Color sColor = Color.blue;
  		if (bet <= 10)
  			g.setColor(Color.red);
  		else if (bet <= 20) {
  			g.setColor(Color.blue);
  			sColor = Color.white;
  		}
  		else if (bet <= 25) 
  			g.setColor(Color.green);
  		else if (bet <= 100) {
  			g.setColor(Color.black);
  			sColor = Color.white;
  		}
  		else if (bet <= 500)
  			g.setColor(purple);
  		else if (bet <= 1000)
  			g.setColor(Color.orange);
  		else g.setColor(Color.lightGray);
		g2d.fillOval(x+9,y+Hand.back.getIconHeight()/3-h/2,w,h);
		int cx = x+9+w/2;
		int cy = (y+Hand.back.getIconHeight()/3-h/2)+h/2;
		g2d.setColor(Color.black);
		g2d.drawOval(x+9,y+Hand.back.getIconHeight()/3-h/2,w,h);
		g2d.setColor(Color.white);
		g2d.fillOval(x+17,y+Hand.back.getIconHeight()/3-h/2+7, w-15, h-15);
//		g2d.setPaint(sColor);
		g2d.setPaint(Color.black);
//		g2d.drawString(s,x+25,y+Hand.back.getIconHeight()/3);
		g2d.drawString(s,x+(w-15-fm.stringWidth(s))/2+16,y+Hand.back.getIconHeight()/3+5);
		g2d.setPaint(Color.white);
		int r = w/2;
		// PI/4
		GeneralPath border = new GeneralPath();
		double a = Math.PI/4 + Math.PI/16;
		int ulx = cx+Math.abs((int)(Math.cos(a) * r));
		int uly = cy-Math.abs((int)(Math.sin(a) * r));
		int llx = cx+Math.abs((int)(Math.cos(a) * (r-8)));
		int lly = cy-Math.abs((int)(Math.sin(a) * (r-8)));
		a = Math.PI/4 - Math.PI/16;
		int urx = cx+Math.abs((int)(Math.cos(a) * r));
		int ury = cy-Math.abs((int)(Math.sin(a) * r));
		int lrx = cx+Math.abs((int)(Math.cos(a) * (r-8)));
		int lry = cy-Math.abs((int)(Math.sin(a) * (r-8)));
		border.moveTo(ulx,uly);
		border.lineTo(llx,lly);
		border.lineTo(lrx,lry);
		border.lineTo(urx,ury);
		border.lineTo(ulx,uly);
		g2d.fill(border);
		border = new GeneralPath();
		a = Math.PI/2 + Math.PI/16;
		ulx = cx-Math.abs((int)(Math.cos(a) * r));
		uly = cy-Math.abs((int)(Math.sin(a) * r));
		llx = cx-Math.abs((int)(Math.cos(a) * (r-8)));
		lly = cy-Math.abs((int)(Math.sin(a) * (r-8)));
		a = Math.PI/2 - Math.PI/16;
		urx = cx+Math.abs((int)(Math.cos(a) * r));
		ury = cy-Math.abs((int)(Math.sin(a) * r));
		lrx = cx+Math.abs((int)(Math.cos(a) * (r-8)));
		lry = cy-Math.abs((int)(Math.sin(a) * (r-8)));
		border.moveTo(ulx,uly);
		border.lineTo(llx,lly);
		border.lineTo(lrx,lry);
		border.lineTo(urx,ury);
		border.lineTo(ulx,uly);
		g2d.fill(border);
		border = new GeneralPath();
		a = Math.PI*3/4 + Math.PI/16;
		ulx = cx-Math.abs((int)(Math.cos(a) * r));
		uly = cy-Math.abs((int)(Math.sin(a) * r));
		llx = cx-Math.abs((int)(Math.cos(a) * (r-8)));
		lly = cy-Math.abs((int)(Math.sin(a) * (r-8)));
		a = Math.PI*3/4 - Math.PI/16;
		urx = cx-Math.abs((int)(Math.cos(a) * r));
		ury = cy-Math.abs((int)(Math.sin(a) * r));
		lrx = cx-Math.abs((int)(Math.cos(a) * (r-8)));
		lry = cy-Math.abs((int)(Math.sin(a) * (r-8)));
		border.moveTo(ulx,uly);
		border.lineTo(llx,lly);
		border.lineTo(lrx,lry);
		border.lineTo(urx,ury);
		border.lineTo(ulx,uly);
		g2d.fill(border);
		border = new GeneralPath();
		a = Math.PI + Math.PI/16;
		ulx = cx-Math.abs((int)(Math.cos(a) * r));
		uly = cy+Math.abs((int)(Math.sin(a) * r));
		llx = cx-Math.abs((int)(Math.cos(a) * (r-8)));
		lly = cy+Math.abs((int)(Math.sin(a) * (r-8)));
		a = Math.PI - Math.PI/16;
		urx = cx-Math.abs((int)(Math.cos(a) * r));
		ury = cy-Math.abs((int)(Math.sin(a) * r));
		lrx = cx-Math.abs((int)(Math.cos(a) * (r-8)));
		lry = cy-Math.abs((int)(Math.sin(a) * (r-8)));
		border.moveTo(ulx,uly);
		border.lineTo(llx,lly);
		border.lineTo(lrx,lry);
		border.lineTo(urx,ury);
		border.lineTo(ulx,uly);
		g2d.fill(border);
		border = new GeneralPath();
		a = Math.PI * 5/4 + Math.PI/16;
		ulx = cx-Math.abs((int)(Math.cos(a) * r));
		uly = cy+Math.abs((int)(Math.sin(a) * r));
		llx = cx-Math.abs((int)(Math.cos(a) * (r-8)));
		lly = cy+Math.abs((int)(Math.sin(a) * (r-8)));	
		a = Math.PI * 5/4 - Math.PI/16;
		urx = cx-Math.abs((int)(Math.cos(a) * r));
		ury = cy+Math.abs((int)(Math.sin(a) * r));
		lrx = cx-Math.abs((int)(Math.cos(a) * (r-8)));
		lry = cy+Math.abs((int)(Math.sin(a) * (r-8)));
		border.moveTo(ulx,uly);
		border.lineTo(llx,lly);
		border.lineTo(lrx,lry);
		border.lineTo(urx,ury);
		border.lineTo(ulx,uly);
		g2d.fill(border);
		border = new GeneralPath();
		a = Math.PI * 3/2 + Math.PI/16;
		ulx = cx+Math.abs((int)(Math.cos(a) * r));
		uly = cy+Math.abs((int)(Math.sin(a) * r));
		llx = cx+Math.abs((int)(Math.cos(a) * (r-8)));
		lly = cy+Math.abs((int)(Math.sin(a) * (r-8)));
		a = Math.PI * 3/2 - Math.PI/16;
		urx = cx-Math.abs((int)(Math.cos(a) * r));
		ury = cy+Math.abs((int)(Math.sin(a) * r));
		lrx = cx-Math.abs((int)(Math.cos(a) * (r-8)));
		lry = cy+Math.abs((int)(Math.sin(a) * (r-8)));
		border.moveTo(ulx,uly);
		border.lineTo(llx,lly);
		border.lineTo(lrx,lry);
		border.lineTo(urx,ury);
		border.lineTo(ulx,uly);
		g2d.fill(border);
		border = new GeneralPath();
		a = Math.PI * 7/4 + Math.PI/16;
		ulx = cx+Math.abs((int)(Math.cos(a) * r));
		uly = cy+Math.abs((int)(Math.sin(a) * r));
		llx = cx+Math.abs((int)(Math.cos(a) * (r-8)));
		lly = cy+Math.abs((int)(Math.sin(a) * (r-8)));
		a = Math.PI * 7/4 - Math.PI/16;
		urx = cx+Math.abs((int)(Math.cos(a) * r));
		ury = cy+Math.abs((int)(Math.sin(a) * r));
		lrx = cx+Math.abs((int)(Math.cos(a) * (r-8)));
		lry = cy+Math.abs((int)(Math.sin(a) * (r-8)));
		border.moveTo(ulx,uly);
		border.lineTo(llx,lly);
		border.lineTo(lrx,lry);
		border.lineTo(urx,ury);
		border.lineTo(ulx,uly);
		g2d.fill(border);	
		border = new GeneralPath();
		a = 0 + Math.PI/16;
		ulx = cx+Math.abs((int)(Math.cos(a) * r));
		uly = cy-Math.abs((int)(Math.sin(a) * r));
		llx = cx+Math.abs((int)(Math.cos(a) * (r-8)));
		lly = cy-Math.abs((int)(Math.sin(a) * (r-8)));
		a = 0 - Math.PI/16;
		urx = cx+Math.abs((int)(Math.cos(a) * r));
		ury = cy+Math.abs((int)(Math.sin(a) * r));
		lrx = cx+Math.abs((int)(Math.cos(a) * (r-8)));
		lry = cy+Math.abs((int)(Math.sin(a) * (r-8)));
		border.moveTo(ulx,uly);
		border.lineTo(llx,lly);
		border.lineTo(lrx,lry);
		border.lineTo(urx,ury);
		border.lineTo(ulx,uly);
		g2d.fill(border);	
		g2d.setPaint(Color.black);
		g2d.drawOval(x+17,y+Hand.back.getIconHeight()/3-h/2+7, w-15, h-15);
	}
	
	public void setBet(int bet) {
		this.bet = bet;
	}
	
	public int getIconWidth() { return 8; }
	
	public int getIconHeight() { return 8; }
}
