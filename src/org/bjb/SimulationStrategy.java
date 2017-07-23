package org.bjb;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;

import com.bric.plaf.BevelButtonUI;

public class SimulationStrategy extends JPanel {
	
	private JLabel round = new JLabel("0");
	private JLabel dealerBlackjacks = new JLabel();
	private JLabel dealerBusts = new JLabel();
	private JPanel plotter; 
	private JPanel playerInfo = new JPanel(new CardLayout());
//	final private JPanel[] playerViews;
//	private final static int FRAME_WIDTH = 800;
	private final static int FRAME_WIDTH = BlackJackApp.APP_WIDTH;
//	private final static int FRAME_HEIGHT = 750;
	private final static int FRAME_HEIGHT = BlackJackApp.APP_HEIGHT;
	private final static int PANEL_HEIGHT = 300;
	private final static int INSET_LEFT = 5;
	private final static int INSET_RIGHT = 5;
	private final static int PANEL_WIDTH = FRAME_WIDTH - INSET_LEFT - INSET_RIGHT;
	private final JPanel navbuttons = new JPanel(new GridLayout());
	private final JButton prev = new JButton("Previous");
	private final JButton next = new JButton("Next");
	private final JPanel buttons = new JPanel();
	private final JButton cancel = new JButton("Cancel");
	private final JButton ok = new JButton("OK");
	private final JButton simagain = new JButton("Repeat Sim");
	private final SimulationPlayerInfo[] pInfos;
	private final Player[] players;
	private final long rounds;
	private final BlackJackGame game;
	
	public SimulationStrategy(final BlackJackGame game,final Player[] players,final long rounds,final int bankroll) {
//		super("Strategy Simulation");
		this.game = game;
		this.players = players;
		this.rounds = rounds;
//		playerViews = new JPanel[players.length];
//		for (int i=0;i<players.length;i++)
//			playerViews[i] = new JPanel();
		pInfos = new SimulationPlayerInfo[players.length];
		for (int i=0;i<players.length;i++) {
			SimulationPlayerInfo pInfo = new SimulationPlayerInfo(players[i],SimulationPlotter.getColor(i));
			pInfos[i] = pInfo;
			playerInfo.add(pInfo,new Integer(i).toString());
		}
//		for (int i=0;i<players.length;i++) 
//			playerInfo.add(new SimulationPlayerInfo(players[i],SimulationPlotter.getColor(i)),new Integer(i+1).toString());
		setSize(FRAME_WIDTH,FRAME_HEIGHT);
		setPreferredSize(new Dimension(FRAME_WIDTH,FRAME_HEIGHT));
//		setMinimumSize(new Dimension(FRAME_WIDTH,FRAME_HEIGHT));
		plotter = new SimulationPlotter(PANEL_WIDTH,PANEL_HEIGHT,players,rounds,bankroll,pInfos);
		playerInfo.setSize(PANEL_WIDTH*3/4,PANEL_HEIGHT*4/5);
		playerInfo.setPreferredSize(new Dimension(PANEL_WIDTH*3/4,PANEL_HEIGHT*4/5));
		playerInfo.setBackground(Color.white);
		prev.setOpaque(false);
		prev.putClientProperty("JButton.segmentHorizontalPosition","first");
		prev.putClientProperty("JButton.segmentVerticalPosition","only");
		prev.setUI(new BevelButtonUI());
		prev.setFont(UIManager.getFont("Button.font"));
		next.setOpaque(false);
		next.putClientProperty("JButton.segmentHorizontalPosition","last");
		next.putClientProperty("JButton.segmentVerticalPosition","only");
		next.setUI(new BevelButtonUI());
		next.setFont(UIManager.getFont("Button.font"));		
		Border raisedbevel = BorderFactory.createRaisedBevelBorder();
		Border blueline = BorderFactory.createLineBorder(Color.blue);
		Border playersBorder = BorderFactory.createCompoundBorder(blueline,raisedbevel);
		playerInfo.setBorder(playersBorder);
		Border greenline = BorderFactory.createLineBorder(Color.green);
		plotter.setBorder(BorderFactory.createCompoundBorder(greenline,raisedbevel));
//		Container pane = getContentPane();
		Container pane = this;
 		pane.setBackground(Color.white);
 		GridBagConstraints c = new GridBagConstraints();
 		c.insets = insets(0,INSET_LEFT,0,INSET_RIGHT);
 		GridBagLayout gbls = new GridBagLayout();
 		pane.setLayout(gbls);
 
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		JLabel header = new JLabel("Strategy Simulation");
  		Font f = header.getFont();
  		f = f.deriveFont(18f);
  		header.setFont(f);
		header.setForeground(Color.blue);
		pane.add(header,c);
		c.gridx = 6;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.EAST;
		JLabel lbl = new JLabel("Round");
		Font boldFont = lbl.getFont().deriveFont(Font.BOLD);
//		boldFont = boldFont.deriveFont(boldFont.getSize()-2);
		lbl.setFont(boldFont);
		pane.add(lbl,c);
 		c.insets = insets(5,INSET_LEFT,5,INSET_RIGHT);
		c.gridx = 7;
		c.anchor = GridBagConstraints.WEST;
		pane.add(round,c);
		c.gridwidth = 8;
		c.gridheight = 8;
		c.gridx = 0;
		c.gridy = 2;
		pane.add(plotter,c);
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridy = 10;
		c.gridx = 0;
		c.anchor = GridBagConstraints.EAST;
		lbl = new JLabel("Dealer blackjacks");
		lbl.setFont(boldFont);
		pane.add(lbl,c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.WEST;
		dealerBlackjacks.setText(new Integer(game.getDealersBlackjacks()).toString());
		pane.add(dealerBlackjacks,c);
		c.gridx = 4;
		c.anchor = GridBagConstraints.EAST;
		lbl = new JLabel("Dealer busts");
		lbl.setFont(boldFont);
		pane.add(lbl,c);
		c.gridx = 5;
		c.anchor = GridBagConstraints.WEST;
		dealerBusts.setText(new Integer(game.getDealersBusts()).toString());
		pane.add(dealerBusts,c);
		c.gridy = 12;
		c.gridwidth = 4;
		c.gridheight = 6;
		c.gridx = 0;
		pane.add(playerInfo,c);
		c.gridy = 19;
		c.gridx = 0;
		c.gridheight = 1;
		c.gridwidth = 2;
//		navbuttons.setBackground(BlackJackApp.teal);
		navbuttons.add(prev);
		navbuttons.add(next);
 		c.insets = insets(0,INSET_LEFT,5,INSET_RIGHT);
		pane.add(navbuttons,c);
		prev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				((CardLayout)playerInfo.getLayout()).previous(playerInfo);
			}
		});
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				((CardLayout)playerInfo.getLayout()).next(playerInfo);
			}
		});
		c.gridx = 0;
		c.gridy = 21;
		buttons.setBackground(Color.white);
//		buttons.add(cancel);
		buttons.add(ok);
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				BlackJackApp.resetContent();
			}
		});
		buttons.add(simagain);
		simagain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				for (Player p : players)
					p.reset(bankroll);
				((SimulationPlotter)plotter).reset(rounds,bankroll);
				run();
			}
		});
		pane.add(buttons,c);
		run();	
    }	
	
	private void run() {
		final SimulationStrategy sim = this;
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {

				for (long l=0;l<rounds;l++) {
					if (l % 100 == 0) {
						round.setText(new Long(l).toString());
						dealerBlackjacks.setText(new Integer(game.getDealersBlackjacks()).toString());
						dealerBusts.setText(new Integer(game.getDealersBusts()).toString());
						((SimulationPlotter)plotter).update(players,l);
					}
					sim.repaint();
					for (SimulationPlayerInfo info : pInfos) {
						info.update();
						info.repaint();
					}
					game.initialDeal(); 
				}
				for (SimulationPlayerInfo info : pInfos) {
					info.update();
					info.complete();
					info.repaint();
				}
				return null;
			}
		};
		worker.start();		
	}
	
	public Insets insets(int t, int l, int b, int r)
	{
		return new Insets(t,l,b,r);
	}
}
