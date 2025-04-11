package engine;

import gui.buttons.DefaultButton;

import java.util.ArrayList;

import properties.ProgramSettings;
import simulator.model.Elevator;

/**
 * Controls the elevator speed.
 * 
 * @author SSE
 */
public class SpeedController {

    /**
     * Creates an instance.
     */
    public SpeedController() {
    }
    
    /**
     * Checks whether the button for a given floor is marked as ignored.
     */
    private boolean isIgnored(ArrayList<DefaultButton> buttons, int floorIndex) {
        return floorIndex >= 0 && floorIndex < buttons.size() && buttons.get(floorIndex).isIgnored();
    }

    /**
     * Adapts the speed for acceleration.
     * 
     * @throws InterruptedException if waiting is interrupted for some reason
     */
    public void adaptSpeed(int iPos, Elevator elevator, ArrayList<DefaultButton> buttons) throws InterruptedException {

        // if two buttons on the way to target floor are disabled, the elevator shall move faster

        // select next button above: Button + iPlus
        int iPlus = elevator.getDirection();
        // adjust if start or target floor
        if (elevator.getCurrentFloor() <= 1
            || (elevator.getCurrentFloor() == ProgramSettings.getInstance().getFloors())) {
            iPlus = 0;
        }

        int iSpeed = ProgramSettings.getInstance().getElevatorsSpeed();
        boolean currentIgnored = isIgnored(buttons, elevator.getCurrentFloor());
        boolean nextIgnored = isIgnored(buttons, elevator.getCurrentFloor() + iPlus);
        boolean prevIgnored = isIgnored(buttons, 
        		Math.min(elevator.getCurrentFloor() - iPlus, ProgramSettings.getInstance().getElevators() - 1));
        int halfFloorHeight = ProgramSettings.getInstance().getFloorsHeight();

        if (currentIgnored) {
            // if previous floor button not ignored, speed up first half way
            if (!prevIgnored) {
                // first half way
                if (iPos <= halfFloorHeight) {
                    // accelerate (i.e., less delay)
                    Thread.sleep(iSpeed - Math.min(iSpeed, 2 * iPos));
                } else {
                    // else slower
                    if (!nextIgnored && iPos > halfFloorHeight) {
                        Thread.sleep(iSpeed + (2 * iPos));
                    } else {
                        Thread.sleep(40);
                    }
                }
            } else {
                Thread.sleep(40);
            }

        } else if (!currentIgnored) {
            if (!nextIgnored) {
                Thread.sleep(iSpeed);
            } else {
                if (iPos > halfFloorHeight) {
                    Thread.sleep(iSpeed + (2 * iPos));
                }
            }

        }

    }
}
