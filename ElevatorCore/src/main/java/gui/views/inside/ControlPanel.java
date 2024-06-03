package gui.views.inside;

import gui.buttons.AuthorizeButton;
import gui.buttons.DefaultButton;
import gui.views.ScrollPanel;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import properties.ProgramSettings;
import simulator.ElevatorSimulator;
import simulator.controllers.AbstractController;
import gui.buttons.EmergencyButton;

/**
 * Implements a control panel.
 * 
 * @author SSE
 */
public class ControlPanel extends ScrollPanel {

    private static final long serialVersionUID = 1048074068165896521L;

    private ArrayList<DefaultButton> lButtons;
    private int iElevatorIndex;
    private EmergencyButton emergencyButton;
    private JButton openButton;
    private JButton closeButton;

    /**
     * Creates a control panel for the specified elevator.
     * 
     * @param iElevatorIndex the 0-based elevator index
     */
    public ControlPanel(int iElevatorIndex) {
        super();
        this.iElevatorIndex = iElevatorIndex;
        lButtons = new ArrayList<DefaultButton>(ProgramSettings.getInstance().getFloors());

        init();
    }

    /**
     * Returns the emergency button.
     * 
     * @return the emergency button (may be <b>null</b>)
     */
    public EmergencyButton getEmergencyButton() {
        return emergencyButton;
    }

    /**
     * Returns the open button.
     * 
     * @return the open button (may be <b>null</b>)
     */
    public JButton getOpenButton() {
        return openButton;
    }

    /**
     * Returns the close button.
     * 
     * @return the close button (may be <b>null</b>)
     */
    public JButton getCloseButton() {
        return closeButton;
    }

    /**
     * Returns the buttons.
     * 
     * @return the buttons
     */
    public ArrayList<DefaultButton> getButtons() {
        return lButtons;
    }

    /**
     * Initializes the control panel.
     */
    private void init() {
        int iwButton = 30;
        int ihButton = 15;

        this.setLayout(new GridLayout(2 + (ProgramSettings.getInstance().getFloors() / 6), 6));
        this.setPreferredSize(
            new Dimension(350, (ihButton + 5) * (ProgramSettings.getInstance().getFloors() / 4) + 40));

        // button to keep door open/close door
        if (ProgramSettings.getInstance().isInnerviewDoorButton()) {
            addOpenCloseButtons();
        } else {
            this.add(new JLabel());
        }

        // emergency button
        if (ProgramSettings.getInstance().isInnerviewEmergency()) {
            addEmergencyButton();
        } else {
            this.add(new JLabel());
        }

        // authorization button
        if (ProgramSettings.getInstance().isInnerviewAuthorization()) {
            this.add(new AuthorizeButton());
        } else {
            this.add(new JLabel());
        }

        // empty lables so that first floor button does not occur directly
        // besides emergency button
        this.add(new JLabel());
        this.add(new JLabel());

        // create floor buttons
        final int floors = ProgramSettings.getInstance().getFloors();
        for (int i = 0; i < floors; i++) {
            DefaultButton mjbButton = new DefaultButton(i, iElevatorIndex, true);
            mjbButton.setPreferredSize(new Dimension(iwButton, ihButton));

            lButtons.add(mjbButton);
            this.add(mjbButton);
        }
    }

    /**
     * Returns the responsible controller.
     * 
     * @return the controller
     */
    private AbstractController getController() {
        return ElevatorSimulator.getInstance().getController(iElevatorIndex);
    }

    /**
     * Adds buttons for door open/close.
     */
    private void addOpenCloseButtons() {
        Icon close = new ImageIcon(getClass().getResource("../../../close.gif"));
        Icon open = new ImageIcon(getClass().getResource("../../../open.gif"));

        openButton = new JButton();
        openButton.setIcon(open);

        closeButton = new JButton();
        closeButton.setIcon(close);

        openButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // invert on each click
                getController().getElevator().setKeepDoorsOpen(!getController().getElevator().isKeepDoorsOpen());
            }

        });
        closeButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // invert on each click
                getController().getElevator().setCloseDoorsNow(!getController().getElevator().isCloseDoorsNow());
            }

        });

        this.add(openButton);
        this.add(closeButton);
    }

    /**
     * Create emergency button.
     */
    private void addEmergencyButton() {
        emergencyButton = new EmergencyButton(-1, iElevatorIndex, true);
        emergencyButton.setPreferredSize(new Dimension(20, 20));
        this.add(emergencyButton);
    }

}
