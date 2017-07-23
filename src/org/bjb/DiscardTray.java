package org.bjb;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JPanel;

public class DiscardTray extends JPanel implements Icon {

	protected void paintComponent(Graphics g) {
		paintIcon(this,g,0,0);
	}
	
	public void paintIcon( Component c, Graphics g, int x, int y ) {
		g.translate( x, y );
		Graphics2D g2d = (Graphics2D)g;
	}
	
	public int getIconWidth() { return 71; }
	
	public int getIconHeight() { return 64; }
}
