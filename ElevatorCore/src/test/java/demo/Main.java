package demo;

import engine.EventsManager;
import gui.windows.MainWindow;
import properties.InstanceFactory;
import properties.ProgramSettings;

/**
 * Customized main program of the elevator simulator.
 * 
 * @author SSE
 */
public class Main {

    private static boolean DEBUG = true; // just for demonstration
    
    /**
     * Starts the program.
     * 
     * @param args ignored
     */
    public static void main(String[] args) {
        ProgramSettings.setInstance(new MyProgramSettings());
        InstanceFactory.setInstance(new MyInstanceFactory());

        EventsManager.BUTTONS.setDebug(DEBUG);
        EventsManager.FLOOR_SENSORS.setDebug(DEBUG);
        EventsManager.ELEVATORS.setDebug(DEBUG);

        new MainWindow();
    }

}
