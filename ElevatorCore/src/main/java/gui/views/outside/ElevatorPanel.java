package gui.views.outside;

import gui.views.ScrollPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import properties.InstanceFactory;
import properties.ProgramSettings;
import gui.buttons.ElevatorOuterButton;
import gui.buttons.EmergencyButton;

/**
 * The floors panel displaying the elevator.
 */
public class ElevatorPanel extends ScrollPanel {

    private static final long serialVersionUID = 1L;
    private JLabel elevatorImage;
    private int iElevatorIndex;
    private ArrayList<ElevatorOuterButton> callButtons;
    private ArrayList<EmergencyButton> emergencyButtons;

    /**
     * @return the emergencyButtons
     */
    public ArrayList<EmergencyButton> getEmergencyButtons() {
        return emergencyButtons;
    }

    public ArrayList<ElevatorOuterButton> getCallButtons() {
        return callButtons;
    }

    public int getElevatorIndex() {
        return iElevatorIndex;
    }

    /**
     * @return the elevatorImage
     */
    public JLabel getElevatorImage() {
        return elevatorImage;
    }

    /**
     * Ueberladener Konstruktor.
     */
    public ElevatorPanel(int iIndex) {
        this.iElevatorIndex = iIndex;
        this.callButtons = new ArrayList<ElevatorOuterButton>(ProgramSettings.getInstance().getFloors());
        createPanel();
    }

    /**
     * Creates the panel with the elevator image.
     */
    private void createPanel() {
        this.setLayout(null);
        this.setOpaque(false);

        this.setPreferredSize(new Dimension(200, Math.max(470,
            (ProgramSettings.getTotalHeight(ProgramSettings.getInstance().getFloors())) + 81)));

        this.elevatorImage = new JLabel();
        Icon icon = new ImageIcon(getClass().getResource("../../../doorsClosed.gif"));
        this.elevatorImage.setIcon(icon);
        this.elevatorImage.setBounds(55, this.getPreferredSize().height - 64, 40, 40);
        this.add(elevatorImage);

        if (ProgramSettings.getInstance().isSynchronized()) {
            final int floorsButtons = ProgramSettings.getInstance().getFloorsButtons();
            final int elevators = ProgramSettings.getInstance().getElevators();
            if (floorsButtons == elevators) {
                createComponents();
            } else {
                int elevPerFloorButtons = Math.round(elevators / floorsButtons);
                // if elevPerFloorButtons == 1 (rounded), then create componens only if less then buttons per floor
                if ((elevPerFloorButtons == 1) && iElevatorIndex < floorsButtons) {
                    createComponents();
                }
                // if iControllerIndex is a multiple of elevPerFloorButtons
                if ((elevPerFloorButtons != 1) && ((iElevatorIndex + 1) % elevPerFloorButtons) == 0) {
                    createComponents();
                }
            }
        } else {
            createComponents();
        }
    }

    /**
     * Creates the panel components.
     */
    private void createComponents() {
        createButtons();
        createFloorLabels();
        if (ProgramSettings.getInstance().isOuterviewEmergency()) {
            createEmergencyButtons();
        }
    }

    /**
     * Creates the buttons to call an elevator.
     */
    public void createButtons() {
        final int floors = ProgramSettings.getInstance().getFloors();
        for (int i = 0; i < floors; i++) {
            ElevatorOuterButton tmpButton = InstanceFactory.createOuterButton(i, iElevatorIndex);
            tmpButton.setBounds(110,
                this.getPreferredSize().height - (i * ProgramSettings.getInstance().getFloorsHeight()) - 45,
                50, 20);
            callButtons.add(tmpButton);
            this.add(tmpButton.getButtonComponent());
        }
    }

    /**
     * Creates the floor labels.
     */
    private void createFloorLabels() {
        final int floors = ProgramSettings.getInstance().getFloors();
        for (int i = 0; i < floors; i++) {
            JLabel lblFloor = new JLabel(Integer.toString(i));
            lblFloor.setText(Integer.toString(i));
            lblFloor.setPreferredSize(new Dimension(10, 5));
            lblFloor.setBounds(35 - (5 * lblFloor.getText().length()),
                this.getPreferredSize().height - (i * ProgramSettings.getInstance().getFloorsHeight()) - 55,
                lblFloor.getText().length() * 10, 45);
            this.add(lblFloor);
        }
    }

    /**
     * Creates the emergency buttons.
     */
    private void createEmergencyButtons() {
        this.emergencyButtons = new ArrayList<EmergencyButton>();
        final int floors = ProgramSettings.getInstance().getFloors();
        for (int i = 0; i < floors; i++) {
            EmergencyButton emButton = new EmergencyButton(i, iElevatorIndex, false);
            emButton.setBounds(170,
                            this.getPreferredSize().height - (i * ProgramSettings.getInstance().getFloorsHeight()) - 45,
                            20, 20);
            emergencyButtons.add(emButton);
            this.add(emButton);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        final int floors = ProgramSettings.getInstance().getFloors();
        final int floorsHeight = ProgramSettings.getInstance().getFloorsHeight();
        for (int i = 0; i < floors; i++) {
            g.drawRect(floorsHeight, this.getPreferredSize().height - (i * floorsHeight) - 70, floorsHeight,
                            floorsHeight);
        }
    }

}