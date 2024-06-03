package simulator.model;

/**
 * Represents an elevator.
 */
public class Elevator {
    
    private boolean bDoorOpen = false;
    private int iDirection = 0;
    private int iCurrentFloor = 0;
    private boolean bKeepDoorsOpen = false;
    private boolean bCloseDoorsNow = false;

    /**
     * Is the door open?
     * 
     * @return {@code true} for open, else {@code false}
     */    
    public boolean isDoorOpen() {
        return bDoorOpen;
    }

    /**
     * Changes whether the door is open/shall open.
     * 
     * @param bDoorOpen {@code true} for open, else {@code false}
     */    
    public void setDoorOpen(boolean bDoorOpen) {
        this.bDoorOpen = bDoorOpen;
    }

    /**
     * Shall the door be kept open or be closed automatically.
     *  
     * @return {@code true} for keep open, else {@code false}
     */
    public boolean isKeepDoorsOpen() {
        return bKeepDoorsOpen;
    }
    
    /**
     * Changes whether the door shalle be kept open or be closed automatically.
     * 
     * @param bKeepDoorsOpen {@code true} for keep open, else {@code false}
     */
    public void setKeepDoorsOpen(boolean bKeepDoorsOpen) {
        this.bKeepDoorsOpen = bKeepDoorsOpen;
    }
    
    /**
     * Shall the doors be closed now.
     * 
     * @return {@code true} for close now, else {@code false}
     */
    public boolean isCloseDoorsNow() {
        return bCloseDoorsNow;
    }
    
    /**
     * Change whether the doors be closed now.
     * 
     * @param bCloseDoorsNow {@code true} for close now, else {@code false}
     */
    public void setCloseDoorsNow(boolean bCloseDoorsNow) {
        this.bCloseDoorsNow = bCloseDoorsNow;
    }
    
    /**
     * Returns the direction.
     * 
     * @return the iDirection (0 none, 1 up, -1 down)
     */
    public int getDirection() {
        return iDirection;
    }
    
    /**
     * Changes the direction.
     * 
     * @param iDirection the direction to set (0 none, 1 up, -1 down)
     */
    public void setDirection(int iDirection) {
        this.iDirection = iDirection;
    }
    
    /**
     * Returns the current static floor, i.e., the value may not be updated when the elevator is moving.
     * 
     * @return the current floor
     */
    public int getCurrentFloor() {
        return iCurrentFloor;
    }
    
    /**
     * Changes the current static floor, i.e., the value may not be updated when the elevator is moving.
     * 
     * @param iCurrentFloor the new current floor
     */
    public void setCurrentFloor(int iCurrentFloor) {
        this.iCurrentFloor = iCurrentFloor;
    }

}
