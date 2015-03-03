package org.flexiblepower.scheduling;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.webconsole.SimpleWebConsolePlugin;
import org.osgi.framework.BundleContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;

@Component(immediate = true)
public class SchedulingServlet extends SimpleWebConsolePlugin {
    private static final long serialVersionUID = 6576166876090020661L;

    public SchedulingServlet() {
        super("scheduling", "Runtime Scheduling", "FPAI", null);
    }

    @Activate
    public void init(BundleContext bundleContext) {
        register(bundleContext);
    }

    @Deactivate
    public void deinit() {
        unregister();
    }

    @Override
    protected void renderContent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter w = resp.getWriter();

        w.println("<div class=\"ui-widget-header ui-corner-top buttonGroup\"></div>");
        w.println("<table class=\"tablesorter nicetable noauto ui-widget\">");
        w.println("<tr><th class=\"ui-widget-header header\">Thread handler</th><th class=\"ui-widget-header header\">Jobs</th></tr>");
        int i = 1;
        for (Entry<String, AbstractScheduler> entry : AbstractScheduler.THREAD_MONITOR) {
            if (i % 2 == 1) {
                w.print("<tr class=\"odd ui-state-default\">");
            } else {
                w.print("<tr class=\"even ui-state-default\">");
            }

            w.print("<td>");
            w.print(entry.getKey());
            w.print("</td><td>");
            List<String> jobs = entry.getValue().getJobs();
            if (jobs.isEmpty()) {
                w.print("-- no jobs scheduled --</td></tr>");
            } else {
                w.print(jobs.get(0));
                for (int ix = 1; ix < jobs.size(); ix++) {
                    w.print("</td></tr>");
                    if (i % 2 == 1) {
                        w.print("<tr class=\"odd ui-state-default\"><td></td><td>");
                    } else {
                        w.print("<tr class=\"even ui-state-default\"><td></td><td>");
                    }
                    w.print(jobs.get(ix));
                }
            }
            w.print("</td></tr>");

            i++;
        }
        w.print("</table>");
        w.println("<div class=\"ui-widget-header ui-corner-bottom buttonGroup\"></div>");
    }
}
