package org.flexiblepower.felix.webconsole.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;

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

@Component(provide = Servlet.class, properties = {
                                                  "felix.webconsole.title=FPAI: ConnectionManager",
                                                  "felix.webconsole.label=fpai-connection-manager" })
public class ConnectionManagerPlugin extends HttpServlet {
    private static final long serialVersionUID = 7146852312931261310L;
    private static final Logger log = LoggerFactory
                                                   .getLogger(ConnectionManagerPlugin.class);

    private static final String[] servedFiles = new String[] {
                                                              "connectionManager.js", "cytoscape.min.js", "index.html" };

    private ConnectionManager connectionManager;

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

        if (Arrays.binarySearch(servedFiles, path, null) >= 0) {
            if (path.endsWith(".js")) {
                resp.setContentType("application/x-javascript");
            } else if (path.endsWith(".html")) {
                resp.setContentType("text/html");
            }

            InputStream input = getClass().getClassLoader()
                                          .getResourceAsStream(path);
            if (input == null) {
                log.debug("Could not find file {}", path);
                resp.sendError(404);
            } else {
                log.debug("Serving file {}", path);
                IOUtils.copy(input, resp.getWriter());
            }
        } else if (path.equals("")) {
            resp.sendRedirect("fpai-connection-manager/index.html");
        } else if (path.equals("getGraph.json")) {
            Collection<? extends ManagedEndpoint> values = connectionManager
                                                                            .getEndpoints().values();
            String graphJson = createGraphJson(values);
            sendJson(resp, graphJson);

        } else {
            resp.getWriter().print("GET Not yet implemented: " + path);
            resp.getWriter().close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path.startsWith("/fpai-connection-manager")) {
            path = path.substring(24);
            if (!path.isEmpty() && path.charAt(0) == '/') {
                path = path.substring(1);
            }
        }
        log.debug("path: " + path);
        if (path.equals("autoconnect.json")) {
            log.debug("autoconnect called");
            connectionManager.autoConnect();
            resp.getWriter().print("{\"autoconnected\": true}");
            resp.getWriter().close();
        } else {
            PrintWriter w = resp.getWriter();
            resp.getWriter().print("POST Not yet implemented: " + path);
            resp.getWriter().close();
        }
    }

    private void sendJson(HttpServletResponse resp, String graphJson) {
        log.debug("Sending nodes and edges as JSON");
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
        JsonArray elements = new JsonArray();

        // add nodes
        for (ManagedEndpoint me : values) {
            JsonObject endpoint = new JsonObject();
            endpoint.addProperty("group", "nodes");

            String pid = me.getPid();
            String[] split = pid.split("\\.");
            log.trace("length " + split.length);
            String name = split[split.length - 1];

            log.debug("Adding {} {}", pid, name);

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
                data.addProperty("id", me.getPid() + ":" + ep.getName());
                data.addProperty("name", ep.getName());
                data.addProperty("parent", me.getPid());
                endpointport.add("data", data);
                elements.add(endpointport);
            }
        }

        // add edges
        for (ManagedEndpoint me : values) {
            for (EndpointPort ep : me.getPorts().values()) {
                for (PotentialConnection pc : ep.getPotentialConnections().values()) {
                    JsonObject connection = new JsonObject();
                    connection.addProperty("group", "edges");
                    EndpointPort either = pc.getEitherEnd();
                    if (either == ep) {
                        EndpointPort other = pc.getOtherEnd(either);
                        String eitherend = either.getEndpoint().getPid() + ":" + either.getName();
                        String otherend = other.getEndpoint().getPid() + ":" + other.getName();

                        JsonObject connectiondata = new JsonObject();
                        connectiondata.addProperty("source", eitherend);
                        connectiondata.addProperty("target", otherend);
                        connectiondata.addProperty("isconnected", true); // pc.isConnected());
                        connection.add("data", connectiondata);
                        elements.add(connection);
                    }
                }
            }
        }
        return elements.toString();
    }
}
