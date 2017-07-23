package org.bjb;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class CardComponent extends JComponent {
	ImageIcon cardIcon = null;
	int ivalue = 0;
	
	public CardComponent(ImageIcon ico) {
		cardIcon = ico;
		Dimension d = new Dimension(cardIcon.getIconWidth(),cardIcon.getIconHeight());
		setSize(d);
		setMinimumSize(d);
		setMaximumSize(d);
		setPreferredSize(d);		
	}
	
	public CardComponent(String path) {
		cardIcon = Card.getIcon(path);
		Dimension d = new Dimension(cardIcon.getIconWidth(),cardIcon.getIconHeight());
		setSize(d);
		setMinimumSize(d);
		setMaximumSize(d);
		setPreferredSize(d);
	}
	
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		cardIcon.paintIcon(this, g, 0, 0);
	}
	
	public void setImageIcon(ImageIcon cardIcon) {
		this.cardIcon = cardIcon;
	}
	
	public void setValue(int ivalue) {
		this.ivalue = ivalue;
	}
}
