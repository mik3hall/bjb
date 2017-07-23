package org.bjb;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JWindow;

public class StatusArea extends JWindow {

	public StatusArea() {
		// Layout the statusArea
		Container statusArea = getContentPane();
 		statusArea.setBackground(Color.white);
 		GridBagConstraints cs = new GridBagConstraints();
 		GridBagLayout gbls = new GridBagLayout();
 		statusArea.setLayout(gbls);
 		statusArea.setSize(240,200);
		statusArea.setPreferredSize(new Dimension(240,200));
		statusArea.setMinimumSize(new Dimension(240,200));
// 		getContentPane().add(statusArea,c);
 		cs.insets = insets(0,0,4,4);
 		cs.gridx = 0;
 		cs.gridy = 0;
  		statusArea.add(new JLabel("Decks: "),cs);
 		cs.gridx = 1;
  		statusArea.add(new JLabel(new Integer(BlackJackApp.getDecks()).toString()),cs);
 		cs.gridy = 1;
 		cs.gridx = 0;
 		statusArea.add(new JLabel("Current card: "),cs);
 		cs.gridx = 1;
 		statusArea.add(new JLabel(new Integer(BlackJackGame.deck.getTopCard()).toString()),cs);
		cs.gridy = 2;
		cs.gridx = 0;
		statusArea.add(new JLabel("Cut card: "),cs);
		cs.gridx = 1;
		statusArea.add(new JLabel(new Integer(BlackJackGame.deck.getCutCard()).toString()),cs);
		cs.gridx = 0;
		cs.gridy = 3;
		statusArea.add(new JLabel("Round"),cs);
		cs.gridx = 1;
		statusArea.add(new JLabel(new Integer(BlackJackGame.getRounds()).toString()),cs);
		cs.gridx = 0;
		cs.gridy = 4;
		statusArea.add(new JLabel("Elapsed: "),cs);
		cs.gridx = 1;
		statusArea.add(new JLabel(BlackJackGame.getElapsed()),cs);
		cs.gridy = 6;
		cs.gridx = 0;
		cs.gridwidth = 6;
		JLabel playStats = new JLabel("Correct Strategy Play statistics");
  		Font f = playStats.getFont();
  		f = f.deriveFont(14f);
  		playStats.setFont(f);
  		playStats.setForeground(Color.blue);
		statusArea.add(playStats,cs);
		cs.gridwidth = 1;
		cs.gridy = 8;
		cs.anchor = GridBagConstraints.EAST;
		JLabel right = new JLabel("Correct:");
		right.setForeground(Color.green);
		statusArea.add(right,cs);
		cs.anchor = GridBagConstraints.WEST;
		int correct = BlackJackApp.getCorrect();
		JLabel rightNum = new JLabel(new Integer(correct).toString());
		cs.gridx = 1;
		statusArea.add(rightNum,cs);
		cs.gridx = 2;
		cs.anchor = GridBagConstraints.EAST;
		JLabel wrong = new JLabel("Incorrect:");
		wrong.setForeground(Color.red);
		statusArea.add(wrong,cs);
		cs.gridx = 3;
		int incorrect = BlackJackApp.getIncorrect();
		JLabel wrongNum = new JLabel(new Integer(incorrect).toString());
		cs.anchor = GridBagConstraints.WEST;
		statusArea.add(wrongNum,cs);
		cs.gridx = 4;
		int per = 0;
		if (incorrect != 0) per = correct * 100 / (incorrect+correct);
		statusArea.add(new JLabel(new Integer(per).toString()+"%"),cs);
	}
	
	public Insets insets(int t, int l, int b, int r)
	{
		return new Insets(t,l,b,r);
	}
}
