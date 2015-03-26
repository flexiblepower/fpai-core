package org.flexiblepower.runtime.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;

import org.flexiblepower.context.FlexiblePowerContext;
import org.flexiblepower.scheduling.AbstractScheduler;
import org.flexiblepower.time.TimeService;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;

@Component(servicefactory = true, provide = { FlexiblePowerContext.class,
                                             ScheduledExecutorService.class,
                                             TimeService.class })
public class RuntimeContext extends AbstractScheduler implements ScheduledExecutorService {
    private Bundle bundle;

    @Activate
    public void activate(ComponentContext context) {
        bundle = context.getUsingBundle();
        start(bundle.getSymbolicName());
        logger.info("Created RuntimeContext for bundle: {}", bundle.getSymbolicName());
    }

    @Deactivate
    public void deactivate() {
        logger.info("Stopping RuntimeContext for bundle: {}", bundle.getSymbolicName());
        stop();
        logger.debug("Stopped RuntimeContext for bundle: {}", bundle.getSymbolicName());
    }

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public Date currentTime() {
        return new Date(currentTimeMillis());
    }

    // These methods are from the ScheduledExecutorService and are provided for backwards compatibility with drivers
    @Override
    public void shutdown() {
        throw new IllegalAccessError("You cannot call any lifecycle methods on the RuntimeContext");
    }

    @Override
    public List<Runnable> shutdownNow() {
        throw new IllegalAccessError("You cannot call any lifecycle methods on the RuntimeContext");
    }

    @Override
    public boolean isShutdown() {
        throw new IllegalAccessError("You cannot call any lifecycle methods on the RuntimeContext");
    }

    @Override
    public boolean isTerminated() {
        throw new IllegalAccessError("You cannot call any lifecycle methods on the RuntimeContext");
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        throw new IllegalAccessError("You cannot call any lifecycle methods on the RuntimeContext");
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        List<Future<T>> list = new ArrayList<Future<T>>(tasks.size());
        for (Callable<T> task : tasks) {
            list.add(submit(task));
        }
        return list;
    }

    @Override
    public <T>
            List<Future<T>>
            invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        List<Future<T>> list = new ArrayList<Future<T>>(tasks.size());
        for (Callable<T> task : tasks) {
            list.add(schedule(task, timeout, unit));
        }
        return list;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("The RuntimeContext does not support this functionality");
    }

    @Override
    public <T>
            T
            invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException,
                                                                                           ExecutionException,
                                                                                           TimeoutException {
        throw new UnsupportedOperationException("The RuntimeContext does not support this functionality");
    }

    @Override
    public void execute(Runnable command) {
        submit(command);
    }

    private static Measurable<Duration> measure(long delay, TimeUnit unit) {
        return Measure.valueOf(unit.convert(delay, TimeUnit.MILLISECONDS), MS);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return schedule(command, measure(delay, unit));
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return schedule(callable, measure(delay, unit));
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return scheduleAtFixedRate(command, measure(initialDelay, unit), measure(period, unit));
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return scheduleWithFixedDelay(command, measure(initialDelay, unit), measure(delay, unit));
    }
}
