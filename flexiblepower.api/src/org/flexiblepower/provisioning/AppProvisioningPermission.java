package org.flexiblepower.provisioning;

import java.security.BasicPermission;

public class AppProvisioningPermission extends BasicPermission {
    private static final long serialVersionUID = -6702746242654331615L;

    public AppProvisioningPermission() {
        super("*");
    }
}
