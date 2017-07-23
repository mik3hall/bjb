package org.bjb;

public class EffectsLocker extends Thread {
	private static final EffectsLocker instance = new EffectsLocker();
	private static int effectsCount = 0;
	
	public synchronized static void acquire() {
		if (effectsCount == 0)
			synchronized(instance) { 
				instance.notify(); 
			}
		effectsCount++;
	}
	
	public synchronized static void init() {
		instance.start();
	}
	
	public synchronized static void release() {
		if (effectsCount == 0) 
			throw new IllegalStateException("EffectsLocker release called when lock not acquired");
		effectsCount--;
		if (effectsCount == 0)
			synchronized(instance) { instance.notify(); }
	}
	
	public void run() {
		System.out.println("EL run");
		while(true) {
			System.out.println("EL LOCKER synchronized");
			synchronized(this) {
				try {
					System.out.println("EL first wait");
					wait();
				}
				catch (InterruptedException iex) {}
			}
			System.out.println("EL EFFECT_LOCK synchronized");
			synchronized(BlackJackApp.EFFECT_LOCK) {
				synchronized(this) {
					try {
						System.out.println("EL second wait");
						notifyAll();
						wait();
					}
					catch (InterruptedException iex) {}
				}
			}
		}
	}
}
