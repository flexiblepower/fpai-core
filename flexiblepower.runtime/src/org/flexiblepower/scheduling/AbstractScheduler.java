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

public abstract class AbstractScheduler implements FlexiblePowerContext, Runnable {
    public static final Unit<Duration> MS = SI.MILLI(SI.SECOND);

    public static final ThreadGroup SCHEDULER_GROUP = new ThreadGroup("FlexiblePower Scheduling");

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final AtomicBoolean running;
    protected final PriorityQueue<Job<?>> jobs;

    private volatile Thread thread;

    public AbstractScheduler() {
        running = new AtomicBoolean(false);
        jobs = new PriorityQueue<Job<?>>();
    }

    public void start(String name) {
        if (running.compareAndSet(false, true)) {
            thread = new Thread(SCHEDULER_GROUP, this, "Scheduler thread for " + name);
            thread.setDaemon(true);
            thread.start();
        }
    }

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

    <T> void remove(Job<T> job) {
        synchronized (jobs) {
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

    protected long getNextJobTime() {
        return jobs.isEmpty() ? Long.MAX_VALUE : jobs.peek().getTimeOfNextRun();
    }

    @Override
    public void run() {
        synchronized (jobs) {
            while (running.get()) {
                long now = currentTimeMillis();
                long waitTime = getNextJobTime() - now;
                if (waitTime <= 0) {
                    Job<?> job = jobs.remove();
                    logger.trace("{} is executing job {}", thread.getName(), job);
                    job.run();
                    if (!job.isDone()) {
                        jobs.add(job);
                    }
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
}
