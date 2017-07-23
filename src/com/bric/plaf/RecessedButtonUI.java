/*
 * @(#)RecessedButtonUI.java
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

/** This resembles the "recessed" button UI as seen in Mac OS 10.5.
 * <P>It is not intended to be an exact replica, but it is very similar.
 * <P>According to <a href="http://nadeausoftware.com/node/87">this</a> article, the "recessed" and "roundRect" look
 * is originally intended to indicate:
 * <br>"to indicate a choice in limiting the scope of an operation, such as the buttons at the top of a Finder when searching."
 * 
 */
public class RecessedButtonUI extends FilledButtonUI {
	
	public static final ButtonShape RECESSED_SHAPE = new ButtonShape(8,12);

	/** The <code>SimpleButtonFill</code> used to achieve the "recessed" look.
	 */
	public static final SimpleButtonFill RECESSED_FILL = new SimpleButtonFill() {
		private float[] fillWeights = new float[] {0, .14f, 1};
		private Color borderColor = new Color(0xff6F6F6F);;
		
		private Color[] normalColors = new Color[] {
				new Color(0x97000000,true),
				new Color(0x58000000,true),
				new Color(0x58000000,true)
		};
		private Color[] darkerColors = new Color[] {
				new Color(0x98000000,true),
				new Color(0x80000000,true),
				new Color(0x80000000,true)
		};

		@Override
		public Paint getDarkerFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("recessedUI.darker", 
					fillRect.height, fillRect.y,
					fillWeights, darkerColors);
		}

		@Override
		public Paint getDarkestFill(Rectangle fillRect) {
			return null;
		}

		@Override
		public Paint getNormalFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("recessedUI.normal", 
					fillRect.height, fillRect.y,
					fillWeights, normalColors);
		}

		@Override
		public Paint getRolloverFill(Rectangle fillRect) {
			return null;
		}

		private float[] borderWeights = new float[] {0, .5f, 1};
		private Color[] borderColors = new Color[] {
				new Color(0xff5F5F5F),
				new Color(0xff979797),
				new Color(0xff979797)
		};
		
		@Override
		public Paint getBorder(AbstractButton button, Rectangle fillRect) {
			int verticalPosition = getVerticalPosition(button);
			if(verticalPosition==POS_ONLY) {
				return PlafPaintUtils.getVerticalGradient("recessedUI.border",
						fillRect.height+2, fillRect.y,
						borderWeights,borderColors);
			}
			return borderColor;
		}
	};

	private static RecessedButtonUI recessedButtonUI = new RecessedButtonUI();
	
	/** This method has to exist in order for to make this UI the button
	 * default by calling:
	 * <br><code>UIManager.getDefaults().put("ButtonUI", "com.bric.plaf.RecessedButtonUI");</code>
	 */
    public static ComponentUI createUI(JComponent c) {
        return recessedButtonUI;
    }
	
	public RecessedButtonUI() {
		super(RECESSED_FILL,RECESSED_SHAPE);
	}

	@Override
	public void paintBorder(Graphics2D g,ButtonInfo info) {
		super.paintBorder(g,info);
		
		int verticalPosition = getVerticalPosition(info.button);
		int horizontalPosition = getVerticalPosition(info.button);
		if(verticalPosition==POS_ONLY && 
				(horizontalPosition==POS_RIGHT || horizontalPosition==POS_MIDDLE)) {
			g.setColor( new Color(0xff6F6F6F) );
			g.drawLine(0, info.fillBounds.y+1, 
					0, info.fillBounds.y+info.fillBounds.height-1);
		}
	}
	
	/** Returns false.  This button is designed to be translucent. */
	@Override
	public boolean isFillOpaque() {
		return false;
	}
};
