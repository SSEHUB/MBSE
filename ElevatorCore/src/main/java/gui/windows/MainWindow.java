package gui.windows;

import gui.views.ScrollPanel;
import gui.views.inside.ControlPanel;
import gui.views.inside.DisplayPanel;
import gui.views.outside.ElevatorPanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import engine.ElevatorEvent;
import engine.ElevatorEvent.Kind;
import engine.EventsManager;
import properties.InstanceFactory;
import properties.ProgramSettings;
import simulator.ElevatorSimulator;
import simulator.model.Elevator;

/**
 * Main window of elevator simulator.
 */
public class MainWindow {

    private static MainWindow instance = null;

    private JFrame frame;
    private Border loweredbevel = BorderFactory.createLoweredBevelBorder();
    private ArrayList<ElevatorPanel> lElevatorPanels = ProgramSettings.createElevatorsList(ElevatorPanel.class);
    private ArrayList<DisplayPanel> lDisplayPanels = ProgramSettings.createElevatorsList(DisplayPanel.class);
    private ArrayList<ControlPanel> lControlPanels = ProgramSettings.createElevatorsList(ControlPanel.class);
    private ArrayList<JScrollPane> lScrollPanes = ProgramSettings.createElevatorsList(JScrollPane.class);

    /**
     * Creates an instance.
     */
    public MainWindow() {
        instance = this;
        createWindow();
    }

    // getter & setter

    /**
     * 
     * Statische Methode, liefert die einzige Instanz dieser
     * 
     * Klasse zurück
     */

    public static MainWindow getInstance() {

        if (instance == null) {

            instance = new MainWindow();

        }

        return instance;
    }

    public static void setInstance(MainWindow mw) {
        instance = mw;
    }

    /**
     * Getter f&uuml;r das Hauptframe
     * 
     * @return frame
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * @return the lElevatorPanels
     */
    public ElevatorPanel getElevatorPanel(int i) {
        return lElevatorPanels.get(i);
    }

    /**
     * @return the lStatusPanels
     */
    public DisplayPanel getDisplayPanel(int i) {
        return lDisplayPanels.get(i);
    }

    public ControlPanel getControlPanel(int i) {
        return lControlPanels.get(i);
    }

    /**
     * @return the lScrollPanes
     */
    public JScrollPane getScrollPane(int i) {
        return lScrollPanes.get(i);
    }

    /**
     * Supports the layout stuffFuer die Gestaltung des GridBagLayouts.
     */
    private void addComponent(Container cont, GridBagLayout gbl, Component c, int x, int y, int width, int height,
                    double weightx, double weighty) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbl.setConstraints(c, gbc);
        cont.add(c);
    }

    /**
     * Creates the elements of the main window.
     */
    private void createWindow() {

        frame = new JFrame("ElevatorSimulator");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setSize(1025, 960);

        // Creating the menu bar
        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar);
        JMenu mainMenu = new JMenu("File");
        JMenuItem beenden = new JMenuItem("Exit");
        mainMenu.add(beenden);
        JMenu helper = new JMenu("?");

        JMenuItem helpMain = new JMenuItem("Help");
        helpMain.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                new InfoWindow("Help on the simulator", "../../helpMain.htm").setVisible(true);
            }
        });
        JMenuItem info = new JMenuItem("Info");
        info.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                new InfoWindow("Info on the simulator", "../../info.htm").setVisible(true);
            }
        });
        helper.add(helpMain);
        helper.add(info);
        menubar.add(mainMenu);
        menubar.add(helper);

        ScrollPanel jpAll = new ScrollPanel();
        jpAll.setAlignmentX(Component.LEFT_ALIGNMENT);
        // control elements in jpAll
        GridBagLayout gbl = new GridBagLayout();
        jpAll.setLayout(gbl);

        // add elevator panel to scroll pane

        int vsbPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
        int hsbPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;

        // x y w h wx wy
        addComponent(jpAll, gbl, createTopArea(), 0, 0, 1, 1, 0, 1.0);
        addComponent(jpAll, gbl, createBottomArea(), 0, 1, 1, 1, 0, 0);

        JScrollPane scpane = new JScrollPane(jpAll, vsbPolicy, hsbPolicy);
        scpane.getHorizontalScrollBar();
        // scpane.setPreferredSize(new Dimension(300, 400));
        frame.getContentPane().add(scpane);

        beenden.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int iAnswer = JOptionPane.showConfirmDialog(null, "Would you like to terminate the program?",
                                "Terminate program", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (iAnswer == JOptionPane.YES_OPTION) {
                    System.exit(1);
                }

            }
        });
        frame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - frame.getSize().width) / 2,
                        (Toolkit.getDefaultToolkit().getScreenSize().height - frame.getSize().height) / 2);

        frame.setVisible(true);

    }

    public ScrollPanel createTopArea() {
        ScrollPanel jpElevatorArea = new ScrollPanel();
        jpElevatorArea.setLayout(new BoxLayout(jpElevatorArea, BoxLayout.LINE_AXIS));
        jpElevatorArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        final int elevators = ProgramSettings.getInstance().getElevators();
        // ElevatorPanels erzeugen und im Fahrstuhlbereich einfügen
        if (elevators > 1) {
            for (int i = 0; i < elevators; i++) {
                jpElevatorArea.add(buildElevatorPanel(i));
            }
        } else {
            jpElevatorArea.add(buildElevatorPanel(0));
        }

        return jpElevatorArea;

    }

    public Component createBottomArea() {
        Component result;
        final int elevators = ProgramSettings.getInstance().getElevators();
        if (elevators > 1) {
            JTabbedPane bottomArea = new JTabbedPane();
            bottomArea.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

            for (int i = 0; i < elevators; i++) {
                JLabel lbl = new JLabel(String.valueOf("Fahrstuhl " + (i + 1)));
                lbl.setPreferredSize(new Dimension(229, 10));

                bottomArea.addTab(Integer.toString(i + 1), buildBottomPanel(i));
                bottomArea.setTabComponentAt(i, lbl);
                bottomArea.setMnemonicAt(i, KeyEvent.VK_2);
            }

            ScrollPanel scrollpanel = new ScrollPanel();
            scrollpanel.setLayout(new FlowLayout());
            scrollpanel.setPreferredSize(new Dimension(350, 35 * (ProgramSettings.getInstance().getFloors()) / 4 + 40));
            bottomArea.add(scrollpanel);
            result = bottomArea;
        } else {
            result = buildBottomPanel(0);
        }
        return result;
    }

    public JPanel buildBottomPanel(int iElevatorIndex) {

        JPanel resultPanel = new JPanel();

        resultPanel.setPreferredSize(new Dimension(1000, 320));
        resultPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        ControlPanel pnlControls = new ControlPanel(iElevatorIndex);
        DisplayPanel pnlDisplays = new DisplayPanel(
                        ElevatorSimulator.getInstance().getController(iElevatorIndex).getDisplayName());

        lControlPanels.add(pnlControls);
        lDisplayPanels.add(pnlDisplays);

        // scrollbar settings
        int vsbPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
        int hsbPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
        JScrollPane pane = new JScrollPane(pnlControls, vsbPolicy, hsbPolicy);
        pane.setPreferredSize(new Dimension(370, 300));
        // set in parent panel
        JPanel jpAll = new JPanel();
        jpAll.setLayout(new BorderLayout());
        JPanel jpInlay = new JPanel(new BorderLayout());

        jpInlay.add(pnlDisplays, BorderLayout.WEST);
        jpInlay.add(pnlControls, BorderLayout.CENTER);

        resultPanel.add(jpInlay, BorderLayout.SOUTH);
        return resultPanel;

    }

    /**
     * Creates a panel for the scroll bar left to the elevator.
     */
    public JScrollPane buildElevatorPanel(int iElevatorIndex) {
        ElevatorPanel pnlElevator = new ElevatorPanel(iElevatorIndex);

        /*
         * scrollbar settings
         */
        int vsbPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
        int hsbPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
        JScrollPane elevatorScrollPane = new JScrollPane(pnlElevator, vsbPolicy, hsbPolicy);
        elevatorScrollPane.setPreferredSize(new Dimension(250, 550));

        // titel border
        TitledBorder titleElevator = BorderFactory.createTitledBorder(loweredbevel, "Elevator" + (iElevatorIndex + 1));
        elevatorScrollPane.setBorder(titleElevator);

        final int floors = ProgramSettings.getInstance().getFloors();
        /*
         * scrollbar to bottom
         */
        elevatorScrollPane.getViewport().setViewPosition(new Point(1, floors * ProgramSettings.getTotalHeight(floors)));

        if (ProgramSettings.getInstance().getElevators() > 1) {
            lElevatorPanels.add(pnlElevator);
            lScrollPanes.add(elevatorScrollPane);
        } else {
            lElevatorPanels.add(pnlElevator);
            lScrollPanes.add(elevatorScrollPane);
        }

        return elevatorScrollPane;
    }

    /**
     * Animates the elevator doors opening.
     * 
     * @param iControllerIndex
     *            elevator controller index
     * @param iTargetIndex
     *            current Floor of the elevator
     */
    public void animateOpenDoors(int iControllerIndex, int iTargetIndex) {
        ElevatorPanel pnlElevator = getElevatorPanel(iControllerIndex);
        ControlPanel pnlControls = getControlPanel(iControllerIndex);

        // disable buttons so that no further elevator call on the same floor
        // can happen during door opening/closing
        if (pnlElevator.getCallButtons().size() != 0) {
            if (ProgramSettings.getInstance().isSynchronized()) {
                setFloorButtonsEnabled(iTargetIndex, false);
                highlightFloorButtons(iTargetIndex, false, 0);
                if (InstanceFactory.isOuterArrowButton()) {
                    highlightFloorButtons(iTargetIndex, false, 1);
                }
            } else { // remove highlight
                pnlElevator.getCallButtons().get(iTargetIndex).setEnabled(false);
                if (InstanceFactory.isOuterArrowButton()) {
                    pnlElevator.getCallButtons().get(iTargetIndex).setHighlight(0, false);
                    pnlElevator.getCallButtons().get(iTargetIndex).setHighlight(1, false);
                } else {
                    pnlElevator.getCallButtons().get(iTargetIndex).setHighlight(false);
                }
            }

            // Removes highlights in status panel and disables buttons
            pnlControls.getButtons().get(iTargetIndex).setHighlight(false);
            pnlControls.getButtons().get(iTargetIndex).setEnabled(false);

            ProgramSettings settings = ProgramSettings.getInstance();

            try {
                // Animate opening of doors
                changeImage("../../doorsOpening.gif", iControllerIndex);
                EventsManager.ELEVATORS
                                .sendEvent(new ElevatorEvent(iControllerIndex, iTargetIndex, Kind.DOORS_OPENING));
                Thread.sleep(settings.getDoorOpeningDelay());

                // single image displaying opened doors
                changeImage("../../doorsOpened.gif", iControllerIndex);
                EventsManager.ELEVATORS.sendEvent(new ElevatorEvent(iControllerIndex, iTargetIndex, Kind.DOORS_OPEN));
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Animates the elevator open waiting and elevator doors closing.
     * 
     * @param iControllerIndex
     *            elevator controller index
     * @param iTargetIndex
     *            current Floor of the elevator
     */
    public void animateCloseDoors(int iControllerIndex, int iTargetIndex) {
        ElevatorPanel pnlElevator = getElevatorPanel(iControllerIndex);
        ControlPanel pnlControls = getControlPanel(iControllerIndex);

        if (pnlElevator.getCallButtons().size() != 0) {
            Elevator elevator = ElevatorSimulator.getInstance().getController(iControllerIndex).getElevator();
            ProgramSettings settings = ProgramSettings.getInstance();

            try {
                // wait maximum closing delay before closing the doors. Cancel
                // if door button is pressed
                final int closingDelay = settings.getMaxDoorClosingDelay();
                for (int i = 1; i <= closingDelay; i++) {
                    Thread.sleep(1);
                    if (elevator.isCloseDoorsNow()) {
                        break;
                    }
                }

                // if doors shall remain open, wait until doors shall be closed
                // through button
                while (elevator.isKeepDoorsOpen() && !elevator.isCloseDoorsNow()) {
                    Thread.sleep(1);
                }

                changeImage("../../doorsClosing.gif", iControllerIndex);
                EventsManager.ELEVATORS
                                .sendEvent(new ElevatorEvent(iControllerIndex, iTargetIndex, Kind.DOORS_CLOSING));
                Thread.sleep(settings.getDoorClosingDelay());

                changeImage("../../doorsClosed.gif", iControllerIndex);
                EventsManager.ELEVATORS.sendEvent(new ElevatorEvent(iControllerIndex, iTargetIndex, Kind.DOORS_CLOSED));
            } catch (InterruptedException e) {

            }

            pnlControls.getButtons().get(iTargetIndex).setEnabled(true);

            // re-enable buttons
            if (ProgramSettings.getInstance().isSynchronized()) {
                setFloorButtonsEnabled(iTargetIndex, true);
            } else {
                if (pnlElevator.getCallButtons().size() != 0) {
                    pnlElevator.getCallButtons().get(iTargetIndex).setEnabled(true);
                }
            }

            if (ProgramSettings.getInstance().isOuterviewEmergency()) {

                if (ProgramSettings.getInstance().isSynchronized()) {
                    activateEmergencyButtons(iTargetIndex, false);
                } else {
                    pnlElevator.getEmergencyButtons().get(iTargetIndex).setActivated(false);
                }
            }
        }
    }

    /**
     * Changes the image of the elevator.
     * 
     * @param path
     *            image path
     * @param iControllerIndex
     *            controller index where to change the image
     */
    private void changeImage(String path, int iControllerIndex) {
        Icon icon = new ImageIcon(getClass().getResource(path));
        ElevatorPanel panel = getElevatorPanel(iControllerIndex);
        panel.getElevatorImage().setIcon(icon);
    }

    /**
     * Highlights/unhighlights the buttons on the given floor.
     * 
     * @param iFloor
     *            the floor to activate/deactivate the emergency buttons on
     * @param bHighlight
     *            the highlight state
     * @param iComponent
     *            the inner component to highlight, e.g., up/down
     */
    public void highlightFloorButtons(int iFloor, boolean bHighlight, int iComponent) {
        final int elevators = ProgramSettings.getInstance().getElevators();
        for (int i = 0; i < elevators; i++) {
            if (lElevatorPanels.get(i).getCallButtons().size() != 0) {
                if (InstanceFactory.isOuterArrowButton()) {
                    getElevatorPanel(i).getCallButtons().get(iFloor).setHighlight(iComponent, bHighlight);
                } else {
                    getElevatorPanel(i).getCallButtons().get(iFloor).setHighlight(bHighlight);
                }
            }
        }
    }

    /**
     * Enables/disables elevator call buttons on the given floor.
     * 
     * @param iFloor
     *            the floor to activate/deactivate the emergency buttons on
     * @param bEnabled
     *            the enable/disable state
     */
    public void setFloorButtonsEnabled(int iFloor, boolean bEnabled) {
        final int elevators = ProgramSettings.getInstance().getElevators();
        for (int i = 0; i < elevators; i++) {
            if (lElevatorPanels.get(i).getCallButtons().size() != 0) {
                getElevatorPanel(i).getCallButtons().get(iFloor).setEnabled(bEnabled);
            }

        }
    }

    /**
     * Activates/deactivates emergency buttons on the given floor.
     * 
     * @param iFloor
     *            the floor to activate/deactivate the emergency buttons on
     * @param bActive
     *            the actication/deactivation state
     */
    public void activateEmergencyButtons(int iFloor, boolean bActive) {
        final int elevators = ProgramSettings.getInstance().getElevators();
        for (int i = 0; i < elevators; i++) {
            if (lElevatorPanels.get(i).getEmergencyButtons().size() != 0) {
                lElevatorPanels.get(i).getEmergencyButtons().get(iFloor).setActivated(bActive);
            }
        }
    }

    /**
     * Closes the window.
     */
    public void close() {
        frame.dispose();
        instance = null;
    }

}
