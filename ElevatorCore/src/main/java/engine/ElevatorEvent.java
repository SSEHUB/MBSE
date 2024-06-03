package engine;

/**
 * An event on the elevator itself.
 * 
 * @author SSE
 */
public class ElevatorEvent extends AbstractElevatorEvent {

    public enum Kind {
        DOORS_OPEN,
        DOORS_OPENING,
        DOORS_CLOSING,
        DOORS_CLOSED
    }
    
    private Kind kind;
    
    /**
     * Creates an elevator event.
     * 
     * @param elevator the elevator index
     * @param floor the floor index
     * @param kind the event kind
     */
    public ElevatorEvent(int elevator, int floor, Kind kind) {
        super(elevator, floor);
        this.kind = kind;
    }
    
    /**
     * Returns the event kind.
     * 
     * @return the kind
     */
    public Kind getKind() {
        return kind;
    }

    @Override
    public String toString() {
        return String.format("ElevatorEvent(%s, kind=%s)", 
            toStringPartial(), kind);
    }
    
    /**
     * An elevator event observer.
     * 
     * @author SSE
     */
    public interface ElevatorEventObserver extends EventObserver<ElevatorEvent> {
        
    }
    
}
