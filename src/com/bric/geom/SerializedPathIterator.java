/*
 * @(#)SerializedPathIterator.java
 *
 * $Date: 2009-03-13 01:03:11 -0500 (Fri, 13 Mar 2009) $
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

import java.awt.geom.PathIterator;

/** A PathIterator that parses serialized shape info.
 */
class SerializedPathIterator implements PathIterator {
    char[] c;
    int ctr = 0;
    double[] data = new double[6];
    int currentSegment = -1;
    int windingRule;
    
    public SerializedPathIterator(String s,int windingRule) {
    	if(!(windingRule==PathIterator.WIND_EVEN_ODD || windingRule==PathIterator.WIND_NON_ZERO))
    		throw new IllegalArgumentException("The winding rule must be PathIterator.WIND_NON_ZERO or PathIterator.WIND_EVEN_ODD");
    	
    	//TODO: is creating a char array a good idea?  Are we wasting memory use?
        c = s.toCharArray();
        this.windingRule = windingRule;
        next();
    }
    
    public int getWindingRule() {
        return windingRule;
    }
    
    public void next() {
        if(ctr==c.length+1) {
            ctr++;
            return;
        }
        if(ctr>=c.length) {
            throw new IllegalStateException("There are no more segments to iterate to.");
        }
        int terms;
        char k = c[ctr];
        switch(k) {
        case 'm':
        case 'M':
            currentSegment = PathIterator.SEG_MOVETO;
            terms = 2;
            break;
        case 'l':
        case 'L':
            currentSegment = PathIterator.SEG_LINETO;
            terms = 2;
            break;
        case 'q':
        case 'Q':
            currentSegment = PathIterator.SEG_QUADTO;
            terms = 4;
            break;
        case 'c':
        case 'C':
            currentSegment = PathIterator.SEG_CUBICTO;
            terms = 6;
            break;
        case 'z':
        case 'Z':
            currentSegment = PathIterator.SEG_CLOSE;
            terms = 0;
            break;
        default:
            System.err.println("\""+(new String(c))+"\"\nctr="+ctr);
        throw new RuntimeException("Unrecognized character in shape data: \'"+c[ctr]+"\'");
        }
        ctr+=2; //skip the marker, and the space
        if(terms>0)
            parse(terms);
    }
    
    private void parse(int terms) {
        for(int a = 0; a<terms; a++) {
            data[a] = parseTerm();
        }
    }
    
    private double parseTerm() {
        boolean negate = false;
        //don't use Float.parseFloat, we can do it
        //faster:
        double number = 0;
        char t = c[ctr];
        
        if(t=='-') {
            negate = true;
            ctr++;
            t = c[ctr];
        }
        long power = -1;
        
        while(t!=' ' && ctr<c.length) {
            if(t>='0' && t<='9') {
                int i = (t-'0');
                number = number*10+i;
                if(power>0) {
                    power *= 10;
                }
            } else if(t=='.') {
                power = 1;
            }
            
            ctr++;
            if(ctr<c.length)
                t = c[ctr];
        }
        if(negate)
            number = -number;
        number = number/power;
        ctr++;
        
        return number;
    }
    
    public int currentSegment(double[] d) {
        d[0] = data[0];
        d[1] = data[1];
        d[2] = data[2];
        d[3] = data[3];
        d[4] = data[4];
        d[5] = data[5];
        return currentSegment;
    }
    
    public int currentSegment(float[] f) {
        f[0] = (float)data[0];
        f[1] = (float)data[1];
        f[2] = (float)data[2];
        f[3] = (float)data[3];
        f[4] = (float)data[4];
        f[5] = (float)data[5];
        return currentSegment;
    }
    
    public boolean isDone() {
        return ctr>c.length+1;
    }
}
