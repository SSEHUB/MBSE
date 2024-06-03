package engine;

/**
 * Represents a floor sensor event indicating that an elevator.
 */
public class FloorSensorEvent extends AbstractElevatorEvent {

    private Position position;
    
    /**
     * Denotes sensor positions.
     * 
     * @author SSE
     */
    public enum Position {
        
        /**
         * Top sensor of a floor detected elevator.
         */
        TOP,
        
        /**
         * Bottom sensor of a floor detected elevator.
         */
        BOTTOM,
        
        /**
         * Elevator is moving on, last event cleared.
         */
        CLEARED;
    }

    /**
     * Creates a floor sensor event.
     * 
     * @param elevator the elevator index
     * @param floor the floor index
     * @param position the sensor position
     */
    public FloorSensorEvent(int elevator, int floor, Position position) {
        super(elevator, floor);
        this.position = position;
    }
    
    /**
     * Creates an event with same settings as <b>this</b> event but position {@link Position#CLEARED}.
     * 
     * @return the created event
     */
    public FloorSensorEvent createClearedEvent() {
        return new FloorSensorEvent(getElevator(), getFloor(), Position.CLEARED);
    }
    
    /**
     * Returns the sensor position within floor.
     * 
     * @return the sensor position
     */
    public Position getPosition() {
        return position;
    }
    
    @Override
    public String toString() {
        return String.format("FloorSensorEvent(%s, position=%s)", toStringPartial(), position); 
    }
    
    /**
     * A floor sensor event observer.
     * 
     * @author SSE
     */
    public interface FloorSensorEventObserver extends EventObserver<FloorSensorEvent> {
        
    }

}
