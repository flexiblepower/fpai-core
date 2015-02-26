package org.flexiblepower.scheduling;

import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.flexiblepower.context.FlexiblePowerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link AbstractScheduler} is a single-thread scheduler that implements most of the {@link FlexiblePowerContext}
 * methods, except the current time.
 */
public abstract class AbstractScheduler implements FlexiblePowerContext, Runnable {
    /**
     * The unit of milliseconds. Used as the base unit to translate {@link Measurable}s into a row amount.
     */
    public static final Unit<Duration> MS = SI.MILLI(SI.SECOND);

    /**
     * The {@link ThreadGroup} on which all activated {@link Thread}s are added.
     */
    public static final ThreadGroup SCHEDULER_GROUP = new ThreadGroup("FlexiblePower Scheduling");

    /**
     * The logger that will be used for all logging. Subclasses should reuse this.
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * The boolean check of the thread should still be running. This is set to true during the {@link #start(String)}
     * call and set to false during the {@link #stop()} call. When extending the {@link #run()} method, this boolean
     * should be the check for the main loop.
     */
    protected final AtomicBoolean running;

    /**
     * The {@link PriorityQueue} of {@link Job}s that are scheduled.
     */
    protected final PriorityQueue<Job<?>> jobs;

    private volatile Thread thread;

    /**
     * Creates a new {@link AbstractScheduler}. The {@link #start(String)} method should be called to make sure that a
     * thread is running.
     */
    public AbstractScheduler() {
        running = new AtomicBoolean(false);
        jobs = new PriorityQueue<Job<?>>();
    }

    /**
     * Starts the thread that will execute the jobs. When jobs were scheduled before this method has been called, these
     * will executed at the moment they are planned (or immediately if that moment is in history).
     *
     * @param name
     *            The name of the scheduler that is used in the thread name.
     */
    public void start(String name) {
        if (running.compareAndSet(false, true)) {
            thread = new Thread(SCHEDULER_GROUP, this, "Scheduler thread for " + name);
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * Stops the execution thread. Notice: this will cancel all jobs that are still scheduled.
     */
    public void stop() {
        synchronized (jobs) {
            running.set(false);
            jobs.notifyAll();
        }

        try {
            thread.join();
        } catch (InterruptedException e) {
        } finally {
            thread = null;
        }
    }

    @Override
    public abstract long currentTimeMillis();

    private <T> Job<T> addJob(Job<T> job) {
        synchronized (jobs) {
            jobs.add(job);
            jobs.notifyAll();
            return job;
        }
    }

    <T> void remove(Job<T> job, boolean mayInterrupt) {
        synchronized (jobs) {
            if (mayInterrupt && job == currentJob) {
                thread.interrupt();
            }

            jobs.remove(job);
            jobs.notifyAll();
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        logger.trace("submit(callable: {})", task);
        return addJob(Job.create(task, this, currentTimeMillis(), 0));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        logger.trace("submit(runnable: {}, result: {})", task, result);
        return addJob(Job.create(task, result, this, currentTimeMillis(), 0));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return submit(task, null);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, Measurable<Duration> delay) {
        logger.trace("schedule(runnable: {}, delay: {})", command, delay);
        long ms = delay.longValue(MS);
        return addJob(Job.create(command, null, this, currentTimeMillis() + ms, 0));
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, Measurable<Duration> delay) {
        logger.trace("schedule(callable: {}, delay: {})", callable, delay);
        long ms = delay.longValue(MS);
        return addJob(Job.create(callable, this, currentTimeMillis() + ms, 0));
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                  Measurable<Duration> initialDelay,
                                                  Measurable<Duration> period) {
        logger.trace("scheduleAtFixedRate(runnable: {}, initialDelay: {}, period: {})", command, initialDelay, period);
        return addJob(Job.create(command,
                                 null,
                                 this,
                                 currentTimeMillis() + initialDelay.longValue(MS),
                                 period.longValue(MS)));
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                                                     Measurable<Duration> initialDelay,
                                                     Measurable<Duration> delay) {
        logger.trace("scheduleWithFixedDelay(runnable: {}, initialDelay: {}, delay: {})", command, initialDelay, delay);
        return addJob(Job.create(command,
                                 null,
                                 this,
                                 currentTimeMillis() + initialDelay.longValue(MS),
                                 -delay.longValue(MS)));
    }

    /**
     * @return The timestamp at which the next job should be run or {@link Long#MAX_VALUE} when there is no job
     *         scheduled.
     */
    protected long getNextJobTime() {
        return jobs.isEmpty() ? Long.MAX_VALUE : jobs.peek().getTimeOfNextRun();
    }

    private volatile long startOfCurrentJob;
    private volatile Job<?> currentJob;

    @Override
    public void run() {
        synchronized (jobs) {
            while (running.get()) {
                long now = currentTimeMillis();
                long waitTime = getNextJobTime() - now;
                if (waitTime <= 0) {
                    currentJob = jobs.remove();
                    startOfCurrentJob = now;
                    logger.trace("{} is executing job {}", thread.getName(), currentJob);
                    currentJob.run();
                    if (!currentJob.isDone()) {
                        jobs.add(currentJob);
                    }
                    currentJob = null;
                } else {
                    logger.trace("{} is sleeping {}ms until next job", thread.getName(), waitTime);
                    try {
                        jobs.wait(waitTime);
                        logger.trace("{} wake up", thread.getName());
                    } catch (final InterruptedException ex) {
                        logger.debug("{} interrupted", thread.getName());
                    }
                }
            }

            while (!jobs.isEmpty()) {
                jobs.peek().cancel(false);
            }
        }
    }

    /**
     * Returns the number of milliseconds that the current job is running. This will return -1 when there is no active
     * job (so the execution thread is idle), otherwise it returns the difference between the {@link #currentTime()} and
     * the time at the moment the job was starting. Notice: when the computer time has changed between these 2 moment,
     * this method could return strange result and even negative numbers.
     *
     * @return the number of milliseconds that the current job is running.
     */
    public long getCurrentExecutionTime() {
        if (currentJob != null) {
            return currentTimeMillis() - startOfCurrentJob;
        } else {
            return -1;
        }
    }
}
