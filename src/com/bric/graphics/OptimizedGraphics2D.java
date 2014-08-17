/*
 * @(#)OptimizedGraphics2D.java
 *
 * $Date: 2009-02-20 00:23:53 -0800 (Fri, 20 Feb 2009) $
 *
 * Copyright (c) 2009 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * http://javagraphics.blogspot.com/
 * 
 * And the latest version should be available here:
 * https://javagraphics.dev.java.net/
 */
package com.bric.graphics;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.AttributedCharacterIterator;
import java.util.Map;

import com.bric.geom.Clipper;
import com.bric.geom.RectangleReader;
import com.bric.geom.ShapeBounds;
import com.bric.math.MathG;
import com.bric.util.JVM;

/** This filter sits on top of a <code>Graphics2D</code> and helps to optimize
 * specific situations.
 *
 */
public class OptimizedGraphics2D extends Graphics2D {
	
	/** The major Java version being used (1.4, 1.5, 1.6, etc.).
	 * Note this may be -1 if this is called without the right security
	 * permissions. */
	public static final float javaVersion = JVM.getMajorJavaVersion(true);
	
	/** Whether this session is on a Mac. */
	public static final boolean isMac = (System.getProperty("os.name").toLowerCase().indexOf("mac")!=-1);
	
	/** If on a Mac: whether Quartz is the rendering pipeline. */
	public static final boolean usingQuartz = isMac && ((javaVersion>0 && javaVersion<=1.4f) ||
		(System.getProperty("apple.awt.graphics.UseQuartz")!=null && System.getProperty("apple.awt.graphics.UseQuartz").toString().equals("true")));
	
	
	/** If this is <code>true</code>, then all optimizations are always applied.
	 * However some optimizations are only improvements under certain settings,
	 * so they aren't applied everywhere.  (For example, the <code>OPTIMIZE_GLYPH_VECTOR</code>
	 * mask only improves performance when Quartz is used on Mac.)
	 * <p>By default this boolean is <code>false</code>, so you should be getting
	 * the best performance possible.  When running the <code>OptimizedGraphics2DTests</code>
	 * class this is set to <code>true</code> so the flags can all be
	 * explored in more detail.
	 */
	protected static boolean testingOptimizations = false;
	
	/** This flag uses the {@link com.bric.geom.Clipper} class to
	 * avoid using the <code>Area</code> class (if possible) when
	 * clipping this <code>Graphics2D</code>.
	 * <P>This will come into play when this graphics already has
	 * a clipping and you ask to further clip it.  If one of
	 * those two shapes is a rectangle, then the <code>Clipper</code>
	 * class can improve performance in calculating the new
	 * clip.
	 */
	public static final long OPTIMIZE_CLIPPING = 1;
	
	/** This flag makes sure a shape that you've asked to
	 * fill touches the clipped area of this graphics
	 * object.
	 * <P>Surprisingly: a lot of work can occur if you've
	 * asked to fill a shape that lies completely outside
	 * the clipping.
	 * <P>This only approximates the current clipping via
	 * the bounds of the current clipping.  It's still possible
	 * with this flag that the incoming shape may not touch
	 * the clipped area, but it's still an improvement in
	 * several cases.
	 * <P>(This does not affect <code>draw()</code> methods,
	 * because it may not be safe to make assumptions
	 * about the size of the stroke without actually calculating
	 * it: and calculating the stroke may be unnecessarily
	 * expensive.)
	 */
	public static final long OPTIMIZE_CLIPPED_SHAPES = 2;
	
	/** This optimizes the <code>getClipBounds()</code> method
	 * a little.
	 * <P>This is perhaps a very trivial optimization,
	 * performance-wise, but it was easy to implement.
	 */
	public static final long OPTIMIZE_CLIP_BOUNDS = 4;
	
	/** When a custom <code>Paint</code> is being used, calls to
	 * <code>drawGlyphVector()</code> are very slow.  Slower than
	 * calling <code>fill(glyphVector.getOutline())</code>.
	 * <p>This only applies to Macs using Quartz: everywhere else
	 * this slows down performance.
	 * 
	 */
	public static final long OPTIMIZE_GLYPH_VECTORS = 8;
	
	/** Painting an image is slower if a custom <code>Paint</code> is
	 * in use.  Strange, no?  So this flag may change the
	 * current paint to a <code>Color</code> before an image
	 * is drawn.
	 * <p>This only applies to Java 1.4; in other environments
	 * this makes no difference in performance.
	 */
	public static final long OPTIMIZE_IMAGE_BACKGROUND = 16;

	/** When <code>.drawChars()</code> is used through a scaling transform
	 * the characters do not scale well.
	 * This flag will convert the characters to glyph/shape
	 * data, and just call <code>.fill()</code>.
	 */
	public static final long FIX_TEXT_RENDERING = 32;
	
	/** Using quartz, the clipping offset is off (+.5,+.5).
	 * This fixes that offset.
	 */
	public static final long FIX_QUARTZ_CLIPPING_OFFSET = 64;
	
	/** The <code>Graphics2D</code>this delegates to. */
	public final Graphics2D g;
	protected long mask;
	protected Paint currentPaint;
	
	public OptimizedGraphics2D(Graphics2D g) {
		this(g,maskForAll);
	}
	
	public OptimizedGraphics2D(Graphics2D g,long mask) {
		this.g = g;
		this.mask = mask;
		currentPaint = g.getPaint();
	}

	public void addRenderingHints(Map hints) {
		g.addRenderingHints(hints);
	}

	public void clearRect(int x, int y, int width, int height) {
		g.clearRect(x, y, width, height);
	}

	private int insideClip = 0;
	public synchronized void clip(Shape s) {
		if(s==null)
			return; 
		
		double dx = 0;
		double dy = 0;
		if( (mask & FIX_QUARTZ_CLIPPING_OFFSET) > 0 && usingQuartz) {
			dx = -.5;
			dy = -.5;
			Rectangle2D rect = RectangleReader.convert(s);
			if(rect!=null) {
				s = rect;
			}
		}
		if(dx!=0 || dy!=0) {
			g.translate(dy, dy);
		}
		try {
			innerClipDefined = false;
			if(( mask & OPTIMIZE_CLIPPING) > 0) {
				if(insideClip==0) {
					insideClip++;
					try {
						Clipper.clip(this, s);
						return;
					} finally {
						 insideClip--;
					}
				}
			}
		g.clip(s);
		} finally {
			if(dx!=0 || dy!=0) {
				g.translate(-dx, -dy);
			}
		}
	}

	public synchronized void clipRect(int x, int y, int width, int height) {
		g.clipRect(x, y, width, height);
		innerClipDefined = false;
	}

	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		g.copyArea(x, y, width, height, dx, dy);
	}

	public Graphics create() {
		return new OptimizedGraphics2D( (Graphics2D)g.create(), mask );
	}

	public void dispose() {
		g.dispose();
	}

	public void draw(Shape s) {
		g.draw(s);
	}

	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		g.draw3DRect(x, y, width, height, raised);
	}

	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		g.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		g.drawBytes(data, offset, length, x, y);
	}

	public void drawChars(char[] data, int offset, int length, int x, int y) {
		if((mask & FIX_TEXT_RENDERING) > 0) {
			AffineTransform t = getTransform();
			if(t.getScaleX()!=1 || t.getScaleY()!=1) {
				Font font = getFont();
				if(offset!=0 || length!=data.length) {
					char[] c = new char[length];
					System.arraycopy(data,offset,c,0,length);
					data = c;
				}
				GlyphVector gv = font.createGlyphVector(getFontRenderContext(), data);
				Shape shape = gv.getOutline(x, y);
				
				if(getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING).equals(RenderingHints.VALUE_TEXT_ANTIALIAS_ON)) {
					Object oldHint = getRenderingHint(RenderingHints.KEY_ANTIALIASING);
					setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					fill(shape);
					setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldHint);
				} else {
					fill(shape);
				}
				return;
			}
		}
		g.drawChars(data,offset,length,x,y);
	}

	public void drawGlyphVector(GlyphVector gv, float x, float y) {
		if(((mask & OPTIMIZE_GLYPH_VECTORS)>0 && usingQuartz) || testingOptimizations) {
			if((currentPaint instanceof Color)==false) {
				fill(gv.getOutline(x,y));
				return;
			}
		}
		g.drawGlyphVector(gv, x, y);
	}

	private static Color EMPTY_COLOR = new Color(0,0,0,0);
	
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		if( ((mask & OPTIMIZE_IMAGE_BACKGROUND) > 0 && (javaVersion>0 && javaVersion<=1.4f)) || testingOptimizations) {
			g.setPaint(EMPTY_COLOR);
			g.drawImage(img, op, x, y);
			g.setPaint(currentPaint);
			return;
		}
		g.drawImage(img, op, x, y);
	}

	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		if( ((mask & OPTIMIZE_IMAGE_BACKGROUND) > 0 && (javaVersion>0 && javaVersion<=1.4f)) || testingOptimizations) {
			try {
				g.setPaint(EMPTY_COLOR);
				return g.drawImage(img, xform, obs);
			} finally {
				g.setPaint(currentPaint);
			}
		}
		return g.drawImage(img, xform, obs);
	}

	public boolean drawImage(Image img, int x, int y, Color bgcolor,
			ImageObserver observer) {
		if( ((mask & OPTIMIZE_IMAGE_BACKGROUND) > 0 && (javaVersion>0 && javaVersion<=1.4f)) || testingOptimizations) {
			try {
				g.setPaint(EMPTY_COLOR);
				return g.drawImage(img, x, y, bgcolor, observer);
			} finally {
				g.setPaint(currentPaint);
			}
		}
		return g.drawImage(img, x, y, bgcolor, observer);
	}

	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		if( ((mask & OPTIMIZE_IMAGE_BACKGROUND) > 0 && (javaVersion>0 && javaVersion<=1.4f)) || testingOptimizations) {
			try {
				g.setPaint(EMPTY_COLOR);
				return g.drawImage(img, x, y, observer);
			} finally {
				g.setPaint(currentPaint);
			}
		}
		return g.drawImage(img, x, y, observer);
	}

	public boolean drawImage(Image img, int x, int y, int width, int height,
			Color bgcolor, ImageObserver observer) {
		if( ((mask & OPTIMIZE_IMAGE_BACKGROUND) > 0 && (javaVersion>0 && javaVersion<=1.4f)) || testingOptimizations) {
			try {
				g.setPaint(EMPTY_COLOR);
				return g.drawImage(img, x, y, width, height, bgcolor, observer);
			} finally {
				g.setPaint(currentPaint);
			}
		}
		return g.drawImage(img, x, y, width, height, bgcolor, observer);
	}

	public boolean drawImage(Image img, int x, int y, int width, int height,
			ImageObserver observer) {
		if( ((mask & OPTIMIZE_IMAGE_BACKGROUND) > 0 && (javaVersion>0 && javaVersion<=1.4f)) || testingOptimizations) {
			try {
				g.setPaint(EMPTY_COLOR);
				return g.drawImage(img, x, y, width, height, observer);
			} finally {
				g.setPaint(currentPaint);
			}
		}
		return g.drawImage(img, x, y, width, height, observer);
	}

	public synchronized boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgcolor,
			ImageObserver observer) {
		if( ((mask & OPTIMIZE_IMAGE_BACKGROUND) > 0 && (javaVersion>0 && javaVersion<=1.4f)) || testingOptimizations) {
			try {
				g.setPaint(EMPTY_COLOR);
				return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
						bgcolor, observer);
			} finally {
				g.setPaint(currentPaint);
			}
		}
		return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
				bgcolor, observer);
	}

	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		if( ((mask & OPTIMIZE_IMAGE_BACKGROUND) > 0 && (javaVersion>0 && javaVersion<=1.4f)) || testingOptimizations) {
			try {
				g.setPaint(EMPTY_COLOR);
				return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
					observer);
			} finally {
				g.setPaint(currentPaint);
			}
		}
		return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
				observer);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		g.drawLine(x1, y1, x2, y2);
	}

	public void drawOval(int x, int y, int width, int height) {
		g.drawOval(x, y, width, height);
	}

	public void drawPolygon(int[] points, int[] points2, int points3) {
		g.drawPolygon(points, points2, points3);
	}

	public void drawPolygon(Polygon p) {
		g.drawPolygon(p);
	}

	public void drawPolyline(int[] points, int[] points2, int points3) {
		g.drawPolyline(points, points2, points3);
	}

	public void drawRect(int x, int y, int width, int height) {
		g.drawRect(x, y, width, height);
	}

	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		g.drawRenderableImage(img, xform);
	}

	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		g.drawRenderedImage(img, xform);
	}

	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	public void drawString(AttributedCharacterIterator iterator, float x,
			float y) {
		g.drawString(iterator, x, y);
	}

	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		g.drawString(iterator, x, y);
	}

	public void drawString(String s, float x, float y) {
		g.drawString(s, x, y);
	}

	public void drawString(String str, int x, int y) {
		g.drawString(str, x, y);
	}

	public boolean equals(Object obj) {
		if(obj==this)
			return true;
		
		if(obj instanceof OptimizedGraphics2D) {
			OptimizedGraphics2D g2 = (OptimizedGraphics2D)obj;
			if(g2.g.equals(g))
				return true;
		} else if(obj instanceof Graphics2D) {
			Graphics2D g2 = (Graphics2D)obj;
			if(g.equals(g2))
				return true;
		}

		return false;
	}

	private Rectangle2D.Float innerClip = new Rectangle2D.Float();
	private boolean innerClipDefined = false;
	private boolean innerClipExists = false;
	private Rectangle2D.Float getInnerClipRect() {
		if(innerClipDefined==false) {
			Shape clip = getClip();
			if(clip==null) {
				innerClipExists = false;
			} else {
				innerClipExists = true;
				ShapeBounds.getBounds(clip,innerClip);
				innerClipDefined = true;
			}
		}
		if(innerClipExists==false)
			return null;
		return innerClip;
	}
	
	public synchronized void fill(Shape s) {
		if((mask & OPTIMIZE_CLIPPED_SHAPES) > 0) {
			Rectangle2D clipRect = getInnerClipRect();
			if(clipRect!=null && s.intersects(clipRect)==false) {
				return;
			}
		}
		g.fill(s);
	}

	
	private Arc2D scratchArc2D = null;
	public synchronized void fillArc(int x, int y, int width, int height, int startAngle,
			int endAngle) {
		if((mask & OPTIMIZE_CLIPPED_SHAPES) > 0) {
			if(scratchArc2D==null)
				scratchArc2D = new Arc2D.Float();
			scratchArc2D.setArc(x, y, width, height, startAngle, endAngle, Arc2D.OPEN);
			fill(scratchArc2D);
			return;
		}
		g.fillArc(x, y, width, height, startAngle, endAngle);
	}

	private Ellipse2D scratchEllipse = null;
	public synchronized void fillOval(int x, int y, int width, int height) {
		if((mask & OPTIMIZE_CLIPPED_SHAPES) > 0) {
			if(scratchEllipse==null)
				scratchEllipse = new Ellipse2D.Float();
			scratchEllipse.setFrame(x, y, width, height);
			fill(scratchEllipse);
			return;
		}
		g.fillOval(x, y, width, height);
	}

	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		if((mask & OPTIMIZE_CLIPPED_SHAPES) > 0) {
			Polygon p = new Polygon(xPoints,yPoints,nPoints);
			fill(p);
			return;
		}
		g.fillPolygon(xPoints, yPoints, nPoints);
	}

	public void fillPolygon(Polygon p) {
		if((mask & OPTIMIZE_CLIPPED_SHAPES) > 0) {
			fill(p);
			return;
		}
		g.fillPolygon(p);
	}

	private Rectangle scratchRectangle = null;
	public synchronized void fillRect(int x, int y, int width, int height) {
		if((mask & OPTIMIZE_CLIPPED_SHAPES) > 0) {
			if(scratchRectangle==null)
				scratchRectangle = new Rectangle();
			scratchRectangle.setBounds(x, y, width, height);
			fill(scratchRectangle);
			return;
		}
		g.fillRect(x, y, width, height);
	}

	private RoundRectangle2D scratchRoundRectangle2D = null;
	public synchronized void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		if((mask & OPTIMIZE_CLIPPED_SHAPES) > 0) {
			if(scratchRoundRectangle2D==null)
				scratchRoundRectangle2D = new RoundRectangle2D.Float();
			scratchRoundRectangle2D.setRoundRect(x, y, width, height, arcWidth, arcHeight);
			fill(scratchRoundRectangle2D);
			return;
		}
		g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	public void finalize() {
		g.finalize();
	}

	public Color getBackground() {
		return g.getBackground();
	}

	public Shape getClip() {
		return g.getClip();
	}

	public Rectangle getClipBounds() {
		return getClipBounds(new Rectangle());
	}

	public Rectangle getClipBounds(Rectangle r) {
		if(( mask & OPTIMIZE_CLIP_BOUNDS)> 0) {
			Rectangle2D.Float clipRect = getInnerClipRect();
			if(clipRect!=null) {
				if(r==null) r = new Rectangle();
				
				float width = clipRect.width;
				float height = clipRect.height;
				if (width < 0 || height < 0) {
					r.x = 0;
					r.y = 0;
					r.width = 0;
					r.height = 0;
				    return r;
				}
				double x = clipRect.x;
				double y = clipRect.y;
				int x1 = MathG.floorInt(x);
				int y1 = MathG.floorInt(y);
				int x2 = MathG.ceilInt(x + width);
				int y2 = MathG.ceilInt(y + height);
				r.x = x1;
				r.y = y1;
				r.width = x2-x1;
				r.height = y2-y1;
				return r;
			}
		}
		
		
		return g.getClipBounds(r);
	}

	public Rectangle getClipRect() {
		return g.getClipRect();
	}

	public Color getColor() {
		return g.getColor();
	}

	public Composite getComposite() {
		return g.getComposite();
	}

	public GraphicsConfiguration getDeviceConfiguration() {
		return g.getDeviceConfiguration();
	}

	public Font getFont() {
		return g.getFont();
	}

	public FontMetrics getFontMetrics() {
		return g.getFontMetrics();
	}

	public FontMetrics getFontMetrics(Font f) {
		return g.getFontMetrics(f);
	}

	public FontRenderContext getFontRenderContext() {
		return g.getFontRenderContext();
	}

	public Paint getPaint() {
		return g.getPaint();
	}

	public Object getRenderingHint(Key hintKey) {
		return g.getRenderingHint(hintKey);
	}

	public RenderingHints getRenderingHints() {
		return g.getRenderingHints();
	}

	public Stroke getStroke() {
		return g.getStroke();
	}

	public AffineTransform getTransform() {
		return g.getTransform();
	}

	public int hashCode() {
		return g.hashCode();
	}

	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		return g.hit(rect, s, onStroke);
	}

	public boolean hitClip(int x, int y, int width, int height) {
		return g.hitClip(x, y, width, height);
	}

	public synchronized void rotate(double theta, double x, double y) {
		g.rotate(theta, x, y);
		innerClipDefined = false;
	}

	public synchronized void rotate(double theta) {
		g.rotate(theta);
		innerClipDefined = false;
	}

	public synchronized void scale(double sx, double sy) {
		g.scale(sx, sy);
		innerClipDefined = false;
	}

	public void setBackground(Color color) {
		g.setBackground(color);
	}

	public synchronized void setClip(int x, int y, int width, int height) {
		g.setClip(x, y, width, height);
		innerClipDefined = false;
	}

	public synchronized void setClip(Shape clip) {
		g.setClip(clip);
		innerClipDefined = false;
	}

	public void setColor(Color c) {
		g.setColor(c);
		currentPaint = c;
	}

	public void setComposite(Composite comp) {
		g.setComposite(comp);
	}

	public void setFont(Font font) {
		g.setFont(font);
	}

	public void setPaint(Paint paint) {
		g.setPaint(paint);
		currentPaint = paint;
	}

	public void setPaintMode() {
		g.setPaintMode();
	}

	public void setRenderingHint(Key hintKey, Object hintValue) {
		g.setRenderingHint(hintKey, hintValue);
	}

	public void setRenderingHints(Map hints) {
		g.setRenderingHints(hints);
	}

	public void setStroke(Stroke s) {
		g.setStroke(s);
	}

	public synchronized void setTransform(AffineTransform Tx) {
		g.setTransform(Tx);
		innerClipDefined = false;
	}

	public void setXORMode(Color c1) {
		g.setXORMode(c1);
	}

	public synchronized void shear(double shx, double shy) {
		g.shear(shx, shy);
		innerClipDefined = false;
	}

	public String toString() {
		return "OptimizedGraphics2D[ g = "+g.toString()+", mask = "+toString(mask)+"]";
	}

	public synchronized void transform(AffineTransform Tx) {
		g.transform(Tx);
		innerClipDefined = false;
	}

	public synchronized void translate(double tx, double ty) {
		g.translate(tx, ty);
		innerClipDefined = false;
	}

	public synchronized void translate(int x, int y) {
		g.translate(x, y);
		innerClipDefined = false;
	}
	
	/** Generates a text description of the argument.
	 * This uses reflection to see which masking fields from this
	 * class are used to make up the argument "mask".
	 * May return "all" or "none" if appropriate.
	 * @param mask a combination of the public static fields
	 * in this class.
	 * @return a text description of the argument.
	 */
	private static String toString(long mask) {
		Field[] f = OptimizedGraphics2D.class.getFields();
		StringBuffer sb = new StringBuffer();
		int ctr = 0;
		int max = 0;
		for(int a = 0; a<f.length; a++) {
			if(f[a].getType().equals(Long.TYPE) && 
					(f[a].getModifiers() & Modifier.STATIC)>0 &&
					(f[a].getModifiers() & Modifier.PUBLIC)>0) {
				try {
					long value = f[a].getLong(null);
					max++;
					if((mask & value) > 0) {
						if(sb.length()>0)
							sb.append(", ");
						sb.append(f[a].getName().toLowerCase());
						ctr++;
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(ctr==max)
			return "all";
		if(sb.length()==0)
			return "none";
		return sb.toString();
	}

	/** The cached mask that includes all other masks.
	 * This is generated via reflection so it's always up-to-date.
	 */
	private static final long maskForAll = getMaskForAll();
	
	/** Returns the mask the includes all public masks in this class.
	 * This uses reflection so it will automatically update as new
	 * fields are added to this class.  Also this value is cached
	 * so it is only calculated once per runtime.
	 */
	private synchronized static long getMaskForAll() {
		Field[] f = OptimizedGraphics2D.class.getFields();
		long mask = 0;
		for(int a = 0; a<f.length; a++) {
			if(f[a].getType().equals(Long.TYPE) && 
					(f[a].getModifiers() & Modifier.STATIC)>0 &&
					(f[a].getModifiers() & Modifier.PUBLIC)>0) {
				try {
					long value = f[a].getLong(null);
					mask += value;
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		return mask;
	}
}
