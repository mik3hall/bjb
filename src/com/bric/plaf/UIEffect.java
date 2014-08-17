package com.bric.plaf;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.Timer;

public abstract class UIEffect {
	final long startTime = System.currentTimeMillis();
	final protected Timer timer;
	final protected JComponent component;
	/** The time (in ms) since this effect was created. */
	protected long elapsedTime;
	/** This is the fraction "elapsedTime / getDuration()". */
	protected float progress;
	
	public UIEffect(JComponent comp) {
		component = comp;
		timer = new Timer(40,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				elapsedTime = System.currentTimeMillis()-startTime;
				float fraction = ((float)elapsedTime)/((float)getDuration());
				if(fraction<1) {
					progress = fraction;
				} else {
					progress = 1;
				}
				if(elapsedTime>getDuration()) {
					timer.stop();
				}
				component.repaint();
			}
		});
		timer.start();
	}
	
	public float getProgress() {
		return progress;
	}
	
	public JComponent getComponent() {
		return component;
	}
	
	public boolean isActive() {
		return timer.isRunning();
	}
	
	/** Returns the duration (in ms) of this effect. */
	public abstract long getDuration();
	
	/** Paints this effect.
	 * @param g the graphics to paint to.  This will be
	 * a copy of the original <code>Graphics2D</code>
	 * destination, so it is safe to clip, transform, 
	 * and composite this however you want to without
	 * restoring its original state.
	 */
	public abstract void paint(Graphics2D g);
	
	public abstract boolean isBackground();
}
