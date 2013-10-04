package org.flexiblepower.provisioning;

/**
 * The {@link AppPermission} is a data object that describes a single permission that is needed for {@link AppInfo}. It
 * store 2 parameters, first is the technical permission itself, which should be parseable by the
 * {@link org.osgi.service.permissionadmin.PermissionInfo} object. The second is the human-readable description of that
 * permission.
 */
public class AppPermission {
    private final String permission;
    private final String description;

    /**
     * Creates a new AppPermission.
     * 
     * @param permission
     *            The technical permission as stored in the permissions file
     * @param description
     *            The human-readable description
     */
    public AppPermission(String permission, String description) {
        this.permission = permission;
        this.description = description;
    }

    /**
     * @return The technical permission as stored in the permissions file
     */
    public String getPermission() {
        return permission;
    }

    /**
     * @return The human-readable description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @see java.lang.Object#toString()
     * @return The human-readable description
     */
    @Override
    public String toString() {
        return description;
    }
}
