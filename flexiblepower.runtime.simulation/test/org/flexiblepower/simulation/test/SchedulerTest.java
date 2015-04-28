package org.flexiblepower.simulation.test;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.measure.Measure;
import javax.measure.unit.SI;

import junit.framework.TestCase;

import org.flexiblepower.runtime.context.RuntimeContext;
import org.flexiblepower.simulation.api.Simulation;
import org.flexiblepower.simulation.context.SimulationContext;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;

public class SchedulerTest extends TestCase {
    public void testRecurringTask() throws InterruptedException {
        SimulationContext scheduler = new SimulationContext();
        scheduler.activate();

        final AtomicInteger runCounter = new AtomicInteger();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runCounter.incrementAndGet();
            }
        }, Measure.valueOf(0, SI.SECOND), Measure.valueOf(1, SI.SECOND));

        Date startDate = new Date(1325376000000L); // 01-01-2012 00:00:00.000
        Date endDate = new Date(1325376059999L); // 01-01-2012 00:00:59.999

        runCounter.set(0);

        scheduler.startSimulation(startDate, endDate, 60);
        long startTime = System.currentTimeMillis();
        while (scheduler.getSimulationClockState() != Simulation.State.STOPPED && System.currentTimeMillis() - startTime < 10000) {
            Thread.sleep(10);
        }
        long duration = System.currentTimeMillis() - startTime;
        assertEquals(Simulation.State.STOPPED, scheduler.getSimulationClockState());

        assertEquals(60, runCounter.get());
        System.out.printf("Running took %dms, expected around 1 sec%n", duration);
        assertTrue(duration > 950 && duration < 1150);

        scheduler.deactivate();
    }

    public void testStopping() throws Exception {
        Bundle bundle = Mockito.mock(Bundle.class);
        ComponentContext cc = Mockito.mock(ComponentContext.class);
        Mockito.when(cc.getUsingBundle()).thenReturn(bundle);
        Mockito.when(bundle.getSymbolicName()).thenReturn("test.bundle");

        final RuntimeContext context = new RuntimeContext();
        context.activate(cc);

        Thread stopThread = new Thread() {
            @Override
            public void run() {
                context.deactivate();
            }
        };
        stopThread.setDaemon(true);
        stopThread.start();

        Thread.sleep(1000);
        assertFalse(stopThread.isAlive());
    }
}
