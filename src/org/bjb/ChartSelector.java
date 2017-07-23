package org.bjb;import javax.swing.*;import java.awt.*;import java.awt.event.*;public class ChartSelector {  	private	ChartViewer cv = null;			public ChartSelector(Player p,Deck d,Hand dealersHand) {		Strategy s = p.getStrategy();		Hand h = p.getHand();		Rules rules = BlackJackGame.getRules();//		int v = h.bestScore();//		int dv = dealersHand.dealersUp();		int sv = h.splitValue();		if (p.isBusted()) {				Toolkit.getDefaultToolkit().beep();			return;		}				// Anything for surrender?				if (s.getName().equals("Basic Strategy")) {			if (rules.isDAS()) {				cv = new ChartViewer(Card.getIcon("images/charts/bs_das_pair_splitting.jpg"));							}			else {				cv = new ChartViewer(Card.getIcon("images/charts/bs_xdas_pair_splitting.jpg"));							}			ChartSelectorDialog dlog = new ChartSelectorDialog(p,d,dealersHand);			dlog.pack();			dlog.setVisible(true);			return;		}		else if (s.getName().equals("Hi-Lo") || s.getName().equals("Hi-Lo lite")) {			if (sv != 0) {				if (rules.isDAS()) { 					if (rules.isH17()) 						cv = new ChartViewer(Card.getIcon("images/charts/hlh17_das_pair_splitting.jpg"));						else cv = new ChartViewer(Card.getIcon("images/charts/hls17_das_pair_splitting.jpt"));				} 				else { 					if (rules.isH17())						cv = new ChartViewer(Card.getIcon("images/charts/hlh17_xdas_pair_splitting.jpg"));					else						cv = new ChartViewer(Card.getIcon("images/charts/hls17_xdas_pair_splitting.jpg"));				}			}			else {		// actionPerformed will differentiate between hard or soft and h17 or s17				ChartSelectorDialog dlog = new ChartSelectorDialog(p,d,dealersHand);				dlog.pack();				dlog.setVisible(true);				return;			}		}		if (cv != null) cv.setVisible(true);	}}class ChartSelectorDialog extends JDialog { 	private static final Color back = Color.yellow;	private	ChartViewer cv = null;	private final ButtonGroup group = new ButtonGroup();	private boolean soft = false, h17 = false;	private Player p = null;	 	// Create an action    private Action canact = new AbstractAction("Cancel") {    	private static final long serialVersionUID = -1L;        // This method is called when the button is pressed        public void actionPerformed(ActionEvent evt) {            setVisible(false);            dispose();        }    };	private JButton cancel = new JButton(canact);	// Create an action    Action okact = new AbstractAction("OK") {    	private final static long serialVersionUID = -1L;        // This method is called when the button is pressed		        public void actionPerformed(ActionEvent evt) {    		Strategy s = p.getStrategy();        	String cmd = group.getSelection().getActionCommand();    		if (s.getName().equals("Basic Strategy")) {	        	if (cmd.equals("Double down")) {        			if (soft)        				cv = new ChartViewer(Card.getIcon("images/charts/bs_soft_doubling.jpg"));        			else        				cv = new ChartViewer(Card.getIcon("images/charts/bs_hard_doubling.jpg"));	        	}	        	else if (cmd.equals("Stand")) {	        		cv = new ChartViewer(Card.getIcon("images/charts/bs_standing.jpg"));	        	}    		}    		else {	        	if (cmd.equals("Double down")) {	        		if (h17) {	        			if (soft)	        				cv = new ChartViewer(Card.getIcon("images/charts/hlh17_soft_doubling.jpg"));	        			else	        				cv = new ChartViewer(Card.getIcon("images/charts/hlh17_hard_doubling.jpg"));	        		}	        		else {	        			if (soft)	        				cv = new ChartViewer(Card.getIcon("images/charts/hls17_soft_doubling.jpg"));	        			else	        				cv = new ChartViewer(Card.getIcon("images/charts/hls17_hard_doubling.jpg"));	        		}	        	}	        	else if (cmd.equals("Stand")) {	        		if (h17) {	        			if (soft)	        				cv = new ChartViewer(Card.getIcon("images/charts/hlh17_soft_standing.jpg"));	        			else 	        				cv = new ChartViewer(Card.getIcon("images/charts/hlh17_hard_standing.jpg"));	        		}	        		else {	        			if (soft)	        				cv = new ChartViewer(Card.getIcon("images/charts/hls17_soft_standing.jpg"));	        			else 	        				cv = new ChartViewer(Card.getIcon("images/charts/hls17_hard.standing.jpg"));	        		}	        	}    		}        	if (cv != null) cv.setVisible(true);        }    }; 	JButton ok = new JButton(okact); 	 	JPanel controls = new JPanel();	public ChartSelectorDialog(Player p,Deck d,Hand dealersHand) {		super(new JFrame("Chart Selector"));		this.p = p;		Strategy s = p.getStrategy();		Hand h = p.getHand();		Rules rules = BlackJackGame.getRules();//		int v = h.bestScore();//		int dv = dealersHand.dealersUp();		int sv = h.splitValue();		String defAction = p.updateBasic(d,dealersHand);		getContentPane().setBackground(back);//		setUndecorated(true);		controls.setBackground(back);		controls.add(cancel);		controls.add(ok);		JPanel buttons = new JPanel();		buttons.setBackground(back);		GridLayout gl = new GridLayout();		gl.setColumns(1);							/*			Need to set rows		*/		buttons.setBackground(Color.white);		getContentPane().setLayout(gl);		if (s.getName().equals("Basic Strategy")) {			if (sv != 0) 				gl.setRows(3);			JRadioButton dbl = new JRadioButton("Double down");			dbl.setActionCommand("Double down");			dbl.setBackground(Color.white);			buttons.add(dbl);			group.add(dbl);			getContentPane().add(dbl);			JRadioButton stand = new JRadioButton("Stand");			stand.setActionCommand("Stand");			stand.setBackground(Color.white);			soft = h.isSoft();			h17 = rules.isH17();			buttons.add(stand);			group.add(stand);			getContentPane().add(stand);			if (defAction.equals("Double down")) dbl.setSelected(true);			else stand.setSelected(true);			getContentPane().add(controls);		}		else if (s.getName().equals("Hi-Lo") || s.getName().equals("Hi-Lo lite")) {			if (sv != 0) 				gl.setRows(3);			JRadioButton dbl = new JRadioButton("Double down");			dbl.setActionCommand("Double down");			dbl.setBackground(Color.white);			buttons.add(dbl);			group.add(dbl);			getContentPane().add(dbl);			JRadioButton stand = new JRadioButton("Stand");			stand.setActionCommand("Stand");			stand.setBackground(Color.white);			soft = h.isSoft();			h17 = rules.isH17();			buttons.add(stand);			group.add(stand);			getContentPane().add(stand);			if (defAction.equals("Double down")) dbl.setSelected(true);			else stand.setSelected(true);			getContentPane().add(controls);		}	}}