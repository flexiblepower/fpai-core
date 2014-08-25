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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(provide = Servlet.class, properties = {
		"felix.webconsole.title=FPAI: ConnectionManager",
		"felix.webconsole.label=fpai-connection-manager" })
public class ConnectionManagerPlugin extends HttpServlet {
	private static final long serialVersionUID = 7146852312931261310L;
	private static final Logger log = LoggerFactory
			.getLogger(ConnectionManagerPlugin.class);

	private static final String[] servedFiles = new String[] {
			"cytoscape.min.js", "index.html" };

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

            resp.setContentType("application/json");
            Collection<? extends ManagedEndpoint> values = connectionManager
                    .getEndpoints().values();

            log.debug("Sending {} nodes and edges as JSON", values.size());
            PrintWriter w = resp.getWriter();
            w.println("[");
            for (ManagedEndpoint me : values) {

                w.println("\t\t{");
                w.println("\t\t\t\"group\": \"nodes\",");
                String pid = me.getPid();
                String[] split = pid.split("\\.");
                log.debug("length " + split.length);
                String name = split[split.length - 1];
                w.println("\t\t\t\"data\": { " + "\"id\": \"" + pid
                          + "\", \"name\": \"" + name + "\"}");
                w.print("\t\t}");

                for (EndpointPort ep : me.getPorts().values()) {
                    w.println(",\n\t\t{");
                    w.println("\t\t\t\"group\": \"nodes\",");
                    w.println("\t\t\t\"data\": { \"id\": \"" + me.getPid()
                              + ":" + ep.getName() + "\"," + " \"name\": \""
                              + ep.getName() + "\"" + ", \"parent\": \""
                              + me.getPid() + "\" }");
                    w.print("\t\t}");
                }
            }
            w.println("\n]");
        } else {
            PrintWriter w = resp.getWriter();
            // TODO: implement a way to show the complete graph. Maybe using
            // http://cytoscape.github.io/cytoscape.js/ ?
            resp.getWriter().write("Not yet implemented: " + path);
        }
    }
}