package org.flexiblepower.runtime.wiring.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

import junit.framework.TestCase;

import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.Connection;
import org.flexiblepower.messaging.ConnectionManager;
import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.ConnectionManager.ManagedEndpoint;
import org.flexiblepower.messaging.ConnectionManager.PotentialConnection;
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

    @Port(name = "A-anyIn", sends = String.class, accepts = Object.class, cardinality = Cardinality.MULTIPLE)
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
        assertEquals(0, connectionManager.getEndpoints().size());

        connectionManagerTracker.close();
    }

    public void testConnected() throws Exception {
        EndpointA a = new EndpointA();
        EndpointB b = new EndpointB();
        EndpointC c = new EndpointC();
        EndpointD d = new EndpointD();
        EndpointE e = new EndpointE();
        ConnectionManager connectionManager = setupEndpoints(a, b, c, d, e);

        System.out.println(connectionManager.getEndpoints());

        EndpointPort portA = connectionManager.getEndpoint(EndpointA.class.getName()).getPort("A-anyIn");
        assertNotNull(portA);
        assertEquals(4, portA.getPotentialConnections().size());

        EndpointPort portB = connectionManager.getEndpoint(EndpointB.class.getName()).getPort("B-anyOut");
        PotentialConnection connAB = portA.getPotentialConnection(portB);
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

        connectionManager.autoConnect();
        assertTrue(connAB.isConnected());
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

        EndpointPort serverPort = connectionManager.getEndpoint(ServerEndpoint.class.getName()).getPort("TextService");

        SortedMap<String, ? extends PotentialConnection> matchingPorts = serverPort.getPotentialConnections();
        assertEquals(2, matchingPorts.size());
        assertTrue(matchingPorts.firstKey().startsWith(SquaringEndpoint.class.getName()));
        assertTrue(matchingPorts.lastKey().startsWith(WorldEndpoint.class.getName()));

        PotentialConnection connToSquaring = matchingPorts.get(matchingPorts.firstKey());
        PotentialConnection connToWorld = matchingPorts.get(matchingPorts.lastKey());
        assertFalse(connToSquaring.isConnected());
        assertFalse(connToWorld.isConnected());

        connToSquaring.connect();
        connToWorld.connect();
        assertTrue(connToSquaring.isConnected());
        assertTrue(connToWorld.isConnected());

        Thread.sleep(SLEEP_TIME);

        connToSquaring.disconnect();
        connToWorld.disconnect();
        assertFalse(connToSquaring.isConnected());
        assertFalse(connToWorld.isConnected());
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
        private final int expectedMessagesHandled;
        private int messagesHandled;

        public EchoEndpoint(int expectedMessagesHandled) {
            this.expectedMessagesHandled = expectedMessagesHandled;
            messagesHandled = 0;
        }

        public void reset() {
            messagesHandled = 0;
        }

        @Override
        public MessageHandler onConnect(final Connection connection) {
            return new MessageHandler() {
                @Override
                public void handleMessage(Object message) {
                    connection.sendMessage(message);
                    messagesHandled++;
                }

                @Override
                public void disconnected() {
                    assertEquals(expectedMessagesHandled, messagesHandled);
                }
            };
        }
    }

    @Port(name = "something", sends = DecodedStringMessage.class, accepts = DecodedStringMessage.class)
    class SendDataEndpoint implements Endpoint {
        @Override
        public MessageHandler onConnect(final Connection connection) {
            connection.sendMessage(new DecodedStringMessage("Ab"));
            return new MessageHandler() {
                @Override
                public void handleMessage(Object message) {
                    // System.out.println("Received " + message);

                    if (message.toString().length() < 1024) {
                        connection.sendMessage(new DecodedStringMessage(message.toString() + message));
                    } else {
                        synchronized (EndpointTester.this) {
                            EndpointTester.this.notifyAll();
                        }
                    }
                }

                @Override
                public void disconnected() {
                }
            };
        }
    }

    public void testChainOfEndpoints() throws Exception {
        EchoEndpoint echo = new EchoEndpoint(10);
        CodecEndpoint echoCodec = new CodecEndpoint();
        CodecEndpoint dataCodec = new CodecEndpoint();
        SendDataEndpoint data = new SendDataEndpoint();

        ConnectionManager connectionManager = setupEndpoints(echo, echoCodec, dataCodec, data);

        SortedMap<String, ? extends ManagedEndpoint> endpoints = connectionManager.getEndpoints();
        assertEquals(4, endpoints.size());

        Iterator<? extends ManagedEndpoint> iterator = endpoints.values().iterator();
        ManagedEndpoint meCodec1 = iterator.next();
        ManagedEndpoint meCodec2 = iterator.next();
        ManagedEndpoint meEcho = iterator.next();
        ManagedEndpoint meData = iterator.next();

        assertTrue(meCodec1.getPid().contains(CodecEndpoint.class.getName()));
        assertTrue(meCodec2.getPid().contains(CodecEndpoint.class.getName()));
        assertTrue(meEcho.getPid().contains(EchoEndpoint.class.getName()));
        assertTrue(meData.getPid().contains(SendDataEndpoint.class.getName()));

        for (ManagedEndpoint ep : new ManagedEndpoint[] { meCodec1, meCodec2 }) {
            SortedMap<String, ? extends EndpointPort> ports = ep.getPorts();
            assertEquals(2, ports.size());
            assertEquals("private", ports.firstKey());
            assertEquals("public", ports.lastKey());
        }

        EndpointPort portEcho = checkNotNull("\"any\" port of the EchoEndpoint", meEcho.getPort("any"));
        EndpointPort portPrivate1 = checkNotNull("\"private\" port of the CodecEndpoint 1", meCodec1.getPort("private"));
        EndpointPort portPrivate2 = checkNotNull("\"private\" port of the CodecEndpoint 2", meCodec2.getPort("private"));
        EndpointPort portPublic1 = checkNotNull("\"public\" port of the CodecEndpoint 1", meCodec1.getPort("public"));
        EndpointPort portPublic2 = checkNotNull("\"public\" port of the CodecEndpoint 2", meCodec2.getPort("public"));
        EndpointPort portData = checkNotNull("\"something\" port of SendDataEndpoint", meData.getPort("something"));

        PotentialConnection connEcho = portEcho.getPotentialConnection(portPrivate1);
        PotentialConnection connPublic = portPublic1.getPotentialConnection(portPublic2);
        PotentialConnection connData = portData.getPotentialConnection(portPrivate2);

        for (int i = 0; i < 100; i++) {
            echo.reset();

            connPublic.connect();
            connEcho.connect();
            connData.connect();

            synchronized (this) {
                wait(1000);
            }

            connData.disconnect();
            connEcho.disconnect();
            connPublic.disconnect();
        }
    }

    private static <T> T checkNotNull(String description, T object) {
        assertNotNull("Object is null: " + description, object);
        return object;
    }

    @Port(name = "test", sends = String.class, accepts = String.class)
    public abstract static class BaseEndpoint implements Endpoint {
        @Override
        public MessageHandler onConnect(Connection connection) {
            System.out.println(BaseEndpoint.this.getClass().getSimpleName() + " connected port " + connection.getPort());
            return new MessageHandler() {
                @Override
                public void handleMessage(Object message) {
                    System.out.println(BaseEndpoint.this.getClass().getSimpleName() + " received " + message);
                }

                @Override
                public void disconnected() {
                }
            };
        }
    }

    @Port(name = "added", sends = String.class, accepts = String.class)
    public static class AddingEndpoint extends BaseEndpoint {
    }

    @Port(name = "test", sends = Integer.class, accepts = String.class)
    public static class ReplacingEndpoint extends BaseEndpoint {
    }

    public void testInheritance() throws Exception {
        AddingEndpoint addingEndpoint = new AddingEndpoint();
        ReplacingEndpoint replacingEndpoint = new ReplacingEndpoint();
        ConnectionManager cm = setupEndpoints(addingEndpoint, replacingEndpoint);

        SortedMap<String, ? extends ManagedEndpoint> endpoints = cm.getEndpoints();
        assertEquals(2, endpoints.size());

        ManagedEndpoint meAdding = endpoints.get(endpoints.firstKey());
        ManagedEndpoint meReplacing = endpoints.get(endpoints.lastKey());

        SortedMap<String, ? extends EndpointPort> portsAdding = meAdding.getPorts();
        assertEquals(2, portsAdding.size());
        assertEquals("added", portsAdding.firstKey());
        assertEquals("test", portsAdding.lastKey());

        assertEquals(1, meReplacing.getPorts().size());
        assertEquals("test", meReplacing.getPorts().firstKey());
    }

    @Port(name = "shouldBeImplemented")
    public interface InterfaceEndpoint extends Endpoint {
    }

    @Port(name = "shouldBeImplemented", accepts = String.class, sends = String.class)
    public class ImplementingEndpoint implements InterfaceEndpoint {
        @Override
        public MessageHandler onConnect(Connection connection) {
            return null;
        }
    }

    @Port(name = "shouldNotBeImplemented", accepts = String.class, sends = String.class)
    public class MissingEndpoint implements InterfaceEndpoint {
        @Override
        public MessageHandler onConnect(Connection connection) {
            return null;
        }
    }

    public void testMissingImplementation() throws Exception {
        ImplementingEndpoint e1 = new ImplementingEndpoint();
        MissingEndpoint e2 = new MissingEndpoint();
        ConnectionManager cm = setupEndpoints(e1, e2);

        SortedMap<String, ? extends ManagedEndpoint> endpoints = cm.getEndpoints();
        assertEquals(1, endpoints.size());

        ManagedEndpoint meImplementing = endpoints.get(endpoints.firstKey());
        assertNotNull(meImplementing.getPort("shouldBeImplemented"));
    }
}
