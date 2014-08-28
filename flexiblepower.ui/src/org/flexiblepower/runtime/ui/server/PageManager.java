package org.flexiblepower.runtime.ui.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.flexiblepower.runtime.ui.server.widgets.AbstractWidgetManager;
import org.flexiblepower.runtime.ui.server.widgets.WidgetRegistration;
import org.flexiblepower.runtime.ui.server.widgets.WidgetRegistry;
import org.flexiblepower.ui.Widget;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

@Component(immediate = true)
public class PageManager extends AbstractWidgetManager {
    private static final Logger logger = LoggerFactory.getLogger(PageManager.class);

    private HttpService httpService;

    @Reference
    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    private String template;

    @Activate
    public void activate(BundleContext context) throws IOException, NamespaceException {
        logger.trace("Entering activate, context = {}", context);
        template = loadTemplate(getClass().getClassLoader());

        HttpContext httpContext = httpService.createDefaultHttpContext();
        httpService.registerResources("/css", "/css", httpContext);
        httpService.registerResources("/img", "/img", httpContext);
        httpService.registerResources("/js", "/js", httpContext);
        logger.debug("Registered resources [/css], [/img] and [/js]");

        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put("alias", "/");
        properties.put("contextId", "fps");
        context.registerService(Servlet.class, new HttpServlet() {
            private static final long serialVersionUID = 87503498577L;
            private final Logger logger = LoggerFactory.getLogger(getClass());

            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
                if (req.getPathInfo() == null || req.getPathInfo().isEmpty() || "/".equals(req.getPathInfo())) {
                    resp.sendRedirect("/dashboard");
                } else {
                    logger.warn("Unknown path [" + req.getPathInfo() + "]");
                    resp.sendError(404);
                }
            }
        }, properties);
        logger.debug("Registered servlet for handling unknown calls");

        logger.trace("Leaving activate");
    }

    @Deactivate
    public void deactivate() {
        logger.trace("Entering deactivate");
        httpService.unregister("/css");
        httpService.unregister("/img");
        httpService.unregister("/js");
        logger.trace("Leaving deactivate");
    }

    private String loadTemplate(ClassLoader classLoader) throws IOException {
        logger.trace("Entering loadTemplate, classLoader = {}", classLoader);
        Reader r = new InputStreamReader(classLoader.getResourceAsStream("index.html"));
        char[] buffer = new char[1024];
        StringBuilder sb = new StringBuilder();
        int read = 0;
        while ((read = r.read(buffer)) >= 0) {
            sb.append(buffer, 0, read);
        }
        r.close();
        logger.trace("Leaving loadTemplate, result = (String of {} characters)", sb.length());
        return sb.toString();
    }

    public String getTemplate(WidgetRegistration activeReg, Locale locale) {
        logger.trace("Entering getTemplate, activeReg = {}, locale = {}", activeReg, locale);
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"center\"><nav><ul>");
        for (WidgetRegistration reg : getRegistrations()) {
            if (activeReg == reg) {
                sb.append("<li class=\"active\">");
            } else {
                sb.append("<li>");
            }
            sb.append("<a href=\"/").append(reg.getName()).append("/index.html\">");
            sb.append("<image src=\"/").append(reg.getName()).append("/menu.png\" />");
            sb.append("<span>").append(reg.getWidget().getTitle(locale)).append("</span>");
            sb.append("</a></li>");
        }
        sb.append("</ul></nav></div>");

        String result = template.replace("$menu$", sb);
        logger.trace("Leaving getTemplate, result = (String of {} characters)", result == null ? -1 : result.length());
        return result;
    }

    @Override
    @Reference(dynamic = true, multiple = true, optional = true, target = "(" + WidgetRegistry.KEY_TYPE
    + "="
    + WidgetRegistry.VALUE_TYPE_FULL
    + ")")
    public void addWidget(Widget widget, Map<String, Object> properties) {
        super.addWidget(widget, properties);
    }

    @Override
    public HttpServlet createServlet(WidgetRegistration registration) {
        return new PageServlet(this, registration, 31536000000L);
    }

    @Override
    public String createPath(WidgetRegistration registration) {
        return "/" + registration.getName();
    }

    @Override
    public String toString() {
        return "PageManager";
    }
}
