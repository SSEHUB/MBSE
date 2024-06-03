package gui.views;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * A scollable panel.
 */
public class ScrollPanel extends JPanel implements Scrollable, MouseMotionListener {

    // http://java.sun.com/docs/books/tutorial/uiswing/examples/components/
    // ScrollDemoProject/src/components/ScrollablePicture.java
    
    private static final long serialVersionUID = 1L;
    private int maxUnitIncrement = 50;

    /**
     * Creates a scroll panel.
     */
    public ScrollPanel() {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // The user is dragging us, so scroll!
        Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
        scrollRectToVisible(r);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return super.getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        // Get the current position.
        int currentPosition = 0;
        if (orientation == SwingConstants.HORIZONTAL) {
            currentPosition = visibleRect.x;
        } else {
            currentPosition = visibleRect.y;
        }

        // Return the number of pixels between currentPosition
        // and the nearest tick mark in the indicated direction.
        if (direction < 0) {
            int newPosition = currentPosition - (currentPosition / maxUnitIncrement) * maxUnitIncrement;
            return (newPosition == 0) ? maxUnitIncrement : newPosition;
        } else {
            return ((currentPosition / maxUnitIncrement) + 1) * maxUnitIncrement - currentPosition;
        }
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width - maxUnitIncrement;
        } else {
            return visibleRect.height - maxUnitIncrement;
        }
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    /**
     * Changes the maximum unit increment (scrollbar buttons)
     * 
     * @param pixels the number of pixel to increment
     */
    public void setMaxUnitIncrement(int pixels) {
        maxUnitIncrement = pixels;
    }

    /**
     * Returns the maxiumum unit increment (scrollbar buttons).
     * 
     * @return the number of pixel to increment
     */
    public int getMaxUnitIncrement() {
        return this.maxUnitIncrement;
    }

}