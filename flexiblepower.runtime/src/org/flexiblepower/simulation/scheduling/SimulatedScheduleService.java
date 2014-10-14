package org.flexiblepower.simulation.scheduling;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.flexiblepower.simulation.Simulation;
import org.flexiblepower.time.TimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;

@Component(provide = { ScheduledExecutorService.class, TimeService.class, Simulation.class })
public class SimulatedScheduleService implements ScheduledExecutorService, TimeService, Simulation, Runnable {
    private static final Logger log = LoggerFactory.getLogger(SimulatedScheduleService.class);

    private volatile boolean running;
    private final Thread thread;
    private final PriorityQueue<Job<?>> jobs = new PriorityQueue<Job<?>>();
    private final SimulationClock simulationClock = new SimulationClock();

    public SimulatedScheduleService() {
        thread = new Thread(this, "SimulatedScheduleService");
    }

    @Activate
    public void activate() {
        running = true;
        thread.start();
    }

    @Deactivate
    public void deactivate() {
        running = false;
        synchronized (this) {
            notifyAll();
        }
        try {
            thread.join();
        } catch (InterruptedException e) {
        }
    }

    // Calculating time stuff

    private long getNextJobTime() {
        return jobs.isEmpty() ? Long.MAX_VALUE : jobs.peek().getTimeOfNextRun();
    }

    private volatile boolean isWaiting = false;
    private volatile long currentTime = 0;

    @Override
    public long getCurrentTimeMillis() {
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
    public Date getTime() {
        return new Date(getCurrentTimeMillis());
    }

    private synchronized void addJob(Job<?> job) {
        jobs.add(job);
        notifyAll();
    }

    // Adding jobs

    @Override
    public void execute(Runnable command) {
        addJob(Job.create(command, this, getCurrentTimeMillis(), 0));
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        Job<?> job = Job.create(command, this, getCurrentTimeMillis() + TimeUnit.MILLISECONDS.convert(delay, unit), 0);
        addJob(job);
        return job;
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        Job<V> job = Job.create(callable, this, getCurrentTimeMillis() + TimeUnit.MILLISECONDS.convert(delay, unit), 0);
        addJob(job);
        return job;
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        Job<?> job = Job.create(command,
                                this,
                                getCurrentTimeMillis() + TimeUnit.MILLISECONDS.convert(initialDelay, unit),
                                TimeUnit.MILLISECONDS.convert(period, unit));
        addJob(job);
        return job;
    }

    // The main run method
    @Override
    public void run() {
        while (running) {
            if (simulationClock.isRunning() || simulationClock.isStopping()) {
                long now = simulationClock.getCurrentTimeMillis();
                log.trace("Simulation step {}", now);
                long waitTime = getNextJobTime() - now;
                if (waitTime <= 0) {
                    Job<?> job = jobs.remove();
                    currentTime = Math.max(currentTime, job.getTimeOfNextRun());
                    log.trace("Executing job {}", job);
                    job.run();
                    if (!job.isDone()) {
                        jobs.add(job);
                        log.trace("Rescheduling job {}", job);
                    }
                } else if (simulationClock.isStopping()) {
                    log.trace("Stopping simulation clock");
                    simulationClock.stop();
                } else {
                    long sleepTime = (long) (waitTime / simulationClock.getSpeedFactor());
                    log.trace("Sleeping {}ms until next job", sleepTime);
                    try {
                        synchronized (this) {
                            if (sleepTime > 0) {
                                isWaiting = true;
                                wait(sleepTime);
                            }
                        }
                    } catch (final InterruptedException ex) {
                    }
                    isWaiting = false;
                }
            } else {
                // Wait for simulation start
                try {
                    synchronized (this) {
                        wait();
                        currentTime = simulationClock.getSimulationStartTime();
                    }
                } catch (InterruptedException e) {
                }
            }
        }
    }

    // Removing jobs

    synchronized void remove(Job<?> job) {
        jobs.remove(job);

        notifyAll();
    }

    // Start or stop the simulation

    @Override
    public synchronized void startSimulation(Date startTime, double speedFactor) {
        startSimulation(startTime, null, speedFactor);
    }

    @Override
    public synchronized void startSimulation(Date startTime, Date stopTime, double speedFactor) {
        log.trace("Starting simulation @ {} until {} with factor {}", startTime, stopTime, speedFactor);
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

        notifyAll();
    }

    @Override
    public synchronized void stopSimulation() {
        log.trace("Signaling the end of the simulation @ {}", simulationClock.getCurrentTimeMillis());
        simulationClock.stop();
    }

    @Override
    public synchronized void changeSpeedFactor(double newSpeedFactor) {
        if (!simulationClock.isStopped()) {
            simulationClock.changeSpeedFactor(newSpeedFactor);
        }
    }

    @Override
    public synchronized void pause() {
        log.trace("Pause @ {}", simulationClock.getCurrentTimeMillis());
        simulationClock.pause();
    }

    @Override
    public synchronized void unpause() {
        log.trace("Unpause @ {}", simulationClock.getCurrentTimeMillis());
        simulationClock.unpause();
        notifyAll();
    }

    @Override
    public synchronized Simulation.State getSimulationClockState() {
        return simulationClock.getState();
    }

    @Override
    public boolean awaitTermination(long arg0, TimeUnit arg1) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> arg0) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T>
            List<Future<T>>
            invokeAll(Collection<? extends Callable<T>> arg0, long arg1, TimeUnit arg2) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> arg0) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T>
            T
            invokeAny(Collection<? extends Callable<T>> arg0, long arg1, TimeUnit arg2) throws InterruptedException,
                                                                                       ExecutionException,
                                                                                       TimeoutException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isShutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTerminated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Future<T> submit(Callable<T> arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<?> submit(Runnable arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Future<T> submit(Runnable arg0, T arg1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        // The scheduler already makes sure that there are no parallel executions of the same runnable, so for
        // simplicity we chose to give both methods the same implementation
        return scheduleAtFixedRate(command, initialDelay, delay, unit);
    }
}
