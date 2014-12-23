package org.flexiblepower.simulation.test;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.flexiblepower.simulation.Simulation;
import org.flexiblepower.simulation.scheduling.SimulatedScheduleService;

public class SchedulerTest extends TestCase {
    public void testRecurringTask() throws InterruptedException {
        SimulatedScheduleService scheduler = new SimulatedScheduleService();
        scheduler.activate();

        final AtomicInteger runCounter = new AtomicInteger();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runCounter.incrementAndGet();
            }
        }, 0, 1, TimeUnit.SECONDS);

        Date startDate = new Date(1325376000000L); // 01-01-2012 00:00:00.000
        Date endDate = new Date(1325376059999L); // 01-01-2012 00:00:59.999

        runCounter.set(0);

        scheduler.startSimulation(startDate, endDate, 60);
        long startTime = System.currentTimeMillis();
        while (scheduler.getSimulationClockState() != Simulation.State.STOPPED) {
            Thread.sleep(10);
        }
        long duration = System.currentTimeMillis() - startTime;

        assertEquals(60, runCounter.get());
        System.out.printf("Running took %dms, expected around 1 sec%n", duration);
        assertTrue(duration > 950 && duration < 1150);
    }
}
