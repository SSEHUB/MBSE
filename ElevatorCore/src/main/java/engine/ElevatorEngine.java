package engine;

import gui.views.inside.ControlPanel;
import gui.views.inside.DisplayPanel;
import gui.windows.MainWindow;

import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import properties.ProgramSettings;
import simulator.ElevatorSimulator;
import simulator.controllers.AbstractController;
import simulator.controllers.AbstractController.DoorAction;
import simulator.model.Elevator;

/**
 * The elevator engine as runnable instance.
 */
public class ElevatorEngine implements Runnable {

    private MainWindow mw;
    private AbstractController myController;
    private Elevator myElevator;
    private JLabel myElevatorImage;
    private ControlPanel myControls;
    private DisplayPanel myDisplays;
    private SpeedController speedController;
    private int baseY;
    private FloorSensorEvent lastHitSensorEvent;

    /**
     * Creates the engine for a given elevator controller
     * 
     * @param controller the elevator controller
     * @param baseY the base vertical position of the elevator image, take the actual one of the image if -1, else the 
     *     given position
     */
    public ElevatorEngine(AbstractController controller, int baseY) {
        mw = MainWindow.getInstance();

        if (ProgramSettings.getInstance().getElevators() > 1) {
            myController = ElevatorSimulator.getInstance().getController(controller.getElevatorIndex());
            myElevatorImage = mw.getElevatorPanel(controller.getElevatorIndex()).getElevatorImage();
            myControls = mw.getControlPanel(controller.getElevatorIndex());
            myDisplays = mw.getDisplayPanel(controller.getElevatorIndex());
        } else { // TODO
            myController = ElevatorSimulator.getInstance().getController(0);
            myElevatorImage = mw.getElevatorPanel(0).getElevatorImage();
            myControls = mw.getControlPanel(0);
            myDisplays = mw.getDisplayPanel(0);
        }
        this.baseY = baseY < 0 ? (int) myElevatorImage.getBounds().getMaxY() : baseY;
System.out.println("BASEY " + this.baseY);        

        myElevator = controller.getElevator();

        if (ProgramSettings.getInstance().isAccelerated()) {
            speedController = new SpeedController();
        }
    }
    
    /**
     * Returns the base vertical position of the elevator image.
     * 
     * @return the position
     */
    public int getBaseY() {
        return baseY;
    }

    @Override
    public void run() {
        boolean doorsOpened = false;
        // synchronized (mw.getlElevatorPanels().get(iControllerIndex)) {
        if (ProgramSettings.getInstance().isInnerviewDoorButton()) {
            myControls.getCloseButton().setEnabled(false);
            myControls.getOpenButton().setEnabled(false);
        }

        while (myController.getCurrentRequest() != null) {

            if (myController.isUpdateNecessary()) {
                myController.updateTarget();
            }

            if (!myController.deletionQueueContains(myController.getCurrentRequest().getFloor())) {

                int floorHeight = ProgramSettings.getInstance().getFloorsHeight();
                // animation of elevator
                for (int i = 0; i < floorHeight; i++) {
                    int dir = myElevator.getDirection();
                    int nextTopY = (int) myElevatorImage.getLocation().getY() - dir;
                    int nextBottomY = nextTopY + myElevatorImage.getHeight();
                    int elevatorY;
                    FloorSensorEvent.Position sensorPosition = null;
                    if (dir > 0) {
                        elevatorY = nextTopY;
                        sensorPosition = FloorSensorEvent.Position.TOP;
                    } else {
                        elevatorY = nextBottomY;
                        sensorPosition = FloorSensorEvent.Position.BOTTOM;
                    }
                    if ((elevatorY - baseY) % floorHeight == 0) {
                        int floor = Math.abs((elevatorY - baseY) / floorHeight);
System.out.println(nextTopY+" "+nextBottomY+" "+elevatorY+" "+baseY+" "+floor);                        
                        FloorSensorEvent evt = new FloorSensorEvent(myController.getElevatorIndex(), floor, sensorPosition);
                        EventsManager.FLOOR_SENSORS.sendEvent(evt);
                        lastHitSensorEvent = evt;
                    } else if (null != lastHitSensorEvent) {
                        EventsManager.FLOOR_SENSORS.sendEvent(lastHitSensorEvent.createClearedEvent());
                        lastHitSensorEvent = null;
                    }
                    myElevatorImage.setLocation((int) myElevatorImage.getLocation().getX(), nextTopY);

                    if (ProgramSettings.getInstance().isAutoscroll()) {
                        scrollWithElevator(myElevator);
                    }

                    try {
                        if (ProgramSettings.getInstance().isAccelerated()) {
                            speedController.adaptSpeed(i, myElevator, myControls.getButtons());
                        } else {
                            Thread.sleep(ProgramSettings.getInstance().getElevatorsSpeed());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            myElevator.setCloseDoorsNow(false);
            myElevator.setKeepDoorsOpen(false);

            myDisplays.updateValues(myElevator, myController.getCurrentRequest());

            boolean targetFound = myController.simulate();

            if (ProgramSettings.getInstance().isDisplayDirection()) {
                myDisplays.changeImageDirection(myElevator.getDirection());
            }

            if (targetFound) {
                DoorAction doorAction = myController.getDoorAction();
                if (DoorAction.AUTO == doorAction || DoorAction.OPEN == doorAction) {
                    if (ProgramSettings.getInstance().isInnerviewDoorButton()) {
                        // open/close doors only if elevator is at position
                        myControls.getCloseButton().setEnabled(true);
                        myControls.getOpenButton().setEnabled(true);
                    }
                    mw.animateOpenDoors(myController.getElevatorIndex(), myElevator.getCurrentFloor());
                    doorsOpened = true;
                }
                if (DoorAction.AUTO == doorAction) { // explicit close is below
                    animateCloseDoors();
                }
            }
        }
        if (doorsOpened && myController.getDoorAction() != DoorAction.AUTO) {
            // explicit control, wait for closing action
            while (myController.getDoorAction() != DoorAction.CLOSE) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            }
            animateCloseDoors();
        }
        if (ProgramSettings.getInstance().isInnerviewDoorButton()) {
            myControls.getCloseButton().setEnabled(true);
            myControls.getOpenButton().setEnabled(true);
        }
    }
    
    /**
     * Actions to close the elevator doors.
     */
    private void animateCloseDoors() {
        mw.animateCloseDoors(myController.getElevatorIndex(), myElevator.getCurrentFloor());
        myController.doAfterAnimate();

        if (ProgramSettings.getInstance().isInnerviewDoorButton()) {
            myControls.getCloseButton().setEnabled(false);
            myControls.getOpenButton().setEnabled(false);
        }        
    }

    /**
     * Scrolls scrollbar with elevator. When the elevator leaves the viewpoint, the scrollbar shall follow.
     * 
     * @param elevator the elevator holding the location
     */
    private void scrollWithElevator(Elevator elevator) {
        Point pImage = new Point(myElevatorImage.getLocation());
        JScrollPane jscroll = mw.getScrollPane(myController.getElevatorIndex());

        if (elevator.getDirection() == 1) {
            if (pImage.getY() < (jscroll.getViewport().getViewPosition().getY())) {
                jscroll.getViewport()
                    .setViewPosition(new Point((int) 1, (int) jscroll.getViewport().getViewPosition().getY()
                        - (elevator.getDirection() * jscroll.getHeight())));
            }
        } else if (elevator.getDirection() == -1) {
            if (pImage.getY() > (jscroll.getViewport().getViewPosition().getY() + 500)) {
                jscroll.getViewport()
                    .setViewPosition(new Point((int) 1, (int) jscroll.getViewport().getViewPosition().getY()
                        - (elevator.getDirection() * jscroll.getHeight())));
            }
        }
    }
    
}