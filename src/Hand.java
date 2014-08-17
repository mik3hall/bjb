package org.bjb;import java.awt.*;import javax.swing.*;class Hand {	// Variable declarations	int numCards;	Card cards[];	boolean soft = false;	static int MaxCards = 12;	static ImageIcon back = Card.getIcon("images/b1fv.png");	JPanel playingArea = null;	int x = 0;	int y = 90;								// default	final static Color purple = new Color(255,0,255);	private boolean split = false;		// DEBUG		// Constructors	public Hand(JPanel playingArea) {			// Constructor		numCards = 0;		cards = new Card[MaxCards];		this.playingArea = playingArea;	}		public Hand(JPanel playingArea,Hand prev) {		// Split hand constructor		this(playingArea);		this.x = prev.x;		this.y = prev.y;		split = true;	}		public Hand(JPanel playingArea,int x,int y) {	// another player		this(playingArea);		this.x = x;		this.y = y;	}		// Method declarations	void addCard(Card c) {		if (!soft && c.iValue == 1 && under(11)) soft = true;		cards[numCards] = c;		numCards++;		if (soft) {			// See if no longer can be 'soft'			int points = 0;			for (int i = 0; i < numCards; i++) 				points += cards[i].iValue;			// If we can't add 10 and not bust we are no longer 'soft'			if (points + 10 > 21) soft = false;		}	}		final int getNumCards() { return numCards; }		int getCount() {		return getCount(numCards-1);	}	int getCount(int idx) {		int last = cards[idx].iValue;		if (last >= 2 && last <= 6) return 1;		else if (last == 10 | last == 1)			return -1;		return 0; 	}		Card peekCard(int cardNum) {		return cards[cardNum];	}		Card removeCard() {		Card out = cards[numCards-1];		cards[numCards-1] = null;		numCards--;		return out;	}		boolean canSurrender() {		// TODO add rule option to allow this		return false;	}		boolean canSplit() {		if (numCards == 2 && (cards[0].getValue() == cards[1].getValue())) 			return true;		return false;	}		boolean canDoubleDown() {		if (numCards == 2) return true;		return false;	}		boolean isSoft() {		return soft;	}		// For dealer or temporary testing with no bet	void show(boolean isDealer, boolean hideFirstCard) {		int x = this.x;		int y = this.y;		if (x == 0) x = 5;		if (isDealer) x = playingArea.getWidth() * 3 / 7;		else {			show(isDealer,hideFirstCard,0);			return;		}		Graphics g = playingArea.getGraphics();				if (isDealer) y = 20;				for (int i=0; i<numCards; i++) {			if (i == 0 && hideFirstCard) 				back.paintIcon(playingArea,g,x,y);			else 				cards[i].imgico.paintIcon(playingArea,g,x,y);						if (isDealer)				x += back.getIconWidth()/3;			else 				y += back.getIconHeight()/3;		}	}		void show(final boolean isDealer, final boolean hideFirstCard, final int bet) {		SwingUtilities.invokeLater(new Runnable() {			public void run() {				_show(isDealer,hideFirstCard,bet);			}		});	}		void _show(boolean isDealer, boolean hideFirstCard, int bet) {		int x = this.x;		int y = this.y;		if (x == 0) x = 5;		if (isDealer) x = playingArea.getWidth() * 3 / 7;		if (split)			new Exception("show " + isDealer + " " + hideFirstCard).printStackTrace();		Graphics2D g = (Graphics2D)playingArea.getGraphics();				if (isDealer) y = 20;		for (int i=0; i<numCards; i++) {			if (i == 0 && hideFirstCard) 				back.paintIcon(playingArea,g,x,y);			else 				cards[i].imgico.paintIcon(playingArea,g,x,y);			if (isDealer)				x += back.getIconWidth()/3;			else 				y += back.getIconHeight()/3;		}		Font font = new Font("Monospaced", Font.BOLD, 18);		g.setFont(font);		g.setStroke(new BasicStroke(2.0f));		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,			RenderingHints.VALUE_ANTIALIAS_ON);    	// a composite with transparency.//    	Composite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .75f);//    	g.setComposite(c);		String s = new StringBuffer("$").append(new Integer(bet).toString()).toString();  		FontMetrics fm = g.getFontMetrics(font);  		int w = fm.stringWidth(s)+10;  		int h = w;  		Color sColor = Color.blue;  		if (bet <= 10)  			g.setColor(Color.red);  		else if (bet <= 20) {  			g.setColor(Color.blue);  			sColor = Color.white;  		}  		else if (bet <= 25)   			g.setColor(Color.green);  		else if (bet <= 100) {  			g.setColor(Color.black);  			sColor = Color.white;  		}  		else if (bet <= 500)  			g.setColor(purple);  		else if (bet <= 1000)  			g.setColor(Color.orange);  		else g.setColor(Color.lightGray);		g.fillOval(x+5,y+back.getIconHeight()/3-h/2,w,h);		g.setColor(Color.black);		g.drawOval(x+5,y+back.getIconHeight()/3-h/2,w,h);		g.setPaint(sColor);		g.drawString(new StringBuffer("$").append(new Integer(bet).toString()).toString(),			x+10,y+back.getIconHeight()/3+5);	}		void clearSplit() {		Graphics2D g2d = (Graphics2D)playingArea.getGraphics();		g2d.setColor(((PlayingArea)playingArea).lightGreen);		g2d.fillRect(x,y,back.getIconWidth()+10,back.getIconHeight()+back.getIconHeight()/2+10);	}		void showSplit(boolean hideFirstCard, int bet,int position) {		int x = this.x;		int y = this.y;		if (x == 0) x = 5;		Graphics2D g = (Graphics2D)playingArea.getGraphics();		x += position * (back.getIconWidth()*5/12 + 10);					for (int i=0; i<numCards; i++) {			if (i == 0 && hideFirstCard) {				g.setClip(x,y,back.getIconWidth()*5/12,back.getIconHeight()*5/12);				back.paintIcon(playingArea,g,x,y);				g.setStroke(new BasicStroke(2.0f));				g.setColor(Color.black);				g.drawRect(x,y,back.getIconWidth()*5/12,back.getIconHeight()*5/12);			}			else {				g.setClip(x,y,cards[i].imgico.getIconWidth()*5/12,cards[i].imgico.getIconHeight()*5/12);				cards[i].imgico.paintIcon(playingArea,g,x,y);				g.setStroke(new BasicStroke(2.0f));				g.setColor(Color.black);				g.drawRect(x,y,cards[i].imgico.getIconWidth()*5/12,cards[i].imgico.getIconHeight()*5/12);			}			y += back.getIconHeight()/3;		}		Font font = new Font("Monospaced", Font.BOLD, 18);		g.setFont(font);		g.setStroke(new BasicStroke(2.0f));		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,			RenderingHints.VALUE_ANTIALIAS_ON);		String s = new StringBuffer("$").append(new Integer(bet).toString()).toString();  		FontMetrics fm = g.getFontMetrics(font);  		int w = fm.stringWidth(s)+10;  		int h = w;  		Color sColor = Color.blue;  		if (bet <= 10)  			g.setColor(Color.red);  		else if (bet <= 20) {  			g.setColor(Color.blue);  			sColor = Color.white;  		}  		else if (bet <= 25)   			g.setColor(Color.green);  		else if (bet <= 100) {  			g.setColor(Color.black);  			sColor = Color.white;  		}  		else if (bet <= 500)  			g.setColor(purple);  		else if (bet <= 1000)  			g.setColor(Color.orange);  		else g.setColor(Color.lightGray);		g.fillOval(x+5,y+back.getIconHeight()/3-h/2,w,h);		g.setColor(Color.black);		g.drawOval(x+5,y+back.getIconHeight()/3-h/2,w,h);		g.setPaint(sColor);		g.drawString(new StringBuffer("$").append(new Integer(bet).toString()).toString(),			x+10,y+back.getIconHeight()/3);	}		boolean blackjack() {		if (numCards == 2) {			if (cards[0].iValue == 1 && cards[1].iValue == 10) return true;			if (cards[1].iValue == 1 && cards[0].iValue == 10) return true;		}		return false;	}		boolean under(int n) {		int points = 0;		for (int i=0;i<numCards;i++) points += cards[i].iValue;		if (points < n) return true;		return false;	}		boolean sixTwo() {		if (numCards != 2) return false;		if (cards[0].iValue == 6 && cards[1].iValue == 2) return true;		if (cards[1].iValue == 6 && cards[0].iValue == 2) return true;		return false;	}			int dealersUp() {				return cards[1].iValue;	}		final public int splitValue() {//		if (!canSplit()) return 0;		if (numCards != 2) return 0;		if (cards[0].iValue != cards[1].iValue) 			return 0;		return cards[0].iValue;	}	 	final public int bestScore() {		int points = 0;		boolean haveAce = false;		for (int i = 0; i < numCards; i++) {			points += cards[i].iValue;			if (cards[i].iValue == 1) haveAce = true;		}		if (haveAce) {			if (points + 10 < 22) points += 10;		}		return points;	}		boolean mustHit() {		if (bestScore() < 17) return true;		else return false;	}		boolean busted() {		if (!under(22)) return true;		else return false;	}		public String toString() {		StringBuffer result = new StringBuffer("Hand: (" + x + "," + y + ") ");		for (int i = 0; i < numCards; i++) {			result.append(cards[i]).append(" ");		}		return result.toString().trim();	}}		// End of Hand class