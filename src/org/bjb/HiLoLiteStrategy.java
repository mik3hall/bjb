package org.bjb;

public class HiLoLiteStrategy extends BasicStrategy implements BalancedStrategy {

	protected int rc;						// running count
	protected int trueCount;				// count per deck
	
	public HiLoLiteStrategy(int decksNum) { super(decksNum); }
	
	public String getName() { return "Hi-Lo lite"; }

	public boolean isBalanced() { return true; }
	
	public boolean insure(Deck d) {
		if (rc > 2) return true;
		return false;
	}
	
	public void updateCount(Deck d,Card c) { 
		int v = c.iValue;
		if (v >= 2 && v <= 6) rc += 1;
		if (v == 10 | v == 1) rc -= 1;
		if (!Simulation.xverbose)
			System.out.println("HLL card " + v + " rc " + rc);
		trueCount = d.trueCount(rc);
	}

	public int getCount() { return trueCount; }
	public int getRunningCount() { return rc; }
	
	public String updateBasic(Deck d,Hand currentHand,Hand dealersHand) {
//		if (getRules().isH17())
//			return updateH17(d,currentHand,dealersHand);
		return updateS17(d,currentHand,dealersHand);
	}
	
	private String updateS17(Deck d,Hand currentHand,Hand dealersHand) {
		int v = currentHand.bestScore();
		if (v == 21) return "Stand";
		int dv = dealersHand.dealersUp();
		int sv = currentHand.splitValue();
		boolean isSurrender = BlackJackApp.canSurrender();
		if (decksNum == 1 || decksNum == 2) {
			if (isSurrender && sv != 0) {
				switch (sv) {
					case 8:
						if (dv == 9 && getCount() >= 4)
							return "Surrender";
						break;
					case 7:
						if ((dv == 9 || dv == 10 || dv == 1) && getCount() >= 2)
							return "Surrender";
						break;
					default:
						break;
				}
			}
			switch (v) {
				case 16:
					if (isSurrender) {
						if (dv == 8 && getCount() >= 2)
							return "Surrender";
						break;
					}
					if ((dv == 7 || dv == 8 || dv == 1) && getCount() >= 4)
						return "Stand";
					break;
				case 15:
					if (isSurrender) {
						if (dv == 8 && getCount() >= 4)
							return "Surrender";
						break;
					}
					if (dv == 2 && getCount() >= -2)
						return "Stand";
					else if (dv == 9 && getCount() >= 4)
						return "Stand";
					break;
				case 14:
					if (isSurrender) {
						if ((dv == 9 || dv == 1) && getCount() >= 4)
							return "Surrender";
						break;
					}
					if ((dv == 2 || dv == 3 || dv == 4) && getCount() >= -2)
						return "Stand";
					break;
				case 13:
					if (isSurrender) {
						if (dv == 10 && getCount() >= 4)
							return "Surrender";
						break;
					}
					if ((dv == 4 || dv == 5 || dv == 6) && getCount() >= -2)
						return "Stand";
					break;
				case 11:
					if (currentHand.canDoubleDown() && (dv == 8 || dv == 9 || dv == 10) && getCount() >= -2)
						return "Double down";
					break;
				case 10:
					if ((dv == 7 || dv == 8) && getCount() >= -2)
						return "Double down";
					break;
				case 9:
					if ((dv == 5 || dv == 6) && getCount() >= -2)
						return "Double down";
					else if (dv == 8 && getCount() >= 4)
						return "Double down";
					break;
				case 8:
					if (dv == 3 && getCount() >= 4)
						return "Double down";
					else if ((dv == 4 || dv == 5) && getCount() >= 2)
						return "Double down";
					else if (dv == 6 && getCount() >= 0)
						return "Double down";
					break;
				default: 
					break;
			}
		}
		// 1-2 deck + shoe games
		if (isSurrender && sv == 8 && dv == 10 && getCount() >= 0)
			return "Surrender";
		switch (v) {
			case 16:
				if (isSurrender) {
					if ((dv == 9 || dv == 10 || dv == 1) && getCount() >= 0)
						return "Surrender";
					break;
				}
				if (dv == 9 && getCount() >= 2)
					return "Stand";
				else if (dv == 10 && getCount() >= 0)
					return "Stand";
				break;
			case 15:
				if (isSurrender) {
					if (dv == 9 && getCount() >= 2)
						return "Surrender";
					else if ((dv == 10 || dv == 1) && getCount() >= 0)
						return "Surrender";
					break;
				}
				if (dv == 10 && getCount() >= 2)
					return "Stand";
				break;
			case 13:
				if (isSurrender) {
					if (dv == 10 && getCount() >= 4)
						return "Surrender";
					break;
				}
				if ((dv == 2 || dv == 3) && getCount() >= 0)
					return "Stand";
				break;
			case 12:
				if (dv == 2 && getCount() >= 2)
					return "Stand";
				else if ((dv == 3 || dv == 4 || dv == 5 || dv == 6) && getCount() >= 0)
					return "Stand";
				break;
			case 11:
				if (currentHand.canDoubleDown() && dv == 1 && getCount() >= 0)
					return "Double down";
				break;
			case 10:
				if (currentHand.canDoubleDown() && dv == 9 && getCount() >= 0)
					return "Double down";
				else if (currentHand.canDoubleDown() && (dv == 10 || dv == 1) && getCount() >= 2)
					return "Double down";
				break;
			case 9:
				if (currentHand.canDoubleDown() && (dv == 2 || dv == 3 || dv == 4) && getCount() >= 0)
					return "Double down";
				else if (currentHand.canDoubleDown() && dv == 7 && getCount() >= 2)
					return "Double down";
				break;
			default:
				break;
		}
		return super.updateBasic(d,currentHand,dealersHand);
	}
	
	private String updateH17(Deck d,Hand currentHand,Hand dealersHand) {
		System.out.println("updateH17");
		return super.updateBasic(d, currentHand, dealersHand);
//		return null;
	}
}
