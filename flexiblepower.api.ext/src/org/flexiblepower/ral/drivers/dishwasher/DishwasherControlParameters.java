package org.flexiblepower.ral.drivers.dishwasher;

import java.util.Date;

import org.flexiblepower.ral.ResourceControlParameters;

/**
 * The control parameter of a generic simple dishwasher. This assumes that a dishwasher has selected a single
 * non-divisible program that can be started at a selected time.
 */
public interface DishwasherControlParameters extends ResourceControlParameters {
    /**
     * @return The time at which the program should be started. The driver is responsible to make sure that the
     *         dishwasher starts at a time as close as possible to this time.
     */
    Date getStartTime();

    /**
     * @return The program that should be started. This value should correspond with the latest
     *         {@link DishwasherState#getProgram()}. Since the communication is asynchronous, it could be the case that
     *         the program has been updated. In that case this control setting should be ignored and a new
     *         {@link DishwasherState} should be sent to the manager.
     */
    String getProgram();
}
