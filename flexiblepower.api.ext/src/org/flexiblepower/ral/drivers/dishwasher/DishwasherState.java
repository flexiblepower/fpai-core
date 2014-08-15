package org.flexiblepower.ral.drivers.dishwasher;

import java.util.Date;

import javax.measure.quantity.Power;

import org.flexiblepower.rai.values.EnergyProfile;
import org.flexiblepower.ral.ResourceState;

/**
 * The {@link DishwasherState}.
 */
public interface DishwasherState extends ResourceState {

    /**
     * @return The latest time at which the current program should start.
     */
    Date getStartTime();

    /**
     * @return The user friendly name of the selected program.
     */
    String getProgram();

    /**
     * @return The energy profile of the selected program.
     */
    EnergyProfile<Power> getEnergyProfile();

}
