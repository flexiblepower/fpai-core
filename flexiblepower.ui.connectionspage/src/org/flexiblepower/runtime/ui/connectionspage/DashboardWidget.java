package org.flexiblepower.runtime.ui.connectionspage;

import java.util.Locale;

import org.flexiblepower.messaging.ConnectionManager;
import org.flexiblepower.ui.Widget;
import org.osgi.framework.BundleContext;

public class DashboardWidget extends BaseWidget implements Widget {
    public DashboardWidget(ConnectionManager connectionManager, BundleContext bundleContext) {
        super(connectionManager, bundleContext);
    }

    @Override
    public String getTitle(Locale locale) {
        return "Connection Manager";
    }
}
