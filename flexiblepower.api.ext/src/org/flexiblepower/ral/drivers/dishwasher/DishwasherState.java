package org.flexiblepower.ral.drivers.dishwasher;

import java.util.Date;

import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;

import org.flexiblepower.efi.util.CommodityProfile;
import org.flexiblepower.ral.ResourceState;

/**
 * The {@link DishwasherState} that describes.
 */
public interface DishwasherState extends ResourceState {
    /**
     * @return The latest time at which the current program should start.
     */
    Date getLatestStartTime();

    /**
     * @return The unique name of the selected program. This will be used for display purposes and in the
     *         {@link DishwasherControlParameters#getProgram()} to make sure that this program will be started.
     */
    String getProgram();

    /**
     * @return The corresponding energy profile of the selected program.
     */
    CommodityProfile<Energy, Power> getEnergyProfile();
}
