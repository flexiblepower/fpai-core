package org.flexiblepower.simulation.context;

import java.util.Date;

import org.flexiblepower.context.FlexiblePowerContext;
import org.flexiblepower.scheduling.AbstractScheduler;
import org.flexiblepower.scheduling.Job;
import org.flexiblepower.simulation.api.Simulation;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;

@Component(provide = { FlexiblePowerContext.class, Simulation.class })
public class SimulationContext extends AbstractScheduler implements Simulation {

    private final SimulationClock simulationClock = new SimulationClock();

    @Activate
    public void activate() {
        start("simulator");
    }

    @Deactivate
    public void deactivate() {
        stop();
    }

    private volatile boolean isWaiting = false;
    private volatile long currentTime = 0;

    @Override
    public long currentTimeMillis() {
        if (simulationClock.isStopped()) {
            return System.currentTimeMillis();
        } else {
            if (isWaiting) {
                long clockTime = simulationClock.getCurrentTimeMillis();
                if (clockTime > currentTime && clockTime < getNextJobTime()) {
                    currentTime = clockTime;
                }
            }
            return currentTime;
        }
    }

    @Override
    public Date currentTime() {
        return new Date(currentTimeMillis());
    }

    // The main run method
    @Override
    public void run() {
        while (running.get()) {
            if (simulationClock.isRunning() || simulationClock.isStopping()) {
                synchronized (jobs) {
                    long now = simulationClock.getCurrentTimeMillis();
                    logger.trace("Simulation step {}", now);
                    long waitTime = getNextJobTime() - now;
                    if (waitTime <= 0) {
                        Job<?> job = jobs.remove();
                        currentTime = Math.max(currentTime, job.getTimeOfNextRun());
                        logger.trace("Executing  {}", job);
                        job.run();
                        if (!job.isDone()) {
                            jobs.add(job);
                            logger.trace("Rescheduling {}", job);
                        }
                    } else if (simulationClock.isStopping()) {
                        logger.trace("Stopping simulation clock");
                        simulationClock.stop();
                    } else {
                        long sleepTime = (long) (waitTime / simulationClock.getSpeedFactor());
                        logger.trace("Sleeping {}ms until next job", sleepTime);
                        try {
                            if (sleepTime > 0) {
                                isWaiting = true;
                                jobs.wait(sleepTime);
                            }
                        } catch (final InterruptedException ex) {
                        }
                        isWaiting = false;
                    }
                }
            } else {
                // Wait for simulation start
                try {
                    synchronized (jobs) {
                        jobs.wait();
                        currentTime = simulationClock.getSimulationStartTime();
                    }
                } catch (InterruptedException e) {
                }
            }
        }
    }

    // Start or stop the simulation

    @Override
    public void startSimulation(Date startTime, double speedFactor) {
        startSimulation(startTime, null, speedFactor);
    }

    @Override
    public void startSimulation(Date startTime, Date stopTime, double speedFactor) {
        synchronized (jobs) {
            logger.trace("Starting simulation @ {} until {} with factor {}", startTime, stopTime, speedFactor);
            Job<?>[] oldJobs = jobs.toArray(new Job[jobs.size()]);
            jobs.clear();

            for (Job<?> job : oldJobs) {
                job.reschedule(startTime.getTime());
                jobs.add(job);
            }

            if (stopTime == null) {
                simulationClock.start(startTime.getTime(), speedFactor);
            } else {
                simulationClock.start(startTime.getTime(), stopTime.getTime(), speedFactor);
            }

            jobs.notifyAll();
        }
    }

    @Override
    public void stopSimulation() {
        synchronized (jobs) {
            logger.trace("Signaling the end of the simulation @ {}", simulationClock.getCurrentTimeMillis());
            simulationClock.stop();
            jobs.notifyAll();
        }
    }

    @Override
    public void changeSpeedFactor(double newSpeedFactor) {
        simulationClock.changeSpeedFactor(newSpeedFactor);
    }

    @Override
    public void pause() {
        logger.trace("Pause @ {}", simulationClock.getCurrentTimeMillis());
        simulationClock.pause();
    }

    @Override
    public void unpause() {
        synchronized (jobs) {
            logger.trace("Unpause @ {}", simulationClock.getCurrentTimeMillis());
            simulationClock.unpause();
            jobs.notifyAll();
        }
    }

    @Override
    public Simulation.State getSimulationClockState() {
        return simulationClock.getState();
    }
}
