package org.flexiblepower.ral.drivers.dishwasher;

import java.util.Date;

import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;

import org.flexiblepower.rai.values.CommodityProfile;
import org.flexiblepower.ral.ResourceState;

/**
 * The {@link DishwasherState} that describes the current state of a dishwasher. In general there are 3 options:
 *
 * <ul>
 * <li><code>latestStartTime == null && startTime == null && program == null</code><br/>
 * This means that the dishwasher is off or no program has been selected. In this case there is nothing to control.</li>
 * <li><code>latestStartTime != null && startTime == null && program != null</code><br/>
 * This means that the dishwasher is on with a selected program that has been delayed. It can be started anywhere
 * between now and the latestStartTime.</li>
 * <li><code>startTime != null && program != null</code><br />
 * This means that the program has started at the startTime and it can not be interrupted anymore.
 * </ul>
 */
public interface DishwasherState extends ResourceState {
    /**
     * @return The latest time at which the current program should start, or <code>null</code> if there is nothing to
     *         start.
     */
    Date getLatestStartTime();

    /**
     * @return The time at which the program has been started, or <code>null</code> if it hasn't been started yet.
     */
    Date getStartTime();

    /**
     * @return The unique name of the selected program. This will be used for display purposes and in the
     *         {@link DishwasherControlParameters#getProgram()} to make sure that this program will be started. This can
     *         be <code>null</code> when the dishwasher is off or no program has been selected.
     */
    String getProgram();

    /**
     * @return The corresponding energy profile of the selected program.
     */
    CommodityProfile<Energy, Power> getEnergyProfile();
}
