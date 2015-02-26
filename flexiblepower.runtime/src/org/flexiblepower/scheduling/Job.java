package org.flexiblepower.scheduling;

import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Job<V> implements ScheduledFuture<V> {
    private final static Logger logger = LoggerFactory.getLogger(Job.class);

    public static <V> Job<V> create(final Runnable runnable,
                                    final V result,
                                    final AbstractScheduler scheduler,
                                    long timeOfNextRun,
                                    long timeStep) {
        return new Job<V>(new Callable<V>() {
            @Override
            public V call() throws Exception {
                runnable.run();
                return result;
            }

            @Override
            public String toString() {
                return runnable.toString();
            }
        }, scheduler, timeOfNextRun, timeStep);
    }

    public static <V> Job<V> create(final Callable<V> callable,
                                    final AbstractScheduler scheduler,
                                    long timeOfNextRun,
                                    long timeStep) {
        return new Job<V>(callable, scheduler, timeOfNextRun, timeStep);
    }

    private final Callable<V> callable;
    private final AbstractScheduler scheduler;

    private volatile V result;
    private volatile Exception exception;

    // Both of these are is milliseconds
    private volatile long timeOfNextRun, timeStep;
    private volatile boolean cancelled;

    private Job(Callable<V> callable, AbstractScheduler scheduler, long timeOfNextRun, long timeStep) {
        this.callable = callable;
        this.scheduler = scheduler;

        this.timeOfNextRun = timeOfNextRun;
        this.timeStep = timeStep;

        result = null;
        exception = null;
        cancelled = false;
    }

    public long getTimeOfNextRun() {
        return timeOfNextRun;
    }

    public long getTimeStep() {
        return timeStep;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long delay = timeOfNextRun - scheduler.currentTimeMillis();
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
        if (timeOfNextRun > 0) {
            cancelled = true;
            timeOfNextRun = 0;
            timeStep = 0;
            scheduler.remove(this, mayInterruptIfRunning);
        }
        return true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return timeOfNextRun == 0;
    }

    public synchronized void run() {
        try {
            result = callable.call();
        } catch (Exception e) {
            logger.warn("Exception during execution of job", e);
            exception = e;
        }
        notifyAll();

        if (timeStep > 0) {
            timeOfNextRun += timeStep;
            logger.trace("Rescheduled {}", this);
        } else if (timeStep < 0) {
            timeOfNextRun = scheduler.currentTimeMillis() + timeStep;
            logger.trace("Rescheduled {}", this);
        } else {
            timeOfNextRun = 0;
            logger.trace("Unscheduled {}", this);
        }
    }

    @Override
    public synchronized V get() throws InterruptedException, ExecutionException {
        while (!isDone()) {
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
        long waitUntil = scheduler.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(timeout, unit);
        long waitTime = waitUntil - scheduler.currentTimeMillis();
        while (!isDone() && waitTime > 0) {
            this.wait(waitTime);
            waitTime = waitUntil - scheduler.currentTimeMillis();
        }

        if (exception != null) {
            throw new ExecutionException(exception);
        } else if (!isDone()) {
            throw new TimeoutException();
        } else {
            return result;
        }
    }

    public void reschedule(long time) {
        if (timeOfNextRun > time) {
            timeOfNextRun = time;
            logger.trace("Rescheduled {}", this);
        }
    }

    @Override
    public String toString() {
        return "Job (" + callable.toString() + ") nextRun: " + timeOfNextRun;
    }
}
