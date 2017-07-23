package org.bjb;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
import javax.swing.JPanel;

public class TipsOver extends JPanel implements ActionListener, Icon {

	public TipsOver() {
		
	}
	
	public void actionPerformed(ActionEvent ae) {
	}
	
	public void paintIcon( Component c, Graphics g, int x, int y ) {
		g.translate( x, y );
		Graphics2D g2d = (Graphics2D)g;
		g.setColor(Color.white);
		g.fillRect(0,0,getIconWidth(),getIconHeight()); 
	    g2d.setPaint(Color.green);

	}
	
	public int getIconWidth() { return 71; }
	
	public int getIconHeight() { return 64; }
}
