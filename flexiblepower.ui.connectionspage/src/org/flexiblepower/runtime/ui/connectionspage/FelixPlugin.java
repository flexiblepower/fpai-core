package org.flexiblepower.runtime.ui.connectionspage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.felix.webconsole.AbstractWebConsolePlugin;
import org.flexiblepower.messaging.ConnectionManager;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

public class FelixPlugin extends AbstractWebConsolePlugin {
    private static final long serialVersionUID = 7228578256793242058L;
    private static final Logger logger = LoggerFactory.getLogger(FelixPlugin.class);

    private final BaseWidget base;

    public FelixPlugin(ConnectionManager connectionManager, BundleContext context) {
        base = new DashboardWidget(connectionManager, context);
        activate(context);
    }

    @Override
    public String getLabel() {
        return "connection-manager";
    }

    @Override
    public String getTitle() {
        return "ConnectionManager";
    }

    @Override
    protected String[] getCssReferences() {
        return new String[] { getLabel() + "/style.css" };
    }

    @Override
    protected void renderContent(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter w = resp.getWriter();
        w.println("<div id=\"graph\"></div>");
        w.println("<div id=\"properties\">");
        w.println("<input type=\"button\" value=\"Autoconnect\" id=\"autoconnect\" />");
        w.println("<h1>Properties of <span id=\"componentname\">&lt;no selection&gt;</span></h1>");
        w.println("</div>");
        w.println("<script src=\"" + getLabel() + "/dom.jsPlumb-1.7.5.js\"></script>");
        w.println("<script src=\"" + getLabel() + "/script.js\"></script>");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null || path.length() <= getLabel().length() + 2) {
            super.service(req, resp);
        } else {
            path = path.substring(getLabel().length() + 2);

            URL url = getBundle().getEntry("widgets/DashboardWidget/" + path);
            if (url != null) {
                serve(url, resp);
            } else if (path.equals("autoconnect")) {
                base.autoconnect();
            } else if (path.equals("connect")) {
                send(base.connect(new Gson().fromJson(req.getReader(), ConnectionInfo.class)), resp);
            } else if (path.equals("disconnect")) {
                send(base.disconnect(new Gson().fromJson(req.getReader(), ConnectionInfo.class)), resp);
            } else if (path.equals("currentState")) {
                send(base.currentState(), resp);
            }
        }
    }

    private void send(Object result, HttpServletResponse resp) throws JsonIOException, IOException {
        resp.setContentType("application/json");
        new Gson().toJson(result, resp.getWriter());
    }

    private void serve(URL url, HttpServletResponse resp) throws IOException {
        if (url.getFile().endsWith(".css")) {
            resp.setContentType("text/css");
        } else if (url.getFile().endsWith(".js")) {
            resp.setContentType("application/javascript");
        }

        IOUtils.copy(url.openStream(), resp.getWriter());
        logger.debug("Served from url: " + url);
    }
}
