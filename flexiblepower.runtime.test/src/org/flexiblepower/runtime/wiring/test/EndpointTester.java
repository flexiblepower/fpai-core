package org.flexiblepower.runtime.wiring.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.flexiblepower.messaging.Connection;
import org.flexiblepower.messaging.ConnectionManager;
import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.ConnectionManager.MatchingPorts;
import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.messaging.MessageHandler;
import org.flexiblepower.messaging.Port;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class EndpointTester extends TestCase {
    private final List<ServiceRegistration<Endpoint>> registrations = new ArrayList<ServiceRegistration<Endpoint>>();
    private ServiceTracker<ConnectionManager, ConnectionManager> connectionManagerTracker;

    public abstract class TestEndpoint implements Endpoint {
        private final String expectedPortName;
        private final Object sendMessage, expectedMessage;

        public TestEndpoint(String expectedPortName, Object sendMessage, Object expectedMessage) {
            this.expectedPortName = expectedPortName;
            this.sendMessage = sendMessage;
            this.expectedMessage = expectedMessage;
        }

        boolean connected = false;

        @Override
        public MessageHandler onConnect(Connection connection) {
            assertEquals(expectedPortName, connection.getPort().name());
            System.out.println("Connection started on " + getClass().getSimpleName() + ": " + connection);
            connection.sendMessage(sendMessage);
            connected = true;

            return new MessageHandler() {
                private boolean gotMessage = false;

                @Override
                public void handleMessage(Object message) {
                    System.out.println(TestEndpoint.this.getClass().getSimpleName() + " got message [" + message + "]");
                    assertEquals(expectedMessage, message);
                    assertTrue(Thread.currentThread().getName().contains(TestEndpoint.this.getClass().getSimpleName()));
                    gotMessage = true;
                }

                @Override
                public void disconnected() {
                    assertTrue(gotMessage);
                    System.out.println("Connection ended on EndpointA");
                    connected = false;
                }
            };
        }

        public void assertConnected() {
            assertTrue(connected);
        }

        public void assertNotConnected() {
            assertFalse(connected);
        }
    }

    @Port(name = "anyIn", sends = String.class, accepts = Object.class)
    public class EndpointA extends TestEndpoint {
        public EndpointA() {
            super("anyIn", "Hello World", 5L);
        }
    }

    @Port(name = "anyOut", sends = Object.class, accepts = String.class)
    public class EndpointB extends TestEndpoint {
        public EndpointB() {
            super("anyOut", 5L, "Hello World");
        }
    }

    @Port(name = "anyInOut", sends = Object.class, accepts = Object.class)
    public class EndpointC implements Endpoint {
        @Override
        public MessageHandler onConnect(Connection connection) {
            return null;
        }
    }

    @Port(name = "stringInOut", sends = String.class, accepts = String.class)
    public class EndpointD implements Endpoint {
        @Override
        public MessageHandler onConnect(Connection connection) {
            return null;
        }
    }

    @Port(name = "stringInOut", sends = String.class, accepts = String.class)
    public class EndpointE implements Endpoint {
        @Override
        public MessageHandler onConnect(Connection connection) {
            return null;
        }
    }

    private final EndpointA endpointA = new EndpointA();
    private final EndpointB endpointB = new EndpointB();

    @Override
    protected void setUp() throws Exception {
        BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
        for (Endpoint endpoint : new Endpoint[] { endpointA,
                                                 endpointB,
                                                 new EndpointC(),
                                                 new EndpointD(),
                                                 new EndpointE() }) {
            registrations.add(context.registerService(Endpoint.class, endpoint, null));
        }

        connectionManagerTracker = new ServiceTracker<ConnectionManager, ConnectionManager>(context,
                ConnectionManager.class,
                null);
        connectionManagerTracker.open();
    }

    @Override
    protected void tearDown() throws Exception {
        connectionManagerTracker.close();

        for (ServiceRegistration<Endpoint> reg : registrations) {
            reg.unregister();
        }
        registrations.clear();
    }

    public void testConnected() throws InterruptedException, IOException {
        ConnectionManager connectionManager = connectionManagerTracker.waitForService(10000);
        EndpointPort portA = null;
        for (EndpointPort port : connectionManager) {
            if (port.getEndpoint() == endpointA) {
                portA = port;
            }
        }

        assertNotNull(portA);
        assertEquals(4, portA.getMatchingPorts().size());

        EndpointPort portB = null;
        MatchingPorts connAB = null;
        for (MatchingPorts match : portA.getMatchingPorts()) {
            EndpointPort otherPort = match.getOtherEnd(portA);
            if (otherPort.getEndpoint() == endpointB) {
                portB = otherPort;
                connAB = match;
            }
        }

        assertNotNull(portB);
        assertNotNull(connAB);
        assertFalse(connAB.isConnected());

        connAB.connect();
        assertTrue(connAB.isConnected());
        endpointA.assertConnected();
        endpointB.assertConnected();

        Thread.sleep(1000);

        connAB.disconnect();

        assertFalse(connAB.isConnected());
        endpointA.assertNotConnected();
        endpointB.assertNotConnected();
    }
}
