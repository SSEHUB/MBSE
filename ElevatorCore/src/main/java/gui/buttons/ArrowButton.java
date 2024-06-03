package gui.buttons;

import gui.windows.MainWindow;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import engine.ButtonEvent;
import engine.EventsManager;
import engine.ButtonEvent.ButtonEventObserver;
import engine.ButtonEvent.Kind;
import properties.ProgramSettings;
import simulator.ElevatorSimulator;
import simulator.model.Request;

/**
 * Spinner for elevator up/down call buttons.
 * 
 * @author SSE 
 */
public class ArrowButton extends JSpinner implements ElevatorOuterArrowButton {

    /**
     * Default button press event observer.
     */
    public static ButtonEventObserver DEFAULT = new ButtonEventObserver() {

        @Override
        public void notifyReceived(ButtonEvent event) {
            if (Kind.BUTTON == event.getKind()) {
                // add/remove direction request
                int iElevatorIndex = event.getElevator();
                int floorId = event.getFloor();
                ElevatorSimulator sim = ElevatorSimulator.getInstance();
                if (event.isHightlighed()) {
                    if (ProgramSettings.getInstance().isSynchronized()) {
                        sim.getMultiController().deleteRequest(iElevatorIndex, floorId);
                    } else {
                        sim.getController(iElevatorIndex).deleteTarget(floorId);
                    }
                } else {
                    Request target = new Request(floorId, event.getValue());
                    if (ProgramSettings.getInstance().isSynchronized()) {
                        sim.getMultiController().addRequest(target, iElevatorIndex, false);
                    } else {
                        sim.getController(iElevatorIndex).addRequest(target);
                    }
                }
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
                ElevatorSimulator.getInstance().getController(event.getElevator()).ignoreFloor(event.getFloor(), true);
            }
        }
        
        @Override
        public Object getHandlingInfo() {
            return Kind.CANCEL;
        }

    };
    
    private static final long serialVersionUID = 1L; 

    private boolean isIgnored;
    private boolean hightlighted[] = new boolean[2];
    private int floorId;
    private int iElevatorIndex;
    private static SpinnerNumberModel snm = new SpinnerNumberModel(1, -1, 1, 2);

    /**
     * Creates a button with floor index/id and elevator index.
     * 
     * @param floorId the floor id/index
     * @param iElevatorIndex the elevator index
     */
    public ArrowButton(int floorId, int iElevatorIndex) {
        super(snm);
        EventsManager.BUTTONS.addObserverIfUnknown(DEFAULT);
        EventsManager.BUTTONS.addObserverIfUnknown(MOUSE);
        this.floorId = floorId;
        this.iElevatorIndex = iElevatorIndex;
        this.isIgnored = false;
        this.remove(this.getEditor());
        this.setBorder(null);
        this.setMaximumSize(new Dimension(20, 25));
        this.setBounds(200, 45, 20, 25);
    
        this.getComponent(0).addMouseListener(new ArrowButtonListener(0));
        this.getComponent(1).addMouseListener(new ArrowButtonListener(1));
    }

    @Override
    public JComponent getButtonComponent() {
        return this;
    }

    /**
     * Returns the floor id/index.
     * 
     * @return id the id
     */
    public int getFloorId() {
        return floorId;
    }

    // TODO jedem elevator mitgeben achtet auf buttonklicks am spinner

    /**
     * Sets the ignored flag.
     * 
     * @param isIgnored the new status of the flag
     */
    private void setIgnored(boolean isIgnored) {
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
     * Returns the highlighting state of the denoted component.
     * 
     * @param iComponent the inner component index (up/down)
     * @return the highlighting state
     */
    public boolean isHighlighted(int iComponent) {
        return hightlighted[iComponent];
    }

    /**
     * Changes the highlighting state of the denoted component.
     * 
     * @param iComponent the inner component index (up/down)
     * @param bHighlight the highlighting state
     */
    public void setHighlight(int iComponent, boolean bHighlight) {
        if (bHighlight) {
            if (iComponent == 0) {
                this.getComponent(iComponent).setBackground(Color.GREEN);
            } else {
                this.getComponent(iComponent).setBackground(Color.YELLOW);
            }
        } else {
            this.getComponent(iComponent).setBackground(null);
        }
        hightlighted[iComponent] = bHighlight;
    }
    
    /**
     * Listener for up/down buttons.
     * 
     * @author SSE
     */
    private class ArrowButtonListener extends MouseAdapter implements MouseListener {
        
        private int iComp;

        /**
         * Creates a listener for the specified component.
         * 
         * @param iComp the component index (up/down)
         */
        public ArrowButtonListener(int iComp) {
            this.iComp = iComp;
        }
        
        @Override
        public void mouseClicked(MouseEvent e) {
            ArrowButton self = ArrowButton.this;
            
            if (e.getButton() == MouseEvent.BUTTON1 && self.isEnabled()) {
                // switch highlights
                if (self.isHighlighted(iComp)) {
                    if (ProgramSettings.getInstance().isOuterviewCancel()) {
                        EventsManager.BUTTONS.sendEvent(new ButtonEvent(iElevatorIndex, floorId, Kind.BUTTON, 0, 
                            true, false));
                        if (ProgramSettings.getInstance().isSynchronized()) {
                            MainWindow.getInstance().highlightFloorButtons(floorId, false, iComp);
                            MainWindow.getInstance().highlightFloorButtons(floorId, false, Math.abs(iComp - 1));
                        } else {
                            setHighlight(iComp, false);
                            setHighlight(Math.abs(iComp - 1), false);
                        }
                    }
                } else {
                    EventsManager.BUTTONS.sendEvent(new ButtonEvent(iElevatorIndex, floorId, Kind.BUTTON, (int) self.getValue(), 
                        false, false));
                    if (ProgramSettings.getInstance().isSynchronized()) {
                        MainWindow.getInstance().highlightFloorButtons(floorId, true, iComp);
                        MainWindow.getInstance().highlightFloorButtons(floorId, false, Math.abs(iComp - 1));
                    } else {
                        setHighlight(iComp, true);
                        setHighlight(Math.abs(iComp - 1), false);
                    }
                }
            }
            
            // right mouse button to deactivate/ignore floors
            if (e.getButton() == MouseEvent.BUTTON3 && self.isEnabled()) {
                DefaultButton downButton = MainWindow.getInstance().getControlPanel(iElevatorIndex).getButtons().get(
                    self.getFloorId());

                if (!self.isIgnored() || !downButton.isIgnored()) {
                    self.setIgnored(true);
                    self.setEnabled(false);
                    EventsManager.BUTTONS.sendEvent(new ButtonEvent(iElevatorIndex, self.getFloorId(), Kind.CANCEL, 0,
                        false, false));
                    downButton.setIgnored(true);
                    downButton.setEnabled(false);
                } else {
                    self.setIgnored(false);
                    self.setEnabled(true);
                    downButton.setIgnored(false);
                    downButton.setEnabled(true);
                }
            }

            

            // TODO cleanup
/*            ArrowButton self = ArrowButton.this;
            
            if (e.getButton() == MouseEvent.BUTTON1 && self.isEnabled()) {

                // add/remove direction request, switch highlights
                if (self.isHighlighted(iComp)) {
                    if (ProgramSettings.getInstance().isOuterviewCancel()) {
                        if (ProgramSettings.getInstance().isSynchronized()) {
                            ElevatorSimulator.getInstance().getMultiController().deleteRequest(iElevatorIndex, id);
                            MainWindow.getInstance().highlightFloorButtons(id, false, iComp);
                            MainWindow.getInstance().highlightFloorButtons(id, false, Math.abs(iComp - 1));
                        } else {
                            ElevatorSimulator.getInstance().getController(iElevatorIndex).deleteTarget(id);
                            setHighlight(iComp, false);
                            setHighlight(Math.abs(iComp - 1), false);
                        }
                    }
                } else {
                    Request target = new Request(self.getId(), (Integer) self.getValue());
                    if (ProgramSettings.getInstance().isSynchronized()) {
                        ElevatorSimulator.getInstance().getMultiController().addRequest(target, iElevatorIndex, false);
                        MainWindow.getInstance().highlightFloorButtons(id, true, iComp);
                        MainWindow.getInstance().highlightFloorButtons(id, false, Math.abs(iComp - 1));
                    } else {
                        ElevatorSimulator.getInstance().getController(iElevatorIndex).addRequest(target);
                        setHighlight(iComp, true);
                        setHighlight(Math.abs(iComp - 1), false);
                    }
                }
            }
            
            // right mouse button to deactivate/ignore floors
            if (e.getButton() == MouseEvent.BUTTON3 && self.isEnabled()) {
                DefaultButton untenbutton = MainWindow.getInstance().getControlPanel(iElevatorIndex).getButtons().get(
                    self.getId());

                if (!self.isIgnored() || !untenbutton.isIgnored()) {
                    self.setIgnored(true);
                    self.setEnabled(false);
                    ElevatorSimulator.getInstance().getController(iElevatorIndex).ignoreFloor(self.getId(), true);
                    untenbutton.setIgnored(true);
                    untenbutton.setEnabled(false);
                } else {
                    self.setIgnored(false);
                    self.setEnabled(true);
                    untenbutton.setIgnored(false);
                    untenbutton.setEnabled(true);
                }
            }*/
        }
    }

}
