package org.flexiblepower.data.applications;

import java.net.URI;
import java.util.List;

import org.flexiblepower.data.IdentifyableObject;
import org.osgi.service.permissionadmin.PermissionInfo;

public interface App extends IdentifyableObject {
    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    List<URI> getBundleLocations();

    void setBundleLocations(List<URI> bundleLocations);

    List<PermissionInfo> getAcceptedPermissions();

    void setAcceptedPermissions(List<PermissionInfo> acceptedPermissions);
}
