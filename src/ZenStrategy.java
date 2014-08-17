package org.bjb;public class ZenStrategy extends Strategy implements UnbalancedStrategy {	private int rc;						// running count	private int keyCount;				// key count	private int decks = 0;				// decks for count	private int multiplier = 3;			// bet increase for better than key count	private int betUnit = 10;	protected int lastTopCard = 0;		// track deck top card for shuffle check		// Based on the book BLACKBELT IN BLACKJACK - Arnold Snyder		public String getName() { return "Zen"; }	public boolean isBalanced() { return false; }	public int getBet() {		if (decks == 1) {			if (rc < 0) return betUnit;			else if (rc < 2) return betUnit*2;			return betUnit*4;		}		else if (decks == 2) {			if (rc < 0) return betUnit;			else if (rc < 4) return betUnit*2;			else if (rc < 6) return betUnit*3;			else if (rc < 8) return betUnit*4;			return betUnit*6;		}		else {			if (rc < 0) return betUnit;			else if (rc < 6) return betUnit*2;			else if (rc < 8) return betUnit*3;			else if (rc < 12) return betUnit*4;			else if (rc < 16) return betUnit*6;			return betUnit*8;		}	}	public boolean insure(Deck d) {		if (decks == 1 || decks == 2) {			if (rc > 0) return true;		}		else if (rc > 2) return true;		return false;	}	public String updateBasic(Deck d,Hand currentHand,Hand dealersHand) {		int v = currentHand.bestScore();		int dv = dealersHand.dealersUp();		switch (v) {			case 16:				if (dv == 10 && rc > 0)					return "Stand";				break;			case 15:				if (dv == 10 && rc > 2)					return "Stand";				break;			case 12:				if (dv == 3 && rc > 0)					return "Stand";				else if (dv == 2 && rc > 2)					return "Stand";				break;			case 10:				if (rc > 2) return "Double down";				break;			default:				break;		}		return super.updateBasic(rc,d,currentHand,dealersHand);	}			public void updateCount(Deck d,Card c) { 				if (decks == 0 || decks != (d.cards.length / 52)) {		// If not set or changed			decks = d.cards.length / 52;			switch (decks) {				case 1:					rc = 0;					keyCount = 2;					break;				case 2:					rc = -4;					keyCount = 1;					break;				case 6:					rc = -20;					keyCount = -4;					break;				case 8:					rc = -28;					keyCount = -6;					break;				default:					break;			}		}		if (d.topCard < lastTopCard) {		// must of been shuffled			rc = 0;		}		else lastTopCard = d.topCard;		// save current		int v = c.iValue;		if (v >= 2 && v <= 6) rc += 1;		if (v == 7 & (c.suit.equals("h") || c.suit.equals("d")))			rc += 1;		if (v == 10 | v == 1) rc -= 1;	}	public int getCount() { return rc; }	public int getInitialRunningCount() {		int irc = -99;		// If you see this we have a bug		switch (decks) {			case 1:				irc = 0;				break;			case 2:				irc = -4;				break;			case 4:				break;		// TODO need this handled			case 6:				irc = -20;				break;			case 8:				irc = -28;				break;			default:				irc = -28;				break;		}			return irc;	}		public int getKeyCount() {		int kc = -99;			// If you see this we have a bug		switch (decks) {			case 1:				kc = 2;				break;			case 2:				kc = 1;				break;			case 4:				break;		// TODO need this handled			case 6:				kc = -4;				break;			case 8:				kc = -6;				break;			default:				kc = -6;				break;		}			return kc;	}		// supported charts	protected boolean isDoubleChart() { return true; }}