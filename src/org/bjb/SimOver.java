package org.bjb;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SimOver extends JPanel implements ActionListener, Icon {

	Font font;
	FontMetrics fontMetrics;
	private boolean active = false;

	public SimOver() {
		font = getFont().deriveFont(36f);
		fontMetrics = getFontMetrics(font);
		setSize(getIconWidth(),getIconHeight());
		setPreferredSize(new Dimension(getIconWidth(),getIconHeight()));
		final SimOver so = this;
		addMouseListener(new java.awt.event.MouseAdapter() {
  			public void mouseEntered(java.awt.event.MouseEvent evt) { 
  				active = true;
				javax.swing.Timer t = new javax.swing.Timer(500,so);
				t.setRepeats(false);
				t.start();	
  			}
			public void mouseExited(java.awt.event.MouseEvent evt) { 
				active = false; 
			}
		});
	}
	
	public void actionPerformed(ActionEvent ae) {
		if (active) {
			Simulation sim = new Simulation(new JFrame(""),true);
			sim.setVisible(true);		
		}
	}
	
	protected void paintComponent(Graphics g) {
		paintIcon(this,g,0,0);
	}
	
	public void paintIcon( Component c, Graphics g, int x, int y ) {
		g.translate( x, y );
		Graphics2D g2d = (Graphics2D)g;
		g.setColor(Color.white);
		g.fillRect(0,0,getIconWidth(),getIconHeight());
		g2d.setFont(font);
	    GradientPaint gp = new GradientPaint(
	                            30.0f, 50.0f,
	                            PlayingArea.lightGreen,
	                            fontMetrics.stringWidth("Sim"),
	                            fontMetrics.getHeight(),
	                            BlackJackApp.teal);             
	    g2d.setPaint(gp);
	    g2d.drawString("Sim",3,41);         
		g.setColor(Color.green);
		g.fillRect(0,0,getIconWidth(),5);
		g.fillRect(0,getIconHeight()-5, getIconWidth(),getIconHeight());
	}
	
	public int getIconWidth() { return 71; }
	
	public int getIconHeight() { return 64; }
}
