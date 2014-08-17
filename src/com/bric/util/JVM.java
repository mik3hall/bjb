/*
 * @(#)JVM.java
 *
 * $Date: 2009-09-07 19:19:45 -0500 (Mon, 07 Sep 2009) $
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
package com.bric.util;

import java.security.AccessControlException;

/** Static methods relating to the JVM. */
public class JVM {

	
	/** This converts the system property "java.version" to a float value.
	 * This drops rightmost digits until a legitimate float can be parsed.
	 * <BR>For example, this converts "1.6.0_05" to "1.6".
	 * <BR>This value is cached as the system property "java.major.version".  Although
	 * technically this value is a String, it will always be parseable as a float.
	 * @throws AccessControlException this may be thrown in unsigned applets!  Beware!
	 */
	public static float getMajorJavaVersion() throws AccessControlException {
		String majorVersion = System.getProperty("java.major.version");
		if(majorVersion==null) {
			String s = System.getProperty("java.version");
			float f = -1;
			int i = s.length();
			while(f<0 && i>0) {
				try {
					f = Float.parseFloat(s.substring(0,i));
				} catch(Exception e) {}
				i--;
			}
			majorVersion = Float.toString(f);
			System.setProperty("java.major.version",majorVersion);
		}
		return Float.parseFloat(majorVersion);
	}

	/** 
	 * 
	 * @param catchSecurityException if true and an exception occurs,
	 * then -1 is returned.
	 * @return the major java version, or -1 if this can't be determined/
	 */
	public static float getMajorJavaVersion(boolean catchSecurityException) {
		try {
			return getMajorJavaVersion();
		} catch(RuntimeException t) {
			if(catchSecurityException) {
				t.printStackTrace();
				return -1;
			}
			throw t;
		}
	}
}
