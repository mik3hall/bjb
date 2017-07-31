package org.bjb;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class Simulation extends JDialog {
		private static final java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userRoot().node("BlackJackApp");
		private static int decks = 1, players = 1, bankroll = 10000, betunit = 10;
		private static long rounds = 25000L;
		private static boolean defEnabled = true;
		private static boolean dealupEnabled = false;
		private static String[] s = new String[] { "Hi-Lo lite","Hi-Lo","Hi-Lo Ranged","Illustrious 18","K-O Rookie","K-O Preferred","Red Seven Easy","Red Seven","Zen","Basic Strategy","Thorp Basic Strategy" };
		static final JTextField deckNum = new JTextField(7);
		static final JTextField initialBankRoll = new JTextField(7);
		static final JTextField initialBetUnit = new JTextField(7);
		static final JTextField roundsNum = new JTextField(14);
		private static DefaultListModel<String> listModel = new DefaultListModel<String>();
		private static final JList<String> playersList = new JList<String>(listModel);
		private static final JScrollPane playersScroller = new JScrollPane(playersList);
		static final JComboBox<String> strategies = new JComboBox<String>(s);
		static final JComboBox<String> evStrategies = new JComboBox<String>(s);
		static final String[] rs = new String[] { "Downtown Las Vegas","Las Vegas Strip","Reno","Atlantic City","Other" };
		static final JComboBox<String> rules = new JComboBox<String>(rs);
		static final String[] cards = new String[] { "A","2","3","4","5","6","7","8","9","10" };
		static final JComboBox<String> dc = new JComboBox<String>(cards);
		static final JComboBox<String> pc1 = new JComboBox<String>(cards);
		static final JComboBox<String> pc2 = new JComboBox<String>(cards);
		static final boolean xverbose = true;
		public static final int RDS_PER_HR = 100;
		private static us.hall.osx.LogOut logout = us.hall.osx.LogOut.getInstance();
		
		// Create an action
	    Action canact = new AbstractAction("Cancel") {
	    	private static final long serialVersionUID = -1L;
	        // This method is called when the button is pressed
	        public void actionPerformed(ActionEvent evt) {
	            setVisible(false);
	        }
	    };
		JButton cancel = new JButton(canact);
		
		// Create an action
	    Action okact = new AbstractAction("OK") {
	    	private static final long serialVersionUID = -1L;
	        // This method is called when the button is pressed
	        public void actionPerformed(ActionEvent evt) {
	 			save();
	            setVisible(false);
				BlackJackGame game = new BlackJackGame(BlackJackApp.preferences,deckNum,(PlayingArea)BlackJackApp.playingArea,true);
				Player[] players = new Player[listModel.getSize()];
				System.out.println("Simulation players size " + players.length);
				int decks = new Integer(deckNum.getText()).intValue();
				for (int i=0;i<players.length;i++) {
					System.out.println(i + " strategy is " + listModel.elementAt(i));
					players[i] = new Player(Strategy.getStrategy(listModel.getElementAt(i),decks),false,0,0,BlackJackApp.playingArea);
					players[i].setBalance(bankroll, true);
				}
				game.setPlayers(players);
				BlackJackApp.simulationStrategy(new SimulationStrategy(game,players,new Long(roundsNum.getText()).longValue(),new Integer(initialBankRoll.getText()).intValue()));
	        }
	    };
		JButton ok = new JButton(okact);

	 	Action default_act = new AbstractAction("Default Buttons") {
	    	private static final long serialVersionUID = -1L;
	        public void actionPerformed(ActionEvent evt) {
				// No action
	        }
	    };
	    JCheckBox defButton = new JCheckBox(default_act);
	 
	    Action rules_view = new AbstractAction("View") {
	    	private static final long serialVersionUID = -1L;
	    	public void actionPerformed(ActionEvent evt) {
	    		String ruleName = (String)rules.getSelectedItem();
	    		if (ruleName.equals("Other")) {
	    			
	    		}
	    		else {
	    			Rules r = Rules.getInstance(ruleName);
	    			new RulesViewer(ruleName,r).setVisible(true);
	    		}
	    	}
	    };
	    JButton rulesView = new JButton(rules_view);
	    
	    Action dealup_act = new AbstractAction("Deal Players Cards Up") {
	    	private static final long serialVersionUID = -1L;
	    	public void actionPerformed(ActionEvent evt) {
	    		// No action
	    		if (dealUpButton.isSelected()) dealupEnabled = true;
	    		else dealupEnabled = false;
	    	}
	    };
	    JCheckBox dealUpButton = new JCheckBox(dealup_act);
	 
	    public static void main(String[] args) {
			Simulation sim = new Simulation(new JFrame(""),true);
			sim.setVisible(true);
	    }
	    
		public Simulation(JFrame owner,boolean modal) {
			super(owner,"Simulation",modal);
		    java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userRoot().node("BlackJackApp");
	        getContentPane().setBackground(Color.white);
	 		getContentPane().setLayout(new GridBagLayout());
	        GridBagConstraints c = new GridBagConstraints();
			c.insets = insets(5,10,5,15);
//			int anchor = c.anchor;
			Border raisedbevel = BorderFactory.createRaisedBevelBorder();
			Border blueline = BorderFactory.createLineBorder(Color.blue);
			Border playersBorder = BorderFactory.createCompoundBorder(blueline,raisedbevel);
			JPanel playersPanel = new JPanel();
//			playersPanel.setBackground(Color.white);
			playersPanel.setBackground(new Color(0xccffcc));
			playersPanel.setBorder(playersBorder);
			playersPanel.setLayout(new GridBagLayout());
			GridBagConstraints pc = new GridBagConstraints();
			pc.insets = insets(0,10,0,10);
			pc.gridx = 0;
			pc.gridy = 1;
			pc.anchor = GridBagConstraints.WEST;
			playersPanel.add(new JLabel("Players"),pc);
			pc.gridy = 2;
			pc.gridheight = 4;
			pc.gridwidth = 3;
			pc.gridx = 0;
			playersPanel.add(playersScroller,pc);
			pc.gridheight = 1;
			pc.gridy = 6;
			pc.gridx = 0;
			strategies.setBackground(Color.white);
			playersPanel.add(strategies,pc);
			strategies.addItemListener(new ItemListener() {
				ItemEvent lastEvent = null;
				
				public void itemStateChanged(ItemEvent e) {
					logout.println("listModel add event " + e);
					String strategy = (String)e.getItem();
					if (!e.equals(lastEvent) && e.getStateChange() == ItemEvent.SELECTED && !strategy.equals("none")) {
						listModel.addElement(strategy);
						lastEvent = e;
					}
				}				
			});
			pc.gridy = 7;
			pc.gridx = 0;
			JPanel playersButtons = new JPanel();
			playersButtons.setBackground(new Color(0xccffcc));
			JButton addPlayer = new JButton("Add");
			playersButtons.add(addPlayer);
			JButton deletePlayer = new JButton("Delete");
			playersButtons.add(deletePlayer);
			playersPanel.add(playersButtons,pc);
			c.gridheight = 7;
			c.gridwidth = 7;
			c.gridy = 0;
			c.gridx = 0;
			c.anchor = GridBagConstraints.WEST;
			getContentPane().add(playersPanel,c);
			JPanel evPanel = new JPanel();
			evPanel.setLayout(new GridBagLayout());
			GridBagConstraints ec = new GridBagConstraints();
			evPanel.setBorder(playersBorder);
			evPanel.setBackground(new Color(0xffffcc));
//			CardComponent dealers = new CardComponent("images/bifv.png");
			ec.gridy = 0;
			ec.gridx = 0;
			ec.gridwidth = 1;
			ec.insets = insets(15,7,0,3);
			evPanel.add(new JLabel("Dealers"),ec);
			ec.gridx = 1;
			ec.gridwidth = 2;
			int anchor = ec.anchor;
			ec.anchor = GridBagConstraints.CENTER;
			evPanel.add(new JLabel("Players"),ec);
			ec.anchor = anchor;
			ec.gridwidth = 1;
			final CardComponent dealers = new CardComponent(Hand.back);
			final CardComponent players1 = new CardComponent(Hand.back);
			final CardComponent players2 = new CardComponent(Hand.back);
			ec.insets = insets(0,7,0,7);
			ec.gridy = 1;
			ec.gridx = 0;
			evPanel.add(dealers,ec);
			ec.gridwidth = 1;
			ec.gridx = 1;
			ec.insets = insets(0,13,0,3);
			evPanel.add(players1,ec);
			ec.gridx = 2;
			ec.insets = insets(0,3,0,7);
			evPanel.add(players2,ec);
			ec.gridy = 4;
			ec.gridx = 0;
			ec.insets = insets(0,7,0,7);
			evPanel.add(dc,ec);
			dc.setBackground(Color.white);
			dc.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						int suit = (int)(3 * Math.random());
						int cardnum = 0;
						String cardChoice = (String)e.getItem();
						if (cardChoice.equals("A")) cardnum = 0;
						else if (cardChoice.equals("10")) cardnum = 10 + (int)(3 * Math.random());
						else cardnum = new Integer(cardChoice).intValue() - 1;
						Card card = new Card(cardnum+13*suit);
						dealers.setImageIcon(card.getIcon());
						dealers.setValue(card.getValue());
						dealers.repaint();
					}
				}				
			});
			ec.gridx = 1;
			ec.insets = insets(0,13,0,3);
			evPanel.add(pc1,ec);
			pc1.setBackground(Color.white);
			pc1.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						int suit = (int)(3 * Math.random());
						int cardnum = 0;
						String cardChoice = (String)e.getItem();
						if (cardChoice.equals("A")) cardnum = 0;
						else if (cardChoice.equals("10")) cardnum = 10 + (int)(3 * Math.random());
						else cardnum = new Integer(cardChoice).intValue() - 1;
						Card card = new Card(cardnum+13*suit);
						players1.setImageIcon(card.getIcon());
						players1.setValue(card.getValue());
						players1.repaint();
					}
				}				
			});
			ec.gridx = 2;
			ec.insets = insets(0,3,0,7);
			evPanel.add(pc2,ec);
			pc2.setBackground(Color.white);
			pc2.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						int suit = (int)(3 * Math.random());
						int cardnum = 0;
						String cardChoice = (String)e.getItem();
						if (cardChoice.equals("A")) cardnum = 0;
						else if (cardChoice.equals("10")) cardnum = 10 + (int)(3 * Math.random());
						else cardnum = new Integer(cardChoice).intValue() - 1;
						Card card = new Card(cardnum+13*suit);
						players2.setImageIcon(card.getIcon());
						players2.setValue(card.getValue());
						players2.repaint();
					}
				}				
			});
			ec.gridx = 0;
			ec.gridy = 5;
			ec.gridwidth = 3;
			ec.insets = insets(13,7,13,3);
			evPanel.add(evStrategies,ec);
			c.gridx = 7;
			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.REMAINDER;
			getContentPane().add(evPanel,c);
			JPanel commonPanel = new JPanel();
			commonPanel.setLayout(new GridBagLayout());			
			GridBagConstraints cc = new GridBagConstraints();
			commonPanel.setBorder(playersBorder);
			commonPanel.setBackground(Color.white);
			cc.fill = GridBagConstraints.NONE;
			cc.gridwidth = 1;
			cc.gridheight = 1;
			cc.gridy = 0;
			cc.gridx = 0;
			cc.anchor = GridBagConstraints.EAST;
			commonPanel.add(new JLabel("Simulation rounds"),cc);
			cc.anchor = GridBagConstraints.WEST;
			cc.gridx = 1;
			roundsNum.setText(new Long(rounds).toString());
			commonPanel.add(roundsNum,cc);
			cc.gridy = 2;
			cc.gridx = 0;
			cc.anchor= GridBagConstraints.EAST;
			decks = prefs.getInt("decks", decks);
			commonPanel.add(new JLabel("Number of decks"),cc);
			cc.gridx = 1;
			cc.anchor = GridBagConstraints.WEST;
			deckNum.setText(new Integer(decks).toString());
			commonPanel.add(deckNum,cc);
			cc.gridy = 4;
			cc.gridx = 0;
			cc.anchor = GridBagConstraints.EAST;
// Hardcoded $10000 is preferentially used in simulations for the bankroll default
// to allow SCORE calculations
//			bankroll = prefs.getInt("bankroll",bankroll);
			commonPanel.add(new JLabel("Initial Player Bankroll"),cc);
			cc.anchor = GridBagConstraints.WEST;
			initialBankRoll.setText(new Integer(bankroll).toString());
			cc.gridx = 1;
			commonPanel.add(initialBankRoll,cc);
			cc.gridx = 0;
			cc.gridy = 6;
			cc.anchor = GridBagConstraints.EAST;
			commonPanel.add(new JLabel("Betting Unit Amount"),cc);
			cc.gridx = 1;
			betunit = prefs.getInt("betunit",betunit);
			cc.anchor = GridBagConstraints.WEST;
			initialBetUnit.setText(new Integer(betunit).toString());
			commonPanel.add(initialBetUnit,cc);
			cc.gridy = 8;
			cc.gridx = 0;
			cc.anchor = GridBagConstraints.EAST;
			commonPanel.add(new JLabel("Rules"),cc);
			rules.setBackground(Color.white);
			cc.gridx = 1;
			cc.anchor = GridBagConstraints.WEST;
			commonPanel.add(rules,cc);
			cc.gridx = 2;
			rulesView.setBackground(Color.white);
			commonPanel.add(rulesView,cc);
			cc.gridx = 3;
			JButton ruleNew = new JButton("New");
			ruleNew.setBackground(Color.white);
			commonPanel.add(ruleNew,cc);
			c.gridwidth = 14;
			c.gridheight = 8;
			c.gridx = 0;
			c.gridy = 9;
			getContentPane().add(commonPanel,c);
			JPanel buttons = new JPanel();
			buttons.setBackground(Color.white);
			buttons.add(ok);
			getRootPane().setDefaultButton(ok);
			buttons.add(cancel);
			c.gridy = 17;
			getContentPane().add(buttons,c);
	 		pack();
	 		positionWindow();
			addWindowListener(new WindowAdapter() {
				public void windowOpened(WindowEvent evt) {
					deckNum.setText(new Integer(decks).toString());
					defButton.setEnabled(defEnabled);		
					dealUpButton.setSelected(dealupEnabled);
				}
			});
		}

		private void save() {
			if (decks != new Integer(deckNum.getText()).intValue()) {
				decks = new Integer(deckNum.getText()).intValue();
				BlackJackApp.setDecks(decks);
			}
//		    java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userRoot().node("BlackJackApp");
			prefs.putInt("decks", decks);
			prefs.putInt("players", players);
			prefs.putBoolean("defbuttons", defEnabled);
			prefs.putBoolean("dealup", dealupEnabled);
			prefs.put("strategy", (String)strategies.getSelectedItem());
			prefs.put("rules",(String)rules.getSelectedItem());
//			BlackJackApp.setDealsUp(dealupEnabled);
		}
				
		public int getDecksNum() { return decks; }
		public int getPlayersNum() { return players; }
		public static Rules getRules() { return Rules.getInstance(prefs.get("rules","Downtown Las Vegas")); }
		public static String getDefaultStrategy() {
			return prefs.get("strategy","Basic Strategy");
		}
		
		public Insets insets(int t, int l, int b, int r)
		{
			return new Insets(t,l,b,r);
		}
			
		public void positionWindow()
		{
			Dimension sSize = this.getToolkit().getScreenSize();	// Position the window
	  	 	int sHeight = sSize.height;
	  	 	int sWidth = sSize.width;
	  	 	Dimension aSize = this.getSize();
	  	 	int aHeight = aSize.height;
	  	 	int aWidth = aSize.width;
	 	 	this.setLocation((sWidth-aWidth)/2,(sHeight-aHeight)/2);
		}
}
