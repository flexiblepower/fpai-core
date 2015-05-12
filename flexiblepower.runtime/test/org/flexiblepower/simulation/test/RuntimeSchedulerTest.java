package org.flexiblepower.simulation.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.flexiblepower.runtime.context.RuntimeContext;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;

public class RuntimeSchedulerTest extends TestCase {
    private RuntimeContext context;

    @Override
    protected void setUp() throws Exception {
        Bundle bundle = Mockito.mock(Bundle.class);
        ComponentContext cc = Mockito.mock(ComponentContext.class);
        Mockito.when(cc.getUsingBundle()).thenReturn(bundle);
        Mockito.when(bundle.getSymbolicName()).thenReturn("test.bundle");

        context = new RuntimeContext();
        context.activate(cc);
    }

    @Override
    protected void tearDown() throws Exception {
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

    public static class OrderTester implements Runnable {
        private final AtomicInteger sharedCounter;
        private final int expectedCounter;

        public OrderTester(AtomicInteger sharedCounter, int expectedCounter) {
            this.sharedCounter = sharedCounter;
            this.expectedCounter = expectedCounter;
        }

        @Override
        public void run() {
            int current = sharedCounter.getAndIncrement();
            System.out.println("OrderTest, expected = " + expectedCounter + ", but is " + current);
            assertEquals(expectedCounter, current);
        }
    }

    public void testOrderOfExecution() throws Throwable {
        AtomicInteger sharedCounter = new AtomicInteger();

        int COUNT = 10;
        List<OrderTester> testers = new ArrayList<RuntimeSchedulerTest.OrderTester>(COUNT);
        List<Future<?>> futurers = new ArrayList<Future<?>>(COUNT);

        for (int ix = 0; ix < COUNT; ix++) {
            testers.add(new OrderTester(sharedCounter, ix));
        }

        // Now submit them all!
        for (OrderTester tester : testers) {
            futurers.add(context.submit(tester));
        }

        // Now test if they all ran in the correct order
        for (Future<?> future : futurers) {
            try {
                future.get();
            } catch (InterruptedException e) {
                fail("Got an unexpected InterruptedException");
            } catch (ExecutionException e) {
                throw e.getCause();
            }
        }
    }
}
