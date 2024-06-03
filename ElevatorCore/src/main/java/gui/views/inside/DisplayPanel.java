package gui.views.inside;

import gui.views.inside.displays.FloorSliderDisplay;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import properties.ProgramSettings;

import simulator.model.Elevator;
import simulator.model.Request;

/**
 * Represents a panel with all display elements of an elevator. 
 */
public class DisplayPanel extends JPanel {

    private static final long serialVersionUID = -7053155399480364159L;

    private JLabel lblCurrentFloor;
    private FloorSliderDisplay floorSliderDisplay;
    private JLabel lblTargetFloor;
    private JLabel lblDoorOpen;
    private JLabel lblDirection;
    private JLabel lblController;
    private String sControllerName;

    private final int PANEL_WIDTH = 500;
    private final int PANEL_HEIGHT = 320;

    /**
     * @return the lblTargetFloor
     */
    public JLabel getLblTargetFloor() {
        return lblTargetFloor;
    }

    /**
     * @return the lblDoorOpen
     */
    public JLabel getLblDoorOpen() {
        return lblDoorOpen;
    }

    /**
     * @return the lblDirection
     */
    public JLabel getLblDirection() {
        return lblDirection;
    }

    /**
     * @return the lblController
     */
    public JLabel getLblController() {
        return lblController;
    }

    /**
     * Creates a panel.
     * 
     * @param sControllerName the elevator controller name
     */
    public DisplayPanel(String sControllerName) {
        super();
        this.sControllerName = sControllerName;
        init();
    }

    /**
     * Initializes the panel.
     */
    private void init() {

        final ProgramSettings settings = ProgramSettings.getInstance(); 
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setLayout(new BorderLayout());

        JPanel jpStatus = new JPanel();
        jpStatus.setLayout(new GridLayout(7, 2));

        // fonts for display
        Font font = new Font("Arial", Font.PLAIN, 13);

        lblController = new JLabel();
        lblController.setFont(font);

        lblController.setText(sControllerName);

        jpStatus.add(new JLabel("Controller:"));
        jpStatus.add(lblController);

        if (settings.isDisplayFloorNumber()) {
            lblCurrentFloor = new JLabel("0");
            lblCurrentFloor.setFont(font);
            jpStatus.add(new JLabel("Current floor: "));
            jpStatus.add(lblCurrentFloor);
        }

        if (settings.isFloorSliderDisplay()) {
            floorSliderDisplay = new FloorSliderDisplay();
            this.add(floorSliderDisplay, BorderLayout.NORTH);
        }

        if (settings.isDisplayTarget()) {
            lblTargetFloor = new JLabel("none");
            lblTargetFloor.setFont(font);
            jpStatus.add(new JLabel("Target floor:"));
            jpStatus.add(lblTargetFloor);
        }

        if (settings.isDisplayDoorstate()) {
            lblDoorOpen = new JLabel();
            lblDoorOpen.setFont(font);
            jpStatus.add(new JLabel("Door:"));
            jpStatus.add(lblDoorOpen);
        }

        if (settings.isDisplayDirection()) {
            lblDirection = new JLabel();
            Icon icArrowZ = new ImageIcon(getClass().getResource("../../../stop.gif"));
            lblDirection.setIcon(icArrowZ);
            jpStatus.add(new JLabel("Direction:"));
            jpStatus.add(lblDirection);
        }

        jpStatus.setPreferredSize(new Dimension(400, 300));
        this.add(jpStatus, BorderLayout.CENTER);

        // Panels fuer abstaende
        this.add(new JPanel(), BorderLayout.EAST);

    }

    /**
     * Changes the elevator status.
     */
    public void updateValues(Elevator elevator, Request currentTarget) {
        int iCurrentFloor = elevator.getCurrentFloor();
        int iDirection = elevator.getDirection();
        ProgramSettings settings = ProgramSettings.getInstance();

        if (settings.isDisplayFloorNumber()) {
            lblCurrentFloor.setText(Integer.toString(iCurrentFloor + iDirection));
        }

        if (settings.isFloorSliderDisplay()) {
            floorSliderDisplay.setValue(iCurrentFloor + iDirection);
        }

        if (settings.isDisplayTarget()) {
            if (currentTarget != null) {
                lblTargetFloor.setText(Integer.toString(currentTarget.getFloor()));
            }
        }
    }

    /**
     * Changes the direction display.
     */
    public void changeImageDirection(int iDirection) {
        switch (iDirection) {
        case 1:
            lblDirection.setIcon(new ImageIcon(getClass().getResource("../../../up.gif")));
            break;
        case -1:
            lblDirection.setIcon(new ImageIcon(getClass().getResource("../../../down.gif")));
            break;
        default:
            lblDirection.setIcon(new ImageIcon(getClass().getResource("../../../stop.gif")));
            break;
        }
    }

}
