package org.flexiblepower.felix.webconsole.plugins;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.flexiblepower.ral.wiring.Resource;
import org.flexiblepower.ral.wiring.ResourceWiringManager;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(provide = Servlet.class, properties = { "felix.webconsole.title=FPAI: Resources",
                                                  "felix.webconsole.label=fpai-resources" })
public class ResourceWiringServlet extends HttpServlet {
    private static final long serialVersionUID = -7014372128039465147L;

    private ResourceWiringManager wiringManager;

    @Reference
    public void setWiringManager(ResourceWiringManager wiringManager) {
        this.wiringManager = wiringManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        writer.write("<p class=\"statline ui-state-highlight\">Number of active appliances: ");
        writer.write(Integer.toString(wiringManager.size()));
        writer.write("</p>");
        writer.write("<table id=\"plugin_table\" class=\"nicetable noauto ui-widget\">");

        writer.write("<thead>");
        writer.write("<tr>");
        writer.write("<th class=\"ui-widget-header header\">Appliance ID</th>");
        writer.write("<th class=\"ui-widget-header header\">Resource Drivers</th>");
        writer.write("<th class=\"ui-widget-header header\">Resource Managers</th>");
        writer.write("<th class=\"ui-widget-header header\">Resource ControllerManager</th>");
        writer.write("</tr>");
        writer.write("</thead>");

        writer.write("<tbody class=\"ui-widget-content\">");
        int counter = 0;
        for (Resource<?, ?> resource : wiringManager.getResources()) {
            counter++;
            if (counter % 2 == 1) {
                writer.write("<tr class=\"odd ui-state-default\"><td>");
            } else {
                writer.write("<tr class=\"even ui-state-default\"><td>");
            }
            writer.write(resource.getId());
            writer.write("</td><td>");
            writer.write(resource.getResourceDrivers().toString());
            writer.write("</td><td>");
            writer.write(resource.getResourceManagers().toString());
            writer.write("</td><td>");
            writer.write(String.valueOf(resource.getControllerManager()));
            writer.write("</td></tr>");
        }
        writer.write("</table>");
    }
}
