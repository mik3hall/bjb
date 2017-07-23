/*
 * @(#)RetroButtonUI.java
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class RetroButtonUI extends FilledButtonUI {
    
	public static ButtonShape RETRO_SHAPE = new ButtonShape(3, 10, 3, 10,
			3, Short.MAX_VALUE, 3, Short.MAX_VALUE);

	/** The <code>SimpleButtonFill</code> used to achieve the "Retro" look.
	 */
	public static final SimpleButtonFill RETRO_FILL = new SimpleButtonFill() {
		private float[] fillWeights = new float[] {0, .4999f, .5f, 1};
		protected Color borderColor = Color.gray;
		
		private Color[] normalColors = new Color[] {
				new Color(240, 240, 240),
				new Color(230, 230, 230),
				new Color(220, 220, 220),
				new Color(230, 230, 230) };
		
		private Color[] darkerColors = new Color[] {
				new Color(200, 200, 200),
				new Color(190, 190, 190),
				new Color(200, 200, 200),
				new Color(210, 210, 210) };

		@Override
		public Paint getDarkerFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("retro.darker", 
					fillRect.height, fillRect.y,
					fillWeights, darkerColors);
		}

		@Override
		public Paint getDarkestFill(Rectangle fillRect) {
			return null;
		}

		@Override
		public Paint getNormalFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("retro.normal", 
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
	
	private static RetroButtonUI retroButtonUI = new RetroButtonUI();

	/** This method has to exist in order for to make this UI the button
	 * default by calling:
	 * <br><code>UIManager.getDefaults().put("ButtonUI", "com.bric.plaf.RetroButtonUI");</code>
	 */
    public static ComponentUI createUI(JComponent c) {
        return retroButtonUI;
    }
	
	public RetroButtonUI() {
		super(RETRO_FILL,RETRO_SHAPE);
	}

	public int getFocusPainting() {
		return PAINT_FOCUS_INSIDE;
	}

	private static BasicStroke outline1 = new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private static BasicStroke outline2 = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private static BasicStroke outline3 = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	@Override
	public void paintBackground(Graphics2D g,ButtonInfo info) {
		super.paintBackground(g,info);
		
		if(info.button.isBorderPainted()) {
			g = (Graphics2D)g.create();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.clip( info.fill );
			g.setColor(new Color(0,0,0,15));
			g.setStroke(outline1);
			g.draw( info.fill );
			g.setStroke(outline2);
			g.draw( info.fill );
			g.setStroke(outline3);
			g.draw( info.fill );
			g.dispose();
		}
	}
	
	@Override
	public boolean isFillOpaque() {
		return true;
	}
}
