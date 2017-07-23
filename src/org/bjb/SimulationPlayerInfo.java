package org.bjb;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.Icon;

public class SimulationPlayerInfo extends JPanel {
	private Color color;
	private Player p;
	private final JLabel wagered = new JLabel();
	private final JLabel balance = new JLabel();
	private final JLabel blackjacks = new JLabel();
	private final JLabel wins = new JLabel();
	private final JLabel losses = new JLabel();
	private final JLabel pushes = new JLabel();
	private final JLabel busts = new JLabel();
	private final JLabel luck = new JLabel();
	private int bankroll;
	private int totalBalance = 0;
	private final JLabel totalBalanceLbl = new JLabel();
	private int lastBalance;
	private int ruin;
	private final JLabel ruinLbl = new JLabel();
	private int ruinRecovery;
	private final JLabel ruinRecoveryLbl = new JLabel();
	private long up = 0L;
	private final JLabel upLbl = new JLabel();
	private long down = 0L;
	private final JLabel downLbl = new JLabel();
	private int max;
	private final JLabel maxLbl = new JLabel();
	private int min;
	private final JLabel minLbl = new JLabel();
	private long n = 0L;
	private final JLabel roundsLbl = new JLabel();
	private double mean = 0d;
	private final JLabel meanLbl = new JLabel();
	private double m2 = 0d;
	private final JLabel varianceLbl = new JLabel();
	private final JLabel stdDevLbl = new JLabel();
	
	public SimulationPlayerInfo(Player p,Color color) {
		this.color = color;
		this.p = p;
		this.bankroll = p.getBalance();
		lastBalance = bankroll;
		Font boldFont = wagered.getFont().deriveFont(Font.BOLD);
 		setBackground(BlackJackApp.lightBlue);
 		GridBagConstraints c = new GridBagConstraints();
 		c.insets = insets(5,5,5,5);
 		GridBagLayout gbls = new GridBagLayout();
 		setLayout(gbls);
 		JLabel strategy = new JLabel(p.getStrategyName(),new ColorIcon(color),JLabel.LEADING);
 		strategy.setFont(boldFont);
 		c.gridx = 0;
 		c.gridy = 0;
 		c.anchor = GridBagConstraints.WEST;
 		add(strategy,c);
 		c.gridx = 1;
 		c.anchor = GridBagConstraints.EAST;
 		JLabel lbl = new JLabel("Balance");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 2;
 		c.anchor = GridBagConstraints.WEST;
 		balance.setText(new Integer(p.getBalance()).toString());
 		add(balance,c);
 		c.gridy = 2;
 		c.gridx = 1;
 		c.anchor = GridBagConstraints.EAST;
 		lbl = new JLabel("Wagered");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 2;
 		c.anchor = GridBagConstraints.WEST;
 		wagered.setText(new Integer(p.getTotalWagered()).toString());
 		add(wagered,c);
 		c.gridx = 1;
 		c.gridy = 4;
 		c.anchor = GridBagConstraints.EAST;
 		lbl = new JLabel("Blackjacks");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.anchor = GridBagConstraints.WEST;
 		c.gridx = 2;
 		blackjacks.setText(new Integer(p.getBlackjacks()).toString());
 		add(blackjacks,c);
 		c.gridx = 1;
 		c.gridy = 6;
 		c.anchor = GridBagConstraints.EAST;
 		lbl = new JLabel("Wins");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 2;
 		c.anchor = GridBagConstraints.WEST;
 		wins.setText(new Integer(p.getWins()).toString());
 		add(wins,c);
 		c.gridx = 1;
 		c.gridy = 8;
 		c.anchor = GridBagConstraints.EAST;
 		lbl = new JLabel("Losses");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 2;
 		c.anchor = GridBagConstraints.WEST;
 		losses.setText(new Integer(p.getLosses()).toString());
 		add(losses,c);
 		c.gridx = 1;
 		c.gridy = 10;
 		c.anchor = GridBagConstraints.EAST;
 		lbl = new JLabel("Pushes");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 2;
 		c.anchor = GridBagConstraints.WEST;
 		pushes.setText(new Integer(p.getPushes()).toString());
 		add(pushes,c);
 		c.gridx = 1;
 		c.gridy = 12;
 		c.anchor = GridBagConstraints.EAST;
 		lbl = new JLabel("Busts");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 2;
 		c.anchor = GridBagConstraints.WEST;
 		busts.setText(new Integer(p.getBusts()).toString());
 		add(busts,c);
 		c.gridx = 1;
 		c.gridy = 14;
 		c.anchor = GridBagConstraints.EAST;
 		lbl = new JLabel("Luck");
 		lbl.setFont(boldFont);
 		add (lbl,c);
 		c.gridx = 2;
 		c.anchor = GridBagConstraints.WEST;
 		DecimalFormat formatter = new DecimalFormat("00.000");
 		String avgev = formatter.format(new Double(p.getAvgEV()).doubleValue());
 		luck.setText(avgev);
 		add(luck,c);
 		c.gridx = 3;
 		c.gridy = 0;
 		c.gridheight = 14;
 		c.fill = GridBagConstraints.VERTICAL;
 		JSeparator vertSep = new JSeparator(JSeparator.VERTICAL);
 		vertSep.putClientProperty("Separator.thickness","2");
 		vertSep.setForeground(color);
 		add(vertSep,c);
 		c.gridx = 4;
 		c.gridheight = 1;
 		c.fill = GridBagConstraints.NONE;
 		lbl = new JLabel("Totals");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 5;
 		lbl = new JLabel("Gain/Loss");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 6;
 		totalBalance = bankroll;
 		totalBalanceLbl.setText(new Long(totalBalance).toString());
 		add(totalBalanceLbl,c);
 		c.gridx = 5;
 		c.gridy = 2;
 		lbl = new JLabel("Ruin");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 6;
 		ruinLbl.setText(new Integer(ruin).toString());
 		add(ruinLbl,c);
 		c.gridx = 5;
 		c.gridy = 4;
 		lbl = new JLabel("Ruin Recovery");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 6;
 		ruinRecoveryLbl.setText(new Integer(ruinRecovery).toString());
 		add(ruinRecoveryLbl,c);
 		c.gridx = 5;
 		c.gridy = 6;
 		lbl = new JLabel("Rounds Up");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 6;
 		upLbl.setText(new Long(up).toString());
 		add(upLbl,c);
 		c.gridy = 8;
 		c.gridx = 5;
 		lbl = new JLabel("Rounds Down");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 6;
 		downLbl.setText(new Long(down).toString());
 		add(downLbl,c);
 		c.gridx = 5;
 		c.gridy = 10;
 		max = bankroll;
 		lbl = new JLabel("Max balance");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 6;
 		maxLbl.setText(new Integer(max).toString());
 		add(maxLbl,c);
 		c.gridx = 5;
 		c.gridy = 12;
 		min = bankroll;
 		lbl = new JLabel("Min balance");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 6;
 		minLbl.setText(new Integer(min).toString());
 		add(minLbl,c);
 		c.gridx = 7;
 		c.gridy = 0;
 		c.gridheight = 14;
 		c.fill = GridBagConstraints.VERTICAL;
 		JSeparator vertSep2 = new JSeparator(JSeparator.VERTICAL);
 		vertSep2.setForeground(color);
 		add(vertSep2,c);
 		c.gridx = 8;
 		c.gridy = 0;
 		c.gridheight = 1;
 		c.fill = GridBagConstraints.NONE;
 		lbl = new JLabel("Stats");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 9;
 		lbl = new JLabel("Rounds");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 10;
 		roundsLbl.setText(new Long(n).toString());
 		add(roundsLbl,c);
 		c.gridx = 9;
 		c.gridy = 2;
 		lbl = new JLabel("Mean");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 10;
 		add(meanLbl,c);
 		c.gridx = 9;
 		c.gridy = 4;
 		lbl = new JLabel("Variance");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 10;
 		add(varianceLbl,c);
 		c.gridy = 6;
 		c.gridx = 9;
 		lbl = new JLabel("Std. Deviation");
 		lbl.setFont(boldFont);
 		add(lbl,c);
 		c.gridx = 10;
 		add(stdDevLbl,c);
	}

	public void update() {
		long aup = 0L,bup = 0L,adown = 0L,bdown = 0L;
//		double result = (double)(lastBalance-p.getBalance());
		updateStats(p.getBalance());
		if (lastBalance > 0 && p.getBalance() <= 0) {
			ruin++;
	 		ruinLbl.setText(new Integer(ruin).toString());
		}
		else if (lastBalance < 0 && p.getBalance() >= 0) {
			ruinRecovery++;
	 		ruinRecoveryLbl.setText(new Integer(ruinRecovery).toString());
		}
 		balance.setText(new Integer(p.getBalance()).toString());	
 		lastBalance = p.getBalance();
 		if (p.getBalance() > bankroll) {
 			bup = up;
 			up++;
 			aup = up;
 			upLbl.setText(new Long(up).toString());
 		}
 		else {
 			bdown = down;
 			down++;
 			adown = down;
 			downLbl.setText(new Long(down).toString());
 		}
 		wagered.setText(new Integer(p.getTotalWagered()).toString());
 		blackjacks.setText(new Integer(p.getBlackjacks()).toString());
 		wins.setText(new Integer(p.getWins()).toString());
 		losses.setText(new Integer(p.getLosses()).toString());
 		pushes.setText(new Integer(p.getPushes()).toString());
 		busts.setText(new Integer(p.getBusts()).toString());
 		DecimalFormat formatter = new DecimalFormat("00.000");
 		String avgev = formatter.format(new Double(p.getAvgEV()).doubleValue());
 		luck.setText(avgev);
 		if (p.getBalance() > max) max = p.getBalance();
 		maxLbl.setText(new Integer(max).toString());
 		if (p.getBalance() < min) min = p.getBalance();
 		minLbl.setText(new Integer(min).toString());
	}

	private void updateStats(double value) {
 		DecimalFormat formatter = new DecimalFormat("00.000");
 		String avgev = formatter.format(new Double(p.getAvgEV()).doubleValue());
		n++;
 		roundsLbl.setText(new Long(n).toString());
		double delta = value - mean;
		mean += delta/n;
		meanLbl.setText(formatter.format(new Double(mean).doubleValue()));
		m2 += delta*(value - mean);
		double var = m2 / (n - 1);
		varianceLbl.setText(formatter.format(new Double(var).doubleValue()));
		double stddev = Math.sqrt(var);
		stdDevLbl.setText(formatter.format(new Double(stddev).doubleValue()));
	}
	
	public void complete() {
 		balance.setText(new Integer(p.getBalance()).toString());	
		if (p.getBalance() >= bankroll)
			totalBalance += p.getBalance() - bankroll;
		else totalBalance -= p.getBalance() - bankroll;
		totalBalanceLbl.setText(new Integer(totalBalance).toString());
	}
	
	public Insets insets(int t, int l, int b, int r)
	{
		return new Insets(t,l,b,r);
	}
	
	static class ColorIcon implements Icon {
		
		private Color color;
		
		ColorIcon(Color color) {
			this.color = color;
		}
		
		public void paintIcon( Component c, Graphics g, int x, int y ) {
			g.translate( x, y );
			g.setColor(color);
			g.fillRect(0,0,getIconWidth(),getIconHeight());
			g.setColor(Color.black);
			g.drawRect(0,0,getIconWidth(),getIconHeight());
		}
		
		public int getIconWidth() { return 16; }
		
		public int getIconHeight() { return 16; }
	}

}
