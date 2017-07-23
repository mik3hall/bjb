package org.bjb;import java.awt.*;import java.awt.event.*;import javax.swing.*;public class Preferences extends JDialog {	// For testing uninitialized preferences/*	static {		java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userRoot().node("BlackJackApp");		try {			prefs.removeNode();		}		catch (java.util.prefs.BackingStoreException bse) {}	}*/	private static final java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userRoot().node("BlackJackApp");	private static int decks = 1, players = 1, bankroll = 1000, betunit = 10;	private static boolean defEnabled = true;	private static boolean dealupEnabled = false;	private static boolean wongingEnabled = true;	private static String[] s = new String[] { "Hi-Lo lite","Hi-Lo","Hi-Lo Ranged","Illustrious 18","K-O Rookie","K-O Preferred","Red Seven Easy","Red Seven","Basic Strategy","Zen","Thorp Basic Strategy" };	private static String[] ws = new String[] { "Unit","Ramped","Mod Kelly" };	static final JTextField deckNum = new JTextField(7);	static final JTextField playerNum = new JTextField(7);	static final JTextField initialBankRoll = new JTextField(7);	static final JTextField initialBetUnit = new JTextField(7);	static final JTextField wongInCount = new JTextField(2);	static final JComboBox<String> strategies = new JComboBox<String>(s);	static final JComboBox<String> wageringStrategies = new JComboBox<String>(ws);	static final String[] rs = new String[] { "Downtown Las Vegas","Las Vegas Strip","Reno","Atlantic City","Other" };	static final JComboBox<String> rules = new JComboBox<String>(rs);	// Create an action    Action canact = new AbstractAction("Cancel") {    	private static final long serialVersionUID = -1L;        // This method is called when the button is pressed        public void actionPerformed(ActionEvent evt) {            setVisible(false);        }    };	JButton cancel = new JButton(canact);		// Create an action    Action okact = new AbstractAction("OK") {    	private static final long serialVersionUID = -1L;        // This method is called when the button is pressed        public void actionPerformed(ActionEvent evt) { 			save();            setVisible(false);        }    };	JButton ok = new JButton(okact); 	Action default_act = new AbstractAction("Default Button Highlighted") {    	private static final long serialVersionUID = -1L;        public void actionPerformed(ActionEvent evt) {			if (defButton.isSelected()) defEnabled = true;			else defEnabled = false;        }    };    JCheckBox defButton = new JCheckBox(default_act);     Action rules_view = new AbstractAction("View") {    	private static final long serialVersionUID = -1L;    	public void actionPerformed(ActionEvent evt) {    		String ruleName = (String)rules.getSelectedItem();    		if (ruleName.equals("Other")) {    			    		}    		else {    			Rules r = Rules.getInstance(ruleName);    			new RulesViewer(ruleName,r).setVisible(true);    		}    	}    };    JButton rulesView = new JButton(rules_view);        Action dealup_act = new AbstractAction("Deal Players Cards Up") {    	private static final long serialVersionUID = -1L;    	public void actionPerformed(ActionEvent evt) {    		if (dealUpButton.isSelected()) dealupEnabled = true;    		else dealupEnabled = false;    	}    };    JCheckBox dealUpButton = new JCheckBox(dealup_act);    Action wonging_act = new AbstractAction("Wonging in is enabled") {    	private static final long serialVersionUID = -1L;    	public void actionPerformed(ActionEvent evt) {    		if (wongingButton.isSelected()) wongingEnabled = true;    		else wongingEnabled = false;    	}    };    JCheckBox wongingButton = new JCheckBox(wonging_act);    	public Preferences(JFrame owner,boolean modal) {		super(owner,"Preferences",modal);//	    java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userRoot().node("BlackJackApp");        getContentPane().setBackground(Color.white); 		getContentPane().setLayout(new GridBagLayout());        GridBagConstraints c = new GridBagConstraints();		c.insets = insets(5,10,5,15);//		int anchor = c.anchor;		c.anchor= GridBagConstraints.EAST;		decks = prefs.getInt("decks", decks);		getContentPane().add(new JLabel("Number of decks"),c);		c.gridx = 1;		c.anchor = GridBagConstraints.WEST;		deckNum.setText(new Integer(decks).toString());		getContentPane().add(deckNum,c);		c.gridy = 3;		c.gridx = 0;		c.anchor = GridBagConstraints.EAST;		getContentPane().add(new JLabel("Number of players"),c);		c.gridx = 1;		c.anchor = GridBagConstraints.WEST;		players = prefs.getInt("players", players);		playerNum.setText(new Integer(players).toString());		getContentPane().add(playerNum,c);		c.gridx = 0;		c.gridy = 5;		c.anchor = GridBagConstraints.EAST;		bankroll = prefs.getInt("bankroll",bankroll);		getContentPane().add(new JLabel("Initial Player Bankroll"),c);		c.anchor = GridBagConstraints.WEST;		initialBankRoll.setText(new Integer(bankroll).toString());		c.gridx = 1;		getContentPane().add(initialBankRoll,c);		c.gridx = 0;		c.gridy = 7;		c.anchor = GridBagConstraints.EAST;		getContentPane().add(new JLabel("Betting Unit Amount"),c);		c.gridx = 1;		betunit = prefs.getInt("betunit",betunit);		c.anchor = GridBagConstraints.WEST;		initialBetUnit.setText(new Integer(betunit).toString());		getContentPane().add(initialBetUnit,c);/*		c.gridx = 0;		c.gridy = 5;		c.gridheight = 2;		Border raisedbevel = BorderFactory.createRaisedBevelBorder();		Border blackline = BorderFactory.createLineBorder(Color.black);		Border border = BorderFactory.createCompoundBorder(blackline,raisedbevel);		JLabel dep = new JLabel("Basic Strategy Dependent");  		Font f = dep.getFont();  		f = f.deriveFont(14f);  		dep.setFont(f);  		dep.setForeground(Color.blue);		getContentPane().add(dep,c);		c.gridx = 1;		JPanel bsOptions = new JPanel();		bsOptions.setBackground(Color.white);		bsOptions.setLayout(new GridLayout(2,1));		ButtonGroup basicStrategy = new ButtonGroup();		JRadioButton total = new JRadioButton("Total",true);		basicStrategy.add(total);		bsOptions.add(total);		JRadioButton content = new JRadioButton("Content",false);		basicStrategy.add(content);		bsOptions.add(content,c);		bsOptions.setBorder(border);		getContentPane().add(bsOptions,c);*/		c.gridheight = 1;		c.gridx = 0;		c.gridy = 11;		c.anchor = GridBagConstraints.EAST;		getContentPane().add(new JLabel("Default human player strategy"),c);		c.gridx = 1;		c.anchor = GridBagConstraints.WEST;		strategies.setBackground(Color.white);		getContentPane().add(strategies,c);		c.gridy = 13;		c.gridx = 0;		c.anchor = GridBagConstraints.EAST;		getContentPane().add(new JLabel("Default human player wagering strategy"),c);		c.gridx = 1;		c.anchor = GridBagConstraints.WEST;		wageringStrategies.setBackground(Color.white);		getContentPane().add(wageringStrategies,c);		c.gridx = 0;		c.gridy = 15;		c.anchor = GridBagConstraints.EAST;		getContentPane().add(new JLabel("Rules"),c);		rules.setBackground(Color.white);		c.gridx = 1;		c.anchor = GridBagConstraints.WEST;		getContentPane().add(rules,c);		c.gridx = 2;		rulesView.setBackground(Color.white);		getContentPane().add(rulesView,c);		c.gridx = 3;		JButton ruleNew = new JButton("New");		ruleNew.setBackground(Color.white);		getContentPane().add(ruleNew,c);		c.gridy = 17;		c.gridx = 0;		defEnabled = prefs.getBoolean("defbuttons",defEnabled);		defButton.setSelected(defEnabled);				defButton.setBackground(Color.white);		getContentPane().add(defButton,c);		c.gridy = 19;		dealupEnabled = prefs.getBoolean("dealup", dealupEnabled);		dealUpButton.setSelected(dealupEnabled);			dealUpButton.setBackground(Color.white);		getContentPane().add(dealUpButton,c);		c.gridy = 21;		c.gridwidth = 2;		JPanel wongingPanel = new JPanel();		wongingPanel.setBackground(Color.white);		wongingEnabled = prefs.getBoolean("wonging", wongingEnabled);		wongingButton.setSelected(wongingEnabled);		wongingButton.setBackground(Color.white);		wongingPanel.add(wongingButton);		wongingPanel.add(new JLabel("Wong in default count"));		wongInCount.setBackground(Color.white);		wongInCount.setText("2");		wongingPanel.add(wongInCount);		getContentPane().add(wongingPanel,c);		c.gridy = 23;		c.gridx = 0;		c.gridwidth = 1;		JPanel buttons = new JPanel();		buttons.setBackground(Color.white);		buttons.add(ok);		getRootPane().setDefaultButton(ok);		buttons.add(cancel);		getContentPane().add(buttons,c); 		pack(); 		positionWindow();		addWindowListener(new WindowAdapter() {			public void windowOpened(WindowEvent evt) {				deckNum.setText(new Integer(decks).toString());				playerNum.setText(new Integer(players).toString());				defButton.setSelected(defEnabled);						dealUpButton.setSelected(dealupEnabled);				wongingButton.setSelected(wongingEnabled);				strategies.setSelectedItem(getDefaultStrategy());				wageringStrategies.setSelectedItem(getDefaultWageringStrategy());				rules.setSelectedItem(prefs.get("rules","Downtown Las Vegas"));				initialBankRoll.setText(new Integer(prefs.getInt("bankroll",bankroll)).toString());				initialBetUnit.setText(new Integer(prefs.getInt("betunit",betunit)).toString());			}		});	}		private void save() {		if (decks != new Integer(deckNum.getText()).intValue()) {			decks = new Integer(deckNum.getText()).intValue();			BlackJackApp.setDecks(decks);		}		if (players != new Integer(playerNum.getText()).intValue()) {			players = new Integer(playerNum.getText()).intValue();			BlackJackApp.setPlayers(players);		}		if (bankroll != new Integer(initialBankRoll.getText()).intValue())			bankroll = new Integer(initialBankRoll.getText()).intValue();		if (betunit != new Integer(initialBetUnit.getText()).intValue())			betunit = new Integer(initialBetUnit.getText()).intValue();//	    java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userRoot().node("BlackJackApp");		prefs.putInt("decks", decks);		prefs.putInt("players", players);		prefs.putInt("bankroll", bankroll);		prefs.putInt("betunit",betunit);		prefs.putBoolean("defbuttons", defEnabled);		prefs.putBoolean("dealup", dealupEnabled);		prefs.putBoolean("wonging", wongingEnabled);		prefs.put("strategy", (String)strategies.getSelectedItem());		prefs.put("wagering", (String)wageringStrategies.getSelectedItem());		prefs.put("rules",(String)rules.getSelectedItem());		BlackJackApp.setDealsUp(dealupEnabled);	}				public int getDecksNum() { return decks; }	public int getPlayersNum() { return players; }	public static int getBankRoll() { return bankroll; }	public static int getBetUnit() { return betunit; }	public static Rules getRules() { 		synchronized(prefs) {			return Rules.getInstance(prefs.get("rules","Downtown Las Vegas")); 		}	}	public static String getDefaultStrategy() {		return prefs.get("strategy","Basic Strategy");	}	public static String getDefaultWageringStrategy() {		return prefs.get("wagering","Unit");	}	public static boolean isDefaultButtons() { return defEnabled; }	public static boolean isWongingEnabled() { return wongingEnabled; }	public static boolean isWongedOut(Player p) {		if (!wongingEnabled) return false;		if (p.getCount() > getWongIn()) return false; 		return true;	}	private static int getWongIn() { return new Integer(wongInCount.getText()).intValue(); }		public Insets insets(int t, int l, int b, int r)	{		return new Insets(t,l,b,r);	}			public void positionWindow()	{		Dimension sSize = this.getToolkit().getScreenSize();	// Position the window  	 	int sHeight = sSize.height;  	 	int sWidth = sSize.width;  	 	Dimension aSize = this.getSize();  	 	int aHeight = aSize.height;  	 	int aWidth = aSize.width; 	 	this.setLocation((sWidth-aWidth)/2,(sHeight-aHeight)/2);	}}