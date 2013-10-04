package org.flexiblepower.runtime.ui.appinfo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.flexiblepower.data.applications.App;
import org.flexiblepower.data.applications.AppDataStore;
import org.flexiblepower.ui.Widget;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(properties = { "widget.type=full", "widget.name=appinfo" })
public class AppInfoPage implements Widget {
    private static final Logger logger = LoggerFactory.getLogger(AppInfoPage.class);

    private BundleContext bundleContext;

    @Activate
    public void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    private AppDataStore appDataStore;

    @Reference
    public void setAppDataStore(AppDataStore appDataStore) {
        this.appDataStore = appDataStore;
    }

    public Map<String, AppInfo> getApps(Locale locale) {
        Map<String, AppInfo> result = new TreeMap<String, AppInfo>();

        try {
            for (String appId : appDataStore.keySet()) {
                App app = appDataStore.get(appId);

                boolean isRunning = false;
                List<String> components = new ArrayList<String>();
                for (URI bundleLocation : app.getBundleLocations()) {
                    Bundle bundle = bundleContext.getBundle(bundleLocation.toString());
                    if (bundle != null) {
                        isRunning = true;
                        components.add(bundle.getSymbolicName());
                    }
                }

                result.put(app.getId(), new AppInfo(app.getId(),
                                                    app.getName(),
                                                    app.getDescription(),
                                                    components,
                                                    isRunning));
            }
        } catch (Exception ex) {
            logger.warn("Could not load apps.", ex);
        }

        return result;
    }

    public void stopApp(AppInfo appInfo) {
        for (String comp : appInfo.getComponents()) {
            Bundle bundle = bundleContext.getBundle(comp);
            if (bundle != null && bundle.getState() == Bundle.ACTIVE) {
                try {
                    bundle.stop();
                } catch (BundleException e) {
                    logger.warn("Could not stop bundle: " + bundle, e);
                }
            }
        }
    }

    public void startApp(AppInfo appInfo) {
        for (String comp : appInfo.getComponents()) {
            Bundle bundle = bundleContext.getBundle(comp);
            if (bundle != null && bundle.getState() == Bundle.RESOLVED) {
                try {
                    bundle.start();
                } catch (BundleException e) {
                    logger.warn("Could not start bundle: " + bundle, e);
                }
            }
        }
    }

    @Override
    public String getTitle(Locale locale) {
        return "App Info";
    }
}
