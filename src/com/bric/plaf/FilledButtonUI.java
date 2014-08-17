package com.bric.plaf;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import com.bric.awt.PaintUtils;
import com.bric.geom.ShapeBounds;
import com.bric.geom.TransformUtils;
import com.bric.graphics.OptimizedGraphics2D;

/** A ButtonUI that includes an enclosed filled shape.
 * <P>This <code>ButtonUI</code> supports several
 * options for controlling the shape of separate buttons.
 * (Note one instance of each UI can be assigned to
 * several buttons, and each button can be configured differently).
 * <P>Each button can have a horizontal and vertical position,
 * allowing grids of buttons to be seamlessly connected.
 * Horizontal positions can be LEFT, MIDDLE, RIGHT and ONLY, and vertical
 * positions can be TOP, MIDDLE, BOTTOM, and ONLY.  You can
 * set these positions by called:
 * <P><code>myButton.putClientProperty(HORIZONTAL_POSITION, LEFT)</code>
 * <P>Also you can define an arbitrary shape for each button.  If
 * this is defined, that shape takes priority over all other curvature/segment
 * information.  You can define this by calling something like:
 * <P><code>myButton.putClientProperty(SHAPE, new Ellipse2D.Float(0,0,100,100));</code>
 * <P>The size of the shape is not important: it will be scaled as necessary to fit
 * the icon and text of each button.  It is essential that the center of this shape
 * be inside the shape.  (That is, a 'C' or 'O' shape is not valid.)
 * <P>This layout is not tested with HTML-rendered text.
 * 
 */
public abstract class FilledButtonUI extends ButtonUI {

	/** Basic information about the geometry/layout of buttons.
	 * The members are final, and should be redefined instead
	 * of replaced.
	 */
	static class ButtonInfo {
 
		/** This handles most -- but not all -- of the basic workings
		 * of a generic button.
		 */
		final BasicButtonListener basicListener;

		/** The button this information relates to. */
		final AbstractButton button;

		/** The path used to paint the border. */
		final protected GeneralPath edge = new GeneralPath();

		/** A list of <code>UIEffects</code>, in the order
		 * they should be rendered.
		 */
		final protected Vector effects = new Vector();

		/** The area that makes up this button. */
		final protected GeneralPath fill = new GeneralPath();

		final protected Rectangle fillBounds = new Rectangle();

		/** The rectangle the icon is painted to. */
		final protected Rectangle iconRect = new Rectangle();

		/** The rectangle the text is painted to. */
		final protected Rectangle textRect = new Rectangle();

		/** The UI that relates to this ButtonInfo object. */
		final FilledButtonUI ui;

		/** Completely untested. */
		final protected Rectangle viewRect = new Rectangle();

		public ButtonInfo(AbstractButton b,FilledButtonUI filledButtonUI) {
			button = b;
			ui = filledButtonUI;

			basicListener = new BasicButtonListener(button) {
				Timer focusBlinker;

				public void focusGained(FocusEvent e) {
					super.focusGained(e);
					if(focusBlinker==null) {
						int rate = UIManager.getInt("TextField.caretBlinkRate");
						if(rate<100) rate = 500;

						focusBlinker = new Timer(rate,new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								Boolean blinkActive = (Boolean)button.getClientProperty("Focus.blink");
								Boolean focusActive = (Boolean)button.getClientProperty(FOCUS_PAINTED);
								if(blinkActive!=null && blinkActive.booleanValue()) {
									focusActive = new Boolean(!focusActive.booleanValue());
								} else {
									focusActive = Boolean.TRUE;
								}
								button.putClientProperty(FOCUS_PAINTED, focusActive);
							}
						});
					}
					button.putClientProperty(FOCUS_PAINTED, Boolean.TRUE);
					focusBlinker.start();
				}
				public void focusLost(FocusEvent e) {
					super.focusLost(e);
					button.putClientProperty(SPACEBAR_PRESSED, Boolean.FALSE);
					focusBlinker.stop();
					repaint(button);
				}

				public void propertyChange(PropertyChangeEvent evt) {
					super.propertyChange(evt);
					if(evt.getPropertyName().equals(AbstractButton.CONTENT_AREA_FILLED_CHANGED_PROPERTY)) {
						if(button.isOpaque() && evt.getOldValue().equals(Boolean.FALSE) && evt.getNewValue().equals(Boolean.TRUE)) {
							//the BasicButtonListener may set the opacity to true
							//we want to reverse that here:
							button.setOpaque(false);
						}
					} else if(evt.getPropertyName().equals(SPACEBAR_PRESSED)) {
						repaint(button);
					} else if(evt.getPropertyName().equals(FOCUS_PAINTED)) {
						repaint(button);
					} else if(evt.getPropertyName().equals("text") ||
							evt.getPropertyName().equals("icon")) {
						ui.updateLayout(button, getButtonInfo(button));
						repaint(button);
					}
				}
			};
		}

		public void updateFillBounds() {
			ShapeBounds.getBounds(fill, fillBounds);
		}
	}

	/** This paints 1 pixel outside the button bounds.  This
	 * is an experimental fix to tackle a repaint bug on Mac.
	 */
	public static void repaint(AbstractButton button) {
		/** A bug on Mac lets the lines immediately
		 * next to this button get painted funky
		 * (usually with focus residue):
		 * so when we lose focus let's be sure
		 * to repaint those, too.
		 */
		Container parent = button.getParent();
		if(parent!=null && false) {
			Rectangle bounds = button.getBounds();
			parent.repaint(bounds.x-1, bounds.y-1, bounds.width+2, bounds.height+2);
		} else if(parent!=null) {
			Rectangle bounds = button.getBounds();
			parent.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}

	private static Hashtable ascentTable = new Hashtable();

	/** A possible value for the VERTICAL_POSITION client property. */
	public static final String BOTTOM = "bottom";

	private static final String BUTTON_INFO_KEY = "com.bric.plaf.FilledButtonUI.ButtonInfo";

	/** When a component is resized, <code>updateLayout</code> is called. */
	protected static final ComponentListener componentListener = new ComponentListener() {

		public void componentHidden(ComponentEvent e) {}

		public void componentMoved(ComponentEvent e) {
			componentResized(e);
		}

		public void componentResized(ComponentEvent e) {
			AbstractButton button = (AbstractButton)e.getSource();
			ButtonInfo info = getButtonInfo(button);
			info.ui.updateLayout(button,info);
			repaint(button);
		}

		public void componentShown(ComponentEvent e) {
			componentResized(e);
		}
	};

	/** Used for blinking focus.  When false, the focus should not be
	 * painted even if the button has the focus.
	 */
	protected static final String FOCUS_PAINTED = "FilledButtonUI.focusPainted";

	/** A static KeyListener for using focus arrow keys. */
	protected static KeyListener focusArrowListener = new FocusArrowListener();

	/** The client property for the horizontal position of this segment.
	 * The recognized values are "left", "middle", "last" and "only".
	 * The default value is assumed to be "only".
	 */
	public static final String HORIZONTAL_POSITION = "JButton.segmentHorizontalPosition";

	/** This client property defines an arbitrary shape for each button.
	 */
	public static final String SHAPE = "JButton.shape";

	protected static KeyListener keyArmingListener = new KeyListener() {
		public void keyPressed(KeyEvent e) {
			int code = e.getKeyCode();
			if(code==KeyEvent.VK_SPACE) {
				AbstractButton button = (AbstractButton)e.getSource();
				Boolean wasPressed = (Boolean)button.getClientProperty(SPACEBAR_PRESSED);
				if(wasPressed==null ||
						wasPressed.booleanValue()==false) {
					button.putClientProperty(SPACEBAR_PRESSED,Boolean.TRUE);
					button.doClick();
				}
			}
		}

		public void keyReleased(KeyEvent e) {
			int code = e.getKeyCode();
			if(code==KeyEvent.VK_SPACE) {
				AbstractButton button = (AbstractButton)e.getSource();
				button.putClientProperty(SPACEBAR_PRESSED,Boolean.FALSE);
			}
		}

		public void keyTyped(KeyEvent e) {}
	};
	/** A possible value for the HORIZONTAL_POSITION client property. */
	public static final String LEFT = "left";

	/** A possible value for the HORIZONTAL_POSITION or VERTICAL_POSITION client property. */
	public static final String MIDDLE = "middle";

	/** A possible value for the HORIZONTAL_POSITION or VERTICAL_POSITION client property. */
	public static final String ONLY = "only";
	/** A constant indicating that focus should be painted
	 * along the edge of the filled shape -- both inside and outside.
	 */
	public static final int PAINT_FOCUS_BOTH = 4;
	/** A constant indicating that focus should be painted
	 * just inside the filled shape.
	 */
	public static final int PAINT_FOCUS_INSIDE = 1;
	/** A constant indicating that focus should be painted
	 * just outside the filled shape.  This is taken into
	 * account when allocating the button size, because it
	 * adds extra pixels to the width/height.
	 */
	public static final int PAINT_FOCUS_OUTSIDE = 2;
	/** A constant indicating that no focus is explicitly painted.
	 * <P>You may use this constant when you aren't happy with how
	 * the <code>paint()</code> method handles focus by default, and
	 * paint your own focus by override <code>paintBackground()</code>
	 * or <code>paintForeground()</code>.
	 */
	public static final int PAINT_NO_FOCUS = 0;
	/** A constant used to indicate the horizontal position of a button. */
	protected static final int POS_BOTTOM = 5;


	/** A constant used to indicate the horizontal position of a button. */
	protected static final int POS_LEFT = 0;
	/** A constant used to indicate the horizontal or vertical position of a button. */
	protected static final int POS_MIDDLE = 1;
	/** A constant used to indicate the horizontal or vertical position of a button. */
	protected static final int POS_ONLY = 3;
	/** A constant used to indicate the horizontal position of a button. */
	protected static final int POS_RIGHT = 2;
	/** A constant used to indicate the horizontal position of a button. */
	protected static final int POS_TOP = 4;
	/** When the position properties change in a button, we need to update the UI. */
	protected static PropertyChangeListener positionAndShapeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			String name = evt.getPropertyName();
			if(name.equals(VERTICAL_POSITION) ||
					name.equals(HORIZONTAL_POSITION) ||
					name.equals(SHAPE) ||
					name.equals("JButton.segmentPosition") ) { //see Apple Tech Note 2196
				AbstractButton button = (AbstractButton)evt.getSource();
				ButtonUI ui = button.getUI();
				if(ui instanceof FilledButtonUI) {
					FilledButtonUI s = (FilledButtonUI)ui;
					s.updateLayout(button, getButtonInfo(button));
					button.invalidate();
					repaint(button);
				}
			}
		}
	};

	/** A possible value for the HORIZONTAL_POSITION client property. */
	public static final String RIGHT = "right";
	/** This client property is set to a Boolean indicating
	 * whether the spacebar is currently pressed or not.
	 */
	protected static final String SPACEBAR_PRESSED = "FilledButtonUI.spacebarPressed";

	/** A translucent composite used to render parts the UI when a button is disabled. */
	protected static AlphaComposite SRC_OVER_TRANSLUCENT = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,.5f);

	/** A possible value for the VERTICAL_POSITION client property. */
	public static final String TOP = "top";

	/** The client property for the vertical position of this segment.
	 * The recognized values are "top", "middle", "bottom" and "only".
	 * The default value is assumed to be "only".
	 */
	public static final String VERTICAL_POSITION = "JButton.segmentVerticalPosition";
	protected static ButtonInfo getButtonInfo(AbstractButton button) {
		ButtonInfo i = (ButtonInfo)button.getClientProperty(BUTTON_INFO_KEY);
		if(i==null)
			throw new NullPointerException("installUI was not called");
		return i;
	}

	/** This looks at the client properties "JButton.segmentPosition"
	 * and HORIZONTAL_POSITION to return on the constants in this
	 * class (LEFT_POS, POS_MIDDLE, RIGHT_POS or POS_ONLY).
	 * <P>This returns POS_ONLY if a shape is defined, because in
	 * this context a position doesn't make sense.
	 */
	protected static int getHorizontalPosition(JComponent b) {
		Shape shape = (Shape)b.getClientProperty(SHAPE);
		if(shape!=null) {
			return POS_ONLY;
		}
		
		String s = (String)b.getClientProperty("JButton.segmentPosition");
		if(s==null)
			s = (String)b.getClientProperty(HORIZONTAL_POSITION);
		if(s==null) s = "only";
		if(s.equalsIgnoreCase("first") || s.equalsIgnoreCase("left"))
			return POS_LEFT;
		if(s.equalsIgnoreCase("middle"))
			return POS_MIDDLE;
		if(s.equalsIgnoreCase("last") || s.equalsIgnoreCase("right"))
			return POS_RIGHT;
		return POS_ONLY;
	}

	/** Returns a rectangle enclosing all the rectangles in 
	 * the argument.
	 */
	private static Rectangle getSum(Rectangle[] array) {
		Rectangle sum = null;
		for(int a = 0; a<array.length; a++) {
			if(array[a].width>0 && array[a].height>0) {
				if(sum==null) {
					sum = new Rectangle(array[a]);
				} else {
					sum = sum.union(array[a]);
				}
			}
		}
		if(sum==null)
			sum = new Rectangle(0,0,0,0);
		return sum;
	}

	/** This deals with a bug/peculiarity for the default Mac font:
	 * several pixels of the ascent are actually empty.  This screws
	 * up certain measurements which assume the font is actually a few
	 * pixels taller than it really is.
	 */
	private static int getUnusedAscent(FontMetrics fm,Font font) {
		Integer value = (Integer)ascentTable.get(font);
		if(value==null) {
			int recordedAscent = fm.getAscent();	
			FontRenderContext frc = new FontRenderContext(new AffineTransform(), false, false);
			GlyphVector gv = font.createGlyphVector(frc, "XYZ");
			Rectangle2D bounds = ShapeBounds.getBounds(gv.getOutline());
			int observedAscent = (int)(Math.ceil(bounds.getHeight())+.5);
			value = new Integer(recordedAscent-observedAscent);
			ascentTable.put(font, value);
		}
		return value.intValue();
	}
	/** This looks at the client property VERTICAL_POSITION
	 * to return on the constants in this
	 * class (POS_TOP, POS_MIDDLE, POS_BOTTOM or POS_ONLY).
	 * <P>This returns POS_ONLY if a shape is defined, because in
	 * this context a position doesn't make sense.
	 */
	protected static int getVerticalPosition(JComponent b) {
		Shape shape = (Shape)b.getClientProperty(SHAPE);
		if(shape!=null) {
			return POS_ONLY;
		}
		
		String s = (String)b.getClientProperty(VERTICAL_POSITION);
		if(s==null) s = "only";
		if(s.equalsIgnoreCase("top"))
			return POS_TOP;
		if(s.equalsIgnoreCase("middle"))
			return POS_MIDDLE;
		if(s.equalsIgnoreCase("bottom"))
			return POS_BOTTOM;
		return POS_ONLY;
	}

	/** Return true if the spacebar is pressed.  This consults
	 * the SPACEBAR_PRESSED property, which is correctly
	 * controlled if a FilledButtonUI is installed in a given
	 * button.
	 * <P>This is visually the same as setting the ButtonModel's
	 * pressed state to true.  However if that property were used
	 * and the mouse was interacting with this button: the two
	 * agents (the KeyListener and the MouseListener) would
	 * constantly be overriding the other's work.
	 * @return
	 */
	protected static boolean isSpacebarPressed(AbstractButton button) {
		Boolean b = (Boolean)button.getClientProperty(SPACEBAR_PRESSED);
		if(b==null) return false;
		return b.booleanValue();
	}

	/** The <code>ButtonFill</code> that controls the main paints
	 * used in this L&F.
	 */
	protected final ButtonFill buttonFill;

	/** The number of pixels thick the focus ring should be. */
	protected int focusSize = 3;

	protected final int maxTopRightRadius, maxTopLeftRadius, maxBottomLeftRadius, maxBottomRightRadius;


	protected final int prefTopRightRadius, prefTopLeftRadius, prefBottomLeftRadius, prefBottomRightRadius;

	/** Creates a new <code>FilledButtonUI</code>.
	 * 
	 * @param buttonFill the <code>ButtonFill</code> to use for this UI.
	 * @param preferredRadius the preferred radius for corners
	 * @param maxRadius the maximum radius for corners
	 */
	public FilledButtonUI(ButtonFill buttonFill,int preferredRadius,int maxRadius) {
		this(buttonFill, preferredRadius, preferredRadius, preferredRadius, preferredRadius,
				maxRadius, maxRadius, maxRadius, maxRadius);
	}

	/** Creates a new <code>FilledButtonUI</code>.
	 * <P>Most of the time you'll want the other, simpler constructor.
	 * This one is used when you want to different corner sizes for
	 * different corners.
	 * 
	 * @param buttonFill the <code>ButtonFill</code> this UI uses.
	 * @param prefTopLeftRadius the preferred top left radius
	 * @param prefBottomLeftRadius the preferred bottom left radius
	 * @param prefBottomRightRadius the preferred top right radius
	 * @param prefTopRightRadius the preferred bottom right radius
	 * @param maxTopLeftRadius the maximum top left radius
	 * @param maxBottomLeftRadius the maximum bottom left radius
	 * @param maxBottomRightRadius the maximum top right radius
	 * @param maxTopRightRadius the maximum bottom right radius
	 */
	public FilledButtonUI(ButtonFill buttonFill,
			int prefTopLeftRadius,int prefBottomLeftRadius,int prefBottomRightRadius,int prefTopRightRadius,
			int maxTopLeftRadius,int maxBottomLeftRadius,int maxBottomRightRadius,int maxTopRightRadius) {
		this.prefTopLeftRadius = prefTopLeftRadius;
		this.prefBottomLeftRadius = prefBottomLeftRadius;
		this.prefBottomRightRadius = prefBottomRightRadius;
		this.prefTopRightRadius = prefTopRightRadius;

		this.maxTopLeftRadius = maxTopLeftRadius;
		this.maxBottomLeftRadius = maxBottomLeftRadius;
		this.maxBottomRightRadius = maxBottomRightRadius;
		this.maxTopRightRadius = maxTopRightRadius;

		this.buttonFill = buttonFill;
	}

	/** The padding between the "content area" (that is, the icon rect
	 * and text rect) and the edges of this button.  This is a calculated
	 * value.
	 */
	private Insets getContentInsets(AbstractButton button) {
		int horizontalPosition = getHorizontalPosition(button);
		int verticalPosition = getVerticalPosition(button);

		Insets i = new Insets(1,1,1,1);
		if(getFocusPainting(button)==PAINT_FOCUS_OUTSIDE || 
				getFocusPainting(button)==PAINT_FOCUS_BOTH) {
			if(horizontalPosition==POS_LEFT || horizontalPosition==POS_ONLY) {
				i.left += focusSize;
			}
			if(horizontalPosition==POS_RIGHT || horizontalPosition==POS_ONLY) {
				i.right += focusSize;
			}
			if(verticalPosition==POS_TOP || verticalPosition==POS_ONLY) {
				i.top += focusSize;
			}
			if(verticalPosition==POS_BOTTOM || verticalPosition==POS_ONLY) {
				i.bottom += focusSize;
			}
		} else {
			if(buttonFill.getShadowHighlight(button)!=null && (verticalPosition==POS_BOTTOM || verticalPosition==POS_ONLY)) {
				i.bottom++;
			}
		}
		return i;
	}

	/** Returns the list of <code>UIEffects</code> this button
	 * is rendering.
	 */
	public List getEffects(AbstractButton button) {
		ButtonInfo info = getButtonInfo(button);
		return info.effects;
	}

	/** Returns one of the PAINT_FOCUS constants
	 * declared in this object.  If PAINT_NO_FOCUS is used,
	 * then this class automatically paints no focus.  In
	 * that case a subclass would be responsible for any rendering of
	 * focus.
	 * <P>If PAINT_FOCUS_OUTSIDE is used, then
	 * a few rings of focus are painted outside
	 * this component under this component.
	 * The order of painting is:
	 * <BR>1.  Shadow Highlight
	 * <BR>2.  Focus
	 * <BR>3.  Background Fill
	 * <BR>4.  Border
	 * <P>If PAINT_FOCUS_INSIDE is used, then
	 * a few rings of focus are painted inside
	 * this component.
	 * The order of painting is:
	 * <BR>1.  Shadow Highlight
	 * <BR>2.  Background Fill
	 * <BR>3.  Focus
	 * <BR>4.  Border
	 * <P>If PAINT_FOCUS_BOTH is used, then
	 * the focus appears both inside and
	 * outside the filled area.
	 * The order of painting is:
	 * <BR>1.  Shadow Highlight
	 * <BR>2.  Background Fill
	 * <BR>3.  Border
	 * <BR>4.  Focus
	 * 
	 * <P>By default this returns PAINT_FOCUS_OUTSIDE
	 * if a shape is defined, or if this button
	 * is in a single row or column of buttons.
	 * (That is, if the horizontal or vertical position
	 * is "only").  Otherwise this returns PAINT_FOCUS_INSIDE,
	 * because once you get to inner buttons the focus
	 * <i>has</i> to be painted on the inside to remain
	 * visible.
	 */
	public int getFocusPainting(AbstractButton button) {
		Shape shape = (Shape)button.getClientProperty(SHAPE);
		if(shape!=null)
			return PAINT_FOCUS_OUTSIDE;
		
		int horizontalPosition = getHorizontalPosition(button);
		int verticalPosition = getVerticalPosition(button);

		if( !(horizontalPosition==POS_ONLY || verticalPosition==POS_ONLY) ) {
			return PAINT_FOCUS_INSIDE;
		}
		return PAINT_FOCUS_OUTSIDE;
	}

	/** Return a copy of the rectangle used to render the icon. */
	public Rectangle getIconBounds(AbstractButton button) {
		return new Rectangle( getButtonInfo(button).iconRect );
	}

	/** This padding is added to the icon rectangle when the content
	 * area is being calculated.  That is: this is the minimum
	 * number of pixels that should be between the icon and the edge
	 * of this button.
	 * <P>This is a constant value that other calculations use.
	 */
	protected Insets getIconPadding() {
		return new Insets(2,3,2,3);
	}

	/** By default this returns a very very large Dimension.
	 * In theory these buttons will have a well-defined preferred size,
	 * but can scale upwards to any necessary size.
	 */
	public Dimension getMaximumSize(JComponent c) {
		return new Dimension(Integer.MAX_VALUE/2, Integer.MAX_VALUE/2);
	}

	/** Returns the preferred size. */
	public Dimension getMinimumSize(JComponent jc) {
		return getPreferredSize( jc );
	}

	/** This is the minimum height of the content area of this button.
	 * That is: if this button has a solid gray fill: this is the height
	 * of those gray pixels.
	 * <P>This height will only be used if it is <i>less than</i> the
	 * preferred height the text and icon would otherwise require.
	 * <P>Also this will not be used for buttons with a custom shape.
	 */
	protected int getPreferredHeight() {
		return 0;
	}

	/** Calculates the preferred size of this button and UI. */
	public Dimension getPreferredSize(JComponent c) {
		AbstractButton button = (AbstractButton)c;
		ButtonCluster cluster = ButtonCluster.getCluster(button);

		Rectangle scratchIconRect = new Rectangle();
		Rectangle scratchTextRect = new Rectangle();
		Rectangle scratchViewRect = new Rectangle(Short.MAX_VALUE, Short.MAX_VALUE);

		FontMetrics fm = button.getFontMetrics(button.getFont());
		SwingUtilities.layoutCompoundLabel(fm, 
				button.getText(), 
				button.getIcon(), 
				button.getVerticalAlignment(), 
				button.getHorizontalAlignment(), 
				button.getVerticalTextPosition(), 
				button.getHorizontalTextPosition(), 
				scratchViewRect, 
				scratchIconRect, 
				scratchTextRect,
				button.getIconTextGap());

		Insets textInsets = getTextPadding();
		scratchTextRect.y -= textInsets.top;
		scratchTextRect.x -= textInsets.left;
		scratchTextRect.width += textInsets.left+textInsets.right;
		scratchTextRect.height += textInsets.top+textInsets.bottom;

		Insets iconInsets = getIconPadding();
		scratchIconRect.y -= iconInsets.top;
		scratchIconRect.x -= iconInsets.left;
		scratchIconRect.width += iconInsets.left+iconInsets.right;
		scratchIconRect.height += iconInsets.top+iconInsets.bottom;

		Rectangle sum = getSum(new Rectangle[] {scratchIconRect, scratchTextRect});
		
		if(cluster!=null && cluster.isStandardized()) {
			/** If standardize: the dimensions of this button need
			 * to make room for all other buttons in the cluster.
			 */
			AbstractButton[] buttons = cluster.getButtons();
			for(int a = 0; a<buttons.length; a++) {
				ButtonUI ui = buttons[a].getUI();
				if(ui instanceof FilledButtonUI) {
					FilledButtonUI fui = (FilledButtonUI)ui;
					Dimension contentSize = fui.getContentSize(buttons[a]);
					sum.width = Math.max(sum.width, contentSize.width);
					sum.height = Math.max(sum.height, contentSize.height);
				}
			}
		}

		Insets padding = getContentInsets(button);
		
		Shape shape = (Shape)button.getClientProperty(SHAPE);
		
		if(shape==null) {
			int minHeight = getPreferredHeight();
			if(sum.height<minHeight)
				sum.height = minHeight;
	
			int leftSide = Math.max(prefTopLeftRadius,prefBottomLeftRadius);
			int rightSide = Math.max(prefTopRightRadius,prefBottomRightRadius);
	
			return new Dimension(sum.width+leftSide+rightSide+padding.left+padding.right,
					sum.height+padding.top+padding.bottom);
		} else {
			GeneralPath resizedShape = findShapeToFitRectangle(shape, sum.width, sum.height);
			Rectangle2D bounds = ShapeBounds.getBounds(resizedShape);
			return new Dimension(
					(int)( bounds.getWidth()+padding.left+padding.right+.99999),
					(int)( bounds.getHeight()+padding.top+padding.bottom+.99999)
			);
		}
	}
	
	
	/** Returns the dimensions required to display the icon
	 * and label.
	 *
	 */
	private Dimension getContentSize(AbstractButton button) {
		Rectangle scratchIconRect = new Rectangle();
		Rectangle scratchTextRect = new Rectangle();
		Rectangle scratchViewRect = new Rectangle(Short.MAX_VALUE, Short.MAX_VALUE);

		FontMetrics fm = button.getFontMetrics(button.getFont());
		SwingUtilities.layoutCompoundLabel(fm, 
				button.getText(), 
				button.getIcon(), 
				button.getVerticalAlignment(), 
				button.getHorizontalAlignment(), 
				button.getVerticalTextPosition(), 
				button.getHorizontalTextPosition(), 
				scratchViewRect, 
				scratchIconRect, 
				scratchTextRect,
				button.getIconTextGap());

		Insets textInsets = getTextPadding();
		scratchTextRect.y -= textInsets.top;
		scratchTextRect.x -= textInsets.left;
		scratchTextRect.width += textInsets.left+textInsets.right;
		scratchTextRect.height += textInsets.top+textInsets.bottom;

		Insets iconInsets = getIconPadding();
		scratchIconRect.y -= iconInsets.top;
		scratchIconRect.x -= iconInsets.left;
		scratchIconRect.width += iconInsets.left+iconInsets.right;
		scratchIconRect.height += iconInsets.top+iconInsets.bottom;

		Rectangle sum = getSum(new Rectangle[] {scratchIconRect, scratchTextRect});
		return new Dimension(sum.width, sum.height);
	}
	
	private static GeneralPath findShapeToFitRectangle(Shape originalShape,int w,int h) {
		GeneralPath newShape = new GeneralPath();
		Rectangle2D rect = new Rectangle2D.Float();
		ShapeBounds.getBounds(originalShape,rect);
		double scale = Math.min( ((double)w)/rect.getWidth(), ((double)h)/rect.getHeight() );
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

	/** This returns the filled shape used to draw this
	 * button.  This returns the <i>original</i> shape -- not
	 * a cloned copy.
	 */
	public GeneralPath getShape(AbstractButton button) {
		ButtonInfo info = getButtonInfo(button);
		return info.fill;
	}

	/** Return a copy of the rectangle used to render the text. */
	public Rectangle getTextBounds(AbstractButton button) {
		return new Rectangle( getButtonInfo(button).textRect );
	}

	/** This padding is added to the text rectangle when the content
	 * area is being calculated.  That is: this is the minimum
	 * number of pixels that should be between the text and the edge
	 * of this button.
	 * <P>This is a constant value that other calculations use.
	 */
	protected Insets getTextPadding() {
		return new Insets(2,4,2,4);
	}

	public void installUI(JComponent c) {
		AbstractButton button = (AbstractButton)c;

		ButtonInfo info = new ButtonInfo(button, this);
		button.putClientProperty(BUTTON_INFO_KEY, info);

		button.addMouseListener(info.basicListener);
		button.addMouseMotionListener(info.basicListener);
		button.addFocusListener(info.basicListener);
		button.addPropertyChangeListener(info.basicListener);
		button.addChangeListener(info.basicListener);
		button.addKeyListener(focusArrowListener);
		button.addComponentListener(componentListener);
		button.addKeyListener(keyArmingListener);
		button.setRequestFocusEnabled(false);
		button.setFocusable(true);
		button.addPropertyChangeListener(positionAndShapeListener);
		button.setOpaque(false);
		button.setRolloverEnabled(true);
		
		if(button.getIcon()!=null) {
			Font font = (Font)UIManager.getFont("IconButton.font");
			if(font!=null)
				button.setFont(font); //miniature-ish
		}

		super.installUI(c);

		updateLayout(button,info);
	}

	/** Controls whether the border is painted with antialiased
	 * rendering hints.
	 * <P>Returns <code>true</code>. */
	public boolean isBorderAntialiased() {
		return true;
	}

	/** This checks to see if updateLayout() needs to be called.
	 * Generally this returns true if the width, height, or
	 * segment position has changed.
	 */
	protected boolean isLayoutValid(AbstractButton button) {
		int horizontalPosition = getHorizontalPosition(button);
		int verticalPosition = getVerticalPosition(button);
		int width = button.getWidth();
		int height = button.getHeight();

		String key = width+" "+height+" "+horizontalPosition+" "+verticalPosition;

		String oldKey = (String)button.getClientProperty("FilledButtonUI.validationKey");
		if(oldKey==null)
			return false;

		if(oldKey.equals(key))
			return true;

		return false;
	}

	/** This calls the other relevant
	 * <code>paint...()</code> methods in this object.
	 * The layering of the focus varies based on whether
	 * it should be painted outside or inside the filled
	 * shape, but otherwise the layers are:
	 * <LI>Filling the bounds with <code>button.getBackground()<code> (if <code>button.isOpaque()<code> is true).
	 * <LI>If <code>getShadowHighlight()</code> is non-null, painting the border 1 pixel below its usual location.
	 * <LI><code>paintBackground(g)</code>
	 * <li><code>paintEffects(g,false)</code>
	 * <li><code>paintIcon(g)</code>
	 * <li><code>paintText(g)</code>
	 * <LI><code>paintForeground(g)</code>
	 * <LI><code>paintEffects(g,true)</code></LI>
	 * 
	 */
	public void paint(Graphics g0, JComponent c) {
		AbstractButton button = (AbstractButton)c;

		if(isLayoutValid(button)==false)
			updateLayout(button, getButtonInfo(button));

		if(button.isOpaque()) {
			g0.setColor(button.getBackground());
			g0.fillRect(0,0,button.getWidth(), button.getHeight());
		}

		Graphics2D g = new OptimizedGraphics2D((Graphics2D)g0);
		
		if(button.isEnabled()==false) {
			g.setComposite(SRC_OVER_TRANSLUCENT);
		}

		ButtonInfo info = getButtonInfo(button);

		Color highlight = buttonFill.getShadowHighlight(button);
		if(highlight!=null && button.isBorderPainted()) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.translate(0,1);
			g.setColor(highlight);
			g.draw(info.edge);
			g.translate(0,-1);
		}

		int focus = button.hasFocus() ? getFocusPainting(button) : PAINT_NO_FOCUS;
		Boolean focusPainted = (Boolean)button.getClientProperty(FOCUS_PAINTED);
		if(focusPainted!=null && focusPainted.equals(Boolean.FALSE))
			focus = PAINT_NO_FOCUS;

		if(button.isFocusPainted()==false)
			focus = PAINT_NO_FOCUS;

		//this shouldn't happen, but just in case:
		if(button.isEnabled()==false)
			focus = PAINT_NO_FOCUS;

		if( focus==PAINT_FOCUS_OUTSIDE ) {
			if(isFillOpaque()) {
				//the opaque fill will overwrite the inner part of
				//this stroke...
				PaintUtils.paintFocus(g, info.fill, focusSize);
			} else {
				//this still has some rendering quirks in
				//Quartz (remove the clipping to study closely)
				//... but other than the top horizontal & vertical
				//line it's OK.  And even those are ... partly there.
				Graphics2D focusG = (Graphics2D)g.create();
				GeneralPath outsideClip = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
				outsideClip.append(new Rectangle(0,0,button.getWidth(),button.getHeight()),false);
				outsideClip.append(info.fill, false);
				focusG.clip(outsideClip);
				PaintUtils.paintFocus(focusG, info.fill, focusSize);
				focusG.dispose();
			}
		}
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

		paintBackground(g, info);
		paintEffects(g, info, true);

		g.setStroke(new BasicStroke(1));
		if( focus==PAINT_FOCUS_INSIDE ) {
			Graphics2D focusG = (Graphics2D)g.create();
			focusG.clip(info.fill);
			PaintUtils.paintFocus(focusG, info.fill, focusSize);
			focusG.dispose();
			paintBorder(g, info);
		} else if(focus==PAINT_FOCUS_BOTH) {
			paintBorder(g, info);
			PaintUtils.paintFocus(g, info.fill, focusSize);
		} else {
			paintBorder(g, info);
		}

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		paintIcon(g, info);
		paintText(g, info);

		g.setComposite(button.isEnabled() ? AlphaComposite.SrcOver : SRC_OVER_TRANSLUCENT);
		paintForeground(g, info);
		paintEffects(g, info, false);
	}

	public void paintBackground(Graphics2D g,ButtonInfo info) {
		if(info.button.isContentAreaFilled()) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Paint fillPaint = buttonFill.getFill(info.button, info.fillBounds);
			g.setPaint(fillPaint);
			g.fill( info.fill );
		}
	}

	public void paintBorder(Graphics2D g,ButtonInfo info) {
		if(info.button.isBorderPainted()) {
			Paint borderColor = buttonFill.getBorder(info.button, info.fillBounds);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
			if(isBorderAntialiased()) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			} else {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			}
			g.setPaint(borderColor);
			g.draw( info.edge );
		}
	}

	/** Paints effects for this button.
	 * The effects, if any, are listed in the <code>effects</code> field
	 * of this object.
	 * 
	 * @param g the Graphics2D to paint to.
	 * @param background if true, then only the effects for the background
	 * are painted.  If false, then only the effects for the foreground
	 * are painted.
	 */
	protected void paintEffects(Graphics2D g,ButtonInfo info,boolean background) {
		int ctr = 0;
		while(ctr<info.effects.size()) {
			UIEffect effect = (UIEffect)info.effects.get(ctr);
			effect.paint( (Graphics2D)g.create() );
			if(effect.isActive()==false) {
				info.effects.remove(ctr);
			} else {
				ctr++;
			}
		}
	}

	public void paintForeground(Graphics2D g,ButtonInfo info) {}

	public void paintIcon(Graphics2D g,ButtonInfo info) {
		AbstractButton button = info.button;
		Icon icon = button.getIcon();
		ButtonModel model = button.getModel();

		if(model.isRollover() && button.getRolloverIcon()!=null)
			icon = button.getRolloverIcon();
		if(model.isPressed() && button.getPressedIcon()!=null)
			icon = button.getPressedIcon();
		if(model.isSelected() && button.getSelectedIcon()!=null)
			icon = button.getSelectedIcon();
		if(model.isRollover() && model.isSelected() && button.getRolloverSelectedIcon()!=null)
			icon = button.getRolloverSelectedIcon();
		if(button.isEnabled()==false && button.getDisabledIcon()!=null)
			icon = button.getDisabledIcon();
		if(button.isEnabled()==false && model.isSelected() && button.getDisabledIcon()!=null)
			icon = button.getDisabledSelectedIcon();

		if(icon!=null) {
			g.setComposite(button.isEnabled() ? AlphaComposite.SrcOver : SRC_OVER_TRANSLUCENT);
			icon.paintIcon(button, g, info.iconRect.x, info.iconRect.y);
		}
	}

	public void paintText(Graphics2D g,ButtonInfo info) {
		ButtonModel model = info.button.getModel();
		FontMetrics fm = info.button.getFontMetrics(info.button.getFont());
		int mnemonicIndex = info.button.getDisplayedMnemonicIndex();
		String text = info.button.getText();
		int textShiftOffset = 0;


		g.setComposite(AlphaComposite.SrcOver);
		/* Draw the Text */
		if(model.isEnabled()) {
			/*** paint the text normally */
			g.setColor(info.button.getForeground());
			BasicGraphicsUtils.drawStringUnderlineCharAt(g,text, mnemonicIndex,
					info.textRect.x + textShiftOffset,
					info.textRect.y + fm.getAscent() + textShiftOffset);
		} else {
			/*** paint the text disabled ***/
			g.setColor(info.button.getBackground().brighter());
			BasicGraphicsUtils.drawStringUnderlineCharAt(g,text, mnemonicIndex,
					info.textRect.x, info.textRect.y + fm.getAscent());
			g.setColor(info.button.getBackground().darker());
			BasicGraphicsUtils.drawStringUnderlineCharAt(g,text, mnemonicIndex,
					info.textRect.x - 1, info.textRect.y + fm.getAscent() - 1);
		}
	}

	public void uninstallUI(JComponent c) {
		AbstractButton button = (AbstractButton)c;

		ButtonInfo info = getButtonInfo(button);

		button.removeMouseListener(info.basicListener);
		button.removeMouseMotionListener(info.basicListener);
		button.removeFocusListener(info.basicListener);
		button.removePropertyChangeListener(info.basicListener);
		button.removeChangeListener(info.basicListener);
		button.removeKeyListener(focusArrowListener);
		button.removeComponentListener(componentListener);
		button.removeKeyListener(keyArmingListener);
		button.removePropertyChangeListener(positionAndShapeListener);

		super.uninstallUI(c);
	}

	/** This redefines the edge, fill, iconRect, and textRect fields
	 * of this object.  Also once the fill is redefined, this should
	 * call <code>updateFillBounds()</code>.
	 */
	protected void updateLayout(AbstractButton button,ButtonInfo info) {
		
		Shape shape = (Shape)button.getClientProperty(SHAPE);
		int horizontalPosition = getHorizontalPosition(button);
		int verticalPosition = getVerticalPosition(button);

		int width = button.getWidth();
		int height = button.getHeight();

		String key = width+" "+height+" "+horizontalPosition+" "+verticalPosition;

		button.putClientProperty("FilledButtonUI.validationKey", key);
		
		int dx = 0;
		int dy = 0;


		if(horizontalPosition==POS_RIGHT || horizontalPosition==POS_ONLY) {
			width--;
		}
		if(verticalPosition==POS_BOTTOM || verticalPosition==POS_ONLY) {
			height--;
		}

		if(getFocusPainting(button)==PAINT_FOCUS_OUTSIDE || 
				getFocusPainting(button)==PAINT_FOCUS_BOTH) {
			if(horizontalPosition==POS_LEFT || horizontalPosition==POS_ONLY) {
				dx+=focusSize;
				width-=focusSize;
			}
			if(horizontalPosition==POS_RIGHT || horizontalPosition==POS_ONLY) {
				width-=focusSize;
			}
			if(verticalPosition==POS_TOP || verticalPosition==POS_ONLY) {
				dy+=focusSize;
				height-=focusSize;
			}
			if(verticalPosition==POS_BOTTOM || verticalPosition==POS_ONLY) {
				height-=focusSize;
			}
		} else {
			if(horizontalPosition==POS_RIGHT || horizontalPosition==POS_ONLY) {
				width--;
			}
			if(verticalPosition==POS_BOTTOM || verticalPosition==POS_ONLY) {
				height--;
			}

			if((verticalPosition==POS_BOTTOM || verticalPosition==POS_ONLY) &&
					buttonFill.getShadowHighlight(button)!=null) {
				height--;
			}
		}
		

		FontMetrics fm = button.getFontMetrics(button.getFont());

		info.viewRect.x = info.viewRect.y = info.textRect.x = info.textRect.y = info.textRect.width = info.textRect.height = 0;
		info.iconRect.x = info.iconRect.y = info.iconRect.width = info.iconRect.height = 0;
		info.viewRect.width = Short.MAX_VALUE;
		info.viewRect.height = Short.MAX_VALUE;

		SwingUtilities.layoutCompoundLabel(fm, 
				button.getText(), 
				button.getIcon(), 
				button.getVerticalAlignment(), 
				button.getHorizontalAlignment(), 
				button.getVerticalTextPosition(), 
				button.getHorizontalTextPosition(), 
				info.viewRect, 
				info.iconRect, 
				info.textRect,
				button.getIconTextGap());

		Insets textInsets = getTextPadding();
		Insets iconInsets = getIconPadding();

		Rectangle tempTextRect = new Rectangle(info.textRect);
		Rectangle tempIconRect = new Rectangle(info.iconRect);
		if(info.textRect.width>0) {
			tempTextRect.y -= textInsets.top;
			tempTextRect.x -= textInsets.left;
			tempTextRect.width += textInsets.left+textInsets.right;
			tempTextRect.height += textInsets.top+textInsets.bottom;
		}
		if(info.iconRect.width>0) {
			tempIconRect.y -= iconInsets.top;
			tempIconRect.x -= iconInsets.left;
			tempIconRect.width += iconInsets.left+iconInsets.right;
			tempIconRect.height += iconInsets.top+iconInsets.bottom;
		}

		Rectangle sum = getSum(new Rectangle[] { tempIconRect, tempTextRect});

		Insets padding = getContentInsets(button);

		float centerX = ((float)(button.getWidth()-padding.left-padding.right))/2f;
		float centerY = ((float)(button.getHeight()-padding.top-padding.bottom))/2f;

		float shiftX = centerX-((float)sum.width)/2f-sum.x+padding.left;
		float shiftY = centerY-((float)sum.height)/2f-sum.y+padding.top;
		
		//define the actual fill and border of the shape:
		//always fill the entire width/height we are allowed
		//(knowing that the width/height fields here are already
		//reduced to compensate for focus rings).
		//The size of this button is the responsibility of the
		//LayoutManager, and the preferred size is calculated
		//in getPreferredSize().  Here we just work with what
		//we're given.
		if(shape!=null) {
			Rectangle2D originalBounds = new Rectangle2D.Float();
			ShapeBounds.getBounds(shape,originalBounds);
			Rectangle2D newBounds = new Rectangle2D.Float(dx,dy,width,height);
			AffineTransform transform = TransformUtils.createAffineTransform(originalBounds,newBounds);
			info.fill.reset();
			info.edge.reset();
			info.fill.append(shape, true);
			info.edge.append(shape, true);
			info.fill.closePath();
			info.edge.closePath();
			info.fill.transform(transform);
			info.edge.transform(transform);
		} else {
			int minR = Math.min(height/2, width/2);
	
			int topRightRadius = Math.min(maxTopRightRadius,minR);
			int topLeftRadius = Math.min(maxTopLeftRadius,minR);
			int bottomRightRadius = Math.min(maxBottomRightRadius,minR);
			int bottomLeftRadius = Math.min(maxBottomLeftRadius,minR);
	
			float k = .22385763f*2;
	
			//define the shapes:
	
			GeneralPath fill = info.fill;
			GeneralPath edge = info.edge;
	
			fill.reset();
			edge.reset();
	
			//this is based on a 4x4 grid enumerating all the possible combinations:
			if(verticalPosition==POS_TOP && horizontalPosition==POS_LEFT) {
				edge.moveTo(width, 0);
				if(topLeftRadius==0) {
					edge.lineTo(0, 0);
				} else {
					edge.lineTo(topLeftRadius, 0);
					edge.curveTo(topLeftRadius-topLeftRadius*k, 0, 
							0, topLeftRadius-topLeftRadius*k, 
							0, topLeftRadius);
				}
				edge.lineTo(0, height);
				fill.append(edge, false);
				fill.lineTo(width, height);
				fill.lineTo(width,0);
			} else if( (verticalPosition==POS_TOP && horizontalPosition==POS_MIDDLE) ||
					(verticalPosition==POS_MIDDLE && horizontalPosition==POS_LEFT) ||
					(verticalPosition==POS_MIDDLE && horizontalPosition==POS_MIDDLE)) {
				edge.moveTo(width, 0);
				edge.lineTo(0, 0);
				edge.lineTo(0, height);
				fill.append(edge, false);
				fill.lineTo(width, height);
				fill.lineTo(width,0);
			} else if(verticalPosition==POS_TOP && horizontalPosition==POS_RIGHT) {
				edge.moveTo(width, height);
				if(topRightRadius==0) {
					edge.lineTo(width,0);
				} else {
					edge.lineTo(width, topRightRadius);
					edge.curveTo(width, topRightRadius-topRightRadius*k, 
							width-topRightRadius+topRightRadius*k, 0, 
							width-topRightRadius, 0);
				}
				edge.lineTo(0, 0);
				edge.lineTo(0, height);
				fill.append(edge, false);
				fill.lineTo(width, height);
			} else if(verticalPosition==POS_TOP && horizontalPosition==POS_ONLY) {
				edge.moveTo(width, height);
				if(topRightRadius==0) {
					edge.lineTo(width, 0);
				} else {
					edge.lineTo(width, topRightRadius);
					edge.curveTo(width, topRightRadius-topRightRadius*k, 
							width-topRightRadius+topRightRadius*k, 0, 
							width-topRightRadius, 0);
				}
				if(topLeftRadius==0) {
					edge.lineTo(0, 0);
				} else {
					edge.lineTo(topLeftRadius, 0);
					edge.curveTo(topLeftRadius-topLeftRadius*k, 0, 
							0, topLeftRadius-topLeftRadius*k, 
							0, topLeftRadius);
				}
				edge.lineTo(0, height);
				fill.append(edge, false);
				fill.lineTo(width, height);
	
				bottomRightRadius = 0;
				bottomLeftRadius = 0;
			} else if(verticalPosition==POS_MIDDLE) {
				edge.moveTo(width,height);
				edge.lineTo(width, 0);
				edge.lineTo(0, 0);
				edge.lineTo(0, height);
				fill.append(edge, false);
				fill.lineTo(width, height);
			} else if(verticalPosition==POS_BOTTOM && horizontalPosition==POS_LEFT) {
				edge.moveTo(width, 0);
				edge.lineTo(0, 0);
				if(bottomLeftRadius==0) {
					edge.lineTo(0,height);
				} else {
					edge.lineTo(0, height-bottomLeftRadius);
					edge.curveTo(0, height-bottomLeftRadius+bottomLeftRadius*k,
							bottomLeftRadius-bottomLeftRadius*k, height,
							bottomLeftRadius, height);
				}
				edge.lineTo(width, height);
				fill.append(edge, false);
				fill.lineTo(width, 0);
			} else if(horizontalPosition==POS_MIDDLE) {
				edge.moveTo(width, 0);
				edge.lineTo(0, 0);
				edge.lineTo(0, height);
				edge.lineTo(width, height);
				fill.append(edge, false);
				fill.lineTo(width, 0);
			} else if(horizontalPosition==POS_RIGHT && verticalPosition==POS_BOTTOM) {
				edge.moveTo(width, 0);
				edge.lineTo(0, 0);
				edge.lineTo(0, height);
				if(bottomRightRadius==0) {
					edge.lineTo(width, height);
				} else {
					edge.lineTo(width-bottomRightRadius, height);
					edge.curveTo(width-bottomRightRadius+bottomRightRadius*k, height, 
							width, height-bottomRightRadius+bottomRightRadius*k, 
							width, height-bottomRightRadius);
				}
				edge.lineTo(width, 0);
				fill.append(edge, false);
				edge.closePath();
			} else if(horizontalPosition==POS_ONLY && verticalPosition==POS_BOTTOM) {
				edge.moveTo(width, 0);
				edge.lineTo(0, 0);
				if(bottomLeftRadius==0) {
					edge.lineTo(0, height);
				} else {
					edge.lineTo(0, height-bottomLeftRadius);
					edge.curveTo(0, height-bottomLeftRadius+bottomLeftRadius*k,
							bottomLeftRadius-bottomLeftRadius*k, height,
							bottomLeftRadius, height);
				}
				if(bottomRightRadius==0) {
					edge.lineTo(width, height);
				} else {
					edge.lineTo(width-bottomRightRadius, height);
					edge.curveTo(width-bottomRightRadius+bottomRightRadius*k, height, 
							width, height-bottomRightRadius+bottomRightRadius*k, 
							width, height-bottomRightRadius);
				}
				edge.lineTo(width, 0);
				fill.append(edge, false);
				edge.closePath();
			} else if(horizontalPosition==POS_LEFT && verticalPosition==POS_ONLY) {
				edge.moveTo(width, 0);
				if(topLeftRadius==0) {
					edge.lineTo(0, 0);
				} else {
					edge.lineTo(topLeftRadius, 0);
					edge.curveTo(topLeftRadius-topLeftRadius*k, 0,
							0, topLeftRadius-topLeftRadius*k,
							0, topLeftRadius);
				}
				if(bottomLeftRadius==0) {
					edge.lineTo(0, height);
				} else {
					edge.lineTo(0, height-bottomLeftRadius);
					edge.curveTo(0, height-bottomLeftRadius+bottomLeftRadius*k,
							bottomLeftRadius-bottomLeftRadius*k, height,
							bottomLeftRadius, height);
				}
				edge.lineTo(width, height);
				fill.append(edge, false);
				fill.lineTo(width, 0);
			} else if(verticalPosition==POS_ONLY && horizontalPosition==POS_RIGHT) {
				edge.moveTo(0,0);
				edge.lineTo(0, height);
				if(bottomRightRadius==0) {
					edge.lineTo(width, height);
				} else {
					edge.lineTo(width-bottomRightRadius, height);
					edge.curveTo(width-bottomRightRadius+bottomRightRadius*k, height,
							width, height-bottomRightRadius+bottomRightRadius*k,
							width, height-bottomRightRadius );
				}
				if(topRightRadius==0) {
					edge.lineTo(width, 0);
				} else {
					edge.lineTo(width, topRightRadius);
					edge.curveTo(width,topRightRadius-topRightRadius*k,
							width-topRightRadius+topRightRadius*k, 0,
							width-topRightRadius, 0);
				}
				edge.lineTo(0, 0);
				fill.append(edge, false);
				edge.closePath();
			} else { //if(horiziontalPosition==ONLY && verticalPosition==ONLY)
				if(topLeftRadius==0) {
					edge.moveTo(0, 0);
				} else {
					edge.moveTo(topLeftRadius, 0);
					edge.curveTo(topLeftRadius-topLeftRadius*k, 0,
							0, topLeftRadius-topLeftRadius*k,
							0, topLeftRadius);
				}
				if(bottomLeftRadius==0) {
					edge.lineTo(0, height);
				} else {
					edge.lineTo(0, height-bottomLeftRadius);
					edge.curveTo( 0, height-bottomLeftRadius+bottomLeftRadius*k,
							bottomLeftRadius-bottomLeftRadius*k, height,
							bottomLeftRadius, height );
				}
				if(bottomRightRadius==0) {
					edge.lineTo(width, height);
				} else {
					edge.lineTo( width-bottomRightRadius, height);
					edge.curveTo(width-bottomRightRadius+bottomRightRadius*k, height,
							width, height-bottomRightRadius+bottomRightRadius*k,
							width, height-bottomRightRadius );
				}
				if(topRightRadius==0) {
					edge.lineTo(width, 0);
				} else {
					edge.lineTo(width, topRightRadius);
					edge.curveTo(width,topRightRadius-topRightRadius*k,
							width-topRightRadius+topRightRadius*k, 0,
							width-topRightRadius, 0);
				}
				edge.lineTo(topLeftRadius, 0);
				fill.append(edge, false);
				edge.closePath();
			}
			fill.closePath();
	
			AffineTransform transform = AffineTransform.getTranslateInstance(dx, dy);
	
			fill.transform(transform);
			edge.transform(transform);
	
			if(button.getVerticalAlignment()==SwingConstants.CENTER &&
					button.getVerticalTextPosition()==SwingConstants.CENTER &&
					info.textRect.width>0) {
				Font font = button.getFont();
				int unusedAscent = getUnusedAscent(fm,font);
				int ascent = fm.getAscent()-unusedAscent;
	
				shiftY = (int)(-sum.y+centerY-ascent/2-unusedAscent+padding.top-textInsets.top);
			}
		}
		
		info.iconRect.setFrame( info.iconRect.x + shiftX, 
				info.iconRect.y + shiftY, 
				info.iconRect.width, 
				info.iconRect.height );
		info.textRect.setRect( (int)(info.textRect.x + shiftX+.5f), 
				(int)(info.textRect.y + shiftY+.5f), 
				info.textRect.width, 
				info.textRect.height);
		
		info.updateFillBounds();
	}
	
	/** This may be used to take some shortcuts in rendering the button if
	 * it is assumed that -- when enabled and isContentArea is true --
	 * the filled area of this button is opaque.
	 */
	public abstract boolean isFillOpaque();
	
	public boolean contains(JComponent c, int x, int y) {
		AbstractButton button = (AbstractButton)c;
		ButtonInfo info = getButtonInfo(button);
		if(isLayoutValid(button)==false)
			updateLayout(button,info);
		return info.fill.contains(x,y);
	}
}