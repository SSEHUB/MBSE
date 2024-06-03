package demo;

import engine.ButtonEvent;
import engine.ElevatorEvent;
import engine.FloorSensorEvent;
import gui.buttons.ArrowButton;
import gui.buttons.DefaultButton;
import simulator.controllers.AbstractEventBasedController;
import simulator.model.Request;

/**
 * Simple event-based elevator controller to demonstrate interplay of {@link ButtonEvent} and {@link FloorSensorEvent}.
 * Elevator goes into direction of button request and would exceed elevator well, unless a button cancel signal
 * (click on same floor again or right mouse click on respective call button) is issued. Please note that canceling 
 * shall be done as as soon as the elevator started to move. No requests are queued as this is just 
 * a simple demonstration. Direction of button ({@link DefaultButton} vs. {@link ArrowButton}) is ignored.
 * 
 * @author SSE
 */
public class SimpleEventController extends AbstractEventBasedController {

    private int currentFloor = 0;
    private Request lastFulfilledRequest = null;
    
    public SimpleEventController(int index) {
        super(index, true); // clear default button observers
    }

    @Override
    public String getDisplayName() {
        return "Simple Event Controller";
    }

    @Override
    protected void buttonEventReceived(ButtonEvent event) {
        if (ButtonEvent.Kind.BUTTON == event.getKind()) {
            if (!event.isHightlighed()) {
                // on a button call, start moving the elevator in the given direction if it is not already moving
                if (getElevatorCurrentDirection() == 0) {
                    moveElevator(signum(event.getFloor() - getElevatorCurrentFloor()));
                }
            } else {
                // on a cancel request, try stopping the elevator in the floor where canceling was requested
                // please note that canceling must be reset manually by a right mouse click on the same button
                stopElevator(event.getFloor());
            }
        } else if (ButtonEvent.Kind.CANCEL == event.getKind()){
            // on a cancel request, try stopping the elevator in the floor where canceling was requested
            // please note that canceling must be reset manually by a right mouse click on the same button
            stopElevator(event.getFloor());
        }
    }
    
    @Override
    protected void floorSensorEventReceived(FloorSensorEvent event) {
        currentFloor = event.getFloor();
        if (isStopRequested() && currentFloor == getCurrentRequestedFloor()) {
            // if stop is requested and we are on the requested floor, prepare stopping but do not completely
            // stop as animation may stop between the floors -> simulate
            lastFulfilledRequest = getCurrentRequest();
            setElevatorCurrentFloor(lastFulfilledRequest.getFloor());
            setCurrentRequest(null); // stop it
            requestStop(false); // rest
        }
    }

    @Override
    protected void elevatorEventReceived(ElevatorEvent event) {
        if (ElevatorEvent.Kind.DOORS_OPEN == event.getKind()) {
            // simplified door handling: when its open, close it and implicitly wait for specified delays
            closeDoors();
        }
    }

    @Override
    public boolean simulate() {
        // if we have a fulfilled request via the floor sensor event on the requested floor, check whether 
        // simulation/animation is done, clear the elevator direction, open the doors and indicate target found 
        // as return
        boolean targetFound = lastFulfilledRequest != null;
        if (targetFound) {
            setElevatorCurrentDirection(0);
            openDoors();
        }
        lastFulfilledRequest = null;
        return targetFound;
    }

}
