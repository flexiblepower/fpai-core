package org.flexiblepower.rai;


public interface ControllableResource {
    /**
     * Handles the allocation that should be created by a controller. The resource manager should translate this
     * allocation into actions that are executed using the connected driver.
     * 
     * @param allocation
     *            The allocation that is to be translated into actions. This allocation should never be null.
     * @throws NullPointerException
     *             When the allocation is null.
     * @throws IllegalStateException
     *             When there is no driver connected to this resource manager
     */
    void handleAllocation(Allocation allocation);

    /**
     * @return The type of resource that this manager represents.
     */
    ResourceType getResourceType();

    void setController(Controller controller);

    void unsetController(Controller controller);
}
