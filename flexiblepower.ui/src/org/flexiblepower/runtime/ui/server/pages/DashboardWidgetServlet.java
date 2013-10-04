package org.flexiblepower.runtime.ui.server.pages;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.flexiblepower.runtime.ui.server.HttpUtils;
import org.flexiblepower.runtime.ui.server.widgets.WidgetRegistration;
import org.osgi.service.useradmin.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DashboardWidgetServlet extends HttpServlet {
    private static final long serialVersionUID = -2367978642451243228L;
    private static final Logger logger = LoggerFactory.getLogger(DashboardWidgetServlet.class);

    private final WidgetRegistration registration;
    private final long expirationTime;

    public DashboardWidgetServlet(WidgetRegistration registration, long expirationTime) {
        logger.trace("Entering constructor, registration = {}, expirationTime = {}", registration, expirationTime);
        this.registration = registration;
        this.expirationTime = expirationTime;
        logger.trace("Leaving constructor");
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.trace("Entering service, req.pathInfo = {}", req.getPathInfo());
        if (req.getPathInfo() == null || req.getPathInfo().isEmpty()) {
            String url = "/widget/" + registration.getId() + "/index.html";
            resp.sendRedirect(url);
            logger.trace("Leaving service, redirected to {}", url);
            return;
        }
        String path = req.getPathInfo().substring(1);

        URL url = registration.getResource(path, req.getLocale());

        if (url == null) {
            logger.debug("No such file, calling method {}", path);
            User user = (User) req.getAttribute("user");
            try {
                String input = HttpUtils.readData(req.getInputStream());
                String result = registration.executeMethod(path, req.getLocale(), user, input);

                resp.setContentLength(result.length());
                resp.setContentType("application/json");

                PrintWriter writer = resp.getWriter();
                writer.write(result);
                writer.close();
            } catch (NoSuchMethodException e) {
                logger.debug("No such method " + path, e);
                resp.sendError(404, e.getMessage());
            } catch (InvocationTargetException e) {
                logger.debug("Error during invocation of " + path, e);
                resp.sendError(500, e.getMessage());
            }
        } else {
            HttpUtils.writeFile(url, expirationTime, path, resp, req.getLocale());
        }
        logger.trace("Leaving service");
    }
}
