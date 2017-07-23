/*
 * @(#)ButtonFill.java
 *
 * $Date: 2011-05-02 16:01:45 -0500 (Mon, 02 May 2011) $
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
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.AbstractButton;

/** Information for rendering a button's background and border.
 */
public abstract class ButtonFill {

	/** If non-null, this is a 1-pixel highlight painted
	 * below this button.  This may vary with the button's
	 * state, but if it is non-null in one state it should
	 * always be non-null.
	 * <P>If this is non-null, this may affect the height of
	 * the button.  You can return a transparent color to
	 * "trick" the UI into giving an extra pixel here, if desired.
	 */
	public abstract Color getShadowHighlight(AbstractButton button);

	/** Returns the current border for a button. */
	public abstract Paint getBorder(AbstractButton button,Rectangle fillRect);
	
	/** Returns the current fill for a button. */
	public abstract Paint getFill(AbstractButton button,Rectangle fillRect);
}
