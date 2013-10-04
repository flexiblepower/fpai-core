package org.flexiblepower.provisioning;

import java.net.URI;
import java.util.List;

public interface AppInfo {
    String getId();

    String getName();

    String getDescription();

    List<URI> getBundleLocations();

    List<AppPermission> getAcceptedPermissions();
}
