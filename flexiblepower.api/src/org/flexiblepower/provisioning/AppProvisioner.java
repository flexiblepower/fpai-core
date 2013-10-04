package org.flexiblepower.provisioning;

import org.flexiblepower.data.applications.App;

public interface AppProvisioner {

    /**
     * Starts the provisioning of the given {@link App}. This provisioning itself should be done asynchronously,
     * during which the returned {@link AppProvisioningStatus} object can be used to get the status.
     * 
     * @param app
     *            The {@link App} that should be installed.
     * @return The {@link AppProvisioningStatus} object that can be used to monitor the progress of the installation.
     */
    AppProvisioningStatus provision(App app);

}
