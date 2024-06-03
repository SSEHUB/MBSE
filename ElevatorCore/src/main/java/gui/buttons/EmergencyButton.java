package gui.buttons;

import gui.windows.MainWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import engine.ButtonEvent;
import engine.EventsManager;
import engine.ButtonEvent.ButtonEventObserver;
import engine.ButtonEvent.Kind;
import properties.ProgramSettings;
import simulator.ElevatorSimulator;

/**
 * An emergency button.
 */
public class EmergencyButton extends JButton {

    /**
     * Default button press event observer.
     */
    public static ButtonEventObserver DEFAULT = new ButtonEventObserver() {

        @Override
        public void notifyReceived(ButtonEvent event) {
            if (Kind.EMERGENCY == event.getKind()) {
                if (ProgramSettings.getInstance().isSynchronized()) {
                    ElevatorSimulator.getInstance().getMultiController()
                        .addPriorityCall(event.getElevator(), event.getFloor(), event.isInside());
                } else {
                    ElevatorSimulator.getInstance().getController(event.getElevator())
                        .addPriorityCall(event.getFloor());
                }
            }
        }
        
        @Override
        public Object getHandlingInfo() {
            return Kind.EMERGENCY;
        }
        
    };
    
    private static final long serialVersionUID = 1L;
    private boolean bActivated = false;
    private int floorId;
    private int iElevatorIndex;
    private boolean inside;
    private Icon iconActive;
    private Icon iconNotActive;

    /**
     * Creates an emergency button
     * 
     * @param floorId floor id/index
     * @param iElevatorIndex - controller/elevator index assigned to this button
     * @param bInside {@code true} if the button is inside (elevator status panel) or outside
     */
    public EmergencyButton(int floorId, int iElevatorIndex, boolean bInside) {
        super();
        EventsManager.BUTTONS.addObserverIfUnknown(DEFAULT);
        this.floorId = floorId;
        this.inside = bInside;
        this.iElevatorIndex = iElevatorIndex;

        this.iconActive = new ImageIcon(getClass().getResource(
                "../../flame_activated.gif"));
        this.iconNotActive = new ImageIcon(getClass().getResource(
                "../../flame.gif"));
        this.setIcon(iconNotActive);

        this.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isActivated()) {
                    setActivated(false);
                    EventsManager.BUTTONS.sendEvent(new ButtonEvent(EmergencyButton.this.iElevatorIndex, 
                        EmergencyButton.this.floorId, Kind.EMERGENCY_RESOLVED, 0, false, EmergencyButton.this.inside));
                } else {
                    setActivated(true);
                    if (!isInside()) {
                        EventsManager.BUTTONS.sendEvent(new ButtonEvent(EmergencyButton.this.iElevatorIndex, 
                            EmergencyButton.this.floorId, Kind.EMERGENCY, 0, false, EmergencyButton.this.inside));
                        if (ProgramSettings.getInstance().isSynchronized()) {
                            MainWindow.getInstance().activateEmergencyButtons(
                                EmergencyButton.this.floorId, true);
                        }
                    }
                }
            }
        });
    }

    /**
     * Returns whether this button is inside/outside an elevator.
     * 
     * @return {@code true} for inside, {@code false} else
     */
    public boolean isInside() {
        return inside;
    }

    /**
     * Is the button activated (differs from {@link #isEnabled()}).
     * 
     * @return {@code true} for acticated, {@code false} else
     */
    public boolean isActivated() {
        return bActivated;
    }

    /**
     * Changes the activation of this button.
     * 
     * @param bActivated {@code true} to activate, {@code false} to passivate
     */
    public void setActivated(boolean bActivated) {
        if (bActivated) {
            this.setIcon(iconActive);
        } else {
            this.setIcon(iconNotActive);
        }
        this.bActivated = bActivated;
    }

}