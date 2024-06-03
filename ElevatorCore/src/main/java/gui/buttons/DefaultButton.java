package gui.buttons;

import gui.views.inside.ControlPanel;
import gui.windows.MainWindow;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import engine.ButtonEvent;
import engine.ButtonEvent.ButtonEventObserver;
import engine.ButtonEvent.Kind;
import engine.EventsManager;
import properties.ProgramSettings;
import simulator.ElevatorSimulator;
import simulator.controllers.AbstractController;
import simulator.model.Request;

/**
 * Default elevator button.
 * 
 * @author SSE
 */
public class DefaultButton extends JButton implements ElevatorOuterButton {

    /**
     * Default button press event observer.
     */
    public static ButtonEventObserver DEFAULT = new ButtonEventObserver() {

        @Override
        public void notifyReceived(ButtonEvent event) {
            if (Kind.BUTTON == event.getKind()) {
                int iElevatorIndex = event.getElevator();
                int floorId = event.getFloor();
                boolean bInside = event.isInside();
                AbstractController controller =  ElevatorSimulator.getInstance().getController(iElevatorIndex);
                ControlPanel controlPanel = MainWindow.getInstance().getControlPanel(iElevatorIndex);
                if (!event.isHightlighed()) {
                    Request target = new Request(floorId, 0);
                    if (ProgramSettings.getInstance().isSynchronized()) {
                        if (ProgramSettings.getInstance().isInnerviewEmergency()) {
                            if (bInside && controlPanel.getEmergencyButton().isActivated()) {
                                ElevatorSimulator.getInstance().getMultiController().addPriorityCall(iElevatorIndex, 
                                    floorId, bInside);
                            } else {
                                ElevatorSimulator.getInstance().getMultiController().addRequest(target,
                                                controller.getElevatorIndex(), bInside);
                            }
                        } else {
                            ElevatorSimulator.getInstance().getMultiController().addRequest(target, 
                                controller.getElevatorIndex(), bInside);
                        }
                    } else {
                        if (ProgramSettings.getInstance().isInnerviewEmergency()) {
                            if (bInside && controlPanel.getEmergencyButton().isActivated()) {
                                controller.addPriorityCall(floorId);
                            } else {
                                controller.addRequest(target);
                            }
                        } else {
                            controller.addRequest(target);
                        }
                    }
                }
                controller.startSimulation();                    
            }
        }
        
        @Override
        public Object getHandlingInfo() {
            return Kind.BUTTON;
        }
        
    };
    
    /**
     * Default mouse button event observer.
     */
    public static ButtonEventObserver MOUSE = new ButtonEventObserver() {

        @Override
        public void notifyReceived(ButtonEvent event) {
            if (Kind.CANCEL == event.getKind()) {
                int iElevatorIndex = event.getElevator();
                AbstractController controller =  ElevatorSimulator.getInstance().getController(iElevatorIndex);
                controller.ignoreFloor(event.getFloor(), true);
            }
        }

        @Override
        public Object getHandlingInfo() {
            return Kind.CANCEL;
        }

    };
    
    private static final long serialVersionUID = 1L;

    private boolean isIgnored;
    private boolean bHighlighted = false;
    private int floorId;
    private int iElevatorIndex;
    private boolean bInside;

    /**
     * Standardkonstruktor
     */
    public DefaultButton() {
        EventsManager.BUTTONS.addObserverIfUnknown(DEFAULT);
        EventsManager.BUTTONS.addObserverIfUnknown(MOUSE);
    }

    /**
     * Creates a default button.
     * 
     * @param floorId
     *            floor id/index
     * @param iElevatorIndex
     *            - elevator index assigned to this button
     * @param bInside
     *            {@code true} if the button is inside (elevator status panel)
     *            or outside
     */
    public DefaultButton(int floorId, int iElevatorIndex, boolean bInside) {
        this();
        this.isIgnored = false;
        this.bInside = bInside;
        this.floorId = floorId;
        this.iElevatorIndex = iElevatorIndex;
        this.setText(Integer.toString(floorId));
        this.setFont(new Font("Arial", Font.PLAIN, 10));
        this.setIconTextGap(0);
        this.addActionListener(new DefaultButtonActionListener());
        this.addMouseListener(new DefaultButtonMouseListener());
    }

    /**
     * Creates a default button.
     * 
     * @param id
     *            floor id/index
     * @param iElevatorIndex
     *            - elevator index assigned to this button
     */
    public DefaultButton(int id, int iElevatorIndex) {
        this(id, iElevatorIndex, false);
    }

    @Override
    public JComponent getButtonComponent() {
        return this;
    }

    /**
     * Returns the button/floor id.
     * 
     * @return the button/floor id
     */
    public int getFloorId() {
        return floorId;
    }

    /**
     * Sets the ignored flag.
     * 
     * @param isIgnored
     *            the new status of the flag
     */
    public void setIgnored(boolean isIgnored) {
        this.isIgnored = isIgnored;
    }

    /**
     * Returns the ignored flag.
     * 
     * @return the ignored flag
     */
    public boolean isIgnored() {
        return isIgnored;
    }

    /**
     * Returns whether the button itself is highlighed.
     * 
     * @return the highlighting state
     */
    public boolean isHighlighted() {
        return bHighlighted;
    }

    /**
     * Changes the highlighting state.
     * 
     * @param aHighlight
     *            the highlighting state
     */
    public void setHighlight(boolean aHighlight) {
        if (aHighlight) {
            this.setBackground(Color.GREEN);
        } else {
            this.setBackground(null);
        }

        this.bHighlighted = aHighlight;
    }

    /**
     * Returns the actual control panel.
     * 
     * @return the control panel
     */
    private ControlPanel getControlPanel() {
        return MainWindow.getInstance().getControlPanel(iElevatorIndex);
    }

    /**
     * Returns the actual controller.
     * 
     * @return the controller
     */
    private AbstractController getController() {
        return ElevatorSimulator.getInstance().getController(iElevatorIndex);
    }

    /**
     * Action listener for button.
     * 
     * @author SSE
     */
    private class DefaultButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            AbstractController controller = getController();
            // if button is highlighed then flip highlight
            boolean isHighlighted = isHighlighted();
            if (isHighlighted) {
                if (ProgramSettings.getInstance().isOuterviewCancel()) {
                    controller.deleteTarget(floorId);
                    getControlPanel().getButtons().get(floorId).setHighlight(false);

                    if (ProgramSettings.getInstance().isSynchronized()) {
                        MainWindow.getInstance().highlightFloorButtons(floorId, false, 0);
                    } else {
                        setHighlight(false);
                    }
                }
            } else {
                // if elevators are synchronized, all buttons on a floor must be
                // highlighted
                if (ProgramSettings.getInstance().isSynchronized()) {
                    if (bInside) {
                        setHighlight(true);
                    } else {
                        MainWindow.getInstance().highlightFloorButtons(floorId, true, 0);
                    }
                } else {
                    setHighlight(true);
                    getControlPanel().getButtons().get(floorId).setHighlight(true);
                }
            }
            EventsManager.BUTTONS.sendEvent(new ButtonEvent(iElevatorIndex, floorId, Kind.BUTTON, 0, 
                isHighlighted, bInside));

            // TODO cleanup
/*            AbstractController controller = getController();
            // if button is highlighed then flip highlight
            if (isHighlighted()) {
                if (ProgramSettings.getInstance().isOuterviewCancel()) {
                    controller.deleteTarget(id);
                    getControlPanel().getButtons().get(id).setHighlight(false);

                    if (ProgramSettings.getInstance().isSynchronized()) {
                        MainWindow.getInstance().highlightFloorButtons(id, false, 0);
                    } else {
                        setHighlight(false);
                    }
                }
            } else {
                Request target = new Request(id, 0);
                // if elevators are synchronized, all buttons on a floor must be
                // highlighted
                if (ProgramSettings.getInstance().isSynchronized()) {
                    if (bInside) {
                        setHighlight(true);
                    } else {
                        MainWindow.getInstance().highlightFloorButtons(id, true, 0);
                    }

                    if (ProgramSettings.getInstance().isInnerviewEmergency()) {
                        if (bInside && getControlPanel().getEmergencyButton().isActivated()) {
                            ElevatorSimulator.getInstance().getMultiController().addPriorityCall(iElevatorIndex, id,
                                            bInside);
                        } else {
                            ElevatorSimulator.getInstance().getMultiController().addRequest(target,
                                            controller.getIndex(), bInside);
                        }
                    } else {
                        ElevatorSimulator.getInstance().getMultiController().addRequest(target, controller.getIndex(),
                                        bInside);
                    }
                } else {
                    setHighlight(true);
                    if (ProgramSettings.getInstance().isInnerviewEmergency()) {
                        if (bInside && getControlPanel().getEmergencyButton().isActivated()) {
                            getController().addPriorityCall(id);
                        } else {
                            controller.addRequest(target);
                        }
                    } else {
                        controller.addRequest(target);
                    }
                    getControlPanel().getButtons().get(id).setHighlight(true);
                }
            }
            controller.startSimulation();*/
        }

    }

    /**
     * Mouse listener for button.
     * 
     * @author SSE
     */
    private class DefaultButtonMouseListener extends MouseAdapter implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            DefaultButton self = DefaultButton.this;
            if (e.getButton() == MouseEvent.BUTTON3) {
                DefaultButton downButton = getControlPanel().getButtons().get(self.getFloorId());
                if (!self.isIgnored() || !downButton.isIgnored()) {
                    self.setIgnored(true);
                    self.setEnabled(false);
                    EventsManager.BUTTONS.sendEvent(new ButtonEvent(iElevatorIndex, floorId, Kind.CANCEL, 0,
                        isHighlighted(), bInside));
                    //getController().ignoreFloor(self.getId(), true); // TODO remove
                    downButton.setIgnored(true);
                    downButton.setEnabled(false);
                } else {
                    self.setIgnored(false);
                    self.setEnabled(true);
                    downButton.setIgnored(false);
                    downButton.setEnabled(true);
                }
            }
        }
    }

}
