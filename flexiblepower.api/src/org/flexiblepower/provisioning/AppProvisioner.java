package org.flexiblepower.provisioning;

import java.util.Map;

public interface AppProvisioner {
    /**
     * Starts the provisioning of the given {@link AppInfo}. This provisioning itself should be done asynchronously,
     * during which the returned {@link AppProvisioningStatus} object can be used to get the status.
     * 
     * @param app
     *            The {@link AppInfo} that should be installed.
     * @return The {@link AppProvisioningStatus} object that can be used to monitor the progress of the installation.
     */
    AppProvisioningStatus provision(AppInfo app);

    /**
     * @return All the apps that are currently installed. This is returned in a read-only map with the identifier as a
     *         key.
     */
    Map<String, AppInfo> getInstalledApps();
}
