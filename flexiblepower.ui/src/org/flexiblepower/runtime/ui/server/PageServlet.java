package org.flexiblepower.runtime.ui.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.flexiblepower.runtime.ui.server.widgets.WidgetRegistration;
import org.flexiblepower.ui.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(PageServlet.class);
    private static final long serialVersionUID = -2367978642451243228L;

    private final PageManager pageManager;
    private final WidgetRegistration registration;
    private final long expirationTime;

    public PageServlet(PageManager pageManager, WidgetRegistration registration, long expirationTime) {
        logger.trace("Entering PageServlet constructor, pageManager = {}, registration = {}, expirationTime = {}",
                     pageManager,
                     registration,
                     expirationTime);
        this.pageManager = pageManager;
        this.registration = registration;
        this.expirationTime = expirationTime;
        logger.trace("Leaving PageServlet constructor");
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.trace("Entering service, req.pathInfo = {}", req.getPathInfo());
        if (req.getPathInfo() == null || req.getPathInfo().isEmpty()) {
            String url = "/" + registration.getName() + "/index.html";
            logger.debug("Sending redirect to {}", url);
            resp.sendRedirect(url);
        } else {
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
                    resp.sendError(404, e.getMessage());
                } catch (InvocationTargetException e) {
                    resp.sendError(500, e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
                    logger.error("Error while invocating the method for url: " + url, e.getCause());
                }
            } else if (path.endsWith(".html")) {
                logger.debug("HTML File found for {}", path);
                String page = pageManager.getTemplate(registration, req.getLocale());
                page = page.replace("$content$", HttpUtils.readData(url.openStream()));

                HttpUtils.setNoCaching(resp);
                resp.setContentType("text/html");
                PrintWriter writer = resp.getWriter();
                writer.write(page);
                writer.close();
            } else {
                logger.debug("Other file found for {}", path);
                HttpUtils.writeFile(url, expirationTime, path, resp, req.getLocale());
            }
        }
        logger.trace("Leaving service", req, resp);
    }
}
