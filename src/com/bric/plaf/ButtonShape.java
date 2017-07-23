/*
 * @(#)ButtonShape.java
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

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import com.bric.geom.RectangularTransform;
import com.bric.geom.ShapeBounds;

/** A mechanism to control the shape of buttons with rounded edges. */
public class ButtonShape implements PositionConstants {
	protected final int maxTopRightRadius, maxTopLeftRadius, maxBottomLeftRadius, maxBottomRightRadius;
	protected final int prefTopRightRadius, prefTopLeftRadius, prefBottomLeftRadius, prefBottomRightRadius;
	
	/** Create a new <code>ButtonShape</code>.
	 * 
	 * @param maxRadius the max radius for corners.  If the corners should always
	 * be rounded, then this should be a very large number.
	 * @param preferredRadius the preferred radius for corners.  This may define the
	 * preferred height of a button.
	 */
	public ButtonShape(int preferredRadius,int maxRadius) {
		this(preferredRadius, preferredRadius, preferredRadius, preferredRadius, 
				maxRadius, maxRadius, maxRadius, maxRadius);
	}
	
	/** This constructor gives you control over each corner of the
	 * button.  Most of the time you can probably use the other (simpler)
	 * constructor, unless you need this level of control.
	 * 
	 */
	public ButtonShape(int prefTopRightRadius, int prefTopLeftRadius,
			int prefBottomLeftRadius, int prefBottomRightRadius,
			int maxTopRightRadius, int maxTopLeftRadius,
			int maxBottomLeftRadius, int maxBottomRightRadius) {
		this.maxTopRightRadius = maxTopRightRadius;
		this.maxTopLeftRadius = maxTopLeftRadius;
		this.maxBottomLeftRadius = maxBottomLeftRadius;
		this.maxBottomRightRadius = maxBottomRightRadius;
		this.prefTopRightRadius = prefTopRightRadius;
		this.prefTopLeftRadius = prefTopLeftRadius;
		this.prefBottomLeftRadius = prefBottomLeftRadius;
		this.prefBottomRightRadius = prefBottomRightRadius;
	}
	
	/** Returns the preferred size of a button should have.
	 * 
	 * @param d an optional Dimension to store the results in.
	 * @param contentWidth the width of the innards of this button.
	 * @param contentHeight the height of the innards of this button.
	 * @param padding the padding between the contents and the border.
	 * @param customShape if non-null, then this shape is scaled to fit the
	 * rectangle containing the contents and the padding.  This is how
	 * you apply a custom button shape (like a circle, diamond, arrow, etc.)
	 * @return the preferred size of this method.
	 */
	public Dimension getPreferredSize(Dimension d,int contentWidth,int contentHeight,Insets padding,Shape customShape) {
		if(d==null) d = new Dimension();
		
		if(customShape==null) {
			int leftSide = Math.max(prefTopLeftRadius,prefBottomLeftRadius);
			int rightSide = Math.max(prefTopRightRadius,prefBottomRightRadius);
	
			d.width = contentWidth+leftSide+rightSide+padding.left+padding.right;
			d.height = contentHeight+padding.top+padding.bottom;
		} else {
			GeneralPath resizedShape = findShapeToFitRectangle(customShape, contentWidth, contentHeight);
			Rectangle2D bounds = ShapeBounds.getBounds(resizedShape);
			d.width = (int)( bounds.getWidth()+padding.left+padding.right+.99999);
			d.height = (int)( bounds.getHeight()+padding.top+padding.bottom+.99999);
		}
		
		return d;
	}

	private static GeneralPath findShapeToFitRectangle(Shape originalShape,int w,int h) {
		GeneralPath newShape = new GeneralPath();
		Rectangle2D rect = new Rectangle2D.Float();
		ShapeBounds.getBounds(originalShape,rect);
		if(originalShape.contains(rect.getX()+rect.getWidth()/2, rect.getY()+rect.getHeight()/2)==false)
			throw new IllegalArgumentException("This custom shape is not allowed.  The center of this shape must be inside the shape.");
		double scale = Math.min( (w)/rect.getWidth(), (h)/rect.getHeight() );
		AffineTransform transform = new AffineTransform();
		while(true) {
			newShape.reset();
			newShape.append(originalShape, true);
			transform.setToScale(scale, scale);
			newShape.transform(transform);
			ShapeBounds.getBounds(newShape, rect);
			
			if(newShape.contains(
					rect.getX()+rect.getWidth()/2-w/2,
					rect.getY()+rect.getHeight()/2-h/2,
					w, h
					)) {
				return newShape;
			}
			
			scale += .01;
		}
	}

	/** Calculates the shape of this button, given certain constraints.
	 * 
	 * @param path an optional destination for the outline.
	 * @param width the width to stretch this fill to.
	 * @param height the height to stretch this fill to.
	 */
	public GeneralPath getShape(GeneralPath path,
			int width,int height) {
		return getShape(path, width, height, POS_ONLY, POS_ONLY, null);
	}

	/** Calculates the shape of this button, given certain constraints.
	 * Note if this is smaller than the preferred size then, well,
	 * the results may look bad.
	 * 
	 * @param path an optional destination for the outline.
	 * @param width the width to stretch this fill to.
	 * @param height the height to stretch this fill to.
	 * @param horizontalPosition POS_ONLY, POS_LEFT, POS_RIGHT or POS_MIDDLE
	 * @param verticalPosition POS_ONLY, POS_TOP, POS_BOTTOM or POS_MIDDLE
	 * @param customShape the special shape this button should take.
	 * This may be a circle, diamond, arrow, etc.
	 * @return the shape of this button, given these constraints.
	 */
	public GeneralPath getShape(GeneralPath path,
			int width,int height,
			int horizontalPosition,int verticalPosition,
			Shape customShape) {
		
		if(path==null) path = new GeneralPath();

		if(horizontalPosition==POS_RIGHT || 
				horizontalPosition==POS_ONLY) {
			width--;
		}
		if(verticalPosition==POS_BOTTOM || 
				verticalPosition==POS_ONLY) {
			height--;
		}

		path.reset();

		//define the actual fill and border of the shape:
		//always fill the entire width/height we are allowed
		//(knowing that the width/height fields here are already
		//reduced to compensate for focus rings).
		//The size of this button is the responsibility of the
		//LayoutManager, and the preferred size is calculated
		//in getPreferredSize().  Here we just work with what
		//we're given.
		if(customShape!=null) {
			Rectangle2D originalBounds = new Rectangle2D.Float();
			ShapeBounds.getBounds(customShape,originalBounds);
			Rectangle2D newBounds = new Rectangle2D.Float(0,0,width,height);
			AffineTransform transform = RectangularTransform.create(originalBounds,newBounds);
			path.append(customShape, true);
			path.closePath();
			path.transform(transform);
		} else {
			int minR = Math.min(height/2, width/2);
	
			int topRightRadius = Math.min(maxTopRightRadius,minR);
			int topLeftRadius = Math.min(maxTopLeftRadius,minR);
			int bottomRightRadius = Math.min(maxBottomRightRadius,minR);
			int bottomLeftRadius = Math.min(maxBottomLeftRadius,minR);
	
			float k = .22385763f*2;
	
			//this is based on a 4x4 grid enumerating all the possible combinations:
			if(verticalPosition==POS_TOP && 
					horizontalPosition==POS_LEFT) {
				path.moveTo(width, 0);
				if(topLeftRadius==0) {
					path.lineTo(0, 0);
				} else {
					path.lineTo(topLeftRadius, 0);
					path.curveTo(topLeftRadius-topLeftRadius*k, 0, 
							0, topLeftRadius-topLeftRadius*k, 
							0, topLeftRadius);
				}
				path.lineTo(0, height);
				path.lineTo(width, height);
				path.lineTo(width,0);
			} else if( (verticalPosition==POS_TOP && 
							horizontalPosition==POS_MIDDLE) ||
					(verticalPosition==POS_MIDDLE && 
							horizontalPosition==POS_LEFT) ||
					(verticalPosition==POS_MIDDLE && 
							horizontalPosition==POS_MIDDLE)) {
				path.moveTo(width, 0);
				path.lineTo(0, 0);
				path.lineTo(0, height);
				path.lineTo(width, height);
				path.lineTo(width,0);
			} else if(verticalPosition==POS_TOP && 
					horizontalPosition==POS_RIGHT) {
				path.moveTo(width, height);
				if(topRightRadius==0) {
					path.lineTo(width,0);
				} else {
					path.lineTo(width, topRightRadius);
					path.curveTo(width, topRightRadius-topRightRadius*k, 
							width-topRightRadius+topRightRadius*k, 0, 
							width-topRightRadius, 0);
				}
				path.lineTo(0, 0);
				path.lineTo(0, height);
				path.lineTo(width, height);
			} else if(verticalPosition==POS_TOP && 
					horizontalPosition==POS_ONLY) {
				path.moveTo(width, height);
				if(topRightRadius==0) {
					path.lineTo(width, 0);
				} else {
					path.lineTo(width, topRightRadius);
					path.curveTo(width, topRightRadius-topRightRadius*k, 
							width-topRightRadius+topRightRadius*k, 0, 
							width-topRightRadius, 0);
				}
				if(topLeftRadius==0) {
					path.lineTo(0, 0);
				} else {
					path.lineTo(topLeftRadius, 0);
					path.curveTo(topLeftRadius-topLeftRadius*k, 0, 
							0, topLeftRadius-topLeftRadius*k, 
							0, topLeftRadius);
				}
				path.lineTo(0, height);
				path.lineTo(width, height);
	
				bottomRightRadius = 0;
				bottomLeftRadius = 0;
			} else if(verticalPosition==POS_MIDDLE) {
				path.moveTo(width,height);
				path.lineTo(width, 0);
				path.lineTo(0, 0);
				path.lineTo(0, height);
				path.lineTo(width, height);
			} else if(verticalPosition==POS_BOTTOM && 
					horizontalPosition==POS_LEFT) {
				path.moveTo(width, 0);
				path.lineTo(0, 0);
				if(bottomLeftRadius==0) {
					path.lineTo(0,height);
				} else {
					path.lineTo(0, height-bottomLeftRadius);
					path.curveTo(0, height-bottomLeftRadius+bottomLeftRadius*k,
							bottomLeftRadius-bottomLeftRadius*k, height,
							bottomLeftRadius, height);
				}
				path.lineTo(width, height);
				path.lineTo(width, 0);
			} else if(horizontalPosition==POS_MIDDLE) {
				path.moveTo(width, 0);
				path.lineTo(0, 0);
				path.lineTo(0, height);
				path.lineTo(width, height);
				path.lineTo(width, 0);
			} else if(horizontalPosition==POS_RIGHT && 
					verticalPosition==POS_BOTTOM) {
				path.moveTo(width, 0);
				path.lineTo(0, 0);
				path.lineTo(0, height);
				if(bottomRightRadius==0) {
					path.lineTo(width, height);
				} else {
					path.lineTo(width-bottomRightRadius, height);
					path.curveTo(width-bottomRightRadius+bottomRightRadius*k, height, 
							width, height-bottomRightRadius+bottomRightRadius*k, 
							width, height-bottomRightRadius);
				}
				path.lineTo(width, 0);
				path.closePath();
			} else if(horizontalPosition==POS_ONLY && 
					verticalPosition==POS_BOTTOM) {
				path.moveTo(width, 0);
				path.lineTo(0, 0);
				if(bottomLeftRadius==0) {
					path.lineTo(0, height);
				} else {
					path.lineTo(0, height-bottomLeftRadius);
					path.curveTo(0, height-bottomLeftRadius+bottomLeftRadius*k,
							bottomLeftRadius-bottomLeftRadius*k, height,
							bottomLeftRadius, height);
				}
				if(bottomRightRadius==0) {
					path.lineTo(width, height);
				} else {
					path.lineTo(width-bottomRightRadius, height);
					path.curveTo(width-bottomRightRadius+bottomRightRadius*k, height, 
							width, height-bottomRightRadius+bottomRightRadius*k, 
							width, height-bottomRightRadius);
				}
				path.lineTo(width, 0);
				path.closePath();
			} else if(horizontalPosition==POS_LEFT && 
					verticalPosition==POS_ONLY) {
				path.moveTo(width, 0);
				if(topLeftRadius==0) {
					path.lineTo(0, 0);
				} else {
					path.lineTo(topLeftRadius, 0);
					path.curveTo(topLeftRadius-topLeftRadius*k, 0,
							0, topLeftRadius-topLeftRadius*k,
							0, topLeftRadius);
				}
				if(bottomLeftRadius==0) {
					path.lineTo(0, height);
				} else {
					path.lineTo(0, height-bottomLeftRadius);
					path.curveTo(0, height-bottomLeftRadius+bottomLeftRadius*k,
							bottomLeftRadius-bottomLeftRadius*k, height,
							bottomLeftRadius, height);
				}
				path.lineTo(width, height);
				path.lineTo(width, 0);
			} else if(verticalPosition==POS_ONLY && 
					horizontalPosition==POS_RIGHT) {
				path.moveTo(0,0);
				path.lineTo(0, height);
				if(bottomRightRadius==0) {
					path.lineTo(width, height);
				} else {
					path.lineTo(width-bottomRightRadius, height);
					path.curveTo(width-bottomRightRadius+bottomRightRadius*k, height,
							width, height-bottomRightRadius+bottomRightRadius*k,
							width, height-bottomRightRadius );
				}
				if(topRightRadius==0) {
					path.lineTo(width, 0);
				} else {
					path.lineTo(width, topRightRadius);
					path.curveTo(width,topRightRadius-topRightRadius*k,
							width-topRightRadius+topRightRadius*k, 0,
							width-topRightRadius, 0);
				}
				path.lineTo(0, 0);
				path.closePath();
			} else { //if(horiziontalPosition==ONLY && verticalPosition==ONLY)
				if(topLeftRadius==0) {
					path.moveTo(0, 0);
				} else {
					path.moveTo(topLeftRadius, 0);
					path.curveTo(topLeftRadius-topLeftRadius*k, 0,
							0, topLeftRadius-topLeftRadius*k,
							0, topLeftRadius);
				}
				if(bottomLeftRadius==0) {
					path.lineTo(0, height);
				} else {
					path.lineTo(0, height-bottomLeftRadius);
					path.curveTo( 0, height-bottomLeftRadius+bottomLeftRadius*k,
							bottomLeftRadius-bottomLeftRadius*k, height,
							bottomLeftRadius, height );
				}
				if(bottomRightRadius==0) {
					path.lineTo(width, height);
				} else {
					path.lineTo( width-bottomRightRadius, height);
					path.curveTo(width-bottomRightRadius+bottomRightRadius*k, height,
							width, height-bottomRightRadius+bottomRightRadius*k,
							width, height-bottomRightRadius );
				}
				if(topRightRadius==0) {
					path.lineTo(width, 0);
				} else {
					path.lineTo(width, topRightRadius);
					path.curveTo(width,topRightRadius-topRightRadius*k,
							width-topRightRadius+topRightRadius*k, 0,
							width-topRightRadius, 0);
				}
				path.lineTo(topLeftRadius, 0);
				path.closePath();
			}
			path.closePath();
		}
		return path;
	}
	
	
}
