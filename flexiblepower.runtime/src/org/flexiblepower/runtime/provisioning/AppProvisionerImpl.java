package org.flexiblepower.runtime.provisioning;

import java.util.Locale;
import java.util.Map;

import org.flexiblepower.provisioning.AppInfo;
import org.flexiblepower.provisioning.AppProvisioner;
import org.flexiblepower.provisioning.AppProvisioningException;
import org.flexiblepower.provisioning.AppProvisioningStatus;
import org.flexiblepower.time.SchedulerService;
import org.osgi.framework.BundleContext;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component
public class AppProvisionerImpl implements AppProvisioner {
    private static final Logger logger = LoggerFactory.getLogger(AppProvisionerImpl.class);

    private PermissionAdmin permAdmin;

    @Reference(optional = true)
    public void setPermissionAdmin(PermissionAdmin permAdmin) {
        this.permAdmin = permAdmin;
    }

    private SchedulerService schedulerService;

    @Reference
    public void setExecutorService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    private BundleContext bundleContext;

    @Activate
    public void init(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public Map<String, AppInfo> getInstalledApps() {
        return null;
    }

    @Override
    public AppProvisioningStatus provision(AppInfo app) {
        Provisioner provisioner = new Provisioner(app);
        schedulerService.execute(provisioner);
        return provisioner;
    }

    public class Provisioner implements AppProvisioningStatus, Runnable {
        private final AppInfo app;
        private final String currentAction;
        private final int status;
        private AppProvisioningException error;

        public Provisioner(AppInfo app) {
            this.app = app;
            currentAction = "Preparing installation";
            status = 0;
        }

        @Override
        public AppInfo getApp() {
            return app;
        }

        @Override
        public String getCurrentAction(Locale locale) {
            return currentAction;
        }

        @Override
        public int getStatus() {
            return status;
        }

        @Override
        public AppProvisioningException getError() {
            return error;
        }

        private void logCurrentAction(String component) {
            logger.debug("Installing " + app.getId()
                         + "("
                         + status
                         + "%): "
                         + currentAction
                         + (component == null ? "" : (" [" + component + "]")));
        }

        @Override
        public void run() {
            // synchronized (AppProvisionerImpl.this) {
            // List<Bundle> bundles = new ArrayList<Bundle>(app.getBundleLocations().size());
            // try {
            // currentAction = "Installing components";
            // logCurrentAction(null);
            // for (URI bundleLocation : app.getBundleLocations()) {
            // try {
            // HttpsURLConnection connection = (HttpsURLConnection) bundleLocation.toURL()
            // .openConnection();
            // connection.setSSLSocketFactory(securityManager.getSSLSocketFactory());
            // bundles.add(bundleContext.installBundle(bundleLocation.toString(),
            // connection.getInputStream()));
            // } catch (BundleException e) {
            // throw new AppProvisioningException("Could not install bundle from URL: " + bundleLocation,
            // e,
            // bundleLocation.toString());
            // } catch (SecurityException e) {
            // throw new AppProvisioningException("Could not install bundle from URL: " + bundleLocation,
            // e,
            // bundleLocation.toString());
            // } catch (IOException e) {
            // throw new AppProvisioningException("Could not install bundle from URL: " + bundleLocation,
            // e,
            // bundleLocation.toString());
            // }
            // status = (33 * bundles.size()) / app.getBundleLocations().size();
            // logCurrentAction(bundleLocation.toString());
            // }
            //
            // currentAction = "Security check (permissions)";
            // logCurrentAction(null);
            // if (permAdmin != null) {
            // Collection<PermissionInfo> acceptedPermissions = app.getAcceptedPermissions();
            // logger.debug("Accepted permissions: " + acceptedPermissions);
            // int count = 0;
            // for (Bundle bundle : bundles) {
            // PermissionInfo[] permissions = permAdmin.getPermissions(bundle.getLocation());
            // logger.debug("Needed permissions for " + bundle.getLocation()
            // + " : "
            // + Arrays.toString(permissions));
            // if (permissions != null && !acceptedPermissions.containsAll(Arrays.asList(permissions))) {
            // throw new AppProvisioningException("One of the components asked for more permissions than needed",
            // bundle.getLocation());
            // }
            // status = 33 + (17 * ++count) / bundles.size();
            // logCurrentAction(bundle.getLocation());
            // }
            // }
            //
            // currentAction = "Security check (signing of components)";
            // logCurrentAction(null);
            // if (securityManager != null) {
            // int count = 0;
            // for (Bundle bundle : bundles) {
            // try {
            // securityManager.checkBundleSignature(bundle,
            // "CN=Marc de Jonge, OU=Service Enabling & Management, O=TNO, L=Groningen, ST=Groningen, C=NL");
            // status = 50 + (17 * ++count) / bundles.size();
            // logCurrentAction(bundle.getLocation());
            // } catch (SecurityException ex) {
            // throw new AppProvisioningException("One of the components was not signed correctly",
            // bundle.getLocation());
            // }
            // }
            // }
            //
            // currentAction = "Starting components";
            // logCurrentAction(null);
            // int count = 0;
            // for (Bundle bundle : bundles) {
            // if (bundle.getState() == Bundle.INSTALLED || bundle.getState() == Bundle.RESOLVED) {
            // try {
            // bundle.start();
            // } catch (BundleException e) {
            // throw new AppProvisioningException("One of the components could not be started",
            // e,
            // bundle.getLocation());
            // }
            // }
            // status = 67 + (33 * ++count) / bundles.size();
            // logCurrentAction(bundle.getLocation());
            // }
            //
            // status = 100;
            // } catch (AppProvisioningException ex) {
            // logger.warn("Installation of app " + app.getId()
            // + " failed on bundle "
            // + ex.getBundleLocation()
            // + ": "
            // + ex.getMessage(), ex);
            // for (Bundle bundle : bundles) {
            // if (bundle.getState() == Bundle.INSTALLED || bundle.getState() == Bundle.RESOLVED) {
            // try {
            // bundle.uninstall();
            // } catch (BundleException e) {
            // logger.warn("Could not uninstall the bundle " + bundle.getLocation());
            // }
            // }
            // }
            // error = ex;
            // status = 100;
            // } catch (Throwable t) {
            // logger.warn("Installation of app " + app.getId() + " failed of unknown causes: " + t.getMessage(),
            // t);
            // for (Bundle bundle : bundles) {
            // if (bundle.getState() == Bundle.INSTALLED || bundle.getState() == Bundle.RESOLVED) {
            // try {
            // bundle.uninstall();
            // } catch (BundleException e) {
            // logger.warn("Could not uninstall the bundle " + bundle.getLocation());
            // }
            // }
            // }
            // error = new AppProvisioningException("Installation of app " + app.getId()
            // + " failed of unknown causes", t, null);
            // status = 100;
            //
            // }
            // }
        }
    }
}
