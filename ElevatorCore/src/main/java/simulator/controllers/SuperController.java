package simulator.controllers;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import properties.ProgramSettings;
import simulator.model.Request;

/**
 * The super controller manages elevator requests if multiple elevators shall be synchronized. Else it just
 * passes on the requests to the individual elevators.
 */
public class SuperController extends AbstractMultiController {

    private ArrayList<AbstractController> lControllers;

    private ConcurrentLinkedQueue<Request> clqPriorityTargets = new ConcurrentLinkedQueue<Request>();;

    /**
     * Creates a super controller on the given elevator controllers.
     * 
     * @param controllers the elevator controllers
     */
    public SuperController(ArrayList<AbstractController> controllers) {
        lControllers = controllers;
    }

    /**
     * Processes the elevator requests for a synchronous elevator control and assigns requests to the next elevator
     * in the given direction.
     * 
     * @param target the target
     * @param priority is it a priority call
     */
    private void delegateCall(Request target, boolean priority) {
        int bestMatch = Integer.MAX_VALUE;
        int iCurrent = Integer.MAX_VALUE;
        for (int i = 0; i < lControllers.size(); i++) {
            // which controller has best prerequisites to handle the target call. Look whether the target floor is
            // on the ignore list of the controller and (the target is over the elevator of the controller and the 
            // actual direction is up) or (the target is below the elevator of the controller and the actual direction 
            // down) or the elevator has no direction and is not moving

            if ((!lControllers.get(i).isFloorIgnored(target.getFloor()))
                && ((target.getFloor() > lControllers.get(i).getElevator().getCurrentFloor()
                    && lControllers.get(i).getElevator().getDirection() == 1)
                    || (target.getFloor() < lControllers.get(i).getElevator().getCurrentFloor()
                        && lControllers.get(i).getElevator().getDirection() == -1)
                    || (lControllers.get(i).getElevator().getDirection() == 0))) {
                // calculate floor distance to target
                int iNext = Math.abs(target.getFloor() - lControllers.get(i).getElevator().getCurrentFloor());
                // is distance shorter store as best match
                if (iCurrent > iNext) {
                    iCurrent = iNext;
                    bestMatch = i;
                }
            }
        }

        AbstractController bestController = lControllers.get(bestMatch);

        if (ProgramSettings.getInstance().isOuterviewEmergency()
            || ProgramSettings.getInstance().isInnerviewEmergency()) {

            // is it a priority call, transfer target to priority queue and process there
            if (priority) {
                bestController.addToPriorityQueue(target);
                bestController.processPriorityCall();
            } else {
                // else just add the target to the best match controller
                bestController.addRequest(target);
            }
        } else {
            bestController.addRequest(target);
        }

        bestController.startSimulation();
    }
    
    @Override
    public void addRequest(Request target, int iControllerIndex, boolean insideElevator) {
        AbstractController controller = lControllers.get(iControllerIndex);
        // check whether the elevators move synchronously
        if (insideElevator) {
            controller.addRequest(target);
            controller.startSimulation();
        } else {
            delegateCall(target, false);
        }
    }

    @Override
    public void deleteRequest(int iControllerIndex, int iFloor) {
        // as synchronized, search for the elevator that goes for the specified floor and delete the target
        for (int i = 0; i < lControllers.size(); i++) {
            if (lControllers.get(i).getRequest(iFloor) != null) {
                lControllers.get(i).deleteTarget(iFloor);
                break;
            }
        }
    }

    @Override
    public void addPriorityCall(int iControllerIndex, int iFloor, boolean insideElevator) {
        Request target = new Request(iFloor, 0);
        AbstractController controller = lControllers.get(iControllerIndex);
        if (insideElevator) {
            controller.addToPriorityQueue(target);
            controller.processPriorityCall();
            controller.startSimulation();
        } else {
            clqPriorityTargets.add(target);
            delegateCall(clqPriorityTargets.poll(), true);
        }
    }

}