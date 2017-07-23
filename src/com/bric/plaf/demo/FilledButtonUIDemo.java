/*
 * @(#)FilledButtonUIDemo.java
 *
 * $Date: 2012-10-02 17:30:57 -0500 (Tue, 02 Oct 2012) $
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
package com.bric.plaf.demo;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

import com.bric.plaf.BevelButtonUI;
import com.bric.plaf.CapsuleButtonUI;
import com.bric.plaf.GradientButtonUI;
import com.bric.plaf.PlasticButtonUI;
import com.bric.plaf.RecessedButtonUI;
import com.bric.plaf.RetroButtonUI;
import com.bric.plaf.RoundRectButtonUI;
import com.bric.plaf.SquareButtonUI;
import com.bric.plaf.TexturedButtonUI;
import com.bric.plaf.VistaButtonUI;
import com.bric.plaf.XPButtonUI;

/** A simple demo of different FilledButtonUI's. 
 * 
 * @name FilledButtonUI
 * @title Buttons: New UIs
 * @release August 2009
 * @blurb Sure the buttons in <a href="http://developer.apple.com/mac/library/technotes/tn2007/tn2196.html#BUTTONS">Apple's Tech Note 2196</a> are great, but they're so... black-box-ish.
 * And they're only available on Macs.
 * <p>This article sets up a <a href="https://javagraphics.java.net/doc/com/bric/plaf/FilledButtonUI.html">new framework</a> for <code>ButtonUIs</code>, and provides around 10 UI's to choose from.
 * (But you can make more if you want to...)
 * @see <a href="http://javagraphics.blogspot.com/2009/08/buttons-new-uis.html">Buttons: New UIs</a>
 */
public class FilledButtonUIDemo extends UIDemo {
	private static final long serialVersionUID = 1L;

	public static BufferedImage createBlurbGraphic(Dimension preferredMaxSize) {
		JButton button = new JButton("RoundRectButtonUI");
		button.setUI(new RoundRectButtonUI());
		button.setSize(button.getPreferredSize());
		BufferedImage image = new BufferedImage(button.getWidth(), button.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		button.paint(g);
		g.dispose();
		return image;
	}
	
	public static void main(String[] args) {
		try {
			String lf = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(lf);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		
		JFrame f = new JFrame("Button UIs");
		f.getContentPane().add(new FilledButtonUIDemo());
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public FilledButtonUIDemo() {
		super(new ComponentUI[] {
				new BevelButtonUI(),
				new CapsuleButtonUI(),
				new GradientButtonUI(),
				new PlasticButtonUI(),
				new RecessedButtonUI(),
				new RetroButtonUI(),
				new RoundRectButtonUI(),
				new SquareButtonUI(),
				new TexturedButtonUI(),
				new VistaButtonUI(),
				new XPButtonUI()
		}, new FilledButtonUIHandler() );
	}
}
