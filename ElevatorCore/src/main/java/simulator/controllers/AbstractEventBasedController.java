package simulator.controllers;

import engine.ButtonEvent;
import engine.ElevatorEvent;
import engine.ElevatorEvent.ElevatorEventObserver;
import engine.ButtonEvent.ButtonEventObserver;
import engine.EventsManager;
import engine.FloorSensorEvent;
import engine.FloorSensorEvent.FloorSensorEventObserver;
import simulator.model.Elevator;
import simulator.model.Request;

/**
 * Basic event-based elevator controller including basic event handling (passed on to controller methods),
 * and simple endless elevator move methods (for translating event handling to elevator requests). Disables
 * automatic door handling by default and provides methods to explicitly open/close the elevator doors.
 * 
 * @author SSE
 */
public abstract class AbstractEventBasedController extends AbstractController {

    private static boolean eventManagerCleaned = false;
    private boolean stop = false;
    private DoorAction doorAction = DoorAction.NONE;

    /**
     * Creates an instance.
     * 
     * @param index the elevator index
     * @param clearButtonObservers whether existing button observers shall be cleared upon first
     *    execution of this constructor
     */
    protected AbstractEventBasedController(int index, boolean clearButtonObservers) {
        super(index);
        if (!eventManagerCleaned && clearButtonObservers) {
            EventsManager.BUTTONS.removeAllObservers();
            eventManagerCleaned = true;
        }
        EventsManager.BUTTONS.addObserver(new ButtonEventObserver() {

            @Override
            public void notifyReceived(ButtonEvent event) {
                if (event.getElevator() == index) {
                    buttonEventReceived(event);
                    startSimulation(); // must be called once
                }
            }

        });
        EventsManager.FLOOR_SENSORS.addObserver(new FloorSensorEventObserver() {

            @Override
            public void notifyReceived(FloorSensorEvent event) {
                if (event.getElevator() == index) {
                    floorSensorEventReceived(event);
                }
            }

        });
        EventsManager.ELEVATORS.addObserver(new ElevatorEventObserver() {
            
            @Override
            public void notifyReceived(ElevatorEvent event) {
                if (event.getElevator() == index) {
                    if (ElevatorEvent.Kind.DOORS_OPEN == event.getKind()) {
                        if (DoorAction.OPEN == doorAction) {
                            doorAction = DoorAction.NONE;
                        }
                    } else if (ElevatorEvent.Kind.DOORS_CLOSED == event.getKind()) {
                        if (DoorAction.CLOSE == doorAction) {
                            doorAction = DoorAction.NONE;
                        }            
                    }
                    elevatorEventReceived(event);
                }
            }
        });
    }

    /**
     * Called when a button event for {@link #getElevatorIndex() this elevator} is received.
     * 
     * @param event the event
     */
    protected abstract void buttonEventReceived(ButtonEvent event);

    /**
     * Called when a floor sensor event for {@link #getElevatorIndex() this elevator} is received.
     * 
     * @param event the event
     */
    protected abstract void floorSensorEventReceived(FloorSensorEvent event);

    /**
     * Called when an elevator event for {@link #getElevatorIndex() this elevator} is received.
     * 
     * @param event the event
     */
    protected abstract void elevatorEventReceived(ElevatorEvent event);

    /**
     * Starts moving the elevator into the given direction. Does not stop even when the well is exceeded.
     * To stop the elevator in a given/the next floor, call {@link #stopElevator(int)} or 
     * {@link #stopElevatorNextFloor(int)}.
     * 
     * @param direction
     */
    protected void moveElevator(int direction) {
        Elevator elevator = getElevator();
        if (elevator.getDirection() == 0) {
            elevator.setDirection(direction);
            setCurrentRequest(new Request(direction > 0 ? Integer.MAX_VALUE : Integer.MIN_VALUE,  direction));
        }
    }

    /**
     * Stops the elevator in the next floor in moving direction.
     * 
     * @param currentFloor the current floor
     */
    protected void stopElevatorNextFloor(int currentFloor) {
        stopElevator(currentFloor  + getElevatorCurrentDirection());
    }

    /**
     * Stops the elevator in the given floor.
     * 
     * @param floor the target floor where to stop, ignored if {@code 0}
     */
    protected void stopElevator(int floor) {
        Elevator elevator = getElevator();
        if (elevator.getDirection() != 0 && hasCurrentRequest()) {
            setCurrentRequest(new Request(getCurrentRequest(), floor));
            requestStop(true);
        }        
    }
    
    protected void closeDoors() {
        doorAction = DoorAction.CLOSE;
    }
    
    protected void openDoors() {
        doorAction = DoorAction.OPEN;
    }
    
    /**
     * Returns whether stopping through {@link #stopElevator(int)} or {@link #stopElevatorNextFloor(int)}
     * is requested but if the elevator is moving, it goes on moving.
     * 
     * @return {@code true} if stop is requested, {@code false} else
     */
    protected boolean isStopRequested() {
        return stop;
    }

    /**
     * Changes whether stopping the elevator is requested.
     * 
     * @param stop {@code true} for requested, {@code false} for not requested/clearing the request as stopped
     */
    protected void requestStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public boolean simulate() {
        return false; // target found?
    }

    @Override
    public void addRequest(Request target) {
    }

    @Override
    public boolean updateTarget() {
        return false;
    }

    @Override
    public void processPriorityCall() {
    }

    @Override
    public void processDeletions() {
    }

    @Override
    public void doAfterAnimate() {
    }

    @Override
    public final DoorAction getDoorAction() {
        return doorAction;
    }

}
