package org.flexiblepower.context;

import java.util.Date;

public interface FlexiblePowerContext {

    long currentTimeMillis();

    Date currentTime();

    Scheduler getScheduler();

    boolean isSimulation();

    Simulation getSimulation();

}
