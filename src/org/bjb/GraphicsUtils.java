package org.bjb;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

public class GraphicsUtils {

	   public static void paint3DBorder(Graphics2D g, Color c1, Color c2, Shape shape)
	    {
	    	// starting with a double-width stroke, use an algorithm that stays closer to the
	    	// middle color at first and then increasingly approaches outside (lighter or darker)
	    	// towards the end to simulate the light increasing or fading at the edges
	    	// (the image will clip any content outside of its bounds, so half of each stroke is lost)
	    	int borderWidth = 4;
	    	int width = borderWidth * 2;
//	    	Color color = PlayingArea.lightGreen;
	    	for (float i = 0; i <= width; i += 1)
	    	{
	    		float percent = (float) Math.pow((i / width), 3);
	    		g.setPaint(combineColors(c1, c2, percent));
	    		g.setStroke(new BasicStroke(width - i));
	    		g.draw(shape);
	    	}
	    }

	    /**
	    * Averages the red, green, blue and alpha of two colors
	    */
	    private static Color combineColors(Color c1, Color c2, float weight)
	    {
	    	float r = c1.getRed() * weight + c2.getRed() * (1 - weight);
	    	float g = c1.getGreen() * weight + c2.getGreen() * (1 - weight);
	    	float b = c1.getBlue() * weight + c2.getBlue() * (1 - weight);
	    	float a = c1.getAlpha() * weight + c2.getAlpha() * (1 - weight);
	    	return new Color((int) r, (int) g, (int) b, (int) a);
	    }
	    
	    public static void hiliteHands(Graphics2D g2d,Player p,Color c1,Color c2) {
	  		int maxCards = 0;
	  		for (int i=0;i<p.hands.length;i++) {
	  			if (p.hands[i] != null && p.hands[i].getNumCards() > maxCards) maxCards = p.hands[i].getNumCards();
	  			else if (p.hands[i] == null) break;
	  		}
	  		g2d.setStroke(new BasicStroke(4.0f));
	  		RoundRectangle2D.Float roundedRectangle = new RoundRectangle2D.Float(p.hands[0].x-5, p.hands[0].y-2, Hand.back.getIconWidth()+10, Hand.back.getIconHeight()+(Hand.back.getIconHeight()/3*maxCards)+5, 15, 15);
    		paint3DBorder(g2d,c1,c2,roundedRectangle);	  		
	    }
}
