package org.bjb;

// Import all the Java API classes needed by this program
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.Timer;

class PlayingArea extends JPanel implements ActionListener {
//	static final Color lightGreen = new Color(0x88ff88);
//	static final Color lightGreen = new Color(0x00cc99);
//	static final Color lightGreen = new Color(0x228b22);
	static final Color lightGreen = new Color(0x32cd32);
//	static final Dimension preferredSize = new Dimension(700,600);
//	static final Dimension minimumSize = new Dimension(700,600);
	MessageViewer mv = null;
	int flashCnt = 0;
	boolean textVisible = false;
	static BufferedImage wood = null;
	static {
		InputStream	is = BlackJackApp.class.getResourceAsStream("images/woodbackground.png");
		try {
			wood = ImageIO.read(is);
		}
		catch (java.io.IOException ioex) { 
			ioex.printStackTrace();
		}
	}
	
	public PlayingArea() {
		setBackground(lightGreen);
		setBorder(new LineBorder(Color.black,1));
//		setSize(700,600);
//		setPreferredSize(preferredSize);
//		setMinimumSize(minimumSize);
	}
	
	public void clear() {
		final Graphics g = getGraphics();
		if (g == null) return;
		final Color c = g.getColor();
		g.setColor(lightGreen);
		g.fillRect(0,0,getWidth(),getHeight());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				decorate((Graphics2D)g);
				g.setColor(c);
			}
		});
	}
	
	private void decorate(Graphics2D g2d) {
		blackJackPays(g2d);
		seventeen(g2d);
		insurance(g2d);
		border(g2d);
		table(g2d);		
	}
	
	public void addGameMessage(String text) {
		mv.addGameMessage(text);
	}
	
	public void clearHandMessages() {
		if (mv != null)
			mv.clearHandMessages();
	}
	
	public void addHandMessage(String text) {
		mv.addHandMessage(text);
	}
	
	public void displayShuffled() {
		if (flashCnt != 0) return;
//		System.out.println("PA displayShuffled");
		final Graphics2D g = (Graphics2D)getGraphics();
		final Color c = g.getColor();
		final Component comp = this;
//		g.setColor(lightGreen);
//		g.fillRect(0,0,getWidth(),getHeight());
		final PlayingArea pa = this;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String s = "Shuffling...";
				int x = 0, y = 20;
				Hand.back.paintIcon(comp,g,x,y);
				Hand.back.paintIcon(comp,g,x+Hand.back.getIconWidth(),y);
				g.setStroke(new BasicStroke(1.0f));
				g.setPaint(Color.blue);
				g.fillRect(x+5,y+Hand.back.getIconHeight()+4,x+2*Hand.back.getIconWidth()-10,Hand.back.getIconHeight()/3);				
				g.setPaint(Color.black);
				g.drawRect(x+5,y+Hand.back.getIconHeight()+4,x+2*Hand.back.getIconWidth()-10,Hand.back.getIconHeight()/3);
				Font font = new Font("Monospaced", Font.BOLD, 18);
				g.setFont(font);
				g.setStroke(new BasicStroke(2.0f));
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		    	// a composite with transparency.
//		    	Composite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .75f);
//		    	g.setComposite(c);
				g.setPaint(Color.white);
				g.drawString(s,x+10,y+Hand.back.getIconHeight()+22);
				g.setColor(c);
				flashCnt = 1;
				textVisible = true;
				Timer t = new Timer(450,pa);
				t.setRepeats(false);
				t.restart();
			}
		});		
	}

	public void actionPerformed(ActionEvent evt) {
//		System.out.println("PA AP flashCnt " + flashCnt + " event " + evt);
		final Graphics2D g = (Graphics2D)getGraphics();
		final Color c = g.getColor();
		final Component comp = this;
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
				String s = "Shuffling...";
				Font font = new Font("Monospaced", Font.BOLD, 18);
				g.setFont(font);
				g.setStroke(new BasicStroke(2.0f));
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);	
				int x = 0, y = 20;
				if (textVisible) {
					Hand.back.paintIcon(comp,g,x,y);
					Hand.back.paintIcon(comp,g,x+Hand.back.getIconWidth(),y);
					g.setStroke(new BasicStroke(1.0f));
					g.setPaint(Color.blue);
//					g.fillRect(x+5,y+Hand.back.getIconHeight()/3-16,x+2*Hand.back.getIconWidth()-10,Hand.back.getIconHeight()/3-2);				
					g.fillRect(x+5,y+Hand.back.getIconHeight()+4,x+2*Hand.back.getIconWidth()-10,Hand.back.getIconHeight()/3);				
					g.setPaint(Color.black);
//					g.drawRect(x+5,y+Hand.back.getIconHeight()/3-16,x+2*Hand.back.getIconWidth()-10,Hand.back.getIconHeight()/3-2);
					g.drawRect(x+5,y+Hand.back.getIconHeight()+4,x+2*Hand.back.getIconWidth()-10,Hand.back.getIconHeight()/3);
					textVisible = false;
				}
				else {
					flashCnt++;
					Hand.back.paintIcon(comp,g,x,y);
					Hand.back.paintIcon(comp,g,x+Hand.back.getIconWidth(),y);
					g.setStroke(new BasicStroke(1.0f));
					g.setPaint(Color.blue);
//					g.fillRect(x+5,y+Hand.back.getIconHeight()/3-16,x+2*Hand.back.getIconWidth()-10,Hand.back.getIconHeight()/3-2);				
					g.fillRect(x+5,y+Hand.back.getIconHeight()+4,x+2*Hand.back.getIconWidth()-10,Hand.back.getIconHeight()/3);				
					g.setPaint(Color.black);
//					g.drawRect(x+5,y+Hand.back.getIconHeight()/3-16,x+2*Hand.back.getIconWidth()-10,Hand.back.getIconHeight()/3-2);
					g.drawRect(x+5,y+Hand.back.getIconHeight()+4,x+2*Hand.back.getIconWidth()-10,Hand.back.getIconHeight()/3);
					g.setPaint(Color.white);
//					g.drawString(s,x+10,y+Hand.back.getIconHeight()/3+5);
					g.drawString(s,x+10,y+Hand.back.getIconHeight()+22);
					g.setColor(c);		
					textVisible = true;
				}
				if (flashCnt < 4) {
					Timer t = new Timer(450,this);
					t.setRepeats(false);
					t.start();
				}
				else {
					g.setColor(lightGreen);
					g.fillRect(0,y,Hand.back.getIconWidth()*2,Hand.back.getIconHeight()*2);
					flashCnt = 0;
					textVisible = false;
//					EffectsLocker.release();
				}
//			}
//		});			
	}
	
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		if (mv == null) 
			mv = new MessageViewer(this);
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
				decorate((Graphics2D)g);
//			}
//		});
	}
	
	private void table(Graphics2D g2d) {
		Rectangle r = new Rectangle(0,0,wood.getWidth(null),wood.getHeight(null));
		Paint paint = g2d.getPaint();
		TexturePaint tp = new TexturePaint(wood,r);
		GeneralPath  ll_corner = new GeneralPath();
		ll_corner.moveTo(0, getHeight()-130);
		ll_corner.lineTo(0,getHeight());
		ll_corner.lineTo(130,getHeight());
		ll_corner.quadTo(30, getHeight()-30, 0, getHeight()-130);
		g2d.setPaint(tp);
		g2d.fill(ll_corner);
		GeneralPath ll_edge = new GeneralPath();
		ll_edge.moveTo(0, getHeight()-50);
		ll_edge.lineTo(0,getHeight());
		ll_edge.lineTo(50,getHeight());
		ll_edge.quadTo(15, getHeight()-15, 0, getHeight()-50);
		g2d.setPaint(BlackJackApp.teal);
		g2d.fill(ll_edge);	
		g2d.setPaint(tp);
		GeneralPath lr_corner = new GeneralPath();
		lr_corner.moveTo(getWidth(), getHeight()-130);
		lr_corner.lineTo(getWidth(), getHeight());
		lr_corner.lineTo(getWidth()-130, getHeight());
		lr_corner.quadTo(getWidth()-30,getHeight()-30, getWidth(),getHeight()-130);
		g2d.fill(lr_corner);
		GeneralPath lr_edge = new GeneralPath();
		lr_edge.moveTo(getWidth(), getHeight()-50);
		lr_edge.lineTo(getWidth(),getHeight());
		lr_edge.lineTo(getWidth()-50,getHeight());
		lr_edge.quadTo(getWidth()-15, getHeight()-15, getWidth(), getHeight()-50);
		g2d.setPaint(BlackJackApp.teal);
		g2d.fill(lr_edge);	
		g2d.setPaint(paint);
	}
	
	private void border(Graphics2D g2d) {
		GeneralPath border = new GeneralPath();
		border.moveTo(220, 45);
		border.lineTo(185, 130);
//		border.curveTo(385, 180, 600, 180, 815, 130);
		border.quadTo(400, 300, 815, 130);
		border.lineTo(780, 40);
		border.quadTo(400, 205, 220, 45);
		g2d.setColor(Color.white);
		g2d.setStroke(new BasicStroke(5.0f));
		g2d.draw(border);
	}
	
	private void blackJackPays(Graphics2D g2) {
		if (mv != null) mv.draw(g2);
		g2.setColor(Color.yellow);
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		String bj = "BLACKJACK";
		String s = "PAYS 3 TO 2";
		Font font = new Font("Monospaced", Font.BOLD, 24);
		FontRenderContext frc = g2.getFontRenderContext();
		GlyphVector gv = font.createGlyphVector(frc, bj);
		int length = gv.getNumGlyphs();	
		AffineTransform init_t = new AffineTransform();
		init_t.rotate(Math.PI / 6);
		for (int i=0;i<length;i++)
			gv.setGlyphTransform(i,init_t);
		for (int i = 0; i < length; i++) {
		  Point2D p = gv.getGlyphPosition(i);
		  double theta = (double) i / (double) (length - 1) * Math.PI * -1 / 6;
		  AffineTransform at = AffineTransform.getTranslateInstance(p.getX(),
			  p.getY());
		  at.translate(225,95);
		  at.rotate(theta);
		  Shape glyph = gv.getGlyphOutline(i);
		  Shape transformedGlyph = at.createTransformedShape(glyph);
		  g2.setColor(Color.black);
		  g2.draw(transformedGlyph);
		  g2.setColor(Color.yellow);
		  g2.fill(transformedGlyph);
		}
		Point2D endP = gv.getGlyphPosition(length-1);
		gv = font.createGlyphVector(frc, s);
		length = gv.getNumGlyphs();
		for (int i = 0; i < length; i++) {
		  Point2D p = gv.getGlyphPosition(i);
		  double theta = (double) i / (double) (length - 1) * Math.PI * -1 / 6;
		  AffineTransform at = AffineTransform.getTranslateInstance(p.getX(),
			  p.getY());
		  at.translate(390+endP.getX(),95+endP.getY());
		  at.rotate(theta);
		  Shape glyph = gv.getGlyphOutline(i);
		  Shape transformedGlyph = at.createTransformedShape(glyph);
		  g2.setColor(Color.black);
		  g2.draw(transformedGlyph);
		  g2.setColor(Color.yellow);
		  g2.fill(transformedGlyph);
		}
	}
	
	private void seventeen(Graphics2D g2) {
		g2.setColor(Color.white);
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		String s1 = "Dealer must draw";
		String to = "to"; 
		String s2 = "16 and stand on 17";
		Font font = new Font("Monospaced", Font.BOLD, 14);
		FontRenderContext frc = g2.getFontRenderContext();
		GlyphVector gv = font.createGlyphVector(frc, s1);
		int length = gv.getNumGlyphs();	
		double len_s1 = (double)length;
		AffineTransform init_t = new AffineTransform();
		init_t.rotate(Math.PI / 6);
		for (int i=0;i<length;i++)
			gv.setGlyphTransform(i,init_t);
		for (int i = 0; i < length; i++) {
		  Point2D p = gv.getGlyphPosition(i);
		  double theta = (double) i / (double) (length - 1) * Math.PI * -1 / 6;
		  AffineTransform at = AffineTransform.getTranslateInstance(p.getX(),
			  p.getY());
		  at.translate(220,110);
		  at.rotate(theta);
		  Shape glyph = gv.getGlyphOutline(i);
		  Shape transformedGlyph = at.createTransformedShape(glyph);
		  g2.setColor(Color.black);
		  g2.draw(transformedGlyph);
		  g2.setColor(Color.white);
		  g2.fill(transformedGlyph);
		}
		Point2D endS1 = gv.getGlyphPosition(length-1);
		gv = font.createGlyphVector(frc,to);
		length = gv.getNumGlyphs();
		for (int i = 0; i < length; i++) {
		  Point2D p = gv.getGlyphPosition(i);
		  AffineTransform at = AffineTransform.getTranslateInstance(p.getX(),
			  p.getY());
		  at.translate(365+endS1.getX(),110+endS1.getY());
		  Shape glyph = gv.getGlyphOutline(i);
		  Shape transformedGlyph = at.createTransformedShape(glyph);
		  g2.setColor(Color.black);
		  g2.draw(transformedGlyph);
		  g2.setColor(Color.white);
		  g2.fill(transformedGlyph);
		}		
//		endP = gv.getGlyphPosition(length-1);
		gv = font.createGlyphVector(frc, s2);
		length = gv.getNumGlyphs();
		for (int i = 0; i < length; i++) {
		  Point2D p = gv.getGlyphPosition(i);
		  double theta = (double) i / (double) (length - 1) * Math.PI * -1 / 6 * ((double)length/len_s1);
		  AffineTransform at = AffineTransform.getTranslateInstance(p.getX(),
			  p.getY());
		  at.translate(405+endS1.getX(),110+endS1.getY());
		  at.rotate(theta);
		  Shape glyph = gv.getGlyphOutline(i);
		  Shape transformedGlyph = at.createTransformedShape(glyph);
		  g2.setColor(Color.black);
		  g2.draw(transformedGlyph);
		  g2.setColor(Color.white);
		  g2.fill(transformedGlyph);
		}		
	}
	
	private void insurance(Graphics2D g2) {
		g2.setColor(Color.white);
/*
		g2.setStroke(new BasicStroke(5.0f));
		CubicCurve2D.Float curve = new CubicCurve2D.Float(220,180, 400,260, 570,260, 745,165);
		g2.draw(curve);
		g2.drawLine(170,180,220,180);
		g2.drawLine(745,165,795,165);
		curve = new CubicCurve2D.Float(170,180,378,340,567,340,795,165);
		g2.draw(curve);
*/
		g2.setStroke(new BasicStroke(2.0f));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		String s1 = "INSURANCE ";
		String s2 = "PAYS 2 TO 1";
		Font font = new Font("Monospaced", Font.BOLD, 24);
		FontRenderContext frc = g2.getFontRenderContext();
		GlyphVector gv = font.createGlyphVector(frc, s1);
		int length = gv.getNumGlyphs();		
		AffineTransform init_t = new AffineTransform();
		init_t.rotate(Math.PI / 6);
		for (int i=0;i<length;i++)
			gv.setGlyphTransform(i,init_t);
		for (int i = 0; i < length; i++) {
		  Point2D p = gv.getGlyphPosition(i);
		  double theta = (double) i / (double) (length - 1) * Math.PI * -1 / 6;
		  AffineTransform at = AffineTransform.getTranslateInstance(p.getX(),
			  p.getY());
		  at.translate(220,140);
		  at.rotate(theta);
		  Shape glyph = gv.getGlyphOutline(i);
		  Shape transformedGlyph = at.createTransformedShape(glyph);
		  g2.setColor(Color.black);
		  g2.draw(transformedGlyph);
		  g2.setColor(Color.white);
		  g2.fill(transformedGlyph);
		}
		Point2D endS1 = gv.getGlyphPosition(length-1);
		gv = font.createGlyphVector(frc,s2);
		length = gv.getNumGlyphs();
		for (int i = 0; i < length; i++) {
		  Point2D p = gv.getGlyphPosition(i);
		  double theta = (double) i / (double) (length - 1) * Math.PI * -1 / 6;
		  AffineTransform at = AffineTransform.getTranslateInstance(p.getX(),
			  p.getY());
		  at.translate(375+endS1.getX(),140+endS1.getY());
		  at.rotate(theta);
		  Shape glyph = gv.getGlyphOutline(i);
		  Shape transformedGlyph = at.createTransformedShape(glyph);
		  g2.setColor(Color.black);
		  g2.draw(transformedGlyph);
		  g2.setColor(Color.white);
		  g2.fill(transformedGlyph);
		}
	}
}