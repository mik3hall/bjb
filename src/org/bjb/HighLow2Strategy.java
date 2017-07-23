package org.bjb;

public class HighLow2Strategy extends HighLowGenericStrategy implements BalancedStrategy {

	public HighLow2Strategy(int decksNum) { super(decksNum); }

	public String getName() { return "Hi-Lo Ranged"; }

	public String updateBasic(Deck d,Hand currentHand,Hand dealersHand) {
		if (getRules().isH17())
			return updateH17(d,currentHand,dealersHand);
		return updateS17(d,currentHand,dealersHand);
	}
	
	private String updateH17(Deck d,Hand currentHand,Hand dealersHand) {
		boolean soft = currentHand.isSoft();
		int v = currentHand.bestScore();
		int dv = dealersHand.dealersUp();
		int sv = currentHand.splitValue();
		// Check surrender
		if (BlackJackApp.canSurrender()) {
		}
		// Check insurance
		if (dv == 1) {
	
		}
		String split = null;
		
		// Check pair splits
		if (sv != 0)
			split = h17_split(sv,dv);
		if (split != null) return split;
		
		// Check soft hands
		boolean isDouble = getRules().canDouble(v) && currentHand.canDoubleDown();
		if (soft) return h17_soft(v,dv,isDouble);
		else if (v > 11) return h17_stiff(v,dv);
		else return h17_hard(v,dv,isDouble);
	}

	/**
	 * Handle splits 
	 * H17, no DAS, based on Table 10
	 * 
	 * @return
	 */
	private String h17_split(int sv,int dv) {
		if (getRules().isDAS()) 	// double not allowed after split
			return h17_das_split(sv,dv);
		switch (sv) {
			case 10:	// 10
				if (dv == 5) {
					if (getCount() >= 5) return "Split";
					return null;
				}
				else if (dv == 6) {
					if (getCount() >= 4) return "Split";
					return null;
				}
				else return "Stand";
			case 1: 
				return "Split";
			case 2:
				if (dv < 4 || dv > 7) return "Hit";
				return "Split";
			case 3:
				if (dv < 3 || dv > 7) return "Hit";
				else if (dv == 3) {
					if (getCount() >= 4) return "Split";
					return null;
				}
				else if (dv == 4) {
					if (getCount() >= 0) return "Split";
					return null;
				}
				return "Split";
			case 4:
			case 5:
				return null;
			case 6:
				if (dv > 6) return "Hit";
				else if (dv == 2) return null;
				else if (dv == 3) {
					if (getCount() >= -1) return "Split";
					return null;
				}
				return "Split";
			case 7:
				if (dv == 1 || dv > 7) return "Hit"; 
				return "Split";
			case 8:
				if (dv == 10) {
					if (getCount() < 6) return "Split";
					return null;
				}
				if (dv == 1) {
					if (getCount() >= -1) return "Split";
					return null;
				}
				return "Split";
			case 9:
				if (dv == 7 || dv == 10) return "Stand"; 
				else if (dv == 1) {
					if (getCount() >= 4) return "Split";
					return null;
				}
				return "Split";
			default:
				return null;
		}
	}

	private String h17_das_split(int sv,int dv) {
		switch (sv) {
			case 10:	// 10
				if (dv == 5) {
					if (getCount() >= 5) return "Split";
					return null;
				}
				else if (dv == 6) {
					if (getCount() >= 4) return "Split";
					return null;
				}
				else return "Stand";
			case 1: 
				return "Split";
			case 2:
				if (dv == 1 || dv > 7) return "Hit";
				return "Split";
			case 3:
				if (dv == 1 || dv > 7) return "Hit";
				else if (dv == 2) {
					if (getCount() >= 1) return "Split";
					return null;
				}
				return "Split";
			case 4:
				if (dv > 6 || dv < 4) return "Hit";
				else if (dv == 6) return "Split";
				else if (dv == 4) {
					if (getCount() >= 4) return "Split";
					return null;
				}
				else if (dv == 5) {
					if (getCount() >= -1) return "Split";
					return null;
				}
				else if (dv == 6) return "Split";
			case 5:
				return null;
			case 6:
				if (dv > 6 || dv == 1) return "Hit";
				return "Split";
			case 7:
				if (dv == 1 || dv > 7) return "Hit"; 
				return "Split";
			case 8:
				if (dv == 1) {
					if (getCount() >= -1) return "Split";
					return null;
				}
				return "Split";
			case 9:
				if (dv == 10) return "Stand"; 
				else if (dv == 1) {
					if (getCount() >= 3) return "Split";
					return null;
				}
				else if (dv == 7) {
					if (getCount() >= 3) return "Split";
				}
				return "Split";
			default:
				return null;
		}
	}
	
	private String h17_soft(int v,int dv,boolean isDouble) {
		switch (v) {
			case 21: return "Stand";
			case 20:
				if (!isDouble || dv > 6 || dv < 4) return "Stand";
				else if (dv == 4) {
					if (getCount() >= 6) return "Double down";
					return "Stand";
				}
				else if (dv == 5 || dv == 6) {
					if (getCount() >= 4) return "Double down";
					return "Stand";
				}
				return null;
			case 19:
				if (!isDouble || dv > 6 || dv < 3) return "Stand";
				else if (dv == 3) {
					if (getCount() >= 5) return "Double down";
					return "Stand";
				}
				else if (dv == 4) {
					if (getCount() >= 3) return "Double down";
					return "Stand";
				}
				else if (dv == 5) {
					if (getCount() >= 2) return "Double down";
					return "Stand";
				}
				else if (dv == 6) {
					if (getCount() >= 0) return "Double down";
					return "Stand";
				}
				return null;
			case 18:
				if (dv > 8 || dv == 1) return "Hit";
				else if (dv == 2) {
					if (isDouble && getCount() >= 0) return "Double down";
					return "Hit";
				}
				else if (dv < 7) {
					if (isDouble) return "Double down";
					return "Stand";
				}
				else if (dv == 7 || dv == 8) return "Stand";
				if (isDouble) return "Double down";
				return "Stand";
			case 17:
				if (dv > 6 || dv == 1) return "Hit";
				else if (dv == 2) {
					if (isDouble && getCount() >= 1) return "Double down";
					return "Hit";
				}
				else if (isDouble) return "Double down";
				return "Hit";
			case 16:
				if (dv > 6 || dv < 3) return "Hit";
				else if (dv == 3) {
					if (isDouble && getCount() >= 4) return "Double down";
					return "Hit";
				}
				else if (isDouble) return "Double down";
				return "Hit";
			case 15:
				if (dv > 6 || dv < 4) return "Hit";
				else if (dv == 4) {
					if (isDouble && getCount() >= 0) return "Double down";
					return "Hit";
				}
				else if (isDouble) return "Double down";
				return "Hit";
			case 14:
				if (dv > 6 || dv < 4) return "Hit";
				else if (dv == 4) {
					if (isDouble && getCount() >= 2) return "Double down";
					return "Hit";
				}
				else if (isDouble) return "Double down";
				return "Hit";
			case 13:
				if (dv > 6 || dv < 4) return "Hit";
				else if (dv == 4) {
					if (isDouble && getCount() >= 3) return "Double down";
					return "Hit";
				}
				else if (dv == 5) {
					if (isDouble && getCount() >= 0) return "Double down";
					return "Hit";
				}
				else if (isDouble) return "Double down";
				return "Hit";
			default:
				return null;
		}
	}

	private String  h17_stiff(int v,int dv) {
		if (v > 16) return "Stand";
		else if (v == 16) {
			if (dv == 10) {
				if (getCount() >= 0) return "Stand";
				return "Hit";
			}
			else if (dv < 7) return "Stand";
			else if (dv > 1 && dv < 7) return "Stand";
			else if (dv == 7) return "Hit";
			else if (dv == 8) {
				if (getCount() >= 6) return "Stand";
				return "Hit";
			}
			else if (dv == 9) {
				if (getCount() >= 5) return "Stand";
				return "Hit";
			}
			else if (dv == 1) {
				if (getCount() >= 3) return "Stand";
			}
			return "Stand";
		}
		else if (v == 15) {
			if (dv == 10) {
				if (getCount() >= 4) return "Stand";
				return "Hit";
			}
			else if (dv == 1) {
				if (getCount() >= 5) return "Stand";
				return "Hit";
			}
			else if (dv < 7) return "Stand";
			return "Hit";
		}
		else if (v == 14) {
			if (dv == 1 || dv > 6) return "Hit";
			return "Stand";
		}
		else if (v == 13) {
			if (dv == 1 || dv > 6) return "Hit";
			return "Stand";
		}
		else if (v == 12) {
			if (dv == 1 || dv > 6) return "Hit";
			else if (dv == 2) {
				if (getCount() >= 3) return "Stand";
				return "Hit";
			}
			else if (dv == 3) {
				if (getCount() >= 2) return "Stand";
				return "Hit";
			}
			else if (dv == 4) {
				if (getCount() >= 0) return "Stand";
				return "Hit";
			}
			return "Stand";
		}
		return "Hit";
	}
	
	private String h17_hard(int v,int dv,boolean isDouble) {
		if (v == 11) {
			if (dv == 1) {
				if (isDouble && getCount() >= 0) return "Double down";
				return "Hit";
			}
			if (isDouble) return "Double down";
			else return "Hit";
		}
		else if (v == 10) {
			if (dv == 10 || dv == 1) {
				if (isDouble && getCount() >= 3) return "Double down";
				return "Hit";
			}
			if (isDouble) return "Double down";
			return "Hit";
		}
		else if (v == 9) {
			if (dv == 1 || dv > 7) return "Hit";
			else if (dv == 2) {
				if (isDouble && getCount() >= 0) return "Double down";
				return "Hit";
			}
			else if (dv < 7) {
				if (isDouble) return "Double down";
				return "Hit";
			}
			else if (dv == 7) {
				if (isDouble && getCount() >= 3) return "Double down";
				return "Hit";
			}
			if (isDouble) return "Double down";
			return "Hit";
		}
		else if (v == 8) {
			if (dv < 4 || dv > 6) return "Hit";
			else if (dv == 4) {
				if (isDouble && getCount() >= 5) return "Double down";
				return "Hit";
			}
			else if (dv == 5) {
				if (isDouble && getCount() >= 3) return "Double down";
				return "Hit";
			}
			else if (dv == 6) {
				if (isDouble && getCount() >= 1) return "Double down";
				return "Hit";
			}
			if (isDouble) return "Double down";
			return "Hit";
		}
		// 7-6-5
		return "Hit";
	}
	
	private String updateS17(Deck d,Hand currentHand,Hand dealersHand) {
		boolean soft = currentHand.isSoft();
		int v = currentHand.bestScore();
		int dv = dealersHand.dealersUp();
		int sv = currentHand.splitValue();
		// Check surrender
		if (BlackJackApp.canSurrender()) {
		}
		// Check insurance
		if (dv == 1) {
			int decksNUM = d.decksNUM;
		}
		String split = null;
		
		// Check pair splits
		if (sv != 0)
			split = s17_split(sv,dv);
		if (split != null) return split;
		
		// Check soft hands
		boolean isDouble = getRules().canDouble(v) && currentHand.canDoubleDown();
		if (soft) return s17_soft(v,dv,isDouble);
		else if (v > 11) return s17_stiff(v,dv);
		else return s17_hard(v,dv,isDouble);
	}
	
	/**
	 * Handle splits 
	 * S17, no DAS, based on Table 11
	 * 
	 * @return
	 */
	private String s17_split(int sv,int dv) {
		if (getRules().isDAS()) 	// double not allowed after split
			return s17_das_split(sv,dv);
		switch (sv) {
			case 10:	// 10
				if (dv == 5) {
					if (getCount() >= 5) return "Split";
					return null;
				}
				else if (dv == 6) {
					if (getCount() >= 5) return "Split";
					return null;
				}
				else return "Stand";
			case 1: 
				return "Split";
			case 2:
				if (dv < 4 || dv > 7) return "Hit";
				return "Split";
			case 3:
				if (dv < 3 || dv > 7) return "Hit";
				else if (dv == 3) {
					if (getCount() >= 4) return "Split";
					return null;
				}
				else if (dv == 4) {
					if (getCount() >= 0) return "Split";
					return null;
				}
				return "Split";
			case 4:
			case 5:
				return null;
			case 6:
				if (dv > 6) return "Hit";
				else if (dv == 2) return null;
				else if (dv == 3) {
					if (getCount() >= -1) return "Split";
					return null;
				}
				return "Split";
			case 7:
				if (dv == 1 || dv > 7) return "Hit"; 
				return "Split";
			case 8:
				if (dv == 10) {
					if (getCount() < 6) return "Split";
					return null;
				}
				return "Split";
			case 9:
				if (dv == 7 || dv == 10) return "Stand"; 
				else if (dv == 1) {
					if (getCount() >= 4) return "Split";
					return null;
				}
				else if (dv == 2) {
					if (getCount() >= -1) return "Split";
					return null;
				}
				return "Split";
			default:
				return null;
		}
	}

	private String s17_das_split(int sv,int dv) {
		switch (sv) {
			case 10:	// 10
				if (dv == 5) {
					if (getCount() >= 5) return "Split";
					return null;
				}
				else if (dv == 6) {
					if (getCount() >= 5) return "Split";
					return null;
				}
				else return "Stand";
			case 1: 
				return "Split";
			case 2:
				if (dv == 1 || dv > 7) return "Hit";
				return "Split";
			case 3:
				if (dv == 1 || dv > 7) return "Hit";
				else if (dv == 2) {
					if (getCount() >= 1) return "Split";
					return null;
				}
				return "Split";
			case 4:
				if (dv > 6 || dv < 4) return "Hit";
				else if (dv == 6) return "Split";
				else if (dv == 4) {
					if (getCount() >= 4) return "Split";
					return null;
				}
				else if (dv == 5) {
					if (getCount() >= 0) return "Split";
					return null;
				}
				else if (dv == 6) return "Split";
			case 5:
				return null;
			case 6:
				if (dv > 6 || dv == 1) return "Hit";
				else if (dv == 2) {
					if (getCount() >= -1) return "Split";
					return null;
				}
				return "Split";
			case 7:
				if (dv == 1 || dv > 7) return "Hit"; 
				return "Split";
			case 8:
				return "Split";
			case 9:
				if (dv == 10) return "Stand"; 
				else if (dv == 1) {
					if (getCount() >= 4) return "Split";
					return null;
				}
				else if (dv == 7) {
					if (getCount() >= 3) return "Split";
				}
				return "Split";
			default:
				return null;
		}
	}
	
	private String s17_soft(int v,int dv,boolean isDouble) {
		switch (v) {
			case 21: return "Stand";
			case 20:
				if (isDouble && (dv == 5 || dv == 6)) {
					if (getCount() >= 5) return "Double down";
					return "Stand";
				}
				return "Stand";
			case 19:
				if (!isDouble || dv > 6 || dv < 3) return "Stand";
				else if (dv == 3) {
					if (getCount() >= 5) return "Double down";
					return "Stand";
				}
				else if (dv == 4) {
					if (getCount() >= 3) return "Double down";
					return "Stand";
				}
				else if (dv == 5) {
					if (getCount() >= 2) return "Double down";
					return "Stand";
				}
				else if (dv == 6) {
					if (getCount() >= 1) return "Double down";
					return "Stand";
				}
				return null;
			case 18:
				if (dv == 10 || dv == 9) return "Hit";
				else if (dv == 1) {
					if (isDouble && getCount() >= 0) return "Double down";
					return "Stand";
				}
				else if (dv == 7 || dv == 8) return "Stand";
				else if (dv < 4) {
					if (isDouble) return "Double down";
					return "Hit";
				}
				else if (dv < 7) {
					if (isDouble) return "Double down";
					return "Stand";
				}
				return null;
			case 17:
				if (dv > 6 || dv == 1) return "Hit";
				else if (dv == 2) {
					if (isDouble && getCount() >= 1) return "Double down";
					return "Hit";
				}
				else if (isDouble) return "Double down";
				return "Hit";
			case 16:
				if (dv > 6 || dv < 3) return "Hit";
				else if (dv == 3) {
					if (isDouble && getCount() >= 4) return "Double down";
					return "Hit";
				}
				else if (isDouble) return "Double down";
				return "Hit";
			case 15:
				if (dv > 6 || dv < 4) return "Hit";
				else if (dv == 4) {
					if (isDouble && getCount() >= 0) return "Double down";
					return "Hit";
				}
				else if (isDouble) return "Double down";
				return "Hit";
			case 14:
				if (dv > 6 || dv < 4) return "Hit";
				else if (dv == 4) {
					if (isDouble && getCount() >= 2) return "Double down";
					return "Hit";
				}
				else if (dv == 5) {
					if (isDouble && getCount() >= -1) return "Double down";
				}
				else if (isDouble) return "Double down";
				return "Hit";
			case 13:
				if (dv > 6 || dv < 4) return "Hit";
				else if (dv == 4) {
					if (isDouble && getCount() >= 4) return "Double down";
					return "Hit";
				}
				else if (dv == 5) {
					if (isDouble && getCount() >= 0) return "Double down";
					return "Hit";
				}
				else if (isDouble) return "Double down";
				return "Hit";
			default:
				return null;
		}
	}
	
	private String s17_stiff(int v,int dv) {
		if (v > 16) return "Stand";
		else if (v == 16) {
			if (dv == 10) {
				if (getCount() >= 0) return "Stand";
				return "Hit";
			}
			else if (dv == 1 || dv == 7) return "Hit";
			else if (dv == 8) {
				if (getCount() >= 6) return "Stand";
				return "Hit";
			}
			else if (dv == 9) {
				if (getCount() >= 5) return "Stand";
				return "Hit";
			}
			return "Stand";
		}
		else if (v == 15) {
			if (dv == 10) {
				if (getCount() >= 4) return "Stand";
				return "Hit";
			}
			else if (dv == 1 || dv > 6) return "Hit";
			return "Stand";
		}
		else if (v == 14) {
			if (dv == 1 || dv > 6) return "Hit";
			return "Stand";
		}
		else if (v == 13) {
			if (dv == 1 || dv > 6) return "Hit";
			else if (dv == 2) {
				if (getCount() >= -1) return "Stand";
				return "Hit";
			}
			return "Stand";	
		}
		else if (v == 12) {
			if (dv == 1 || dv > 6) return "Hit";
			else if (dv == 2) {
				if (getCount() >= 3) return "Stand";
				return "Hit";
			}
			else if (dv == 3) {
				if (getCount() >= 1) return "Stand";
				return "Hit";
			}
			else if (dv == 4) {
				if (getCount() >= 0) return "Stand";
				return "Hit";
			}
			return "Stand";
		}
		return "Hit";		
	}
	
	private String s17_hard(int v,int dv,boolean isDouble) {
		if (v == 11) {
			if (dv == 1) {
				if (isDouble && getCount() >= 1) return "Double down";
				return "Hit";
			}
			if (isDouble) return "Double down";
			return "Hit";
		}
		else if (v == 10) {
			if (dv == 10) {
				if (isDouble && getCount() >= 3) return "Double down";
				return "Hit";
			}
			else if (dv == 1) {
				if (isDouble && getCount() >= 4) return "Double down";
				return "Hit";
			}
			if (isDouble) return "Double down";   
			return "Hit";
		}
		else if (v == 9) {
			if (dv == 1 || dv > 7) return "Hit";
			else if (dv == 2) {
				if (isDouble && getCount() >= 0) return "Double down";
				return "Hit";
			}
			else if (dv == 7) {
				if (isDouble && getCount() >= 3) return "Double down";
				return "Hit";
			}
			if (isDouble) return "Double down";
			return "Hit";
		}
		else if (v == 8) {
			if (dv < 3 || dv > 6) return "Hit";
			else if (dv == 3) {
				if (isDouble && getCount() >= 9) return "Double down";
				return "Hit";
			}
			else if (dv == 4) {
				if (isDouble && getCount() >= 6) return "Double down";
				return "Hit";
			}
			else if (dv == 5) {
				if (isDouble && getCount() >= 3) return "Double down";
				return "Hit";
			}
			else if (dv == 6) {
				if (isDouble && getCount() >= 1) return "Double down";
				return "Hit";
			}
			if (isDouble) return "Double down";
			return "Hit";
		}
		// 7-6-5
		return "Hit";
	}
}
