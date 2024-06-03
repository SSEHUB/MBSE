package properties;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import gui.buttons.DefaultButton;
import gui.buttons.ElevatorOuterArrowButton;
import gui.buttons.ElevatorOuterButton;
import simulator.controllers.AbstractController;
import simulator.controllers.AbstractMultiController;

/**
 * Creates relevant program instances. Before starting the Elevator simulator, set your implementation
 * instance of this factory through {@link #setInstance(InstanceFactory)}.
 * 
 * @author SSE
 */
public abstract class InstanceFactory {
    
    private static InstanceFactory INSTANCE;
    private static Class<? extends ElevatorOuterButton> outerButtonClass;

    /**
     * Sets once the instance to be used
     * 
     * @param instance the instance to be used
     */
    public static void setInstance(InstanceFactory instance) {
        if (null == INSTANCE && null != instance) {
            INSTANCE = instance;
        }
    }
    
    /**
     * Returns the class to be used for creating outer buttons. Please consider the constructor convention
     * for this class as specified in {@link ElevatorOuterButton}.
     * 
     * @return the class to be used
     */
    public abstract Class<? extends ElevatorOuterButton> getOuterButtonClass();

    /**
     * Returns the controller class to be used. Please consider the constructor convention
     * for this class as specified in {@link AbstractController}.
     * 
     * @return the class to be used
     */
    public abstract AbstractController createControllerInstance(int iElevatorIndex);
    
    /**
     * Returns the controller class for multiple elevators to be used. Please consider the constructor convention
     * for this class as specified in {@link AbstractMultiController}. Called only if 
     * {@link ProgramSettings#isSynchronized()}.
     * 
     * @return the class to be used
     */
    public abstract AbstractMultiController createMultiControllerInstance(ArrayList<AbstractController> lControllers);

    
    /**
     * Returns whether the outer button based on {@link #getOuterButtonClass()} is an {@link ElevatorOuterArrowButton}.
     * 
     * @return {@code true} for an arrow button, {@code false} else
     */
    public static boolean isOuterArrowButton() {
        if (null == outerButtonClass) {
            createOuterButton(0, 0); // lazy
        }
        return outerButtonClass.isInstance(ElevatorOuterArrowButton.class);
    }

    /**
     * Creates a single outer elevator button. Considers {@link #getOuterButtonClass()} or {@link DefaultButton}.
     * 
     * @param floorId the 0-based floor index
     * @param iElevatorIndex the 0-based elevator index
     * @return the created elevator button
     */
    public static ElevatorOuterButton createOuterButton(int floorId, int iElevatorIndex) {
        ElevatorOuterButton result = null;
        Class<? extends ElevatorOuterButton> cls = 
            outerButtonClass == null ? INSTANCE.getOuterButtonClass() : outerButtonClass;
        try {
            Constructor<? extends ElevatorOuterButton> cons = cls.getConstructor(Integer.TYPE, Integer.TYPE);
            result = cons.newInstance(floorId, iElevatorIndex);
            outerButtonClass = cls;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException 
            | InvocationTargetException e) {
            System.err.println("Cannot create outer button for class " + INSTANCE.getOuterButtonClass() 
                + ": falling back to " + DefaultButton.class);
        }
        if (null == result) {
            outerButtonClass = DefaultButton.class;
            result = new DefaultButton(floorId, iElevatorIndex);
        }
        return result;
    }

    /**
     * Creates a single elevator controller. Calls {@link #createControllerInstance(int)}.
     * 
     * @param iElevatorIndex the 0-based elevator to create the controller for
     * @return the elevator controller
     */
    public static AbstractController createController(int iElevatorIndex) {
        return INSTANCE.createControllerInstance(iElevatorIndex);
    }
    
    /**
     * Creates a multi-elevator controller for the given controllers. Calls 
     * {@link #createMultiControllerInstance(ArrayList)}.
     * 
     * @param lControllers the lower-level controllers of the elevators.
     * @return the multi-elevator controller
     */
    public static AbstractMultiController createMultiController(ArrayList<AbstractController> lControllers) {
        return INSTANCE.createMultiControllerInstance(lControllers);
    }

}
