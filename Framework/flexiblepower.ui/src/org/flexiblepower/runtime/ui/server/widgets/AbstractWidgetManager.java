package org.flexiblepower.runtime.ui.server.widgets;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;

import org.flexiblepower.ui.Widget;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Reference;

public abstract class AbstractWidgetManager {
    private static final Logger logger = LoggerFactory.getLogger(AbstractWidgetManager.class);

    private final WidgetRegistry registry = new WidgetRegistry();
    private final Map<Widget, ServiceRegistration<Servlet>> serviceRegistrations = new HashMap<Widget, ServiceRegistration<Servlet>>();

    @Reference(dynamic = true, multiple = true, optional = true, target = "(" + WidgetRegistry.KEY_TYPE
                                                                          + "="
                                                                          + WidgetRegistry.VALUE_TYPE_FULL
                                                                          + ")")
    public void addWidget(Widget widget, Map<String, Object> properties) {
        logger.trace("Entering addWidget, widget = {}, properties = {}", widget, properties);
        BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();

        WidgetRegistration registration = registry.registerWidget(widget, properties);

        HttpServlet servlet = createServlet(registration);
        Dictionary<String, Object> servletProperties = new Hashtable<String, Object>(properties);
        servletProperties.put("alias", createPath(registration));
        servletProperties.put("contextId", "fps");
        logger.debug("Registering servlet with properties: " + servletProperties);
        ServiceRegistration<Servlet> serviceRegistration = context.registerService(Servlet.class,
                                                                                   servlet,
                                                                                   servletProperties);
        serviceRegistrations.put(widget, serviceRegistration);
        logger.trace("Leaving addWidget");
    }

    public void removeWidget(Widget widget) {
        logger.trace("Entering removeWidget, widget = {}", widget);

        registry.unregisterWidget(widget);

        ServiceRegistration<Servlet> registration = serviceRegistrations.get(widget);
        if (registration != null) {
            registration.unregister();
        }
        logger.trace("Leaving removeWidget");
    }

    public abstract String createPath(WidgetRegistration registration);

    public abstract HttpServlet createServlet(WidgetRegistration registration);

    public Iterable<WidgetRegistration> getRegistrations() {
        return registry;
    }
}
