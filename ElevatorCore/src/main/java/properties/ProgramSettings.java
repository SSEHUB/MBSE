package properties;

import java.util.ArrayList;

import simulator.controllers.AbstractMultiController;

/**
 * Provides relevant configuration settings. Before starting the Elevator simulator, set your implementation
 * instance of this factory through {@link #setInstance(ProgramSettings)}. Values returned here shall be constant.
 * 
 * @author SSE
 */
public abstract class ProgramSettings {
    
    private static ProgramSettings INSTANCE;
    
    /**
     * Sets once the singleton instance to be used
     * 
     * @param instance the instance to be used
     */
    public static void setInstance(ProgramSettings instance) {
        if (null == INSTANCE && null != instance) {
            INSTANCE = instance;
        }
    }
    
    /**
     * Returns the singleton instance of this class.
     * 
     * @return the singleton settings instance
     */
    public static ProgramSettings getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the total number of floors.
     * 
     * @return the floors #
     */
    public abstract int getFloors();
    
    /**
     * Returns the number of elevators.
     * 
     * @return the elevators #
     */
    public abstract int getElevators();
    
    /**
     * Helper function to create a typed list with {@link #getElevators()} initial positions.
     * 
     * @param <T> the type
     * @param cls the type class
     * @return the created list
     */
    public static <T> ArrayList<T> createElevatorsList(Class<T> cls) {
        return new ArrayList<T>(getInstance().getElevators());        
    }
    
    /**
     * Returns the speed of the elevators.
     * 
     * @return the speed (as waiting time in ms), see {@link Speed} for examples
     */
    public abstract int getElevatorsSpeed();

    
    /**
     * Returns the number of elevator call buttons per floor.
     * 
     * @return the number of buttons
     */
    public abstract int getFloorsButtons();
    
    /**
     * Returns whether the elevators shall operate synchronized and, thus, require an {@link AbstractMultiController}.
     *
     * @return {@code true} for synchronized, {@code false} for not synchronized
     */
    public abstract boolean isSynchronized();
    
    /**
     * Returns whether elevators shall moved in accelerated fashion.
     * 
     * @return {@code true} for accelerated, {@code false} for normal
     */
    public abstract boolean isAccelerated();
    
    /**
     * Returns whether an elevator call can be cancelled from outside.
     * 
     * @return {@code true} for cancel button, {@code} false else
     */
    public abstract boolean isOuterviewCancel();

    /**
     * Display a door control button in the elevator.
     * 
     * @return {@code true} for display, {@code} false else
     */
    public abstract boolean isInnerviewDoorButton();
    
    /**
     * Display a special actions authorization button in the elevator.
     * 
     * @return {@code true} for display, {@code} false else
     */
    public abstract boolean isInnerviewAuthorization();
    
    /**
     * Display the elevator moving direction.
     * 
     * @return {@code true} for display, {@code} false else
     */
    public abstract boolean isDisplayDirection();

    /**
     * Display the actual floor number.
     * 
     * @return {@code true} for display, {@code} false else
     */
    public abstract boolean isDisplayFloorNumber();

    /**
     * Display the actual floor as slider.
     * 
     * @return {@code true} for display, {@code} false else
     */
    public abstract boolean isFloorSliderDisplay();
    
    /**
     * Display the target/requested floor number.
     * 
     * @return {@code true} for display, {@code} false else
     */
    public abstract boolean isDisplayTarget();
    
    /**
     * Display the door state.
     * 
     * @return {@code true} for display, {@code} false else
     */
    public abstract boolean isDisplayDoorstate();

    /**
     * Do the outer panels have an emergency button?
     * 
     * @return {@code true} for emergency button, {@code} false else
     */
    public abstract boolean isOuterviewEmergency();

    
    /**
     * Do the inner panels have an emergency button?
     * 
     * @return {@code true} for emergency button, {@code} false else
     */
    public abstract boolean isInnerviewEmergency();
    
    /**
     * Returns the door opening/display delay.
     * 
     * @return the delay at door opening in ms
     */
    public int getDoorOpeningDelay() {
        return 780;
    }
    
    /**
     * Returns the maximum delay/waiting time before door closing.
     * 
     * @return the delay/waiting time before door closing in ms
     */
    public int getMaxDoorClosingDelay() {
        return 2000;
    }

    /**
     * Returns the door closing/display delay.
     * 
     * @return the delay at door closing in ms
     */
    public int getDoorClosingDelay() {
        return 800;
    }

    // ------------------------- simulation program ------------------------
    
    /**
     * Display height of a floor in simulation.
     */
    public abstract int getFloorsHeight();

    /**
     * Shall the simulation program enable autoscroll.
     * 
     * @return {@code true} for autoscroll, {@code} false else
     */
    public abstract boolean isAutoscroll();
    
    /**
     * returns the total height on the i-th floor.
     * 
     * @param iFloor the floor number
     * @return the height
     * @see #getFloorsHeight()
     */
    public static int getTotalHeight(int iFloor) {
        return iFloor * INSTANCE.getFloorsHeight();
    }

}
