package org.flexiblepower.runtime.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.flexiblepower.control.ControllerManager;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.ResourceState;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;

@Component(immediate = true)
public class ResourceConnector {
    static final Logger logger = LoggerFactory.getLogger(ResourceConnector.class);

    static final String KEY_APPLIANCE_IDS = "applianceIds";
    static final String KEY_APPLIANCE_ID = "applianceId";

    final static class ApplianceService<RS extends ResourceState> {
        private final String applianceId;
        private ControllerManager controller;
        private final Set<ResourceManager<RS>> managers = new HashSet<ResourceManager<RS>>();
        private final Set<ResourceDriver<RS, ?>> drivers = new HashSet<ResourceDriver<RS, ?>>();

        public ApplianceService(String applianceId) {
            this.applianceId = applianceId;
        }

        public synchronized void setController(ControllerManager controller) {
            if (this.controller == controller) {
                return;
            }

            if (this.controller != null) {
                logger.warn("[{}] Setting the controller while there is already one active! Removed the old one: {}",
                            applianceId,
                            this.controller);
                unsetController(this.controller);
            }

            this.controller = controller;
            for (ResourceManager<RS> manager : managers) {
                logger.debug("Bound resource manager for [" + applianceId + "] to controller " + controller);
                controller.registerResource(manager);
            }
        }

        public synchronized void unsetController(ControllerManager controller) {
            if (this.controller != controller) {
                logger.error("Could not unset the controller, because it does not match the registered one");
                return;
            }

            for (ResourceManager<RS> manager : managers) {
                logger.debug("Unbound resource manager for [" + applianceId + "] to controller " + controller);
                controller.unregisterResource(manager);
            }
            this.controller = null;
        }

        public synchronized void addManager(ResourceManager<RS> manager) {
            if (managers.add(manager)) {
                if (controller != null) {
                    logger.debug("Bound resource manager for [" + applianceId + "] to controller " + controller);
                    controller.registerResource(manager);
                }

                for (ResourceDriver<RS, ?> driver : drivers) {
                    logger.debug("Bound resource driver for [" + applianceId + "] to its manager " + manager);
                    manager.registerDriver(driver);
                }
            }
        }

        public synchronized void removeManager(ResourceManager<RS> manager) {
            if (managers.remove(manager)) {
                if (controller != null) {
                    logger.debug("Unbound resource manager for [" + applianceId + "] to controller " + controller);
                    controller.unregisterResource(manager);
                }
                for (ResourceDriver<RS, ?> driver : drivers) {
                    logger.debug("Unbound resource driver for [" + applianceId + "] to its manager " + manager);
                    manager.unregisterDriver(driver);
                }
            }
        }

        public synchronized void addDriver(ResourceDriver<RS, ?> driver) {
            if (drivers.add(driver)) {
                for (ResourceManager<RS> manager : managers) {
                    logger.debug("Bound resource driver for [" + applianceId + "] to its manager " + manager);
                    manager.registerDriver(driver);
                }
            }
        }

        public synchronized void removeDriver(ResourceDriver<RS, ?> driver) {
            if (drivers.remove(driver)) {
                for (ResourceManager<RS> manager : managers) {
                    logger.debug("Unbound resource driver for [" + applianceId + "] to its manager " + manager);
                    manager.unregisterDriver(driver);
                }
            }
        }
    }

    final class ControllerTracker implements ServiceTrackerCustomizer<ControllerManager, ControllerManager> {
        @Override
        public ControllerManager addingService(ServiceReference<ControllerManager> reference) {
            synchronized (ResourceConnector.this) {
                ControllerManager controller = context.getService(reference);
                modifiedService(reference, controller);
                return controller;
            }
        }

        @Override
        public void modifiedService(ServiceReference<ControllerManager> reference, ControllerManager controller) {
            synchronized (ResourceConnector.this) {
                Set<String> applianceIds = getApplianceIds(reference);
                Set<String> oldApplianceIds = controlledAppliances.get(controller);
                if (oldApplianceIds == null) {
                    oldApplianceIds = new HashSet<String>();
                }
                oldApplianceIds.removeAll(applianceIds);

                for (String applianceId : applianceIds) {
                    getApplianceService(applianceId).setController(controller);
                }
                for (String applianceId : oldApplianceIds) {
                    getApplianceService(applianceId).unsetController(controller);
                }
                controlledAppliances.put(controller, applianceIds);
            }
        }

        @Override
        public void removedService(ServiceReference<ControllerManager> reference, ControllerManager controller) {
            synchronized (ResourceConnector.this) {
                for (String applianceId : controlledAppliances.remove(controller)) {
                    getApplianceService(applianceId).unsetController(controller);
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    final class ManagerTracker implements ServiceTrackerCustomizer<ResourceManager, ResourceManager> {
        @Override
        public ResourceManager addingService(ServiceReference<ResourceManager> reference) {
            synchronized (ResourceConnector.this) {
                ResourceManager manager = context.getService(reference);
                modifiedService(reference, manager);
                return manager;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void modifiedService(ServiceReference<ResourceManager> reference, ResourceManager manager) {
            synchronized (ResourceConnector.this) {
                String applianceId = getApplianceId(reference);
                String oldApplianceId = managenedAppliance.get(manager);
                if (oldApplianceId != null && !oldApplianceId.equals(applianceId)) {
                    getApplianceService(oldApplianceId).removeManager(manager);
                }
                if (applianceId != null) {
                    getApplianceService(applianceId).addManager(manager);
                }
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void removedService(ServiceReference<ResourceManager> reference, ResourceManager manager) {
            synchronized (ResourceConnector.this) {
                String oldApplianceId = getApplianceId(reference);
                if (oldApplianceId != null) {
                    getApplianceService(oldApplianceId).removeManager(manager);
                }
                context.ungetService(reference);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    final class DriverTracker implements ServiceTrackerCustomizer<ResourceDriver, ResourceDriver> {
        @Override
        public ResourceDriver addingService(ServiceReference<ResourceDriver> reference) {
            synchronized (ResourceConnector.this) {
                ResourceDriver driver = context.getService(reference);
                modifiedService(reference, driver);
                return driver;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void modifiedService(ServiceReference<ResourceDriver> reference, ResourceDriver driver) {
            synchronized (ResourceConnector.this) {
                String applianceId = getApplianceId(reference);
                String oldApplianceId = managenedAppliance.get(driver);
                if (oldApplianceId != null && !oldApplianceId.equals(applianceId)) {
                    getApplianceService(oldApplianceId).removeDriver(driver);
                }
                if (applianceId != null) {
                    getApplianceService(applianceId).addDriver(driver);
                }
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void removedService(ServiceReference<ResourceDriver> reference, ResourceDriver driver) {
            synchronized (ResourceConnector.this) {
                String oldApplianceId = getApplianceId(reference);
                if (oldApplianceId != null) {
                    getApplianceService(oldApplianceId).removeDriver(driver);
                }
                context.ungetService(reference);
            }
        }
    }

    final class ResourceConnectorServlet extends HttpServlet {
        private static final long serialVersionUID = -7014372128039465147L;

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            PrintWriter writer = resp.getWriter();
            writer.write("<p class=\"statline ui-state-highlight\">Number of active appliances: ");
            writer.write(Integer.toString(applianceServices.size()));
            writer.write("</p>");
            writer.write("<table id=\"plugin_table\" class=\"nicetable noauto ui-widget\">");

            writer.write("<thead>");
            writer.write("<tr>");
            writer.write("<th class=\"ui-widget-header header\">Appliance ID</th>");
            writer.write("<th class=\"ui-widget-header header\">Resource Driver</th>");
            writer.write("<th class=\"ui-widget-header header\">Resource Manager</th>");
            writer.write("<th class=\"ui-widget-header header\">Resource Controller</th>");
            writer.write("</tr>");
            writer.write("</thead>");

            writer.write("<tbody class=\"ui-widget-content\">");
            int counter = 0;
            for (Entry<String, ApplianceService<?>> entry : applianceServices.entrySet()) {
                counter++;
                if (counter % 2 == 1) {
                    writer.write("<tr class=\"odd ui-state-default\"><td>");
                } else {
                    writer.write("<tr class=\"even ui-state-default\"><td>");
                }
                writer.write(entry.getKey());
                writer.write("</td><td>");
                writer.write(entry.getValue().drivers.toString());
                writer.write("</td><td>");
                writer.write(entry.getValue().managers.toString());
                writer.write("</td><td>");
                writer.write(String.valueOf(entry.getValue().controller));
                writer.write("</td></tr>");
            }
            writer.write("</table>");
        }
    }

    final Map<String, ApplianceService<?>> applianceServices;

    final Map<ControllerManager, Set<String>> controlledAppliances;
    final Map<ResourceManager<?>, String> managenedAppliance;
    final Map<ResourceDriver<?, ?>, String> drivenAppliance;

    public ResourceConnector() {
        applianceServices = new HashMap<String, ApplianceService<?>>();

        controlledAppliances = new HashMap<ControllerManager, Set<String>>();
        managenedAppliance = new HashMap<ResourceManager<?>, String>();
        drivenAppliance = new HashMap<ResourceDriver<?, ?>, String>();
    }

    @SuppressWarnings("rawtypes")
    private ApplianceService<?> getApplianceService(String applianceId) {
        if (applianceId == null) {
            return null;
        }
        if (!applianceServices.containsKey(applianceId)) {
            applianceServices.put(applianceId, new ApplianceService(applianceId));
        }
        return applianceServices.get(applianceId);
    }

    private BundleContext context;

    private ServiceTracker<ControllerManager, ControllerManager> controllerTracker;
    @SuppressWarnings("rawtypes")
    private ServiceTracker<ResourceManager, ResourceManager> managerTracker;
    @SuppressWarnings("rawtypes")
    private ServiceTracker<ResourceDriver, ResourceDriver> driverTracker;

    private ServiceRegistration<Servlet> servletRegistration;

    @SuppressWarnings("rawtypes")
    @Activate
    public void init(BundleContext context) {
        this.context = context;

        controllerTracker = new ServiceTracker<ControllerManager, ControllerManager>(context,
                                                                                     ControllerManager.class,
                                                                                     new ControllerTracker());
        managerTracker = new ServiceTracker<ResourceManager, ResourceManager>(context,
                                                                              ResourceManager.class,
                                                                              new ManagerTracker());
        driverTracker = new ServiceTracker<ResourceDriver, ResourceDriver>(context,
                                                                           ResourceDriver.class,
                                                                           new DriverTracker());

        controllerTracker.open();
        managerTracker.open();
        driverTracker.open();

        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put("felix.webconsole.title", "FPAI: Resources");
        properties.put("felix.webconsole.label", "fpai-resources");
        properties.put("felix.webconsole.css", "");
        try {
            servletRegistration = context.registerService(Servlet.class, new ResourceConnectorServlet(), properties);
        } catch (NoClassDefFoundError ex) {
            // This means that the Servlet class can not be found. Ignore and run without GUI.
        }
    }

    @Deactivate
    public void destroy() {
        controllerTracker.close();
        managerTracker.close();
        driverTracker.close();

        controllerTracker = null;
        managerTracker = null;
        driverTracker = null;
        context = null;

        if (servletRegistration != null) {
            servletRegistration.unregister();
        }
    }

    @SuppressWarnings("unchecked")
    private Set<String> getApplianceIds(ServiceReference<?> reference) {
        Object property = reference.getProperty(KEY_APPLIANCE_IDS);
        if (property == null) {
            logger.warn("The applianceIds property has not been set of service " + reference.getProperty(Constants.SERVICE_ID));
            return Collections.emptySet();
        } else if (property instanceof String[]) {
            return new HashSet<String>(Arrays.asList((String[]) property));
        } else if (property instanceof Collection) {
            return new HashSet<String>((Collection<String>) property);
        } else if (property instanceof String) {
            return new HashSet<String>(Arrays.asList(property.toString()));
        } else {
            logger.warn("The applianceIds property is not of the correct type (String[] or List<String>) of service " + reference.getProperty(Constants.SERVICE_ID));
            return Collections.emptySet();
        }
    }

    private String getApplianceId(ServiceReference<?> reference) {
        Object property = reference.getProperty(KEY_APPLIANCE_ID);
        if (property == null) {
            logger.warn("The applianceId property has not been set for service " + reference.getProperty(Constants.SERVICE_ID));
            return null;
        } else {
            return property.toString();
        }
    }
}
