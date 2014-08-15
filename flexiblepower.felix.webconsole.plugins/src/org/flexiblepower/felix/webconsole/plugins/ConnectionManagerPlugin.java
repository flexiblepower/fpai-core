package org.flexiblepower.felix.webconsole.plugins;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.flexiblepower.messaging.ConnectionManager;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(provide = Servlet.class, properties = { "felix.webconsole.title=FPAI: ConnectionManager",
                                                  "felix.webconsole.label=fpai-connection-manager" })
public class ConnectionManagerPlugin extends HttpServlet {
    private static final long serialVersionUID = 7146852312931261310L;

    private ConnectionManager connectionManager;

    @Reference
    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO: implement a way to show the complete graph. Maybe using http://cytoscape.github.io/cytoscape.js/ ?
        resp.getWriter().write("Not yet implemented");
    }
}
