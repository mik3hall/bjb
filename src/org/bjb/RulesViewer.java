package org.bjb;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class RulesViewer extends JFrame {

		String[] doubleOpts = new String[] { "Double on any (DOA)","Double on 10-11 only","Double on 9-10-11 only" };
		JComboBox<String> doubling = new JComboBox<String>(doubleOpts);

		// Create an action
	    Action okact = new AbstractAction("OK") {
	    	private static final long serialVersionUID = -1L;
	        // This method is called when the button is pressed
	        public void actionPerformed(ActionEvent evt) {
	            setVisible(false);
	        }
	    };
		JButton ok = new JButton(okact);
		
		public RulesViewer(String name,final Rules rules) {
			super("Rules for " + name);
	        getContentPane().setBackground(Color.white);
	 		getContentPane().setLayout(new GridBagLayout());
	        GridBagConstraints c = new GridBagConstraints();
			c.insets = insets(5,10,5,15);
			c.anchor = GridBagConstraints.WEST;
			JLabel nameLbl = new JLabel(name);
	  		Font f = nameLbl.getFont();
	  		f = f.deriveFont(14f);
	  		nameLbl.setFont(f);
	  		nameLbl.setForeground(Color.blue);
			getContentPane().add(nameLbl,c);
			c.gridy += 2;
			String label17 = null;
			if (rules.isH17())
				label17 = "Dealer hit on 17 (H17)";
			else label17 = "Dealer stand on 17 (S17)";
			JCheckBox h17 = new JCheckBox(label17);
			if (rules.isH17())
				h17.setSelected(true);
			getContentPane().add(h17,c);
			c.gridy += 2;
			JCheckBox das = new JCheckBox("Double down after split allowed (DAS)");
			if (rules.isDAS())
				das.setSelected(true);
			getContentPane().add(das,c);
			c.gridy += 2;
			doubling.setBackground(Color.white);
			if (rules.getDDs().length == 0)
				doubling.setSelectedItem("Double on any (DOA)");
			else if (rules.getDDs().length == 2)
				doubling.setSelectedItem("Double on 10-11 only");
			else if (rules.getDDs().length == 3)
				doubling.setSelectedItem("Double on 9-10-11 only");
			doubling.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					String dd = (String)e.getItem();
					if (e.getStateChange() == ItemEvent.SELECTED) {
						if (dd.equals("Double on any (DOA)"))
							rules.setDDs(new int[0]);
						else if (dd.equals("Double on 10-11 only"))
							rules.setDDs(new int[] { 10,11 });
						else if (dd.equals("Double on 9-10-11 only"))
							rules.setDDs(new int[] { 9,10,11 });
					}						
				}				
			});
			getContentPane().add(doubling,c);
			c.gridy += 2;
			JCheckBox splitAcesDraw = new JCheckBox("Draw on split aces");
			if (rules.isDrawToSplitAces())
				splitAcesDraw.setSelected(true);
			getContentPane().add(splitAcesDraw,c);
			c.gridy += 2;
			JCheckBox resplit = new JCheckBox("Resplit pairs allowed");
			if (rules.isResplit())
				resplit.setSelected(true);
			getContentPane().add(resplit,c);
			c.gridy += 2;
			JCheckBox insurance = new JCheckBox("Insurance offered");
			if (rules.isInsurance())
				insurance.setSelected(true);
			getContentPane().add(insurance,c);
			c.gridy += 2;
			JCheckBox surrender = new JCheckBox("Late surrender allowed");
			if (rules.isSurrender())
				surrender.setSelected(true);
			getContentPane().add(surrender,c);
			c.gridy += 2;
			getContentPane().add(ok,c);
			getRootPane().setDefaultButton(ok);
			pack();
		}

		public Insets insets(int t, int l, int b, int r)
		{
			return new Insets(t,l,b,r);
		}
}
