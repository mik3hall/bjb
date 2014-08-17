package com.bric.plaf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;

/** This resembles the "bevel" button UI as seen in Mac OS 10.5.
 * <P>It is not intended to be an exact replica, but it is very similar.
 * <P>According to <a href="http://nadeausoftware.com/node/87">this</a> article, the "bevel" look
 * is often used for:
 * <BR>"Buttons with icons, or buttons sized larger than a standard Mac button".
 */
public class BevelButtonUI extends FilledButtonUI {
	
	public BevelButtonUI() {
		super(BEVEL_FILL, 4, 4);
	}
	
	public void paintBackground(Graphics2D g,ButtonInfo info) {
		if(info.button.isBorderPainted()) {
			int verticalPosition = getVerticalPosition(info.button);
			//this little clause gives a much-needed boost to the aqua "bevel" look:
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if(verticalPosition==POS_ONLY) {
				g.setPaint(new GradientPaint(0, 0, new Color(0,0,0,25),
						0, info.fillBounds.height, new Color(0,0,0,80)));
			} else {
				g.setPaint(new Color(0,0,0,35));
			}
			BasicStroke stroke = new BasicStroke(2.5f);
			g.translate(0, 1);
			//yeah, strangely necessary
			g.fill( stroke.createStrokedShape(info.fill) );
			g.translate(0, -1);
		}
		super.paintBackground(g,info);
	}
	
	protected int getPreferredHeight() {
		return 18;
	}
	
	/** The <code>SimpleButtonFill</code> used to achieve the "bevel" look.
	 */
	public static final SimpleButtonFill BEVEL_FILL = new SimpleButtonFill() {
		private float[] fillWeights = new float[] {0, .15f, .151f, .8f, 1};
		private float[] borderWeights = new float[] {0,.2f, .8f, 1};
		private Color[] darkerColors = new Color[] {
				new Color(0xffDCDCDC),
				new Color(0xffDCDCDC),
				new Color(0xffD0D0D0),
				new Color(0xffD0D0D0),
				new Color(0xffE4E4E4)
		};
		private Color[] borderColors = new Color[] {
				new Color(0xffAFAFAF),
				new Color(0xff838383),
				new Color(0xff838383),
				new Color(0xff6C6C6C)
		};
		private Color[] normalColors = new Color[] {
				new Color(0xffFFFFFF),
				new Color(0xffFAFAFA),
				new Color(0xffEEEEEE),
				new Color(0xffEEEEEE),
				new Color(0xffFFFFFF)
		};
		private Color[] darkestColors = new Color[] {
				new Color(0xffCFCFCF),
				new Color(0xffCFCFCF),
				new Color(0xffBABABA),
				new Color(0xffBABABA),
				new Color(0xffCDCDCD)
		};

		protected Paint getDarkerFill(Rectangle fillRect) {
			return getVerticalGradient("bevelUI.darker", 
					fillRect.height, fillRect.y,
					fillWeights, darkerColors);
		}

		protected Paint getDarkestFill(Rectangle fillRect) {
			return getVerticalGradient("bevelUI.darkest", 
					fillRect.height, fillRect.y,
					fillWeights, darkestColors);
		}

		protected Paint getNormalFill(Rectangle fillRect) {
			return getVerticalGradient("bevelUI.normal", 
					fillRect.height, fillRect.y,
					fillWeights, normalColors);
		}

		protected Paint getRolloverFill(Rectangle fillRect) {
			return null;
		}

		public Paint getBorder(AbstractButton button, Rectangle fillRect) {
			return getVerticalGradient("bevelUI.border", 
					fillRect.height+1, fillRect.y,
					borderWeights, borderColors);
		}

		public Color getShadowHighlight(AbstractButton button) {
			return null;
		}
	};
	
	public boolean isFillOpaque() {
		return true;
	}
}