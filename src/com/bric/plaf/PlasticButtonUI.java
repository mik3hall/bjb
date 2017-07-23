/*
 * @(#)PlasticButtonUI.java
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
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/** This is ButtonUI is very loosely based on the
 * buttons in GarageBand.  They are translucent,
 * with a simple gradient from top to bottom.
 * The bottom half is coated with a light 
 * shadow that curves upwards on the far
 * left/right sides for a nice curved effect.
 * <P>When pressed or armed a thick ring
 * appears inside the button.
 * 
 *
 */
public class PlasticButtonUI extends FilledButtonUI {

	public static final SimpleButtonFill PLASTIC_FILL = new SimpleButtonFill() {
		private Color borderColor = new Color(0xff333333);
		private float[] fillWeights = new float[] {0, 1};
		private Color[] normalColors = new Color[] {
				new Color(0x00000000,true),
				new Color(0x22000000,true)
		};
		private Color[] darkestColors = new Color[] {
				new Color(0x22000000,true),
				new Color(0x44000000,true)
		};

		@Override
		public Paint getDarkerFill(Rectangle fillRect) {
			return null;
		}

		@Override
		public Paint getDarkestFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("plasticUI.darkest", 
					fillRect.height, fillRect.y,
					fillWeights, darkestColors);
		}

		@Override
		public Paint getNormalFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("plasticUI.normal", 
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
	
	final Color fill;
	public static final ButtonShape PLASTIC_SHAPE = new ButtonShape(3,3);

	private static PlasticButtonUI plasticButtonUI = new PlasticButtonUI();
	
	/** This method has to exist in order for to make this UI the button
	 * default by calling:
	 * <br><code>UIManager.getDefaults().put("ButtonUI", "com.bric.plaf.PlasticButtonUI");</code>
	 */
    public static ComponentUI createUI(JComponent c) {
        return plasticButtonUI;
    }
	
	public PlasticButtonUI() {
		this(new Color(0,0,0,40));
	}
	
	public PlasticButtonUI(Color fill) {
		super(PLASTIC_FILL, PLASTIC_SHAPE);
		this.fill = fill;
	}
	
	private static GeneralPath shadow = new GeneralPath();
	private static Stroke[] outlines;
	
	@Override
	public void paintBackground(Graphics2D g, ButtonInfo info) {
		if(info.button.isContentAreaFilled()) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(fill);
			g.fill(info.fill);
		}
		super.paintBackground(g, info);
		
		if(info.button.isContentAreaFilled()) {
			g = (Graphics2D)g.create();
			g.clip(info.fill);
			int horizontalPosition = getHorizontalPosition(info.button);
			
			int midY = info.fillBounds.y+info.fillBounds.height/2;
			int k = Math.min(info.fillBounds.height/2, 15);
			k = Math.min(info.fillBounds.width/2, k);
			
			synchronized(shadow) {
				shadow.reset();
				if(horizontalPosition==POS_LEFT || horizontalPosition==POS_ONLY) {
					shadow.moveTo(info.fillBounds.x+1, midY-k);
					shadow.curveTo(info.fillBounds.x+1,midY,
							info.fillBounds.x+1,midY,
							info.fillBounds.x+k+1,midY);
				} else {
					shadow.moveTo(info.fillBounds.x+1, midY);
				}
				if(horizontalPosition==POS_RIGHT || horizontalPosition==POS_ONLY) {
					shadow.lineTo(info.fillBounds.x+info.fillBounds.width-k, midY);
		
					shadow.curveTo(info.fillBounds.x+info.fillBounds.width,midY,
							info.fillBounds.x+info.fillBounds.width,midY,
							info.fillBounds.x+info.fillBounds.width,midY-k);
				} else {
					shadow.lineTo(info.fillBounds.x+info.fillBounds.width, midY);
				}
				
				shadow.lineTo(info.fillBounds.x+info.fillBounds.width,
						info.fillBounds.y+info.fillBounds.height);
				shadow.lineTo(info.fillBounds.x+1,
						info.fillBounds.y+info.fillBounds.height);
				shadow.closePath();
				
				g.setColor(new Color(0,0,0,30));
				g.fill(shadow);
			}
		}
		//this button only knows 1 "darkened" state:
		if(info.button.isSelected() || info.button.getModel().isArmed() ||
				info.button.getModel().isPressed() || isSpacebarPressed(info.button)) {
			g.setColor(new Color(0,0,0,10));
			
			if(outlines==null) {
				outlines = new Stroke[10];
				for(int a = 0; a<outlines.length; a++) {
					outlines[a] = new BasicStroke(2*(a+1),BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
				}
			}
			for(int a = 0; a<outlines.length; a++) {
				g.setStroke(outlines[a]);
				g.draw( info.fill );
			}
		}
	}


	/** Returns false.  This button is designed to be translucent. */
	@Override
	public boolean isFillOpaque() {
		return false;
	}
}
