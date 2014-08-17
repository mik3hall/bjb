package com.bric.plaf;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.AbstractButton;

/** Information for rendering a button's background and border.
 */
public abstract class ButtonFill {

	/** If non-null, this is a 1-pixel highlight painted
	 * below this button.  This may vary with the button's
	 * state, but if it is non-null in one state it should
	 * always be non-null.
	 * <P>If this is non-null, this may affect the height of
	 * the button.  You can return a transparent color to
	 * "trick" the UI into giving an extra pixel here, if desired.
	 */
	public abstract Color getShadowHighlight(AbstractButton button);

	/** Returns the current border for a button. */
	public abstract Paint getBorder(AbstractButton button,Rectangle fillRect);
	
	/** Returns the current fill for a button. */
	public abstract Paint getFill(AbstractButton button,Rectangle fillRect);

	
	/** Tweens between the two arguments. */
	private static Color tween(Color c1,Color c2,float p) {
		int r1 = c1.getRed();
		int g1 = c1.getGreen();
		int b1 = c1.getBlue();
		int a1 = c1.getAlpha();
		
		int r2 = c2.getRed();
		int g2 = c2.getGreen();
		int b2 = c2.getBlue();
		int a2 = c2.getAlpha();
		
		return new Color( (int)(r1*(1-p)+r2*p),
				(int)(g1*(1-p)+g2*p),
				(int)(b1*(1-p)+b2*p),
				(int)(a1*(1-p)+a2*p) 
		);
	}
	
	/** The table used to store vertical gradients. */
	private static Hashtable verticalGradients;
	
	/** Create a vertical gradient.
	 * 
	 * @param name an identifying key for this gradient (used to cache it).
	 * @param height the height of the gradient
	 * @param y the y offset of the gradient
	 * @param positions the fractional positions of each color (between [0,1]).
	 * @param colors one color for each position.
	 * @return the vertical gradient.
	 */
	protected synchronized static Paint getVerticalGradient(String name,
			int height,int y,
			float[] positions,
			Color[] colors) {
		if(verticalGradients==null) {
			verticalGradients = new Hashtable();
		}
		
		String key = name+" "+height+" "+y;
		Paint paint = (Paint)verticalGradients.get(key);
		if(paint==null) {
			height = Math.max(height, 1); //before a component is laid out, it may be 0x0
			BufferedImage bi = new BufferedImage(1,height,BufferedImage.TYPE_INT_ARGB);
			int[] array = new int[height];
			for(int a = 0; a<array.length; a++) {
				float f = (float)a;
				f = f/((float)(array.length-1));
				boolean hit = false;
				findMatch : for(int b = 1; b<positions.length; b++) {
					if(f>=positions[b-1] && f<positions[b]) {
						float p = (f-positions[b-1])/(positions[b]-positions[b-1]);
						array[a] = tween(colors[b-1],colors[b],p).getRGB();
						hit = true;
						break findMatch;
					}
				}
				if(!hit)
					array[a] = colors[colors.length-1].getRGB();
			}
			bi.getRaster().setDataElements(0, 0, 1, height, array);
			paint = new TexturePaint( bi, new Rectangle(0,y,1,height) );
			verticalGradients.put(key,paint);
		}
		return paint;
	}
}
