package org.flexiblepower.felix.webconsole.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.flexiblepower.messaging.ConnectionManager;
import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.ConnectionManager.ManagedEndpoint;
import org.flexiblepower.messaging.ConnectionManager.PotentialConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Component(provide = Servlet.class, properties = { "felix.webconsole.title=FPAI: ConnectionManager",
                                                  "felix.webconsole.label=fpai-connection-manager" })
public class ConnectionManagerPlugin extends HttpServlet {
    private static final long serialVersionUID = 7146852312931261310L;
    private static final Logger logger = LoggerFactory
                                                      .getLogger(ConnectionManagerPlugin.class);

    private static final String[] SERVED_FILES = new String[] { "connectionManager.js",
                                                               "cytoscape.min.js",
                                                               "index.html" };

    private ConnectionManager connectionManager;
    private HashMap<String, PotentialConnection> connectionCache;

    @Reference
    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                                                                          throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path.startsWith("/fpai-connection-manager")) {
            path = path.substring(24);
            if (!path.isEmpty() && path.charAt(0) == '/') {
                path = path.substring(1);
            }
        }

        if (Arrays.binarySearch(SERVED_FILES, path, null) >= 0) {
            serveFile(resp, path);
        } else if (path.equals("")) {
            resp.sendRedirect("fpai-connection-manager/index.html");
        } else if (path.equals("getGraph.json")) {
            sendJson(resp, createGraphJson(connectionManager.getEndpoints().values()));
        } else {
            resp.getWriter().print("GET Not yet implemented: " + path);
            resp.getWriter().close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter w = resp.getWriter();

        String path = req.getPathInfo();
        if (path.startsWith("/fpai-connection-manager")) {
            path = path.substring(24);
            if (!path.isEmpty() && path.charAt(0) == '/') {
                path = path.substring(1);
            }
        }
        logger.debug("path: " + path);
        if (path.endsWith(".json")) {
            resp.setContentType("application/json");
        }
        if (path.equals("autoconnect.json")) {
            autoconnect(w);
        } else if (path.equals("connect.json")) {
            connect(req, w);
        } else if (path.equals("disconnect.json")) {
            disconnect(req, w);
        } else {
            w.print("POST Not yet implemented: " + path);
        }
        w.close();
    }

    private void serveFile(HttpServletResponse resp, String path) throws IOException {
        if (path.endsWith(".js")) {
            resp.setContentType("application/x-javascript");
        } else if (path.endsWith(".html")) {
            resp.setContentType("text/html");
        }

        InputStream input = getClass().getClassLoader()
                                      .getResourceAsStream(path);
        if (input == null) {
            logger.debug("Could not find file {}", path);
            resp.sendError(404);
        } else {
            logger.debug("Serving file {}", path);
            IOUtils.copy(input, resp.getWriter());
        }
    }

    private void autoconnect(PrintWriter w) {
        logger.debug("autoconnect called");
        connectionManager.autoConnect();
        w.print("{\"autoconnected\": true, \"class\": \"\"}");
    }

    private void connect(HttpServletRequest req, PrintWriter w) {
        final String id = req.getParameter("id");
        if (connectionCache.containsKey(id)) {
            PotentialConnection connection = connectionCache.get(id);
            if (!connection.isConnected()) {
                logger.debug("Calling connect for " + id);
                try {
                    connection.connect();
                    w.print("{\"status\": \"Connected " + id + "\", \"class\": \"\"}");
                } catch (IllegalStateException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                    w.print("{\"status\": \"Connect was called for " + id
                            + ", but " + e.getMessage() + "\", \"class\": \"ui-state-error\"}");
                }

            } else {
                logger.error("Connect was called for " + id + ", but it was already connected");
                w.print("{\"status\": \"Connect was called for " + id
                        + ", but it was already connected\", \"class\": \"ui-state-error\"}");
            }
        } else {
            logger.error("Connect was called for " + id + ", but it was not found in the cache");
            w.print("{\"status\": \"Connect was called for " + id
                    + ", but it was not found in the cache\", \"class\": \"ui-state-error\"}");
        }
    }

    private void disconnect(HttpServletRequest req, PrintWriter w) {
        final String id = req.getParameter("id");
        if (connectionCache.containsKey(id)) {
            PotentialConnection connection = connectionCache.get(id);
            if (connection.isConnected()) {
                logger.debug("Calling disconnect for " + id);
                connection.disconnect();
                w.print("{\"status\": \"Disconnected " + id + "\", \"class\": \"\"}");
            } else {
                w.print("{\"status\": \"Disconnect was called for " + id
                        + ", but it was already disconnected\", \"class\": \"ui-state-error\"}");
                logger.error("Disconnect was called for " + id + ", but it was already disconnected");
            }
        } else {
            w.print("{\"status\": \"Disonnect was called for " + id
                    + ", but it was not found in the cache\", \"class\": \"ui-state-error\"}");
            logger.error("Disonnect was called for " + id + ", but it was not found in the cache");
        }
    }

    private void sendJson(HttpServletResponse resp, String graphJson) {
        logger.debug("Sending nodes and edges as JSON");
        resp.setContentType("application/json");
        try {
            PrintWriter w = resp.getWriter();
            w.print(graphJson);
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createGraphJson(Collection<? extends ManagedEndpoint> values) {
        if (connectionCache == null) {
            connectionCache = new HashMap<String, ConnectionManager.PotentialConnection>();
        }

        JsonArray elements = new JsonArray();

        generateNodes(values, elements);
        generateEdges(values, elements);

        return elements.toString();
    }

    private void generateNodes(Collection<? extends ManagedEndpoint> values, JsonArray elements) {
        for (ManagedEndpoint me : values) {
            JsonObject endpoint = new JsonObject();
            endpoint.addProperty("group", "nodes");

            String pid = me.getPid();
            String[] split = pid.split("\\.");
            logger.trace("length " + split.length);
            String name = split[split.length - 2];

            logger.debug("Adding {} {}", pid, name);

            JsonObject endpointdata = new JsonObject();
            endpointdata.addProperty("id", pid);
            endpointdata.addProperty("name", name);
            endpoint.add("data", endpointdata);

            elements.add(endpoint);

            for (EndpointPort ep : me.getPorts().values()) {
                // add endpoint port
                JsonObject endpointport = new JsonObject();
                endpointport.addProperty("group", "nodes");
                JsonObject data = new JsonObject();
                data.addProperty("id", me.getPid() + "-" + ep.getName());
                data.addProperty("name", ep.getName());
                data.addProperty("parent", me.getPid());
                endpointport.add("data", data);
                elements.add(endpointport);
            }
        }
    }

    private void generateEdges(Collection<? extends ManagedEndpoint> values, JsonArray elements) {
        int i = 0;
        for (ManagedEndpoint me : values) {
            for (EndpointPort ep : me.getPorts().values()) {
                for (PotentialConnection pc : ep.getPotentialConnections().values()) {
                    JsonObject connection = new JsonObject();
                    connection.addProperty("group", "edges");
                    EndpointPort either = pc.getEitherEnd();
                    if (either == ep) {
                        EndpointPort other = pc.getOtherEnd(either);
                        String eitherend = either.getEndpoint().getPid() + "-" + either.getName();
                        String otherend = other.getEndpoint().getPid() + "-" + other.getName();

                        String id = "connection-" + i++ + "-" + (eitherend + "-" + otherend).hashCode();
                        connectionCache.put(id, pc);

                        JsonObject connectiondata = new JsonObject();
                        connectiondata.addProperty("id", id);
                        connectiondata.addProperty("source", eitherend);
                        connectiondata.addProperty("target", otherend);
                        connectiondata.addProperty("isconnected", pc.isConnected()); // pc.isConnected());
                        connectiondata.addProperty("unconnectable", !pc.isConnectable());
                        connection.add("data", connectiondata);
                        if (pc.isConnected()) {
                            connection.addProperty("classes", "isconnected");
                        } else if (!pc.isConnectable()) {
                            connection.addProperty("classes", "unconnectable");
                        } else {
                            connection.addProperty("classes", "notconnected");
                        }
                        elements.add(connection);
                    }
                }
            }
        }
    }
}
