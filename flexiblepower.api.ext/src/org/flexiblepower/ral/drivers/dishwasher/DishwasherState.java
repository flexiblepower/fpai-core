package org.flexiblepower.ral.drivers.dishwasher;

import java.util.Date;

import org.flexiblepower.rai.values.EnergyProfile;
import org.flexiblepower.ral.ResourceState;

public interface DishwasherState extends ResourceState {

    Date getStartTime();

    String getProgram();
    
    EnergyProfile getEnergyProfile();

}
