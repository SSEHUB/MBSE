package engine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import engine.ButtonEvent.ButtonEventObserver;
import engine.ElevatorEvent.ElevatorEventObserver;
import engine.Event.EventObserver;
import engine.FloorSensorEvent.FloorSensorEventObserver;

/**
 * A simple event distribution mechanism. The event manager is pre-loaded with default simulator observers, in 
 * particular for button handling. If this is not desired, please call {@link #removeAllObservers()} and register
 * your own observers.
 * 
 * @author SSE
 */
public class EventsManager {

    public static final EventManager<FloorSensorEvent, FloorSensorEventObserver> FLOOR_SENSORS = new EventManager<>();
    public static final EventManager<ButtonEvent, ButtonEventObserver> BUTTONS = new EventManager<>();
    public static final EventManager<ElevatorEvent, ElevatorEventObserver> ELEVATORS = new EventManager<>();

    /**
     * Implements a generic event manager.
     * @param <E> the event type
     * @param <O> the related event observer type
     * @author SSE
     */
    public static class EventManager<E extends Event, O extends EventObserver<E>> {

        private List<O> observers = new ArrayList<>();
        private boolean debug;

        /**
         * Sends a floor sensor event.
         * 
         * @param event the event
         */
        public void sendEvent(E event) {
            if (debug) {
                System.out.println(event);
            }
            for (O obs : observers) {
                obs.notifyReceived(event);
            }
        }
        
        /**
         * Returns whether an observer has already been registered.
         * 
         * @param observer the observer
         * @return {@code true} for registered, {@code false} for not yet registered
         */
        public boolean containsObserver(O observer) {
            return null != observer && observers.contains(observer);
        }

        /**
         * Adds an observer.
         * 
         * @param observer the observer
         */
        public void addObserverIfUnknown(O observer) {
            if (!containsObserver(observer)) {
                addObserver(observer);
            }
        }

        /**
         * Adds an observer.
         * 
         * @param observer the observer (ignored if <b>null</b>)
         */
        public void addObserver(O observer) {
            if (null != observer) {
                observers.add(observer);
            }
        }

        /**
         * Removes an observer.
         * 
         * @param observer the observer
         */
        public void removeObserver(O observer) {
            if (null != observer) {
                observers.remove(observer);
            }
        }
        
        /**
         * Removes all observers satisfying {@code predicate}.
         * 
         * @param predicate the predicate to select observers to be removed
         */
        public void removeObserverIf(Predicate<O> predicate) {
            observers.removeIf(predicate);
        }
        
        /**
         * Removes all observers.
         */
        public void removeAllObservers() {
            observers.clear();
        }
        
        /**
         * Enables/disables logging by debugging.
         * 
         * @param debug enable/disable logging
         */
        public void setDebug(boolean debug) {
            this.debug = debug;
        }
        
    }
    
    /**
     * Removes all observers.
     */
    public static void removeAllObservers() {
        FLOOR_SENSORS.removeAllObservers();
        BUTTONS.removeAllObservers();
        ELEVATORS.removeAllObservers();
    }
    
}
