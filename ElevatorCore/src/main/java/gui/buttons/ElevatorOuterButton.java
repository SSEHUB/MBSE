package gui.buttons;

import javax.swing.JComponent;

/**
 * An outer button for calling/controlling an elevator Must have a public constructor with two ints 
 * {@code (int id, int iElevatorIndex)}.
 */
public interface ElevatorOuterButton {
    
    /**
     * Sets the highlighting for this button.
     * 
     * @param aHighlight the highlighting state
     */
    public default void setHighlight(boolean aHighlight) {}

    /**
     * Sets the highlighting for the denoted inner component of this button.
     * 
     * @param iComponent the component index (depends on button)
     * @param bHighlight the highlighting state
     */
    public default void setHighlight(int iComponent, boolean bHighlight) {}

    /**
     * Returns the actual button.
     * 
     * @return the button
     */
    public JComponent getButtonComponent();

    /**
     * Enables (or disables) the button.
     * @param enabled {@code true} to enable the button, otherwise {@code false}
     */
    public default void setEnabled(boolean enabled) {
        getButtonComponent().setEnabled(enabled);
    }

    /**
     * Sets the bounding rectangle of this button to the specified
     * {@code x}, {@code y}, {@code width}, and {@code height}.
     * 
     * @param x the new X coordinate for the upper-left
     *                    corner of this {@code Rectangle}
     * @param y the new Y coordinate for the upper-left
     *                    corner of this {@code Rectangle}
     * @param width the new width for this {@code Rectangle}
     * @param height the new height for this {@code Rectangle}
     */
    public default void setBounds(int x, int y, int width, int height) {
        getButtonComponent().setBounds(x, y, width, height);
    }

}
