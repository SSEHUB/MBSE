package demo;

import java.util.ArrayList;

import gui.buttons.DefaultButton;
import gui.buttons.ElevatorOuterButton;
import properties.InstanceFactory;
import simulator.controllers.AbstractController;
import simulator.controllers.AbstractMultiController;
import simulator.controllers.DefaultEController;
import simulator.controllers.SuperController;

/**
 * Example instance factory based on default classes of the elevator simulator.
 * 
 * @author SSE
 */
public class MyInstanceFactory extends InstanceFactory {

    @Override
    public Class<? extends ElevatorOuterButton> getOuterButtonClass() {
        return DefaultButton.class;
    }

    @Override
    public AbstractController createControllerInstance(int iElevatorIndex) {
        return new DefaultEController(iElevatorIndex);
        //return new SimpleEventController(iElevatorIndex);
    }
    
    @Override
    public AbstractMultiController createMultiControllerInstance(ArrayList<AbstractController> lControllers) {
        return new SuperController(lControllers);
    }

}
