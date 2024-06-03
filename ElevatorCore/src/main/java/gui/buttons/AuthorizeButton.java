package gui.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import engine.ButtonEvent;
import engine.EventsManager;
import engine.ButtonEvent.ButtonEventObserver;
import engine.ButtonEvent.Kind;

/**
 * Allows to authorize for special privileged actions.
 * 
 * @author SSE
 */
public class AuthorizeButton extends JButton {

    private static final long serialVersionUID = -5747868174847160408L;

    /**
     * Default button press event observer.
     */
    public static ButtonEventObserver DEFAULT = new ButtonEventObserver() {

        @Override
        public void notifyReceived(ButtonEvent event) {
            if (Kind.AUTHORIZE == event.getKind()) {
                // action in original implementation not implemented
            }
        }
        
        @Override
        public Object getHandlingInfo() {
            return Kind.AUTHORIZE;
        }
        
    };
    
    /**
     * Creates the button.
     */
    public AuthorizeButton() {
        EventsManager.BUTTONS.addObserverIfUnknown(DEFAULT);
        Icon icon = new ImageIcon(getClass().getResource("../../key.gif"));
        setIcon(icon);
        addActionListener(new AuthorizeButtonListener());
    }
        
    /**
     * Button action listener.
     * 
     * @author SSE
     */
    private class AuthorizeButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            try {
                int result = Integer.valueOf(JOptionPane.showInputDialog(null,
                        "Enter your authorization level!\n"
                                + "(0: no authorization\n"
                                + " 1-7: authorization levels)"));
                if (result < 0 || result > 7) {
                    JOptionPane.showMessageDialog(null, "Invalid authorization level", "Fehler",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    EventsManager.BUTTONS.sendEvent(new ButtonEvent(-1, -1, Kind.AUTHORIZE, result, 
                        false, false));
                }
            } catch (NumberFormatException e) {
                // ignored
            }
        }
        
    }
}