package org.flexiblepower.ral.messages;

/**
 * This enumeration contains all possible states that may be fed back by an appliance driver to an energy app in
 * response to an {@link Allocation}.
 */
public enum AllocationStatus {
    /**
     * The appliance driver has accepted the {@link Allocation}, meaning that it will try to follow the instructions
     * from the {@link Allocation} message as closely as possible.
     */
    ACCEPTED,
    /**
     * The driver is not able or willing to follow up the instructions.
     */
    REJECTED,
    /**
     * The driver has not yet started but is processing the instructions in order to be able to start on time. Once
     * the PROCESSING state has begun it is not possible anymore for the energy app to send a new Allocation message
     * to overrule a previous one.
     *
     * This PROCESSING state corresponds with the allocationDelay attribute of the ControlSpaceUpdate message.
     *
     * This state is optional. It is allowed to go from the Accepted state directly to the Started state, if no
     * processing time is needed.
     */
    PROCESSING,
    /**
     * The driver has started the {@link Allocation} of the energy app.
     */
    STARTED,
    /**
     * When the {@link Allocation} has been completed, the appliance driver will inform the energy app by sending a
     * finished event.
     */
    FINISHED
}