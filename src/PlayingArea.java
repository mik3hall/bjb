package org.bjb;

// Import all the Java API classes needed by this program
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.CubicCurve2D;
import javax.swing.*;
import javax.swing.border.*;

class PlayingArea extends JPanel {
	static final Color lightGreen = new Color(0x88ff88);	
//	static final Dimension preferredSize = new Dimension(700,600);
//	static final Dimension minimumSize = new Dimension(700,600);
	MessageViewer mv = null;
	
	public PlayingArea() {
		setBackground(lightGreen);
		setBorder(new LineBorder(Color.black,1));
//		setSize(700,600);
//		setPreferredSize(preferredSize);
//		setMinimumSize(minimumSize);
	}
	
	public void clear() {
		final Graphics g = getGraphics();
		final Color c = g.getColor();
		g.setColor(lightGreen);
		g.fillRect(0,0,getWidth(),getHeight());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				blackJackPays((Graphics2D)g);
				seventeen((Graphics2D)g);
				insurance((Graphics2D)g);
				g.setColor(c);
			}
		});
	}

	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		if (mv == null) {
			mv = new MessageViewer(this);
			mv.addMessage("Hello, World!");
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				blackJackPays((Graphics2D)g);
				seventeen((Graphics2D)g);
				insurance((Graphics2D)g);
			}
		});
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