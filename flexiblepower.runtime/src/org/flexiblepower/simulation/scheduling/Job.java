package org.flexiblepower.simulation.scheduling;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Job<V> implements ScheduledFuture<V> {
    private final static Logger logger = LoggerFactory.getLogger(Job.class);

    public static Job<Object> create(final Runnable runnable,
                                     final SimulatedScheduleService scheduleService,
                                     long timeOfNextRun,
                                     long timeStep) {
        return new Job<Object>(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                runnable.run();
                return null;
            }

            @Override
            public String toString() {
                return runnable.toString();
            }
        }, scheduleService, timeOfNextRun, timeStep);
    }

    public static <V> Job<V> create(final Callable<V> callable,
                                    final SimulatedScheduleService scheduleService,
                                    long timeOfNextRun,
                                    long timeStep) {
        return new Job<V>(callable, scheduleService, timeOfNextRun, timeStep);
    }

    private final Callable<V> callable;
    private final SimulatedScheduleService scheduleService;

    private volatile V result;
    private volatile Exception exception;

    // Both of these are is milliseconds
    private volatile long timeOfNextRun, timeStep;

    private Job(Callable<V> callable, SimulatedScheduleService scheduleService, long timeOfNextRun, long timeStep) {
        this.callable = callable;
        this.scheduleService = scheduleService;

        this.timeOfNextRun = timeOfNextRun;
        this.timeStep = timeStep;

        result = null;
        exception = null;
    }

    public long getTimeOfNextRun() {
        return timeOfNextRun;
    }

    public long getTimeStep() {
        return timeStep;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long delay = timeOfNextRun - scheduleService.getCurrentTimeMillis();
        if (delay < 0) {
            delay = 0;
        }
        return unit.convert(delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (o.getClass() != getClass()) {
            return -1;
        } else {
            Job<?> job = (Job<?>) o;
            if (timeOfNextRun < job.timeOfNextRun) {
                return -1;
            } else if (timeOfNextRun > job.timeOfNextRun) {
                return 1;
            } else {
                return hashCode() - job.hashCode();
            }
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        timeOfNextRun = 0;
        timeStep = 0;
        scheduleService.remove(this);
        return true;
    }

    @Override
    public boolean isCancelled() {
        return isDone();
    }

    @Override
    public boolean isDone() {
        return timeOfNextRun == 0;
    }

    public synchronized void run() {
        logger.debug("Running Job " + this);
        try {
            result = callable.call();
        } catch (Exception e) {
            logger.warn("Exception during execution of job", e);
            exception = e;
        }
        notifyAll();

        if (timeStep > 0) {
            timeOfNextRun += timeStep;
            logger.debug("Rescheduled job " + this + " to " + timeOfNextRun);
        } else {
            timeOfNextRun = 0;
            logger.debug("Unscheduled job " + this);
        }
    }

    @Override
    public synchronized V get() throws InterruptedException, ExecutionException {
        if (!isDone()) {
            this.wait();
        }

        if (exception != null) {
            throw new ExecutionException(exception);
        } else {
            return result;
        }
    }

    @Override
    public synchronized V get(long timeout, TimeUnit unit) throws InterruptedException,
                                                          ExecutionException,
                                                          TimeoutException {
        if (!isDone()) {
            this.wait(TimeUnit.MILLISECONDS.convert(timeout, unit));
        }

        if (exception != null) {
            throw new ExecutionException(exception);
        } else {
            return result;
        }
    }

    public void reschedule(long time) {
        if (timeOfNextRun > time) {
            // if (timeStep > 0) {
            // timeOfNextRun -= ((timeOfNextRun - time) / timeStep) * timeStep;
            // } else {
            // timeOfNextRun = time;
            // }
            timeOfNextRun = time;

            if (logger.isDebugEnabled()) {
                logger.debug("Rescheduled job " + this + " to " + new Date(timeOfNextRun));
            }
        }
    }

    @Override
    public String toString() {
        return "Job (" + callable.toString() + ")";
    }
}
