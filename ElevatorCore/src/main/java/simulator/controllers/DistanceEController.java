package simulator.controllers;

import properties.ProgramSettings;
import simulator.model.Request;

/**
 * An elevator controller which considers the distance between two floors and not (only) the
 * elevator direction. Applies a priority scheme to prevent loosing elevator requests.
 */
public class DistanceEController extends AbstractController {

    private int iTravelledDistance = 0;
    private int iPriorityThreshold;
    private boolean bSameFloor;

    /**
     * Creates a controller instance.
     * 
     * @param iElevator elevator index
     */
    public DistanceEController(int iElevator) {
        super(iElevator);
        final int floors = ProgramSettings.getInstance().getFloors();
        iPriorityThreshold = floors / 2;
        setCurrentRequest(null);
    }
    
    @Override
    public String getDisplayName() {
        return "Distance Controller";
    }

    @Override
    public boolean simulate() {
        return lockRequestList(lTargetList -> {

            processDeletions();

            if (!isPriorityQueueEmpty() && !hasPriorityCall()) {
                processPriorityCall();
            }

            if (getElevatorCurrentFloor() + getElevatorCurrentDirection() <= lTargetList.size()
                && getElevatorCurrentFloor() + getElevatorCurrentDirection() >= 0) {
                setElevatorCurrentFloor(getElevatorCurrentFloor() + getElevatorCurrentDirection());
                iTravelledDistance++;
            }

            boolean targetFound = getCurrentRequest() != null && getElevatorCurrentFloor() == getCurrentRequestedFloor();

            if (getElevatorCurrentDirection() != 0 || bSameFloor) {
                bSameFloor = false;

                if (targetFound) {
                    lTargetList.set(getCurrentRequestedFloor(), null);
                    setCurrentRequest(null);
                    // is there one/multiple priority calls
                    if (hasPriorityCall() && isPriorityQueueEmpty()) {
                        setHasPriorityCall(false);
                    } else if (!isPriorityQueueEmpty()) {
                        processPriorityCall();
                    }

                    for (int i = 0; i < lTargetList.size(); i++) {
                        if (lTargetList.get(i) != null) {
                            lTargetList.get(i).adjustPriority(iTravelledDistance);
                        }
                    }
                    updateTargetByPriority();
                    // if there is no target, stay here
                    if (!hasCurrentRequest()) {
                        setElevatorCurrentDirection(0);
                    }
                    iTravelledDistance = 0;
                }

            }

            return targetFound;
        });
    }

    @Override
    public void addRequest(Request target) {
        lockRequestList(lTargetList -> {
            if (lTargetList.get(target.getFloor()) == null) {
                lTargetList.set(target.getFloor(), target);
            }

            // if elevator has no target, determine direction
            if (getElevatorCurrentDirection() == 0) {
                bSameFloor = false;
                if (target.getFloor() > getElevatorCurrentFloor()) {
                    setElevatorCurrentDirection(1);
                } else if (target.getFloor() < getElevatorCurrentFloor()) {
                    setElevatorCurrentDirection(-1);
                } else {
                    bSameFloor = true;
                }
            }

            // actualize priorities
            for (int i = 0; i < lTargetList.size(); i++) {
                if (lTargetList.get(i) != null && i != target.getFloor()) {
                    lTargetList.get(i).adjustPriority(iTravelledDistance);
                }
            }
            iTravelledDistance = 0;

            // if there is no target, set this floor as target to move the elevator
            if (!hasCurrentRequest()) {
                setCurrentRequest(target);
            } else {
                // request update that target is being actualized
                setUpdateNecessary(true);
            }
            startSimulation();
        });
    }

    @Override
    public boolean updateTarget() {
        setUpdateNecessary(false);
        // search next target between current floor and actual target
        if (!hasPriorityCall()) {
            if (!hasCurrentRequest()) {
                return lockRequestList(lTargetList -> {
                    for (int i = 0; i < lTargetList.size(); i++) {
                        if (lTargetList.get(i) != null) {
                            setCurrentRequest(lTargetList.get(i));
                            return true;
                        }
                    }
                    return false;
                });
            } else {
                return updateTargetByPriority();
            }
        }
        return false;
    }

    /**
     * Es wird das neue Zielstockwerk gesucht. Dabei wird die Priorit&auml;t
     * jedes Stockwerks miteinbezogen, damit keins ausgelassen wird. Beim
     * &Uuml;berschreiten des PriorityThresholds wird das Target mit der
     * gr&ouml;ssten &Uuml;berschreitung angew&auml;hlt.
     * 
     * @return true, wenn ein Ziel gefunden wurde, sonst false
     */
    private boolean updateTargetByPriority() {
        return lockRequestList(lTargetList -> {
            Request bestPriority = null;
            // search for target with highest priority over threshold
            for (int i = 0; i < lTargetList.size(); i++) {
                if (lTargetList.get(i) != null && lTargetList.get(i).getPriority() >= iPriorityThreshold) {
                    if (bestPriority == null || lTargetList.get(i).getPriority() > bestPriority.getPriority()) {
                        bestPriority = lTargetList.get(i);
                    }
                }
            }

            // if target over threshold found, set this to next target
            if (bestPriority != null) {
                setCurrentRequest(bestPriority);
                if (getElevatorCurrentFloor() < bestPriority.getFloor()) {
                    setElevatorCurrentDirection(1);
                } else if (getElevatorCurrentFloor() > bestPriority.getFloor()) {
                    setElevatorCurrentDirection(-1);
                }
                return true;
            } else {
                // else select next target by distance
                int down;
                int up;
                for (int i = 1; i < lTargetList.size(); i++) {
                    down = getElevatorCurrentFloor() - i;
                    up = getElevatorCurrentFloor() + i;

                    // check above current floor whether the target is there
                    if (up < lTargetList.size() && lTargetList.get(up) != null) {
                        setCurrentRequest(lTargetList.get(up));
                        setElevatorCurrentDirection(1);
                        return true;
                    }

                    // check below current floor whether the target is there
                    if (down >= 0 && lTargetList.get(down) != null) {
                        setCurrentRequest(lTargetList.get(down));
                        setElevatorCurrentDirection(-1);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void processPriorityCall() {
        setHasPriorityCall(true);
        setCurrentRequest(pollPriorityQueue());
        if (getCurrentRequestedFloor() > getElevatorCurrentFloor()) {
            setElevatorCurrentDirection(1);
        } else if (getCurrentRequestedFloor() < getElevatorCurrentFloor()) {
            setElevatorCurrentDirection(-1);
        } else {
            bSameFloor = true;
        }
    }

    @Override
    public void processDeletions() {
        lockRequestList(lTargetList -> {
            processDeletionQueue(iDelFloor -> {
                if (getCurrentRequest() == lTargetList.get(iDelFloor)) {

                    lTargetList.set(iDelFloor, null);
                    setCurrentRequest(null);

                    if (updateTargetByPriority()) {
                        setElevatorCurrentFloor(getElevatorCurrentFloor() + (-getElevatorCurrentDirection()));
                        setElevatorCurrentFloor(getElevatorCurrentFloor() + (-getElevatorCurrentDirection())); // ?
                    } else {
                        setElevatorCurrentFloor(getElevatorCurrentFloor() + getElevatorCurrentDirection());
                        setElevatorCurrentDirection(0);
                    }

                } else {
                    lTargetList.set(iDelFloor, null);
                }
            });
        });
    }

    @Override
    public void doAfterAnimate() {
    }
    
}
