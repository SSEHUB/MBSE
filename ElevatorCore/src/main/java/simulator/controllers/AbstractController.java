package simulator.controllers;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;

import simulator.model.Elevator;
import simulator.model.Request;
import engine.ElevatorEngine;
import properties.ProgramSettings;

/**
 * An abstract elevator controller with priority queue.
 * 
 * @author SSE
 */
public abstract class AbstractController {
    
    private int iElevator;
    private ConcurrentLinkedQueue<Request> clqPriorityQueue = new ConcurrentLinkedQueue<Request>();
    private ConcurrentLinkedQueue<Integer> clqDeletionQueue = new ConcurrentLinkedQueue<Integer>();    
    private boolean hasPriorityCall = false;
    private Thread simuThread = null;
    private ArrayList<Request> lRequestList = new ArrayList<Request>();
    private Request currentRequest = null;
    private ArrayList<Boolean> lIgnoreList = new ArrayList<Boolean>();
    private boolean updateNecessary = false;
    private Elevator eElevator = new Elevator();
    private int baseY = -1; // initialize by engine on first run
    
    /**
     * Creates a controller instance.
     * 
     * @param iElevator elevator index
     */
    protected AbstractController(int iElevator) {
        this.iElevator = iElevator;
        final int floors = ProgramSettings.getInstance().getFloors();
        for (int i = 0; i <= floors; i++) {
            lRequestList.add(null);            
            lIgnoreList.add(false);
        }
    }
    
    /**
     * Returns the display name of the controller.
     * 
     * @return the display name
     */
    public abstract String getDisplayName();
    
    /**
     * Returns the controlled elevator.
     * 
     * @return the elevator
     */
    public Elevator getElevator() {
        return eElevator;
    }
    
    /**
     * Returns the direction of the elevator.
     * 
     * @return 0 for one, -1 for down, 1 for up
     */
    public int getElevatorCurrentDirection() {
        return getElevator().getDirection();
    }
    
    /**
     * Changes the direction of the elevator.
     * 
     * @param iDirection 0 for one, -1 for down, 1 for up
     */
    public void setElevatorCurrentDirection(int iDirection) {
        getElevator().setDirection(iDirection);
    }

    /**
     * Returns the current floor of the elevator.
     * 
     * @return the current floor index
     */
    public int getElevatorCurrentFloor() {
        return getElevator().getCurrentFloor();
    }

    /**
     * Changes the current floor of the elevator.
     * 
     * @param iCurrentFloor the floor index
     */
    public void setElevatorCurrentFloor(int iCurrentFloor) {
        getElevator().setCurrentFloor(iCurrentFloor);
    }

    /**
     * Returns the elevator/controller index.
     * 
     * @return the index
     */
    public final int getElevatorIndex() {
        return iElevator;
    }
    
    /**
     * Adds a request to the priority queue.
     * 
     * @param request the request
     */
    public void addToPriorityQueue(Request request) {
        clqPriorityQueue.add(request);
    }
    
    /**
     * Returns the first element from the priority queue.
     * 
     * @return the first element, <b>null</b> if there is no element
     * @see #isPriorityQueueEmpty()
     */
    public Request pollPriorityQueue() {
        return clqPriorityQueue.poll();
    }
    
    /**
     * Returns whether there are no targets in the priority queue.
     * 
     * @return {@code true} for empty, {@code false} else
     */
    public boolean isPriorityQueueEmpty() {
        return clqPriorityQueue.isEmpty();
    }
    
    /**
     * Returns whether the deletion queue contains the given floor.
     * 
     * @param iFloor the floor index to look for
     * @return {@code true} if contained, {@code false} else
     */
    public boolean deletionQueueContains(int iFloor) {
        return clqDeletionQueue.contains(iFloor);
    }

    /**
     * Processes the deletion queue element by element.
     * 
     * @param consumer the element consumer 
     */
    public void processDeletionQueue(Consumer<Integer> consumer) {
        while (!clqDeletionQueue.isEmpty()) {
            Integer iDelFloor = clqDeletionQueue.poll();
            consumer.accept(iDelFloor);
        }
    }
    
    /**
     * Returns whether there is currently a priority call.
     * 
     * @return {@code true} if there is a priority call, {@code false} else
     */
    public final boolean hasPriorityCall() {
        return hasPriorityCall;
    }
    
    /**
     * Changes whether there is currently a priority call.
     * 
     * @param hasPriorityCall {@code true} if there is a priority call, {@code false} else
     */
    protected void setHasPriorityCall(boolean hasPriorityCall) {
        this.hasPriorityCall = hasPriorityCall;
    }

    /**
     * Returns whether there is a current elevator moving request.
     * 
     * @return {code true} if there is a request, {@code false} else
     */
    public final boolean hasCurrentRequest() {
        return currentRequest != null;
    }
    
    /**
     * Returns the current elevator moving request.
     * 
     * @return the current request, may be <b>null</b> for none
     */
    public final Request getCurrentRequest() {
        return currentRequest;
    }

    /**
     * Returns the floor of the current request. Fails if there is no current request.
     * 
     * @return the requested floor
     * @see #getCurrentRequest()
     * @see #hasCurrentRequest()
     */
    public final int getCurrentRequestedFloor() {
        return currentRequest.getFloor();
    }

    /**
     * Changes the current request.
     * 
     * @param request the request to set, may be <b>null</b> for none
     */
    protected final void setCurrentRequest(Request request) {
        this.currentRequest = request;
    }
    
    /**
     * Returns the simulation thread.
     * 
     * @return the simulation thread
     */
    protected final Thread getSimuThread() {
        return simuThread;
    }

    /**
     * Locks the request list for concurrent modifications and lets {@code consumer} operate on the request list.
     * 
     * @param consumer the consumer
     */
    public final void lockRequestList(Consumer<ArrayList<Request>> consumer) {
        synchronized(lRequestList) {
            consumer.accept(lRequestList);
        }
    }

    /**
     * Locks the request list for concurrent modifications and lets {@code function} operate on the request list, 
     * returning the result of {@code function}.
     * 
     * @param function the function to execute
     * @return the value of {@code function}
     */
    public final <R> R lockRequestList(Function<ArrayList<Request>, R> function) {
        synchronized(lRequestList) {
            return function.apply(lRequestList);
        }
    }
    
    /**
     * Returns the request for the given floor.
     * 
     * @param iFloor the floor
     * @return the request, may be <b>null</b> for none
     */
    public Request getRequest(int iFloor) {
        return lRequestList.get(iFloor);
    }
    
    /**
     * Returns whether the specified floor is ignored.
     * 
     * @param iFloor the floor to check
     * @return {@code true} for ignored, {@code false} for not ignored
     */
    public boolean isFloorIgnored(int iFloor) {
        return lIgnoreList.get(iFloor);
    }
    
    /**
     * Ignores the given floor or not.
     * 
     * @param iFloor the floor to ignore
     * @param ignore {@code true} for ignore, {@code false} for not ignore
     */
    public final void ignoreFloor(int iFloor, boolean ignore) {
        this.lIgnoreList.set(iFloor, ignore);
    }
    
    /**
     * Starts the simulation (thread).
     */
    public final void startSimulation() {
        if (simuThread == null || !simuThread.isAlive()) {
            ElevatorEngine engine = new ElevatorEngine(this, baseY);
            simuThread = new Thread(engine);
            simuThread.start();
            baseY = engine.getBaseY();
        }
    }

    /**
     * Adds a priority call.
     * 
     * @param floor the target floor
     */
    public final void addPriorityCall(int floor) {
        clqPriorityQueue.add(new Request(floor, 0));
        if (currentRequest == null) {
            processPriorityCall();
        }
        startSimulation();
    }
    
    /**
     * Removes the given floor as target.
     * 
     * @param iFloor the floor to be removed
     */
    public final void deleteTarget(int iFloor) {
        clqDeletionQueue.add(iFloor);
        startSimulation();
    }
    
    /**
     * Performs simulation operations, called after moving/animating the elevator over one floor.
     * 
     * @return {@code true} if a target was found and the doors shall open afterwards
     */
    public abstract boolean simulate();
    
    /**
     * Adds a (new) target/elevator request.
     * 
     * @param target the target
     */
    public abstract void addRequest(Request target);

    /**
     * Whether updating before moving the elevator is necessary.
     * 
     * @return {@code true} if an update is necessary, {@code false}
     * @see #updateTarget()
     */
    public final boolean isUpdateNecessary() {
        return updateNecessary;
    }
    
    /**
     * Changes whether an update of the target is needed before moving the elevator.
     * 
     * @param updateNecessary the new state
     */
    protected void setUpdateNecessary(boolean updateNecessary) {
        this.updateNecessary = updateNecessary;
    }

    /**
     * Called by engine if update is necessary to update the actual target. Shall reset the update necessary flag.
     * 
     * @return {@code true} for updated, {@code false}
     * @see #isUpdateNecessary()
     * @see #setUpdateNecessary(boolean)
     */
    public abstract boolean updateTarget();
    
    /**
     * Process an elevator priority call. Shall reset {@link #hasPriorityCall()}.
     * 
     * @see #setHasPriorityCall(boolean)
     */
    public abstract void processPriorityCall();
    
    /**
     * Process elevator target deletions.
     */
    public abstract void processDeletions();
    
    /**
     * Called after elevator animation.
     */
    public abstract void doAfterAnimate();
    
    public enum DoorAction {
        OPEN,
        CLOSE,
        AUTO,
        NONE
    }
    
    public DoorAction getDoorAction() {
        return DoorAction.AUTO;
    }
    
    /**
     * Returns the signum of {@code value}, e.g., to turn a value into a direction.
     *  
     * @param value the value
     * @return the signum
     */
    public static int signum(int value) {
        int result;
        if (value > 0) {
            result = 1;
        } else if (value < 0) {
            result = -1;
        } else {
            result = 0;
        }
        return result;
    }
    
}
