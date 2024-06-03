package engine;

/**
 * An event from a button outside the elevator.
 * 
 * @author SSE
 */
public class ButtonEvent extends AbstractElevatorEvent {

    private Kind kind;
    private boolean highlighted;
    private boolean inside;
    private int value;
    
    /**
     * Button kind.
     * 
     * @author SSE
     */
    public enum Kind {
        
        /**
         * Normal button (default with direction = 0, up-button with direction 1, down-button with direction -1)
         */
        BUTTON,
        
        /**
         * Indicates a cancel request (right mouse click, usually value = 0).
         */
        CANCEL, 
        
        /**
         * Indicates an authorization (value = authorization level, elevator and floor may be -1)
         */
        AUTHORIZE,
        
        /**
         * Emergency condition raised.
         */
        EMERGENCY,
        
        /**
         * Emergency condition resolved.
         */
        EMERGENCY_RESOLVED;
    }
    
    /**
     * Creates a floor sensor event.
     * 
     * @param elevator the elevator index
     * @param floor the floor index
     * @param kind the button kind
     * @param value (indicates for {@link Kind#BUTTON} the direction (1=up, 0=none/unspecified), -1=down), 
     *     for {@link Kind#AUTHORIZE} the authorization level)
     * @param highlighted is the source button highlighted
     * @param inside is the button inside the elevator or not
     */
    public ButtonEvent(int elevator, int floor, Kind kind, int value, boolean highlighted, boolean inside) {
        super(elevator, floor);
        this.kind = kind;
        this.value = value;
        this.highlighted = highlighted;
        this.inside = inside;
    }
    
    /**
     * Returns the sensor position within floor.
     * 
     * @return the sensor position
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * Returns the button value.
     * 
     * @return for {@link Kind#BUTTON} the direction (1=up, 0=none/unspecified), -1=down), 
     *     for {@link Kind#AUTHORIZE} the authorization level, else 0
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns whether the button is highlighted.
     * 
     * @return {@code true} for highlighted, {@code false} else
     */
    public boolean isHightlighed() {
        return highlighted;
    }

    /**
     * Returns whether the button is inside the elevator or outside.
     * 
     * @return {@code true} for inside, {@code false} else
     */
    public boolean isInside() {
        return inside;
    }

    @Override
    public String toString() {
        return String.format("ButtonEvent(%s, kind=%s, highlighted=%b, inside=%b)", 
            toStringPartial(), kind, highlighted, inside);
    }
    
    /**
     * A button event observer.
     * 
     * @author SSE
     */
    public interface ButtonEventObserver extends EventObserver<ButtonEvent> {
        
    }
    
}
