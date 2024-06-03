package simulator;

import gui.windows.MainWindow;
import properties.InstanceFactory;
import properties.ProgramSettings;

import java.util.ArrayList;

import simulator.controllers.AbstractController;
import simulator.controllers.AbstractMultiController;

/**
 * The elevator simulator.
 * 
 * @author SSE
 */
public class ElevatorSimulator {

    private static ElevatorSimulator instance = null;

    private ArrayList<AbstractController> lControllers;
    private AbstractMultiController multiController;

    /**
     * Creates the simulator. Prevents external creation.
     */
    private ElevatorSimulator() {
        initController();
        initGUI();
    }

    /**
     * Returns a controller in case of multiple elevators.
     * 
     * @param i the 0-based controller index
     * @return the specified controller
     */
    public AbstractController getController(int i) {
        return lControllers.get(i);
    }

    /**
     * Returns the multi elevator controller.
     * 
     * @return the multi elevator controller (may be <b>null</b> for none)
     */
    public AbstractMultiController getMultiController() {
        return multiController;
    }

    /**
     * Returns the singleton instance of the simulator.
     * 
     * @return the singleton instance.
     */
    public static ElevatorSimulator getInstance() {
        if (instance == null) {
            instance = new ElevatorSimulator();
        }
        return instance;
    }

    /**
     * Initializes the simulator UI.
     */
    public void initGUI() {
        MainWindow.getInstance();
    }

    /**
     * Initializes the controller.
     */
    private void initController() {
        final int elevators = ProgramSettings.getInstance().getElevators();
        lControllers = new ArrayList<AbstractController>();
        for (int i = 0; i < elevators; i++) {
            lControllers.add(InstanceFactory.createController(i));
        }

        if (ProgramSettings.getInstance().isSynchronized() && elevators > 1) {
            this.multiController = InstanceFactory.createMultiController(lControllers);
        }
    }

}
