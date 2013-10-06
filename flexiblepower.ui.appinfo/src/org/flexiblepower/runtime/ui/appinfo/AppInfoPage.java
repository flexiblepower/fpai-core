package org.flexiblepower.runtime.ui.appinfo;

import java.net.URI;
import java.util.Locale;
import java.util.Map;

import org.flexiblepower.provisioning.AppInfo;
import org.flexiblepower.provisioning.AppProvisioner;
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

    private AppProvisioner appProvisioner;

    @Reference
    public void setAppProvisioner(AppProvisioner appProvisioner) {
        this.appProvisioner = appProvisioner;
    }

    public Map<String, AppInfo> getApps(Locale locale) {
        return appProvisioner.getInstalledApps();
    }

    public void stopApp(AppInfo appInfo) {
        for (URI location : appInfo.getBundleLocations()) {
            Bundle bundle = bundleContext.getBundle(location.toString());
            if (bundle != null && bundle.getState() == Bundle.ACTIVE) {
                try {
                    bundle.stop();
                } catch (BundleException ex) {
                    logger.warn("Could not stop bundle: " + bundle, ex);
                }
            }
        }
    }

    public void startApp(AppInfo appInfo) {
        for (URI location : appInfo.getBundleLocations()) {
            Bundle bundle = bundleContext.getBundle(location.toString());
            if (bundle != null && bundle.getState() == Bundle.RESOLVED) {
                try {
                    bundle.start();
                } catch (BundleException ex) {
                    logger.warn("Could not start bundle: " + bundle, ex);
                }
            }
        }
    }

    @Override
    public String getTitle(Locale locale) {
        return "App Info";
    }
}
