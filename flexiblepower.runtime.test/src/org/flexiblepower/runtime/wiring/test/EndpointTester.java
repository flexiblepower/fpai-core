package org.flexiblepower.runtime.wiring.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.Connection;
import org.flexiblepower.messaging.ConnectionManager;
import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.ConnectionManager.MatchingPorts;
import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.messaging.MessageHandler;
import org.flexiblepower.messaging.Port;
import org.flexiblepower.messaging.Ports;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class EndpointTester extends TestCase {
    private static final int SLEEP_TIME = 500;

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

    @Port(name = "A-anyIn", sends = String.class, accepts = Object.class)
    public class EndpointA extends TestEndpoint {
        public EndpointA() {
            super("A-anyIn", "Hello World", 5L);
        }
    }

    @Port(name = "B-anyOut", sends = Object.class, accepts = String.class)
    public class EndpointB extends TestEndpoint {
        public EndpointB() {
            super("B-anyOut", 5L, "Hello World");
        }
    }

    @Port(name = "C-anyInOut", sends = Object.class, accepts = Object.class)
    public class EndpointC implements Endpoint {
        @Override
        public MessageHandler onConnect(Connection connection) {
            return null;
        }
    }

    @Port(name = "D-stringInOut", sends = String.class, accepts = String.class)
    public class EndpointD implements Endpoint {
        @Override
        public MessageHandler onConnect(Connection connection) {
            return null;
        }
    }

    @Port(name = "E-stringInOut", sends = String.class, accepts = String.class)
    public class EndpointE implements Endpoint {
        @Override
        public MessageHandler onConnect(Connection connection) {
            return null;
        }
    }

    protected ConnectionManager setupEndpoints(Endpoint... endpoints) throws Exception {
        BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
        for (Endpoint endpoint : endpoints) {
            registrations.add(context.registerService(Endpoint.class, endpoint, null));
        }

        connectionManagerTracker = new ServiceTracker<ConnectionManager, ConnectionManager>(context,
                ConnectionManager.class,
                null);
        connectionManagerTracker.open();

        ConnectionManager connectionManager = connectionManagerTracker.waitForService(1000);
        assertNotNull(connectionManager);
        return connectionManager;
    }

    @Override
    protected void tearDown() throws Exception {
        for (ServiceRegistration<Endpoint> reg : registrations) {
            reg.unregister();
        }
        registrations.clear();

        ConnectionManager connectionManager = connectionManagerTracker.waitForService(1000);
        assertEquals(0, connectionManager.getEndpointPorts().size());

        connectionManagerTracker.close();
    }

    public void testConnected() throws Exception {
        EndpointA a = new EndpointA();
        EndpointB b = new EndpointB();
        EndpointC c = new EndpointC();
        EndpointD d = new EndpointD();
        EndpointE e = new EndpointE();
        ConnectionManager connectionManager = setupEndpoints(a, b, c, d, e);

        EndpointPort portA = getOnly(connectionManager.getEndpointPortsOf(a));
        assertNotNull(portA);
        assertEquals(4, portA.getMatchingPorts().size());

        EndpointPort portB = null;
        MatchingPorts connAB = null;
        for (MatchingPorts match : portA.getMatchingPorts()) {
            EndpointPort otherPort = match.getOtherEnd(portA);
            if (otherPort.getEndpoint() == b) {
                portB = otherPort;
                connAB = match;
            }
        }

        assertNotNull(portB);
        assertNotNull(connAB);
        assertFalse(connAB.isConnected());

        connAB.connect();
        assertTrue(connAB.isConnected());
        a.assertConnected();
        b.assertConnected();

        Thread.sleep(SLEEP_TIME);

        connAB.disconnect();

        assertFalse(connAB.isConnected());
        a.assertNotConnected();
        b.assertNotConnected();
    }

    @Port(name = "TextService",
            accepts = { String.class, Integer.class },
            sends = String.class,
            cardinality = Cardinality.MULTIPLE)
    static class ServerEndpoint implements Endpoint {
        @Override
        public MessageHandler onConnect(final Connection connection) {
            return new MessageHandler() {
                @Override
                public void handleMessage(Object message) {
                    System.out.println(" <-- Received message [" + message + "]");

                    if (message instanceof Integer) {
                        int nr = (Integer) message;
                        String reply = nr + "^2 = " + (nr * nr);
                        System.out.println(" <-- Sending reply [" + reply + "]");
                        connection.sendMessage(reply);
                    } else if (message instanceof String) {
                        String reply = "Hello " + message;
                        System.out.println(" <-- Sending reply [" + reply + "]");
                        connection.sendMessage(reply);
                    } else {
                        fail("Unknown message type: " + message.getClass());
                    }
                }

                @Override
                public void disconnected() {
                }
            };
        }
    }

    @Port(name = "HelloConnection", sends = String.class, accepts = String.class, cardinality = Cardinality.SINGLE)
    static class WorldEndpoint implements Endpoint {
        @Override
        public MessageHandler onConnect(Connection connection) {
            assertEquals("HelloConnection", connection.getPort().name());

            System.out.println(" --> Sending [World] to server");
            connection.sendMessage("World");
            return new MessageHandler() {
                private boolean received = false;

                @Override
                public void handleMessage(Object message) {
                    System.out.println(" --> Received [" + message + "] from server");
                    assertEquals("Hello World", message);
                    received = true;
                }

                @Override
                public void disconnected() {
                    assertTrue(received);
                }
            };
        }
    }

    @Port(name = "SquareConnection", sends = Integer.class, accepts = String.class, cardinality = Cardinality.SINGLE)
    static class SquaringEndpoint implements Endpoint {
        @Override
        public MessageHandler onConnect(Connection connection) {
            assertEquals("SquareConnection", connection.getPort().name());

            System.out.println(" ==> Sending [4] to server");
            connection.sendMessage(4);
            return new MessageHandler() {
                private volatile boolean received = false;

                @Override
                public void handleMessage(Object message) {
                    System.out.println(" ==> Received [" + message + "] from server");
                    assertEquals("4^2 = 16", message);
                    received = true;
                }

                @Override
                public void disconnected() {
                    assertTrue(received);
                }
            };
        }
    }

    public void testMultipleConnections() throws Exception {
        ServerEndpoint serverEndpoint = new ServerEndpoint();
        WorldEndpoint worldEndpoint = new WorldEndpoint();
        SquaringEndpoint squaringEndpoint = new SquaringEndpoint();
        ConnectionManager connectionManager = setupEndpoints(serverEndpoint, worldEndpoint, squaringEndpoint);
        EndpointPort serverPort = getOnly(connectionManager.getEndpointPortsOf(serverEndpoint));

        Set<? extends MatchingPorts> matchingPorts = serverPort.getMatchingPorts();
        assertEquals(2, matchingPorts.size());

        MatchingPorts[] matches = matchingPorts.toArray(new MatchingPorts[2]);

        EndpointPort otherEnd = matches[0].getOtherEnd(serverPort);
        assertTrue(otherEnd.getEndpoint() == worldEndpoint ^ otherEnd.getEndpoint() == squaringEndpoint);
        otherEnd = matches[1].getOtherEnd(serverPort);
        assertTrue(otherEnd.getEndpoint() == worldEndpoint ^ otherEnd.getEndpoint() == squaringEndpoint);

        assertFalse(matches[0].isConnected());
        assertFalse(matches[1].isConnected());

        matches[0].connect();
        matches[1].connect();
        assertTrue(matches[0].isConnected());
        assertTrue(matches[1].isConnected());

        Thread.sleep(SLEEP_TIME);

        matches[0].disconnect();
        matches[1].disconnect();
        assertFalse(matches[0].isConnected());
        assertFalse(matches[1].isConnected());
    }

    static class StringMessage {
        private final String message;

        StringMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return message;
        }
    }

    static class DecodedStringMessage extends StringMessage {
        DecodedStringMessage(String message) {
            super(message);
        }
    }

    static class EncodedStringMessage extends StringMessage {
        EncodedStringMessage(String message) {
            super(message);
        }
    }

    @Ports({ @Port(name = "private", sends = DecodedStringMessage.class, accepts = DecodedStringMessage.class),
        @Port(name = "public", sends = EncodedStringMessage.class, accepts = EncodedStringMessage.class) })
    static class CodecEndpoint implements Endpoint {
        private Connection privateConnection, publicConnection;

        @Override
        public MessageHandler onConnect(Connection connection) {
            if (connection.getPort().name().equals("private")) {
                privateConnection = connection;

                return new MessageHandler() {
                    @Override
                    public void handleMessage(Object message) {
                        DecodedStringMessage msg = (DecodedStringMessage) message;
                        char[] data = msg.toString().toCharArray();
                        for (int ix = 0; ix < data.length; ix++) {
                            data[ix]++;
                        }

                        if (publicConnection != null) {
                            publicConnection.sendMessage(new EncodedStringMessage(new String(data)));
                        }
                    }

                    @Override
                    public void disconnected() {
                        privateConnection = null;
                    }
                };
            } else if (connection.getPort().name().equals("public")) {
                publicConnection = connection;

                return new MessageHandler() {
                    @Override
                    public void handleMessage(Object message) {
                        EncodedStringMessage msg = (EncodedStringMessage) message;
                        char[] data = msg.toString().toCharArray();
                        for (int ix = 0; ix < data.length; ix++) {
                            data[ix]--;
                        }

                        if (privateConnection != null) {
                            privateConnection.sendMessage(new DecodedStringMessage(new String(data)));
                        }
                    }

                    @Override
                    public void disconnected() {
                        publicConnection = null;
                    }
                };
            } else {
                return null;
            }
        }
    }

    @Port(name = "any", sends = DecodedStringMessage.class, accepts = DecodedStringMessage.class)
    static class EchoEndpoint implements Endpoint {
        @Override
        public MessageHandler onConnect(final Connection connection) {
            return new MessageHandler() {
                @Override
                public void handleMessage(Object message) {
                    connection.sendMessage(message);
                }

                @Override
                public void disconnected() {
                }
            };
        }
    }

    @Port(name = "something", sends = DecodedStringMessage.class, accepts = DecodedStringMessage.class)
    static class SendDataEndpoint implements Endpoint {
        @Override
        public MessageHandler onConnect(final Connection connection) {
            connection.sendMessage(new DecodedStringMessage("Ab"));
            return new MessageHandler() {
                @Override
                public void handleMessage(Object message) {
                    System.out.println("Received " + message);

                    if (message.toString().length() < 1024) {
                        connection.sendMessage(new DecodedStringMessage(message.toString() + message));
                    }
                }

                @Override
                public void disconnected() {
                }
            };
        }
    }

    public void testChainOfEndpoints() throws Exception {
        EchoEndpoint echo = new EchoEndpoint();
        CodecEndpoint echoCodec = new CodecEndpoint();
        CodecEndpoint dataCodec = new CodecEndpoint();
        SendDataEndpoint data = new SendDataEndpoint();

        ConnectionManager connectionManager = setupEndpoints(echo, echoCodec, dataCodec, data);
        EndpointPort[] ports = connectionManager.getEndpointPortsOf(echoCodec).toArray(new EndpointPort[0]);
        assertEquals(2, ports.length);
        EndpointPort publicPort = ports[0].getName().equals("public") ? ports[0] : ports[1];
        MatchingPorts publicConn = getOnly(publicPort.getMatchingPorts());

        publicConn.connect();

        EndpointPort echoPort = getOnly(connectionManager.getEndpointPortsOf(echo));
        Set<? extends MatchingPorts> echoConns = echoPort.getMatchingPorts();
        for (MatchingPorts conn : echoConns) {
            if (conn.getOtherEnd(echoPort).getEndpoint() == echoCodec) {
                conn.connect();
                break;
            }
        }

        EndpointPort dataPort = getOnly(connectionManager.getEndpointPortsOf(data));
        Set<? extends MatchingPorts> dataConns = dataPort.getMatchingPorts();
        for (MatchingPorts conn : dataConns) {
            if (conn.getOtherEnd(dataPort).getEndpoint() == dataCodec) {
                conn.connect();
                break;
            }
        }
    }

    private <T> T getOnly(Set<T> set) {
        assertEquals(1, set.size());
        return set.iterator().next();
    }
}
