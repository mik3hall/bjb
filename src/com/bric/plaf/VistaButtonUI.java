/*
 * @(#)VistaButtonUI.java
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

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/** A <code>ButtonUI</code> that resembles buttons in Vista.
 * <P>As of this writing this UI does not perform animated
 * fades, but is otherwise a reasonable replica.
 */
public class VistaButtonUI extends FilledButtonUI {

	private static final SimpleButtonFill VISTA_FILL = new SimpleButtonFill() {
		final float[] weights = new float[] {0, .35f, .49999f, .5f, 1};
		final Color[] normalColors = new Color[] {
				Color.white,
				Color.white,
				new Color(235, 235, 235),
				new Color(221, 221, 221),
				new Color(207, 207, 207)
		};
		final Color[] rolloverColors = new Color[] {
				new Color(234, 246, 253),
				new Color(234, 246, 253),
				new Color(217, 240, 252),
				new Color(190, 230, 252),
				new Color(167, 217, 245)
		};
		final Color[] pressedColors = new Color[] {
				new Color(229, 244, 252),
				new Color(211, 236, 249),
				new Color(196, 229, 246),
				new Color(152, 209, 239),
				new Color(104, 179, 219)
		};
		Color normalBorderColor = new Color(112, 112, 112);
		Color focusedBorderColor = new Color(60, 127, 177);
		Color pressedBorderColor = new Color(44, 98, 139);

		@Override
		public Paint getDarkerFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("vista.darker", 
					fillRect.height, 
					fillRect.y, 
					weights, pressedColors );
		}

		@Override
		public Paint getDarkestFill(Rectangle fillRect) {
			return null;
		}

		@Override
		public Paint getNormalFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("vista.normal", 
					fillRect.height, 
					fillRect.y, 
					weights, normalColors );
		}

		@Override
		public Paint getRolloverFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("vista.rollover", 
					fillRect.height, 
					fillRect.y, 
					weights, rolloverColors );
		}

		@Override
		public Paint getBorder(AbstractButton button, Rectangle fillRect) {
			if(button.getModel().isPressed() || button.getModel().isSelected() ||
					isSpacebarPressed(button) )
				return pressedBorderColor;
			
			if(button.hasFocus())
				return focusedBorderColor;
			
			return normalBorderColor;
		}
		
	};
	
	public static final ButtonShape VISTA_SHAPE = new ButtonShape(2,2);

	private static VistaButtonUI vistaButtonUI = new VistaButtonUI();
	
	/** This method has to exist in order for to make this UI the button
	 * default by calling:
	 * <br><code>UIManager.getDefaults().put("ButtonUI", "com.bric.plaf.VistaButtonUI");</code>
	 */
    public static ComponentUI createUI(JComponent c) {
        return vistaButtonUI;
    }
	
	public VistaButtonUI() {
		super(VISTA_FILL, VISTA_SHAPE);
	}

	@Override
	public int getFocusPainting(AbstractButton button) {
		return PAINT_NO_FOCUS;
	}


	private final static Color focusedHighlight = new Color(66, 209, 245);
	private final static Color normalHighlight = new Color(255, 255, 255, 220);
	private final static Color darkHighlight = new Color(0, 0, 0, 60);
	
	@Override
	public void paintBorder(Graphics2D g, ButtonInfo info) {
		if(info.button.isBorderPainted()) {
			Graphics2D clippedG = (Graphics2D)g.create();
			clippedG.clip( info.fill );
			clippedG.setStroke( new BasicStroke(3) );
			if(info.button.getModel().isSelected() || info.button.getModel().isPressed() ||
					isSpacebarPressed(info.button)) {
				clippedG.setColor( darkHighlight );
			} else if(info.button.hasFocus() && info.button.isFocusPainted()) {
				clippedG.setColor( focusedHighlight );
			} else {
				clippedG.setColor( normalHighlight );
			}
			clippedG.draw( info.fill );
			clippedG.dispose();
		}
		super.paintBorder(g, info);
		
	}

	@Override
	public boolean isFillOpaque() {
		return true;
	}
}
