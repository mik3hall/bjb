/*
 * @(#)MathG.java
 *
 * $Date: 2009-09-07 07:44:40 -0500 (Mon, 07 Sep 2009) $
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
package com.bric.math;

import java.util.Arrays;
import java.util.Random;

/** This provides some alternative implementations of a few methods from
 * the Math class.
 * <P>This class may use approximations with various levels of error.  The "G"
 * in the name stands for "Graphics", because it was originally conceived
 * as a tool to speed up graphics.  When I iterate over every pixel in an image
 * to perform some operation: I don't really need the precision that the Math
 * class offers.
 * <P>Many thanks to Oleg E. for some insights regarding machine error and
 * design.
 *
 * @see http://javagraphics.blogspot.com/2009/05/math-studying-performance.html
 */
public abstract class MathG {
	/** Runs some tests comparing Math and MathG.
	 */
    public static void main(String[] args) {
		System.out.println("Running comparison of Math vs MathG on "+System.getProperty("os.name")+" "+System.getProperty("os.version")+", Java "+System.getProperty("java.version"));
    	testIncreasingMax();
    	testEverything();
    	System.out.println("Done.");
    }
    
    private static void testEverything() {
		System.out.println("\tCalling testEverything()");
    	long[] times = new long[200];
		double[] values = new double[1000000];
		Random random = new Random(0);
		for(int a = 0; a<values.length; a++) {
			if(false) { //only positive numbers
				values[a] = (random.nextDouble())*10000;
			} else { //include negative numbers
				values[a] = (random.nextDouble()-.5)*10000*2;
			}
		}
		
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				sin01(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		System.out.println("\tMathG.sin01() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				sin00004(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		System.out.println("\tMathG.sin00004() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				Math.sin(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		System.out.println("\tMath.sin() median time: "+times[times.length/2]+" ms");
		
		/////////////////////////////
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				cos01(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		System.out.println("\tMathG.cos01() median time: "+times[times.length/2]+" ms");

		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				cos00004(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		System.out.println("\tMathG.cos00004() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				Math.cos(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		System.out.println("\tMath.cos() median time: "+times[times.length/2]+" ms");

		////////////////////////////////
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				floorDouble(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		System.out.println("\tMathG.floorDouble() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				floorInt(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		System.out.println("\tMathG.floorInt() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				Math.floor(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		System.out.println("\tMath.floorDouble() median time: "+times[times.length/2]+" ms");
		
		/////////////////////////
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				ceilDouble(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		System.out.println("\tMathG.ceilDouble() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				ceilInt(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		System.out.println("\tMathG.ceilInt() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				Math.ceil(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		System.out.println("\tMath.ceilDouble() median time: "+times[times.length/2]+" ms");
		
		///////////////////////////////////

		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				roundDouble(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		System.out.println("\tMathG.roundDouble() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				roundInt(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		System.out.println("\tMathG.roundInt() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				Math.round(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		System.out.println("\tMath.round() median time: "+times[times.length/2]+" ms");
	
		/////////////
		
		double maxError = 0;
		for(int a = 0; a<values.length; a++) {
			double error = sin01(values[a])-Math.sin(values[a]);
			if(error<0) error = -error;
			if(error>maxError)
				maxError = error;
		}
		System.out.println("max error for sin01 = "+maxError);
		
		maxError = 0;
		for(int a = 0; a<values.length; a++) {
			double error = sin00004(values[a])-Math.sin(values[a]);
			if(error<0) error = -error;
			if(error>maxError)
				maxError = error;
		}
		System.out.println("max error for sin00004 = "+maxError);
		

		maxError = 0;
		for(int a = 0; a<values.length; a++) {
			double error = ceilDouble(values[a])-Math.ceil(values[a]);
			if(error<0) error = -error;
			if(error>maxError)
				maxError = error;
		}
		System.out.println("max error for ceil = "+maxError);

		maxError = 0;
		for(int a = 0; a<values.length; a++) {
			double error = floorDouble(values[a])-Math.floor(values[a]);
			if(error<0) error = -error;
			if(error>maxError)
				maxError = error;
		}
		System.out.println("max error for floor = "+maxError);

		maxError = 0;
		for(int a = 0; a<values.length; a++) {
			double error = roundDouble(values[a])-Math.round(values[a]);
			if(error<0) error = -error;
			if(error>maxError)
				maxError = error;
		}
		System.out.println("max error for round = "+maxError);
    }
    
    private static void testIncreasingMax() {
		System.out.println("\tCalling testIncreasingMax()");
		double max = 2;
		while(max<2e10) {
			System.out.println("max: "+max);
			long[] times = new long[200];
			double[] values = new double[1000000];
			Random random = new Random(0);
			for(int a = 0; a<values.length; a++) {
				values[a] = (random.nextDouble()-.5)*max*2;
			}
			
			
			for(int a = 0; a<times.length; a++) {
				Thread.yield();
				times[a] = System.currentTimeMillis();
				for(int b = 0; b<values.length; b++) {
					sin01(values[b]);
				}
				times[a] = System.currentTimeMillis()-times[a];
			}
			
			Arrays.sort(times);
			System.out.println("\tMathG.sin01() median time: "+times[times.length/2]+" ms");
			
			for(int a = 0; a<times.length; a++) {
				Thread.yield();
				times[a] = System.currentTimeMillis();
				for(int b = 0; b<values.length; b++) {
					sin00004(values[b]);
				}
				times[a] = System.currentTimeMillis()-times[a];
			}
			
			Arrays.sort(times);
			System.out.println("\tMathG.sin00004() median time: "+times[times.length/2]+" ms");
			
			for(int a = 0; a<times.length; a++) {
				Thread.yield();
				times[a] = System.currentTimeMillis();
				for(int b = 0; b<values.length; b++) {
					Math.sin(values[b]);
				}
				times[a] = System.currentTimeMillis()-times[a];
			}
			
			Arrays.sort(times);
			System.out.println("\tMath.sin() median time: "+times[times.length/2]+" ms");
			
			
			double maxError = 0;
			for(int a = 0; a<values.length; a++) {
				double error = sin01(values[a])-Math.sin(values[a]);
				if(error<0) error = -error;
				if(error>maxError)
					maxError = error;
			}
			if(maxError>.011) { //this is not supposed to happen!
				System.err.println("max error for sin01 = "+maxError);
			}
			
			maxError = 0;
			for(int a = 0; a<values.length; a++) {
				double error = sin00004(values[a])-Math.sin(values[a]);
				if(error<0) error = -error;
				if(error>maxError)
					maxError = error;
			}
			if(maxError>.00004) { //this is not supposed to happen!
				System.err.println("max error for sin00004 = "+maxError);
			}
	
			max = max*10;
		}
	}

	/** Finds the closest integer that is less than or equal to the argument as a double.
	 * @warning do not use an argument greater than 1e10, or less than 1e-10.
	 */
	public static final double floorDouble(double d) {
		int i;
		if(d>=0) {
			i = (int)d;
		} else {
			i = -((int)(-d))-1;
		}
		return i;
	}
	
	/** Finds the closest integer that is less than or equal to the argument as an int.
	 * @warning do not use an argument greater than 1e10, or less than 1e-10.
	 */
	public static final int floorInt(double d) {
		int i;
		if(d>=0) {
			i = (int)d;
		} else {
			i = -((int)(-d))-1;
		}
		return i;
	}

	/** Rounds a double to the nearest integer value.
	 * @warning do not use an argument greater than 1e10, or less than 1e-10.
	 */
	public static final int roundInt(double d) {
		int i;
		if(d>=0) {
			i = (int)(d+.5);
		} else {
			i = (int)(d-.5);
		}
		return i;
	}
	
	/** Rounds a double to the nearest integer value.
	 * @warning do not use an argument greater than 1e10, or less than 1e-10.
	 */
	public static final double roundDouble(double d) {
		int i;
		if(d>=0) {
			i = (int)(d+.5);
		} else {
			i = (int)(d-.5);
		}
		return i;
	}
	
	/** Finds the closest integer that is greater than or equal to the argument as an int.
	 * @warning do not use an argument greater than 1e10, or less than 1e-10.
	 */
	public static final int ceilInt(double d) {
		int i;
		if(d>=0) {
			i = -((int)(-d))+1;
		} else {
			i = (int)(d);
		}
		return i;
	}
	
	/** Finds the closest integer that is greater than or equal to the argument as a double.
	 * @warning do not use an argument greater than 1e10, or less than 1e-10.
	 */
	public static final double ceilDouble(double d) {
		int i;
		if(d>=0) {
			i = -((int)(-d))+1;
		} else {
			i = (int)(d);
		}
		return i;
	}
	
	private static final double PI = Math.PI;
	private static final double TWO_PI = 2.0*Math.PI;
	private static final double PI_OVER_2 = Math.PI/2.0;
	private static double[] sinCoefficients01 = createSinApproximation(new double[] {0, Math.PI/2});
	private static double[] sinCoefficients00004 = createSinApproximation(new double[] {0, Math.PI/4, Math.PI/2});
	
	/** Creates a polynomial equation that approximates the first arc of the sine curve.
	 */
	private static double[] createSinApproximation(double[] points) {
		double[][] coefficientsMatrix = new double[points.length*2][points.length*2+1];
		for(int row = 0; row<coefficientsMatrix.length; row+=2) {
			//make one row focusing on the value of sin(x),
			//and the next row focusing on the value of sin'(x)
			for(int column = 0; column<coefficientsMatrix[row].length-1; column++) {
				int power = points.length*2-column-1;
				coefficientsMatrix[row][column] = Math.pow(points[row/2], power);
				if(power==0) { //no derivative for this one
					coefficientsMatrix[row+1][column] = 0;
				} else {
					coefficientsMatrix[row+1][column] = power*Math.pow(points[row/2], power-1);
				}
			}
			coefficientsMatrix[row][coefficientsMatrix[row].length-1] = Math.sin(points[row/2]);
			coefficientsMatrix[row+1][coefficientsMatrix[row].length-1] = Math.cos(points[row/2]);
		}
		
		Equations.solve(coefficientsMatrix, true);
		double[] returnValue = new double[coefficientsMatrix.length];
		for(int a = 0; a<returnValue.length; a++) {
			returnValue[a] = coefficientsMatrix[a][coefficientsMatrix[a].length-1];
		}
		return returnValue;
	}
	
	/** Returns an approximate value of the sin(v) that should be
	 * within plus-or-minus .0108 of the value returned by Math.sin().
	 * <P>If the argument is greater in magnitude than 1e10, then
	 * this delegates to Math.sin().
	 * 
	 * @param v
	 * @return an approximate value of sin(v)
	 */
	public static final double sin01(double v) {
		/** This is exactly the same code as sin00004, except it
		 * uses the smaller polynomial.  I avoided refactoring
		 * the code to use a common method in the theory that
		 * avoiding adding a method to the stack trace may
		 * shave off just a tiny bit of performance.  Normally
		 * this would be excessive, but my goal is optimum
		 * performance in really tight loops: every line counts!
		 */
		double finalMultiplier;
		if(v<0) {
			finalMultiplier = -1;
			v = -v;
		} else {
			finalMultiplier = 1;
		}
		
		if(v>1.0E10) {
			if(printedOverflowError==false) {
				printedOverflowError = true;
				System.err.println("Warning: MathG is not designed to estimate the sine of values of 1.0e10.  Math.sin() will be used, which may result in slower performance.");
			}
			return finalMultiplier*Math.sin(v);
		} else if(v<.01) {
			//if we're that small, then y=sin(x) -> y=x
			//sin(.01)-.01 = -1.6666583333574403E-7
			return v*finalMultiplier;
		}
		
		if(v>TWO_PI) {
			//v = v%TWO_PI;
			long m = (long)(v/TWO_PI);
			v = v-m*TWO_PI;
		}
		if(v>PI) {
			v = v-PI;
			finalMultiplier = -finalMultiplier;
		}
		if(v>PI_OVER_2) {
			v = PI-v;
		}
		
		double result = sinCoefficients01[0];
		for(int a = 1, n = sinCoefficients01.length; a<n; a++) {
			result = result*v+sinCoefficients01[a];
		}
		result = result*finalMultiplier;
		
		return result;
	}
	
	/** Controls whether an error message has been printed to
	 * System.err. yet this session regarding calling
	 * calculateSin() on values that are too large.
	 */
	private static boolean printedOverflowError = false;

	/** Returns an approximate value of the cos(v) that should be
	 * within plus-or-minus .0108 of the value returned by Math.cos().
	 * <P>If the argument is greater in magnitude than 1e10, then
	 * this delegates to Math.cos().
	 * 
	 * @param v
	 * @return an approximate value of cos(v)
	 */
	public static final double cos01(double v) {
		if(v>1e10 || v<1e-10)
			return Math.cos(v);
		return sin01(v-PI_OVER_2);
	}

	/** Returns an approximate value of the sin(v) that should be
	 * within plus-or-minus .00004 of the value returned by Math.sin().
	 * <P>If the argument is greater in magnitude than 1e10, then
	 * this delegates to Math.sin().
	 * 
	 * @param v
	 * @return an approximate value of sin(v)
	 */
	public static final double sin00004(double v) {
		double finalMultiplier;
		if(v<0) {
			finalMultiplier = -1;
			v = -v;
		} else {
			finalMultiplier = 1;
		}
		
		if(v<.01) {
			//if we're that small, then y=sin(x) -> y=x
			//sin(.01)-.01 = -1.6666583333574403E-7
			return v*finalMultiplier;
		} else if(v>1.0E10) {
			if(printedOverflowError==false) {
				printedOverflowError = true;
				System.err.println("Warning: MathG is not designed to estimate the sine of values of 1.0e10.  Math.sin() will be used, which may result in slower performance.");
			}
			return finalMultiplier*Math.sin(v);
		}
		
		if(v>TWO_PI) {
			//v = v%TWO_PI;
			long m = (long)(v/TWO_PI);
			v = v-m*TWO_PI;
		}
		if(v>PI) {
			v = v-PI;
			finalMultiplier = -finalMultiplier;
		}
		if(v>PI_OVER_2) {
			v = PI-v;
		}
		
		double result = sinCoefficients00004[0];
		for(int a = 1, n = sinCoefficients00004.length; a<n; a++) {
			result = result*v+sinCoefficients00004[a];
		}
		result = result*finalMultiplier;
		
		return result;
	}

	/** Returns an approximate value of the cos(v) that should be
	 * within plus-or-minus .00004 of the value returned by Math.cos().
	 * <P>If the argument is greater in magnitude than 1e10, then
	 * this delegates to Math.cos().
	 * 
	 * @param v
	 * @return an approximate value of cos(v)
	 */
	public static final double cos00004(double v) {
		if(v>1e10 || v<1e-10)
			return Math.cos(v);
		return sin00004(v-PI_OVER_2);
	}
}
