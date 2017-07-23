package org.bjb;

public class HighLowGenericStrategy extends BasicStrategy {
	protected int rc;						// running count
	private int trueCount;					// count per deck
	
	public HighLowGenericStrategy(int decksNum) { super(decksNum); }

	public String getName() {
		throw new IllegalStateException("HighLowGenericStrategy ");
	}
	
	public boolean isBalanced() { return true; }
	
	public int getBet() { return benchmarkBet(); }
	
	// As indicated in "Professional Blackjack" Benchmark rules
	private int benchmarkBet() {
		if (trueCount > 3) return 100;
		else if (trueCount == 3) return 75;
		else if (trueCount == 2) return 50;
		else if (trueCount > -1) return 25;
		else return 10;		// all negative
	}
	
	public void reset() {
		rc = 0;
	}
	
	public void updateCount(Deck d,Card c) { 
		int v = c.iValue;
		if (v >= 2 && v <= 6) rc += 1;
		else if (v == 10 | v == 1) rc -= 1;
//		System.out.println("HL card " + v + " rc " + rc + " true count " + getCount());
		trueCount = d.trueCount(rc);
	}
	
	public int getCount() { return trueCount; }
	public int getRunningCount() { return rc; }
}
