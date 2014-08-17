package org.bjb;/** * A concrete class with no bet spread that implements basic strategy **/public class BasicStrategy extends Strategy {	public String getName() { return "Basic Strategy"; }		public boolean isBalanced() { return false; }		public boolean insure(Deck d) { return false; }		public int getBet() { return 10; }		public void updateCount(Deck d,Card c) {}		public int getCount() { return 0; }		public String updateBasic(Deck d,Hand currentHand,Hand dealersHand) {		return super.updateBasic(0,d,currentHand,dealersHand);	}		// supported charts	protected boolean isSurrenderChart() { return false; }	protected boolean isSplitChart() { return false; }	protected boolean isDoubleChart() { return false; }	protected boolean isSoftChart() { return false; }}