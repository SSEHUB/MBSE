package engine;

/**
 * An abstract elevator event.
 * 
 * @author SSE
 */
public abstract class AbstractElevatorEvent extends Event {
    
    private int elevator;
    private int floor;
    
    /**
     * Creates a floor sensor event.
     * 
     * @param elevator the elevator index
     * @param floor the floor index
     */
    public AbstractElevatorEvent(int elevator, int floor) {
        this.elevator = elevator;
        this.floor = floor;
    }
    
    /**
     * Returns the elevator index.
     * 
     * @return the elevator index
     */
    public int getElevator() {
        return elevator;
    }
    
    /**
     * Returns the floor index.
     * 
     * @return the floor index
     */
    public int getFloor() {
        return floor;
    }    

    /**
     * Pre-formatted data for {@code toString}.
     * 
     * @return the text
     */
    protected String toStringPartial() {
        return String.format("elevator=%d, floor=%d", getElevator(), getFloor()); 
    }
    
}
