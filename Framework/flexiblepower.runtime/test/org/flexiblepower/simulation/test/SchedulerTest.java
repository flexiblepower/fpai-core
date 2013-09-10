package org.flexiblepower.simulation.test;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.flexiblepower.simulation.schedule.SimulatedScheduleService;

public class SchedulerTest extends TestCase {
    public void testRecurringTask() {
        SimulatedScheduleService scheduler = new SimulatedScheduleService();
        scheduler.activate();

        final AtomicInteger runCounter = new AtomicInteger();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runCounter.incrementAndGet();
            }
        }, 0, 1, TimeUnit.SECONDS);

        Date d1_1_2012 = new Date(1325376000000l);

        for (int i = 1; i < 50; i++) {
            runCounter.set(0);

            scheduler.startSimulation(d1_1_2012, i);
            try {
                Thread.sleep(9500 / i);
            } catch (InterruptedException e) {
            }
            scheduler.stopSimulation();

            Assert.assertEquals(10, runCounter.get());
        }

    }
}
