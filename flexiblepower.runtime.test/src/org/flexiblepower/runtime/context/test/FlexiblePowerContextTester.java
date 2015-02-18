package org.flexiblepower.runtime.context.test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.measure.Measure;
import javax.measure.unit.SI;

import junit.framework.TestCase;

import org.flexiblepower.context.FlexiblePowerContext;
import org.flexiblepower.context.Scheduler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlexiblePowerContextTester extends TestCase {
    private static final Logger logger = LoggerFactory.getLogger(FlexiblePowerContext.class);

    private ServiceTracker<FlexiblePowerContext, FlexiblePowerContext> contextTracker;

    private final BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();

    private FlexiblePowerContext getContext() throws InterruptedException {

        contextTracker = new ServiceTracker<FlexiblePowerContext, FlexiblePowerContext>(bundleContext,
                                                                                        FlexiblePowerContext.class,
                                                                                        null);
        contextTracker.open();
        FlexiblePowerContext service = contextTracker.waitForService(10000);

        return service;
    }

    public void testContext() throws InterruptedException {
        FlexiblePowerContext ctx = getContext();

        assertTrue("Time should be system time (approximately)",
                   Math.abs(System.currentTimeMillis() - ctx.currentTimeMillis()) < 20);
        assertTrue("Date should be system time (approximately)",
                   Math.abs(System.currentTimeMillis() - ctx.currentTime().getTime()) < 20);
        assertFalse("The runtime should not be a simulation", ctx.isSimulation());
        assertNull("The runtime should not be a simulation", ctx.getSimulation());
    }

    public void testSchedulerThread() throws Exception {
        FlexiblePowerContext ctx = getContext();
        Scheduler scheduler = ctx.getScheduler();

        logger.info("Adding 100 runnables");
        Queue<Future<?>> futures = new LinkedList<Future<?>>();
        for (int ix = 0; ix < 100; ix++) {
            final boolean firstThread = ix == 0;
            futures.add(scheduler.submit(new Runnable() {
                @Override
                public void run() {
                    String threadName = Thread.currentThread().getName();
                    if (firstThread) {
                        logger.info("TreadName: {}", threadName);
                    }
                    assertTrue("Any scheduled stuff should be run on the 1 local thread",
                               threadName.contains(bundleContext.getBundle().getSymbolicName()));
                }
            }));
        }

        for (Future<?> future = futures.poll(); future != null; future = futures.poll()) {
            assertNull(future.get(1, TimeUnit.SECONDS)); // Should not take long!
        }

        final AtomicInteger counter = new AtomicInteger();

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                counter.incrementAndGet();
            }
        }, Measure.valueOf(0.05, SI.SECOND), Measure.valueOf(0.1, SI.SECOND));

        Thread.sleep(4000);
        assertEquals(40, counter.get());
        Thread.sleep(1000);
        assertEquals(50, counter.get());
        future.cancel(false);
        Thread.sleep(1000);
        assertEquals(50, counter.get());
        assertNull(future.get(0, TimeUnit.SECONDS));
    }
}
