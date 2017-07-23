package org.bjb;

public class UnitBettingStrategy implements BettingStrategy {

		public int getWager() {
			return Preferences.getBetUnit();
		}
}
