package org.bjb;// Import all the Java API classes needed by this programimport java.io.*;import java.awt.*;import java.awt.event.*;import javax.swing.*;import javax.swing.border.*;class BlackJackGame {	// Variable declarations	JTextField decks;	static Deck deck;	static final Rules rules = new Rules();	static int rounds = 0;	static String elapsedTime;	static long startTime;	Hand currentHand;	Hand playersHand;	Hand dealersHand;	private static final int DELAY_TIME = 850;	private static final int MAX_PLAYERS = 7;//	Hand splitHand;	ImageIcon splitImg = null, soft_doubleImg = null, hard_doubleImg = null, standImg = null;	Preferences preferences = null;	JPanel playingArea;	BufferedReader keyboardInput;	boolean gameOver = true;	int decksNUM = 1;	int playersNUM = 1;	int currPlayer = 0;			// Active player if more than one	int gamePlayer = 0;			// default number for human game player	Player[] players = null;	private static final int[] incs = new int[] { 100, 60, 30, -30, -60, -100 };	boolean hideHoleCard = true;	// Method declarations	public BlackJackGame(Preferences preferences,JTextField decks,JPanel playingArea) {	// Constructor		this.preferences = preferences; 		this.decks = decks;		decksNUM = preferences.getDecksNum();		if (decksNUM > 2) hideHoleCard = false;		playersNUM = preferences.getPlayersNum();		deck = new Deck(decksNUM);		keyboardInput = new BufferedReader(new InputStreamReader(System.in));		this.playingArea = playingArea;		// Card w=71 h=96		players = new Player[playersNUM];		players[0] = new Player(new HighLowStrategy(),true,5+151*(MAX_PLAYERS-1),90,playingArea);		if (playersNUM > 1) {			int x = 5+151*(MAX_PLAYERS-1), y = 90;			for (int i=1;i<playersNUM;i++) {				x -= 151;				y += incs[i-1]; 				players[i] = new Player(new HighLowStrategy(),false,x,y,playingArea);			}		}		startTime = System.currentTimeMillis();	}		public static void updateElapsed() {		long elapsed = System.currentTimeMillis() - startTime;		long hh = elapsed / (1000 * 60 * 60);				long mm = (elapsed / (1000 * 60)) - (hh * 60);		StringBuffer elapsedTime = new StringBuffer();		if (hh > 0) elapsedTime.append(new Long(hh).toString()).append(":");		else elapsedTime.append(":");		if (mm < 10) elapsedTime.append("0");		elapsedTime.append(new Long(mm).toString());		BlackJackGame.elapsedTime = elapsedTime.toString();	}		static Rules getRules() { return rules; }	public void setDecks(int decks) {				if (decks != decksNUM) {			decksNUM = decks;			if (decksNUM > 2) hideHoleCard = false;			else hideHoleCard = true;			deck = new Deck(decksNUM);		}	}	public void setPlayers(int playersNUM) {		setGameOver(true);		updateElapsed();		this.playersNUM = playersNUM;		players = new Player[playersNUM];		players[0] = new Player(new HighLowStrategy(),true,5+151*(MAX_PLAYERS-1),90,playingArea);		if (playersNUM > 1) {			int x = 5+151*(MAX_PLAYERS-1), y = 90;			for (int i=1;i<playersNUM;i++) {				x -= 151;				y += incs[i-1]; 				if (i % 7 == 0)					players[i] = new Player(new HighLowStrategy(),false,x,y,playingArea);				else if (i % 7 == 1) 					players[i] = new Player(new BasicStrategy(),false,x,y,playingArea);				else if (i % 7 == 2) 					players[i] = new Player(new KORookieStrategy(),false,x,y,playingArea);				else if (i % 7 == 3)					players[i] = new Player(new ThorpBasicStrategy(),false,x,y,playingArea);				else if (i % 7 == 4)					players[i] = new Player(new Illustrious18Strategy(),false,x,y,playingArea);				else if (i % 7 == 5)					players[i] = new Player(new KOPreferredStrategy(),false,x,y,playingArea);				else if (i % 7 == 6)					players[i] = new Player(new Red7EasyStrategy(),false,x,y,playingArea);			}		}		final SwingWorker worker = new SwingWorker() {			public Object construct() {				initialDeal();				return null;			}		};		worker.start();/*		SwingUtilities.invokeLater(new Runnable() {			public void run() { initialDeal(); }		});*/	}	public void setGameOver(boolean over) {//		new Exception("game over").printStackTrace();		gameOver = over;	}		public boolean isGameOver() { 		return gameOver; 	}		public void processChart() {		if (isGameOver()) return;		Player p = players[currPlayer];		ChartSelector cs = new ChartSelector(p,deck,dealersHand);	}		/**	 * Return an instance of the named strategy seeded with information from previous 	 * one if provided	 **/	public static Strategy getStrategy(String name,Strategy previous) {		Strategy s = null;		if (name.equals("Basic Strategy")) 			s = new BasicStrategy();		else if (name.equals("Hi-Lo"))			s = new HighLowStrategy();		// should update strategy count by enumerating players and using		// get visible count method		return null;	}	/**	 * update counts for all players except an optional excluded player 	 * for cases like the player already knew his own hole card	 **/	private void updateCounts(Card c,Player exclude) {//		System.out.println("update counts " + c + " exclude " + exclude);		for (int i=0;i<playersNUM;i++) {			if (exclude != null && players[i].equals(exclude)) continue;//			System.out.println("\tupdating " + players[i]);			players[i].updateCount(deck,c);		}	}		boolean hit() {		if (gameOver) {//			new Exception("Hit on game over").printStackTrace();			Toolkit.getDefaultToolkit().beep();			return false;		}		Card cd = deck.deal();		players[currPlayer].addCard(cd);		BlackJackApp.playDrop();		updateCounts(cd,null);				if (!players[currPlayer].busted()) {			if (!players[currPlayer].isHuman() && !players[currPlayer].isSplit())				players[currPlayer].show(false,hideHoleCard);			else {				players[currPlayer].show(false,false);				BlackJackApp.setDefault(players[currPlayer].updateBasic(deck,dealersHand));			}		}		else {	// BUSTED			players[currPlayer].lost();			BlackJackApp.playPlayerBust();			players[currPlayer].show(false,false);					Card hc = players[currPlayer].getHand().peekCard(0);			updateCounts(hc,players[currPlayer]);	// TODO assumes player knew his own card for count so excluded here - verify this.			if (players[currPlayer].hasNextHand()) {	// Q. A split hand to play?				players[currPlayer].switchHand();		// Yes - switch to it				return true;							// continue player			}			return false;				// switch player		}		return true;			// continue player	}		// TODO add rule to restrict single additional card draw to split aces	// also possibly add rule to restrict to a single aces split, no second resplit	// possible bug, split didn't appear to hit first hand when it should of??????	boolean split() {		if (!playersHand.canSplit()) return false;		// Unsuccessful split		Card c = playersHand.removeCard();		Hand splitHand = new Hand(playingArea,playersHand);		splitHand.addCard(c);		BlackJackApp.playDrop();									updateCounts(c,null);							// The hole card pair needs counting				Card players_hole = deck.deal();		playersHand.addCard(players_hole);		BlackJackApp.playDrop();		updateCounts(players_hole,null);				// dealt up//		players[currPlayer].updateCount(deck,players_hole);	// player knows it now		Card split_hole = deck.deal();		splitHand.addCard(split_hole);		players[currPlayer].addSplit(splitHand);		BlackJackApp.playDrop();		updateCounts(split_hole,null);//		players[currPlayer].updateCount(deck,split_hole);	// knows this too		players[currPlayer].show(false,false);		if (players[currPlayer].isHuman())			BlackJackApp.setDefault(players[currPlayer].updateBasic(deck,dealersHand));		return true;	}		boolean doubleDown() {		Hand h = players[currPlayer].getHand();		int v = h.bestScore();		if (!rules.isDOA() && !(v == 10 || v == 11)) {			// TODO support 9-10-11 variation??? 			System.out.println("double down against rules");			Toolkit.getDefaultToolkit().beep();						return true;		}		if (!h.canDoubleDown()) {			System.out.println("double down not allowed");			Toolkit.getDefaultToolkit().beep();						return true;		}		Card cd = deck.deal();		players[currPlayer].addCard(cd);		BlackJackApp.playDrop();		updateCounts(cd,null);		players[currPlayer].doubleDown();		players[currPlayer].show(false,false);		return stand();				// let stand handle mulitple hand check (only splits currently)	}			boolean insure() {		players[currPlayer].setInsured();		return false;	}		boolean stand() {		if (players[currPlayer].hasNextHand()) {			players[currPlayer].switchHand();			return true;			// continue player		}		return false;				// switch player	}		void initialDeal() {		BlackJackApp.guiDisable();		deck.checkShuffle();		setGameOver(false);		updateElapsed();		rounds++;		// set the wagers before we show any hands		for (int i=0;i<playersNUM;i++) {			if (players[i].isHuman()) {				try {					if (!players[i].enterWager())					players[i].setWager(players[i].getBet());				}				catch (Exception ex) { ex.printStackTrace(); }			}			else 				players[i].setWager(players[i].getBet());		}		playersHand = new Hand(playingArea,5+151*(MAX_PLAYERS-1),90);		currentHand = playersHand;//		splitHand = null;		dealersHand = new Hand(playingArea);		for (int i=0; i<2; i++) {			Card c = deck.deal();			currentHand.addCard(c);			if (i == 0)				BlackJackApp.playDraw();			else 				BlackJackApp.playDrop();			if (players[0].isHuman() && i == 0 && hideHoleCard) 				players[0].updateCount(deck,c);			else if (players[0].isHuman() && i == 0)				updateCounts(c,null);			else if (i != 0)				updateCounts(c,null);			Card dc = deck.deal();			dealersHand.addCard(dc);			if (i == 0)				BlackJackApp.playDraw();			else 				BlackJackApp.playDrop();					if (i != 0)				updateCounts(dc,null);		}		java.awt.Point pscr = playingArea.getLocationOnScreen();		dealersHand.show(true,true);		players[0].setHand(currentHand,dealersHand.peekCard(1));		players[0].setCurrent(true);			// flash the current player		if (players[0].isHuman())			players[0].show(false,false);		else 			players[0].show(false,hideHoleCard);					if (playersNUM > 1) {			int x = 5+151*(MAX_PLAYERS-1), y = 90;			for (int i=1;i<playersNUM;i++) {				x -= 151;				y += incs[i-1]; 				Hand h = new Hand(playingArea,x,y);				for (int j=0; j<2; j++) {					Card c = deck.deal();					h.addCard(c);					if (i == 0)						BlackJackApp.playDraw();					else 						BlackJackApp.playDrop();					if (j == 0 && hideHoleCard) 						players[i].updateCount(deck,c);					else updateCounts(c,null);				}				players[i].setHand(h,dealersHand.peekCard(1));				if (players[i].isHuman()) 					players[i].show(false,false);				else players[i].show(false,hideHoleCard);			}		}				// Do a round to check for insurance		if (dealersHand.peekCard(1).getValue() == 1) {					for (int i=0;i<playersNUM;i++) {				if (players[i].isHuman()) {					JOptionPane pane = new JOptionPane(					"<html>"+					"<head>"+					"<style type=\"text/css\">"+					"b { font: 14pt \"Lucida Grande\" }"+					"p { font: 12pt \"Lucida Grande\"; margin-top: 8px }"+					"</style>"+					"</head>"+					"<b>Insurance?</b><p>"+					"The dealer is showing a ace.<br>"+					"Do you want to take insurance?",					JOptionPane.WARNING_MESSAGE					);					Object[] options = { "Yes", "No" };					pane.setOptions(options);					pane.setInitialValue(options[1]);					JDialog dialog = pane.createDialog(new JFrame(""), "Warning: Dealer Ace up card");					dialog.show();					if (((String)pane.getValue()).equals("Yes")) 						players[i].setInsured();				}				else {					String option = players[i].updateBasic(deck,dealersHand);					if (option.equals("Insurance"))						players[i].setInsured();				}			}		}		// Check for initial blackjacks		if (currentHand.blackjack() && !dealersHand.blackjack()) {			BlackJackApp.playBlackjack();			players[0].setWager(players[0].getWager()*3/2);			players[0].won();			players[0].show(false,false);			updateElapsed();			if (playersNUM > 1) 					nextPlayer();			BlackJackApp.playBtnReset();			return;		}		else if (dealersHand.blackjack()) {		// Dealer natural?			BlackJackApp.playDealerBJ();			Card dc = dealersHand.peekCard(0);			updateCounts(dc,null);			dealersHand.show(true,false);			for (int i=0;i<playersNUM;i++) {	// everybody loses unless they also have natural				Card c = players[i].getHand().peekCard(0);				updateCounts(c,null);			// update everybody for hole card				if (players[i].blackjack()) continue;		// a push so just skip				players[i].lost();				players[i].show(false,false);			}			setGameOver(true);			BlackJackApp.playBtnReset();			updateElapsed();		}/*		else if (dealersHand.peekCard(1).getValue() == 1) {		// Do a round checking for insurance results			for (int i=0;i<playersNUM;i++) {	// everybody loses unless they also have natural				Card c = players[i].getHand().peekCard(0);				updateCounts(c,null);			// update everybody for hole card				if (players[i].blackjack()) continue;		// a push so just skip				players[i].lostInsurance();				players[i].show(false,false);			}					}*/		else if (players[currPlayer].isHuman()) {			BlackJackApp.setDefault(players[currPlayer].updateBasic(deck,dealersHand));		}		else {			autoPlay(players[currPlayer]);			nextPlayer();		}		BlackJackApp.guiEnable();	}	void nextPlayer() {		BlackJackApp.guiDisable();		while (currPlayer != (playersNUM - 1)) {			players[currPlayer].setCurrent(false);		// reset possible flash on prior			currPlayer++;			players[currPlayer].setCurrent(true);		// flash the new current player			if (players[currPlayer].isHuman()) {				players[currPlayer].show(false,false);				BlackJackApp.setDefault(players[currPlayer].updateBasic(deck,dealersHand));				BlackJackApp.guiEnable();				return;			// Let the man play			}			else {				players[currPlayer].show(false,hideHoleCard);				autoPlay(players[currPlayer]);			}		}		// Need to verify not all player hands busted or were blackjack TODO		boolean activePlayers = false;		for (int i=0;i<playersNUM;i++) {			if (players[i].isSplit()) {			// Make sure we start on the 1st hand				players[i].splitReset();			}			if (!players[i].busted() && !players[i].blackjack()) {				activePlayers = true;				break;			} 			boolean activeSplit = false;			while (players[i].hasNextHand()) {				Hand h = players[i].nextHand();				if (!(h.busted())) {					activeSplit = true;					break;				}			}			if (activeSplit) {				activePlayers = true;				break;			}		}		if (activePlayers && dealersHand.mustHit()) 			while (dealersHand.mustHit()) {				Card dc = deck.deal();				dealersHand.addCard(dc);				BlackJackApp.playDrop();				updateCounts(dc,null);				dealersHand.show(true,false);			}		else dealersHand.show(true,false);							// Done somehow update counts for dealers hole card		Card dc = dealersHand.peekCard(0);		updateCounts(dc,null);				// TODO verify nextplayers are getting payed for blackjack??? autoplay does, but subsequent human players????		if (dealersHand.busted()) {					// TODO need to check counts in here????			BlackJackApp.playDealerBust();			for (int i=0;i<playersNUM;i++) {				if (players[i].isSplit())					players[i].splitReset();				if (!(players[i].busted() || players[i].blackjack())) {					players[i].won();					if (!players[i].isSplit() && hideHoleCard) {		// Not already counted for bust or bj & not split, count hole card now						Card hc = players[i].getHand().peekCard(0);						updateCounts(hc,players[i]);										}				}				while (players[i].hasNextHand()) {					Hand h = players[i].nextHand();					if (!(h.busted()))						players[i].won();				}				players[i].show(false,false);			}		}		else if (activePlayers) for (int i=0;i<playersNUM;i++) {		// TODO counts? split hands????			if (players[i].isSplit())			// Make sure we start on the 1st hand				players[i].splitReset();			if (!(players[i].busted() || players[i].blackjack())) {				if (!players[i].isSplit()) {			// Not already counted for bust or bj & not split, count hole card now					Card hc = players[i].getHand().peekCard(0);					if (hideHoleCard)						updateCounts(hc,players[i]);				}				if (dealersHand.bestScore() > players[i].bestScore()) {					players[i].lost();								}				else if (dealersHand.bestScore() < players[i].bestScore())					players[i].won();				while (players[i].hasNextHand()) {					Hand h = players[i].nextHand();					if (!h.busted()) {						if (dealersHand.bestScore() > h.bestScore())							players[i].lost();						else if (dealersHand.bestScore() < h.bestScore())							players[i].won();					}				}			}			players[i].show(false,false);		}		currPlayer = 0;		setGameOver(true);		updateElapsed();		BlackJackApp.setDefault("Play");		BlackJackApp.guiEnable();	}		public synchronized void delay(int ms) {		try { wait(ms); }		catch (InterruptedException iex) {}	}		void autoPlay(Player p) {		playersHand = players[currPlayer].getHand();		currentHand = playersHand;		if (playersHand.blackjack()) {			BlackJackApp.playBlackjack();			delay(DELAY_TIME);					players[currPlayer].setWager(players[currPlayer].getWager()*3/2);					players[currPlayer].won();			players[currPlayer].show(false,false);			return;		}		String strategy = players[currPlayer].updateBasic(deck,dealersHand);		boolean done = false;		while (!done) {			if (strategy.equals("Split")) {				delay(DELAY_TIME);				split();//				if (players[currPlayer].hasNextHand())//					players[currPlayer].switchHand();//				else {//					done = true;			// TODO - get rid of this when split debugged//					continue;//				}			}			else if (strategy.equals("Double down")) {				delay(DELAY_TIME);				if (!doubleDown()) {		// true indicates continue with current players next hand					done = true;					continue;				}			}			else if (strategy.equals("Stand")) {				delay(DELAY_TIME);				if (!stand()) {				// true indicates continue with current players next hand					done = true;					continue;				}			}			else {				delay(DELAY_TIME);				if (!hit()) {		// true indicates continue with current players next hand					done = true;					continue;				}			}			strategy = players[currPlayer].updateBasic(deck,dealersHand);		}		updateElapsed();	}		void tie() {		players[currPlayer].setCurrent(false);			// reset possible prior flash		if (playersNUM > 1) {			if (currPlayer == (playersNUM - 1)) {				currPlayer = 0;				updateElapsed();				setGameOver(true);				return;			}			else currPlayer++;			players[currPlayer].setCurrent(true);		// flash the new current player			if (!players[currPlayer].isHuman()) 				autoPlay(players[currPlayer]);		}		updateElapsed();		setGameOver(true);	}	void showBalances() {		for (int i=0;i<playersNUM;i++)			players[i].showBalance();	}		void showChart() {		ImageIcon ico = null;/*		if (basicStrategy.getText().equals("Split")) {			if (splitImg == null)				splitImg = Card.getIcon("images/pair_splitting.jpg");			ico = splitImg;		}		else if (basicStrategy.getText().equals("Double down")) {			if (currentHand.isSoft()) {				if (soft_doubleImg == null)					soft_doubleImg = Card.getIcon("images/soft_doubling.jpg");				ico = soft_doubleImg;			}			else {				if (hard_doubleImg == null)					hard_doubleImg = Card.getIcon("images/hard_doubling.jpg");				ico = hard_doubleImg;			}		}		else {			if (standImg == null)				standImg = Card.getIcon("images/standing.jpg");			ico = standImg;		}		ChartViewer viewer = new ChartViewer(ico);		viewer.setVisible(true);*/	}	}	// End of BlackJackGame class