package org.flexiblepower.simulation.api;

import java.util.Date;

/**
 * When a service of this type is available in the service repository, we are running in a simulation environment. It is
 * possible for any package to give a controlling mechanism for this simulation.
 */
public interface Simulation {
    /**
     * The current state of the simulation.
     */
    public static enum State {
        /**
         * The simulation is currently stopped.
         */
        STOPPED,
        /**
         * The simulation is currently running.
         */
        RUNNING,
        /**
         * The simulation is currently paused.
         */
        PAUSED,
        /**
         * The simulation has been asked to stop, but needs to finish up its tasks first.
         */
        STOPPING
    }

    /**
     * Starts the simulation without a planned ending.
     *
     * @param startTime
     *            The virtual time at which to start the simulation.
     * @param speedFactor
     *            The speedFactor at which to start (this can be changed dynamically during the run).
     */
    void startSimulation(Date startTime, double speedFactor);

    /**
     * Starts the simulation with a planned ending.
     *
     * @param startTime
     *            The virtual time at which to start the simulation.
     * @param stopTime
     *            The virtual time at which the simulation will be stopped automatically.
     * @param speedFactor
     *            The speedFactor at which to start (this can be changed dynamically during the run).
     */
    void startSimulation(Date startTime, Date stopTime, double speedFactor);

    /**
     * Stops the current simulation.
     */
    void stopSimulation();

    /**
     * Pauses the current simulation.
     */
    void pause();

    /**
     * Unpauses the current simulation.
     */
    void unpause();

    /**
     * Changes the speedfactor during the run of the simulation. When the simulation has stopped, this has no effect.
     *
     * @param newSpeedFactor
     *            The new speedFactor.
     */
    void changeSpeedFactor(double newSpeedFactor);

    /**
     * @return The current state of simulation.
     */
    State getSimulationClockState();
}
