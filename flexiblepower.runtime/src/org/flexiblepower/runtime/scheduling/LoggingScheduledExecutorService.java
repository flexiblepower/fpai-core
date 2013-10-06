package org.flexiblepower.runtime.scheduling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.flexiblepower.time.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;

@Component(provide = { ScheduledExecutorService.class, SchedulerService.class })
public class LoggingScheduledExecutorService extends ScheduledThreadPoolExecutor implements SchedulerService {
    static final Logger logger = LoggerFactory.getLogger(LoggingScheduledExecutorService.class);

    private static class CallableWrapper<T> implements Callable<T> {
        private final Callable<T> wrapped;

        public CallableWrapper(Callable<T> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public T call() {
            try {
                return wrapped.call();
            } catch (Exception ex) {
                logger.error("Error while executing " + wrapped, ex);
                return null;
            }
        }
    }

    private static class RunnableWrapper implements Runnable {
        private final Runnable wrapped;

        public RunnableWrapper(Runnable wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public void run() {
            try {
                wrapped.run();
            } catch (Exception ex) {
                logger.error("Error while executing " + wrapped, ex);
            }
        }
    }

    public LoggingScheduledExecutorService() {
        super(Runtime.getRuntime().availableProcessors() * 2);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(new CallableWrapper<T>(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return super.submit(new RunnableWrapper(task), result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(new RunnableWrapper(task));
    }

    private <T> List<Callable<T>> wrap(Collection<? extends Callable<T>> tasks) {
        List<Callable<T>> result = new ArrayList<Callable<T>>(tasks.size());
        for (Callable<T> task : tasks) {
            result.add(new CallableWrapper<T>(task));
        }
        return result;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return super.invokeAll(wrap(tasks));
    }

    @Override
    public <T>
            List<Future<T>>
            invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return super.invokeAll(wrap(tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return super.invokeAny(wrap(tasks));
    }

    @Override
    public <T>
            T
            invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException,
                                                                                           ExecutionException,
                                                                                           TimeoutException {
        return super.invokeAny(wrap(tasks), timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        super.execute(new RunnableWrapper(command));
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return super.schedule(new RunnableWrapper(command), delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return super.schedule(new CallableWrapper<V>(callable), delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return super.scheduleAtFixedRate(new RunnableWrapper(command), initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return super.scheduleWithFixedDelay(new RunnableWrapper(command), initialDelay, delay, unit);
    }
}
