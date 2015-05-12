package org.flexiblepower.scheduling;

import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Job<V> implements ScheduledFuture<V> {
    private final static Logger logger = LoggerFactory.getLogger(Job.class);

    public static <V> Job<V> create(final Runnable runnable,
                                    final V result,
                                    final AbstractScheduler scheduler,
                                    long timeOfNextRun,
                                    long timeStep,
                                    AtomicLong serialGenerator) {
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
        }, scheduler, timeOfNextRun, timeStep, serialGenerator.getAndIncrement());
    }

    public static <V> Job<V> create(final Callable<V> callable,
                                    final AbstractScheduler scheduler,
                                    long timeOfNextRun,
                                    long timeStep,
                                    AtomicLong serialGenerator) {
        return new Job<V>(callable, scheduler, timeOfNextRun, timeStep, serialGenerator.getAndIncrement());
    }

    private final Callable<V> callable;
    private final AbstractScheduler scheduler;

    private volatile V result;
    private volatile Throwable exception;

    // Serial number for the jobs to distinguish between them for sorting
    private final long serial;

    // Both of these are is milliseconds
    private volatile long timeOfNextRun, timeStep;
    private volatile boolean cancelled;

    private Job(Callable<V> callable, AbstractScheduler scheduler, long timeOfNextRun, long timeStep, long serial) {
        this.callable = callable;
        this.scheduler = scheduler;

        this.timeOfNextRun = timeOfNextRun;
        this.timeStep = timeStep;
        this.serial = serial;

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
            } else if (serial < job.serial) {
                return -1;
            } else if (serial > job.serial) {
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
        } catch (Throwable e) {
            logger.warn("Exception during execution of job", e);
            exception = e;
        }
        notifyAll();

        if (timeStep > 0) {
            reschedule(timeOfNextRun + timeStep);
        } else if (timeStep < 0) {
            reschedule(scheduler.currentTimeMillis() - timeStep);
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
        timeOfNextRun = time;
        logger.trace("Rescheduled {}", this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Job (").append(callable).append(')');
        if (timeOfNextRun > 0) {
            sb.append(" nextRun: ").append(timeOfNextRun);
        } else if (cancelled) {
            sb.append(" cancelled");
        } else {
            sb.append(" done");
        }

        if (timeStep > 0) {
            sb.append(", scheduled at fixed rate ").append(timeStep).append("ms");
        } else if (timeStep < 0) {
            sb.append(", scheduled with delay ").append(timeStep).append("ms");
        }

        return sb.toString();
    }
}
