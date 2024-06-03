package gui.views.inside.displays;

import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;

import properties.ProgramSettings;

/**
 * Realizes a (classical) floor slider display (alternative: numeric display).
 * 
 * @author SSE
 */
public class FloorSliderDisplay extends JSlider {
    
    private static final long serialVersionUID = 841340392425621437L;

    /**
     * Creates the display.
     */
    public FloorSliderDisplay() {
        super(0, ProgramSettings.getInstance().getFloors(), 0);
        init();
    }
    
    /**
     * Initializes the display.
     */
    private void init() {
        setPaintTrack(false);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>(getMaximum());
        JLabel nn;
        for (int i = 0; i < getMaximum(); i++) {
            nn = new JLabel("" + i);
            labelTable.put(i, nn);
        }
        setLabelTable(labelTable);
        setPaintLabels(true);
        setOpaque(false);
    }
    
}