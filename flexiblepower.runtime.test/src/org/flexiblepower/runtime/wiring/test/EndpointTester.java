package org.flexiblepower.runtime.wiring.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.flexiblepower.messaging.Connection;
import org.flexiblepower.messaging.ConnectionManager;
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

    @Port(name = "anyIn", sends = String.class, accepts = Object.class)
    public class EndpointA implements Endpoint {
        @Override
        public MessageHandler onConnect(Connection connection) {
            return null;
        }
    }

    @Port(name = "anyOut", sends = Object.class, accepts = String.class)
    public class EndpointB implements Endpoint {
        @Override
        public MessageHandler onConnect(Connection connection) {
            return null;
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

    @Override
    protected void setUp() throws Exception {
        BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
        for (Endpoint endpoint : new Endpoint[] { new EndpointA(),
                                                  new EndpointB(),
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

    public void testConnected() throws InterruptedException {
        ConnectionManager connectionManager = connectionManagerTracker.waitForService(10000);
        System.out.println(connectionManager.getEndpointPorts());
    }
}
