package gui.components;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import gui.components.data.Section;

public class DrawingPanel extends CustomPanel implements MouseMotionListener, MouseListener {

    public DrawingPanel(int w, int h, int count) {
        super(w, h, count);

        addMouseMotionListener(this);
        addMouseListener(this);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        paintSections(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        paintSections(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private void paintSections(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            for (int i = 0; i < sections.size(); i++) {
            	final Section s = sections.get(i);
                if (e.getX() > s.getX() && e.getX() < s.getX() + s.getWidth() && e.getY() > s.getY() && e.getY() < s.getY() + s.getHeight()) {
                    drawSection(i, 1D);
                }
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            for (Section s : sections) {
                if (e.getX() > s.getX() && e.getX() < s.getX() + s.getWidth() && e.getY() > s.getY() && e.getY() < s.getY() + s.getHeight())
                    s.erase();
            }
        }

        repaint();
    }
    
    public void drawSection(int index, double val) {
    	final Section s = sections.get(index);
    	s.setBlack(val);
    	repaint();
    }
}
