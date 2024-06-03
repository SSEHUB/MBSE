package engine;

/**
 * Common elevator simulation event.
 * 
 * @author SSE
 */
public class Event {

    /**
     * A floor sensor event observer.
     * 
     * @author SSE
     */
    public interface EventObserver<T extends Event> {
        
        /**
         * Notifies about a received event.
         * 
         * @param event the event
         */
        public void notifyReceived(T event);
        
        /**
         * The class registering the observer. For selecting/deleting observers.
         * 
         * @return the class, may be <b>null</b> (the enclosing class or <b>null</b> if top-level)
         */
        public default Class<?> declaringClass() {
            return getClass().getEnclosingClass(); // the default observers are static inner classes
        }
        
        /**
         * Information on the handled sub-event, an enum constant. For selecting/deleting observers.
         * 
         * @return the class, may be <b>null</b> (default)
         */
        public default Object getHandlingInfo() {
            return null;
        }
        
    }
}
