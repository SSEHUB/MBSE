package simulator.controllers;

import simulator.model.Request;

/**
 * Default controller to control an elevator.
 */
public class DefaultEController extends AbstractController {

    private boolean bSameFloor;
    
    /**
     * Creates a controller instance.
     * 
     * @param iElevator
     *            elevator index
     */
    public DefaultEController(int iElevator) {
        super(iElevator);
    }

    @Override
    public String getDisplayName() {
        return "Default built-in Controller";
    }
    
    @Override
    public boolean simulate() {
        return lockRequestList(lTargetList -> {

            // process deletions first
            processDeletions();

            // handle a priority call if there is one
            if (!isPriorityQueueEmpty() && !hasPriorityCall()) {
                processPriorityCall();
            }

            // move the elevator but only if it stays within its floor limits
            if (getElevatorCurrentFloor() + getElevatorCurrentDirection() <= lTargetList.size()
                    && getElevatorCurrentFloor() + getElevatorCurrentDirection() >= 0) {
                setElevatorCurrentFloor(getElevatorCurrentFloor() + getElevatorCurrentDirection());
            }

            if (getElevatorCurrentDirection() != 0 || bSameFloor) {
                bSameFloor = false;
            }

            return hasCurrentRequest() && getElevatorCurrentFloor() == getCurrentRequestedFloor() || bSameFloor;
        });
    }

    @Override
    public void doAfterAnimate() {
        // synchronized (lTargetList) {
        processDeletions();

        lockRequestList(lTargetList -> {
            lTargetList.set(getCurrentRequestedFloor(), null);
        });
        setCurrentRequest(null);
        if (hasPriorityCall() && isPriorityQueueEmpty()) {
            setHasPriorityCall(false);
        } else if (!isPriorityQueueEmpty()) {
            processPriorityCall();
        }

        // search for new target, first direction of target and then without as otherwise targets may be ignored
        if (!hasPriorityCall() && (!updateTarget(getElevatorCurrentDirection(), true)
            && !updateTarget(getElevatorCurrentDirection(), false))) {

            setElevatorCurrentDirection(getElevatorCurrentDirection() * -1);
            // stay there if there are no targets
            if (!updateTarget(getElevatorCurrentDirection(), true) && !updateTarget(getElevatorCurrentDirection(), false)) {
                setElevatorCurrentDirection(0);
            }
        }
        // }
    }

    @Override
    public void addRequest(Request target) {
        lockRequestList(lTargetList -> {
            if (lTargetList.get(target.getFloor()) == null) {
                lTargetList.set(target.getFloor(), target);
            }

            // determine target floor if there is not already a target floor
            if (getElevatorCurrentDirection() == 0) {
                // bSameFloor = false;
                if (target.getFloor() > getElevatorCurrentFloor()) {
                    setElevatorCurrentDirection(1);
                } else if (target.getFloor() < getElevatorCurrentFloor()) {
                    setElevatorCurrentDirection(-1);
                } else {
                    bSameFloor = true;
                    if (!hasCurrentRequest()) {
                        setCurrentRequest(target);
                        return;
                    }
                }
            }

            // determine target in desired direction if target is not yet reached
            if (!hasCurrentRequest() || getCurrentRequestedFloor() != getElevatorCurrentFloor()) {
                if (target.getFloor() != getElevatorCurrentFloor() && !hasPriorityCall()
                                && !updateTarget(getElevatorCurrentDirection(), true)) {

                    // if no target found then go for target in any direction
                    updateTarget(getElevatorCurrentDirection(), false);

                    // still no target, look for opposite direction
                    if (!hasCurrentRequest()) {
                        updateTarget(getElevatorCurrentDirection() * -1, false);
                    }
                }
            }
            startSimulation();
        });
    }

    /**
     * Updates the actual target.
     * 
     * @param iDir the desired direction (0 none, 1 up, -1 down)
     * @param bAttendTargetDir unclear, usually {@code true}
     * @return {@code true} if the target was updated, {@code false} else
     */
    private boolean updateTarget(int iDir, boolean bAttendTargetDir) {
        return lockRequestList(lTargetList -> {
            // search for target depending on direction
            for (int i = getElevatorCurrentFloor(); i < lTargetList.size() && i >= 0
                && getElevatorCurrentDirection() != 0; i += iDir) {
                if (bAttendTargetDir) {
                    if (lTargetList.get(i) != null && ((lTargetList.get(i).getDirection() == getElevatorCurrentDirection())
                        || lTargetList.get(i).getDirection() == 0)) {
                        setCurrentRequest(lTargetList.get(i));
                        return true;
                    }
                } else {
                    if (lTargetList.get(i) != null) {
                        setCurrentRequest(lTargetList.get(i));
                        return true;
                    }
                }
            }
            return false;
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
                    // search target in both directions
                    if (!updateTarget(getElevatorCurrentDirection(), true)) {
                        if (updateTarget(getElevatorCurrentDirection() * -1, true)) {
                            setElevatorCurrentFloor(getElevatorCurrentFloor() + getElevatorCurrentDirection());
                            setElevatorCurrentFloor(getElevatorCurrentFloor() + getElevatorCurrentDirection()); // ?
                            setElevatorCurrentDirection(getElevatorCurrentDirection() * -1);
                        } else {
                            setElevatorCurrentFloor(getElevatorCurrentFloor() + getElevatorCurrentDirection());
                            setElevatorCurrentDirection(0);
                        }
                    }
                    // updateTarget(eElevator.getDirection(), true);
                } else {
                    lTargetList.set(iDelFloor, null);
                }
            });
        });
    }

    @Override
    public boolean updateTarget() {
        return false;
    }

}
