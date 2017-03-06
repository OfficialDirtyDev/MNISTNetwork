package gui.components;

import gui.components.data.Section;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CustomPanel extends JPanel {

	private static final long serialVersionUID = -8727517478033719182L;
	protected ArrayList<Section> sections;
	private int width;
	private int height;
	private int count;

	public CustomPanel(int w, int h, int count) {
		super();
		this.width = w;
		this.height = h;
		this.count = count;

		setPreferredSize(new Dimension(w, h));
		setBackground(Color.WHITE);
		generateSections();
	}

	private void generateSections() {
		sections = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			for (int j = 0; j < count; j++) {
				sections.add(new Section(i * (width / count), j * (height / count), width / count, height / count));
			}
		}

		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		generateSections(g);
		drawSections(g);

	}

	private void generateSections(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);

		for (Section s : sections) {
			g.drawLine(0, s.getY(), width, s.getY());
			g.drawLine(s.getX(), 0, s.getX(), height);
		}
	}

	private void drawSections(Graphics g) {
		for (Section s : sections) {
			int c = (int) (255 - (s.getBlack() * 255D));
			g.setColor(new Color(c,c,c));
			g.fillRect(s.getX(), s.getY(), s.getWidth(), s.getHeight());
		}
	}

	public ArrayList<Double> getPixels() {
		ArrayList<Double> pixels = new ArrayList<>();
		for (Section s : sections) {
			pixels.add(1 - s.getBlack());
		}

		return pixels;
	}

	public void clear() {
		for (Section s : sections) {
			s.erase();
		}
		repaint();
	}

	public void drawDigit(double[] pixels) {
		for (int i = 0; i < pixels.length; i++) {
			sections.get(i).setBlack(1 - pixels[i]);
		}
		repaint();
	}
	
	public ArrayList<Section> getSections() {
		return sections;
	}
}
