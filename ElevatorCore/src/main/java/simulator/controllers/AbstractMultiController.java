package simulator.controllers;

import simulator.model.Request;

/**
 * Multi-elevator controller. Must provide a public constructor with 
 * {@code (ArrayList&lt;AbstractController&gt; lControllers)}.
 * 
 * @author SSe
 */
public abstract class AbstractMultiController {

    /**
     * Adds the request, either here or directly to the specific controller.
     * 
     * @param target the request
     * @param iControllerIndex if this is a controller-specific call, specify the controller index
     * @param insideElevator {@code true} if call was raised within elevator, else {@code false}
     */
    public abstract void addRequest(Request target, int iControllerIndex, boolean insideElevator);
    
    /**
     * Delete request (target).
     * 
     * @param iControllerIndex if this is a controller-specific call, specify the controller index
     * @param iFloor target floor
     */
    public abstract void deleteRequest(int iControllerIndex, int iFloor);
    
    /**
     * At asynchronous processing, a priority call is added to the priority queue of the controller. At synchronous
     * processing, the target will be added into the priority targets of this class and dispached to the respective
     * controller.
     * 
     * @param iControllerIndex if this is a controller-specific call, specify the controller index
     * @param iFloor target floor
     * @param insideElevator {@code true} if call was raised within elevator, else {@code false}
     */
    public abstract void addPriorityCall(int iControllerIndex, int iFloor, boolean insideElevator);
    
}
