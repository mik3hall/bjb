/*
 * @(#)UIEffect.java
 *
 * $Date: 2012-07-16 22:16:27 -0500 (Mon, 16 Jul 2012) $
 *
 * Copyright (c) 2012 by Jeremy Wood.
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class UIEffect {
	
	public static enum State { NOT_STARTED, ACTIVE, FINISHED };
	
	final long startTime = System.currentTimeMillis();
	final protected Timer timer;
	final protected JComponent component;
	/** The time (in ms) since this effect was created. */
	protected long elapsedTime;
	/** This is the fraction "elapsedTime / getDuration()". */
	protected float progress = 0;
	private boolean changeListenersUsed = false;
	
	protected final long duration;
	List<ChangeListener> changeListeners = new Vector<ChangeListener>();
	
	State state = State.NOT_STARTED;
	
	public UIEffect(JComponent comp,int totalDuration,int updateInterval) {
		component = comp;
		duration = totalDuration;
		timer = new Timer(updateInterval,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				elapsedTime = System.currentTimeMillis()-startTime;
				float fraction = ((float)elapsedTime)/((float)getDuration());
				if(fraction<1) {
					setState( State.ACTIVE );
					setProgress(fraction);
				} else {
					timer.stop();
					setState( State.FINISHED );
					if(!setProgress(1)) { //still fire listeners because the state changed:
						fireChangeListeners();
					}
				}
				if(component!=null)
					component.repaint();
			}
		});
		
		//invoke later so the subclass can regain
		//control of this thread and add listeners
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setProgress(0);
				timer.start();
			}
		});
	}
	
	public float getProgress() {
		return progress;
	}
	
	protected boolean setProgress(float newValue) {
		if(progress==newValue) {
			if(!changeListenersUsed) {
				changeListenersUsed = true;
				fireChangeListeners();
			}
			return false;
		}
		changeListenersUsed = true;
		progress = newValue;
		fireChangeListeners();
		return true;
	}
	
	public JComponent getComponent() {
		return component;
	}
	
	public State getState() {
		return state;
	}
	
	private boolean setState(State state) {
		if(state==null) throw new NullPointerException();
		if(this.state==state)
			return false;
		this.state = state;
		return true;
	}
	
	public void stop() {
		timer.stop();
		if( setState( State.FINISHED ) )
			fireChangeListeners();
	}
	
	public void addChangeListener(ChangeListener l) {
		changeListeners.add(l);
	}
	
	public void removeChangeListener(ChangeListener l) {
		changeListeners.remove(l);
	}
	
	protected void fireChangeListeners() {
		for(int a = 0; a<changeListeners.size(); a++) {
			try {
				ChangeListener l = changeListeners.get(a);
				l.stateChanged( new ChangeEvent( this ) );
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Returns the duration (in ms) of this effect. */
	public final long getDuration() {
		return duration;
	}
}
