package org.flexiblepower.simulation.scheduling;

import java.util.Date;

import org.flexiblepower.simulation.api.Simulation;
import org.flexiblepower.simulation.api.Simulation.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulationClock {

    private static final Logger LOG = LoggerFactory.getLogger(SimulationClock.class);

    private long simulationStartTime;
    private long simulationStopTime;
    private long wallStartTime;
    private double speedFactor;
    private Simulation.State state;

    public SimulationClock() {
        state = Simulation.State.STOPPED;
        simulationStartTime = 0;
        simulationStopTime = 0;
        wallStartTime = 0;
        speedFactor = 0;
    }

    public synchronized void start(long startTime, double speedFactor) {
        start(startTime, Long.MAX_VALUE, speedFactor);
    }

    public synchronized void start(long startTime, long stopTime, double speedFactor) {
        if (state != Simulation.State.STOPPED) {
            throw new IllegalStateException("SimulationClock can not be started if already running");
        }
        simulationStartTime = startTime;
        simulationStopTime = stopTime;
        wallStartTime = System.currentTimeMillis();
        this.speedFactor = speedFactor;
        state = Simulation.State.RUNNING;
        LOG.debug("Simulation starting with simulated time " + new Date(startTime).toString());
    }

    public synchronized void pause() {
        if (state != Simulation.State.RUNNING) {
            throw new IllegalStateException("SimulationClock can not be paused if not running");
        }
        simulationStartTime = getCurrentTimeMillis();
        state = Simulation.State.PAUSED;
        LOG.debug("Simulation paused at simulated time " + new Date(simulationStartTime).toString());
    }

    public synchronized void unpause() {
        if (state != Simulation.State.PAUSED) {
            throw new IllegalStateException("SimulationClock can not be unpaused if not paused");
        }
        state = Simulation.State.RUNNING;
        wallStartTime = System.currentTimeMillis();
        LOG.debug("Simulation upaused at simulated time " + new Date(simulationStartTime).toString());
    }

    public synchronized void stop() {
        LOG.debug("Simulation stopped");
        state = Simulation.State.STOPPED;
    }

    public synchronized void changeSpeedFactor(double newSpeedFactor) {
        switch (state) {
        case RUNNING:
            long simulationTime = getCurrentTimeMillis();
            stop();
            start(simulationTime, simulationStopTime, newSpeedFactor);
            break;
        case PAUSED:
            speedFactor = newSpeedFactor;
            break;
        default:
            throw new IllegalStateException("Cannot change speedFactor while stopped");
        }
    }

    public boolean isRunning() {
        return state == State.RUNNING;
    }

    public boolean isStopped() {
        return state == State.STOPPED;
    }

    public boolean isPaused() {
        return state == State.PAUSED;
    }

    public boolean isStopping() {
        return state == State.STOPPING;
    }

    public double getSpeedFactor() {
        return speedFactor;
    }

    public Simulation.State getState() {
        return state;
    }

    /**
     * Calculate the current simulated time based on the wall clock
     * 
     * This method has the side effect that it checks if the simulation is finished. If the simulation is stopped or
     * finished, the method will return 0.
     * 
     * @return the current simulated time
     */
    public synchronized long getCurrentTimeMillis() {
        switch (state) {
        case STOPPED:
            return 0;
        case PAUSED:
            return simulationStartTime;
        case STOPPING:
            return simulationStopTime;
        default:
            long currentTime = (long) ((System.currentTimeMillis() - wallStartTime) * speedFactor + simulationStartTime);
            if (currentTime > simulationStopTime) {
                LOG.debug("Simulation reached end time " + new Date(simulationStopTime).toString()
                          + ", stopping simulation...");
                state = State.STOPPING;
                return simulationStopTime;
            } else {
                return currentTime;
            }
        }
    }

}
