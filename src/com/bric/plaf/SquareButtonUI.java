/*
 * @(#)SquareButtonUI.java
 *
 * $Date: 2012-07-03 01:10:05 -0500 (Tue, 03 Jul 2012) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * http://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.bric.plaf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/** This resembles the "square" button UI as seen in Mac OS 10.5.
 * <P>It is not intended to be an exact replica, but it is very similar.
 * <P>According to <a href="http://nadeausoftware.com/node/87">this</a> article, the "square" look
 * is often used for:
 * <BR>"Buttons on a tool bar, or buttons smaller than a standard button".
 */
public class SquareButtonUI extends FilledButtonUI {
	
	public static final ButtonShape SQUARE_SHAPE = new ButtonShape(0,0);

	/** The <code>SimpleButtonFill</code> used to achieve the "Square" look.
	 */
	public static final SimpleButtonFill SQUARE_FILL = new SimpleButtonFill() {
		private float[] fillWeights = new float[] {0, .25f, .9f, 1};
		protected Color borderColor = new Color(0xff838383);
		
		private Color[] normalColors = new Color[] {
				new Color(0xFFF4F4F4),
				new Color(0xFFDDDDDD),
				new Color(0xFFF9F9F9),
				new Color(0xFFF9F9F9)
		};
		
		private Color[] darkestColors = new Color[] {
				new Color(0xFFB9B9B9),
				new Color(0xFF727272),
				new Color(0xFFCBCBCB),
				new Color(0xFFCBCBCB)
		};
		
		private Color[] darkerColors = new Color[] {
				new Color(0xFFBEBEBE),
				new Color(0xFF838383),
				new Color(0xFFDCDCDC),
				new Color(0xFFDCDCDC)
		};

		@Override
		public Paint getDarkerFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("square.darker", 
					fillRect.height, fillRect.y,
					fillWeights, darkerColors);
		}

		@Override
		public Paint getDarkestFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("square.darkest", 
					fillRect.height, fillRect.y,
					fillWeights, darkestColors);
		}

		@Override
		public Paint getNormalFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("square.normal", 
					fillRect.height, fillRect.y,
					fillWeights, normalColors);
		}

		@Override
		public Paint getRolloverFill(Rectangle fillRect) {
			return null;
		}
		
		@Override
		public Paint getBorder(AbstractButton button, Rectangle fillRect) {
			return borderColor;
		}
	};

	private static SquareButtonUI squareButtonUI = new SquareButtonUI();
	
	/** This method has to exist in order for to make this UI the button
	 * default by calling:
	 * <br><code>UIManager.getDefaults().put("ButtonUI", "com.bric.plaf.SquareButtonUI");</code>
	 */
    public static ComponentUI createUI(JComponent c) {
        return squareButtonUI;
    }
    
	public SquareButtonUI() {
		super(SQUARE_FILL, SQUARE_SHAPE);
	}
	
	private static Color shadow = new Color(0,0,0,20);
	@Override
	public void paintBackground(Graphics2D g,ButtonInfo info) {
		super.paintBackground(g, info);
		if(info.button.getClientProperty(SHAPE)==null) {
			g.setColor(shadow);
			if(info.button.isContentAreaFilled() &&
					info.button.isBorderPainted()) {
				g.drawRect(info.fillBounds.x+1, info.fillBounds.y+2, 
						info.fillBounds.width-2, info.fillBounds.height-2);
			} else if(info.button.isContentAreaFilled()) {
				g.drawRect(info.fillBounds.x, info.fillBounds.y, 
						info.fillBounds.width, info.fillBounds.height);
			}
		}
	}
	
	@Override
	public boolean isFillOpaque() {
		return true;
	}
};
