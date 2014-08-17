/*
 * @(#)EmptyPathException.java
 *
 * $Date: 2009-02-20 01:34:41 -0600 (Fri, 20 Feb 2009) $
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
package com.bric.geom;

/** This indicates that a path had no shape data.
 * <P>This means it had no lines, quadratic or cubic
 * segments in it (although it may have had a MOVE_TO
 * and a CLOSE segment).
 *
 */
public class EmptyPathException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EmptyPathException() {
	}

	public EmptyPathException(String message) {
		super(message);
	}

	public EmptyPathException(Throwable cause) {
		super(cause);
	}

	public EmptyPathException(String message, Throwable cause) {
		super(message, cause);
	}

}
