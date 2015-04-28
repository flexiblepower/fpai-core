package org.flexiblepower.runtime.ui.connectionspage;

import java.util.Hashtable;
import java.util.Map;

import javax.servlet.Servlet;

import org.flexiblepower.messaging.ConnectionManager;
import org.flexiblepower.ui.Widget;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;
import aQute.bnd.annotation.metatype.Configurable;
import aQute.bnd.annotation.metatype.Meta;

@Component(designate = ConnectionsPage.Config.class, configurationPolicy = ConfigurationPolicy.optional)
public class ConnectionsPage {
    @Meta.OCD(description = "Configuration for the ConnectionManager widgets",
              name = "ConnectionManager UI Configuration")
    public interface Config {
        @Meta.AD(deflt = "true", description = "Should the plugin be shown in the felix dashboard?", required = false)
        boolean felixPluginActive();

        @Meta.AD(deflt = "false", description = "Should the plugin be shown in the FPAI dashboard?", required = false)
        boolean dashboardWidgetActive();
    }

    private ConnectionManager connectionManager;
    private ServiceRegistration<Widget> dashboardWidgetRegistration;
    private ServiceRegistration<Servlet> felixPluginRegistration;

    @Reference
    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Activate
    public void activate(BundleContext context, Map<String, Object> properties) {
        Config config = Configurable.createConfigurable(Config.class, properties);

        if (config.dashboardWidgetActive()) {
            try {
                DashboardWidget dashboardWidget = new DashboardWidget(connectionManager, context);

                Hashtable<String, Object> widgetProperties = new Hashtable<String, Object>();
                widgetProperties.put("widget.type", "full");
                widgetProperties.put("widget.name", "connection-manager");
                dashboardWidgetRegistration = context.registerService(Widget.class, dashboardWidget, widgetProperties);
            } catch (NoClassDefFoundError error) {
                // this could happen if there is no FPAI dashboard loaded, just ignore the start then
            }
        }

        if (config.felixPluginActive()) {
            try {
                FelixPlugin felixPlugin = new FelixPlugin(connectionManager, context);

                Hashtable<String, Object> widgetProperties = new Hashtable<String, Object>();
                widgetProperties.put("felix.webconsole.category", "FPAI");
                widgetProperties.put("felix.webconsole.label", felixPlugin.getLabel());
                widgetProperties.put("felix.webconsole.title", felixPlugin.getTitle());
                felixPluginRegistration = context.registerService(Servlet.class, felixPlugin, widgetProperties);
            } catch (NoClassDefFoundError error) {
                // this could happen if there is no felix dashboard loaded, just ignore the start then
            }
        }
    }

    @Deactivate
    public void deactivate() {
        if (dashboardWidgetRegistration != null) {
            dashboardWidgetRegistration.unregister();
            dashboardWidgetRegistration = null;
        }

        if (felixPluginRegistration != null) {
            felixPluginRegistration.unregister();
            felixPluginRegistration = null;
        }
    }
}
