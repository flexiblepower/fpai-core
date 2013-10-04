package org.flexiblepower.simulation.api;

import java.util.Date;

public interface Simulation {

    public static enum State {
        STOPPED, RUNNING, PAUSED, STOPPING
    }

    public void startSimulation(Date startTime, double speedFactor);

    public void startSimulation(Date startTime, Date stopTime, double speedFactor);

    public void stopSimulation();

    public void pause();

    public void unpause();

    public void changeSpeedFactor(double newSpeedFactor);

    public Date getTime();

    public State getSimulationClockState();

}
