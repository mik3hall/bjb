package com.bric.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

import com.bric.math.MathG;

/** This can identify if a shape is a Rectangle, Rectangle2D or other.
 * <P>If a shape is a rectangle, then certain operations can be
 * optimized.
 * <P>Also there is a bug when clipping shapes using Quartz on
 * Mac: a GeneralPath encapsulating exactly the same area
 * as a Rectangle2D may not clip correctly.  If this abstract
 * shape is instead converted to a Rectangle2D: the bug
 * goes away!
 */
public class RectangleReader {
	/** This studies a shape and determines if it is
	 * a <code>Rectangle</code>, a <code>Rectangle2D</code>,
	 * or neither.
	 * 
	 * @param shape the shape to study
	 * @return a <code>Rectangle</code>, <code>Rectangle2D</code>,
	 * or <code>null</code>.
	 */
	public static final Rectangle2D convert(Shape shape) {
		if(shape==null)
			return null;
		
		if(shape instanceof Rectangle)
			return (Rectangle)shape;
		
		if(shape instanceof Rectangle2D) {
			Rectangle2D rect = (Rectangle2D)shape;
			return getRectangle( rect );
		}
				
		/* Lots of ways we could approach this...
		 * This is a straight-forward logical approach that
		 * could probably stand to be improved performance-
		 * wise:
		 * 1.  Get the bounds of the shape.
		 * 2.  Iterate over the shape a second time, and see if
		 * all points are collinear with the bounds.
		 * 
		 */
		
		double[] data = new double[6];
		
		int k;

		double lastX = 0;
		double lastY = 0;
		
		PathIterator i = shape.getPathIterator(null);
		
		double left = 0;
		double right = 0;
		double top = 0;
		double bottom = 0;
		boolean defined = false;

		while(i.isDone()==false) {
			k = i.currentSegment(data);
			k = SimplifiedPathIterator.simplify(k, lastX, lastY, data);
			if(k==PathIterator.SEG_MOVETO) {
				lastX = data[0];
				lastY = data[1];
				//multiple paths are a deal-breaker
				if(defined)
					return null;
			} else if(k==PathIterator.SEG_CLOSE) {
				//do nothing
			} else if(k==PathIterator.SEG_LINETO) {
				if(defined==false) {
					left = right = lastX;
					top = bottom = lastY;
					defined = true;
				} else {
					if(lastX<left) left = lastX;
					if(lastY<top) top = lastY;
					if(lastX>right) right = lastX;
					if(lastY>bottom) bottom = lastY;
				}

				if(data[0]<left) left = data[0];
				if(data[1]<top) top = data[1];
				if(data[0]>right) right = data[0];
				if(data[1]>bottom) bottom = data[1];
				lastX = data[0];
				lastY = data[1];
			} else {
				return null;
			}
			i.next();
		}

		if(defined==false)
			return null;
		
		i = shape.getPathIterator(null);
		
		double moveX = 0;
		double moveY = 0;
		
		double x1 = 0;
		double y1 = 0;
		double x2 = 0;
		double y2 = 0;

		while(i.isDone()==false) {
			k = i.currentSegment(data);
			k = SimplifiedPathIterator.simplify(k, lastX, lastY, data);
			boolean checkLine = false;
			if(k==PathIterator.SEG_MOVETO) {
				lastX = data[0];
				lastY = data[1];
				moveX = lastX;
				moveY = lastY;
			} else if(k==PathIterator.SEG_CLOSE) {
				x1 = lastX;
				y1 = lastY;
				x2 = moveX;
				y2 = moveY;
				checkLine = true;
			} else if(k==PathIterator.SEG_LINETO) {
				x1 = lastX;
				y1 = lastY;
				x2 = data[0];
				y2 = data[1];
				checkLine = true;
				
				lastX = data[0];
				lastY = data[1];
			}
			
			if(checkLine) {
				if(SimplifiedPathIterator.collinear(x1, y1, x2, y2, left, top)==false &&
						SimplifiedPathIterator.collinear(x1, y1, x2, y2, left+right, top+bottom)==false) {
					return null;
				}
			}
			
			i.next();
		}
		
		return getRectangle(left,top,right-left,bottom-top);
	}
	
	private static final double TOL = .000000000001;
	
	/** This checks to see if a Rectangle2D can be expressed as
	 * a int-based Rectangle.
	 * @param r
	 * @return a new <code>Rectangle</code> if possible, or 
	 * the original argument if not.
	 */
	private static final Rectangle2D getRectangle(Rectangle2D r) {
		double x = r.getX();
		double y = r.getY();
		double w = r.getWidth();
		double h = r.getHeight();
		Rectangle newRect = getRectangle(x,y,w,h);
		if(newRect!=null)
			return newRect;
		return r;
	}
	
	/** This checks to see if a Rectangle2D can be expressed as
	 * a int-based Rectangle.
	 * @param r
	 * @return a new <code>Rectangle</code> if possible, or 
	 * the null if not.
	 */
	private static final Rectangle getRectangle(double x,double y,double w,double h) {
		if(w<0) {
			x = x+w;
			w = -w;
		}
		if(h<0) {
			y = y+w;
			h = -h;
		}
		
		int iw = MathG.roundInt(w);
		int ih = MathG.roundInt(h);
		if(Math.abs(iw-w)>TOL)
			return null;
		if(Math.abs(ih-h)>TOL)
			return null;
		int ix = MathG.roundInt(x);
		int iy = MathG.roundInt(y);
		if(Math.abs(ix-x)>TOL)
			return null;
		if(Math.abs(iy-y)>TOL)
			return null;
		
		return new Rectangle(ix,iy,iw,ih);
	}
}
