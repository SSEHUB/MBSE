package simulator.model;

/**
 * Elevator movement request.
 */
public class Request {

    private int iFloor;
    private int iPriority;
    private int iDirection;

    /**
     * Creates a request with default values. No floor, lowest priority, no
     * direction.
     */
    public Request() {
        iFloor = -1;
        iPriority = 1;
        iDirection = 0;
    }

    /**
     * Creates a request with given values and basic priority.
     * 
     * @param floor the target floor
     * @param direction the requested direction (may be 0 for none)
     */
    public Request(int floor, int direction) {
        iFloor = floor;
        iDirection = direction;
        iPriority = 1;
    }
    
    /**
     * Creates a new request with changed target floor.
     * 
     * @param request the request to take the information from
     * @param floor the new target floor (shall be in same direction of current movement)
     */
    public Request(Request request, int floor) {
        this.iFloor = floor;
        this.iDirection = request.iDirection;
        this.iPriority = request.iPriority;
    }

    /**
     * Returns the target floor.
     * 
     * @return the the target floor, may be negative for none
     */
    public int getFloor() {
        return iFloor;
    }

    /**
     * Returns the priority
     * 
     * @return the priority, 1 for normal
     */
    public int getPriority() {
        return iPriority;
    }

    /**
     * Adjusts the priority by the given distance.
     * 
     * @param distance the distance to adjust the priority
     */
    public void adjustPriority(int distance) {
        this.iPriority += distance;
    }

    /**
     * Returns the direction.
     * 
     * @return the direction (0 none, 1 up, -1 down)
     */
    public int getDirection() {
        return iDirection;
    }

}
