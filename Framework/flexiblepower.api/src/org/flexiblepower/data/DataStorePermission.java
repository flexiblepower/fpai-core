package org.flexiblepower.data;

import java.security.Permission;

public final class DataStorePermission extends Permission {
    private static final long serialVersionUID = 7109497170446695772L;

    private final int READ = 0x1;
    private final int WRITE = 0x2;

    private final int action;

    public DataStorePermission(String name) {
        this(name, "read");
    }

    public DataStorePermission(String name, String action) {
        super(name);

        String[] actions = action.split(",");
        int result = 0;
        for (String a : actions) {
            a = a.toLowerCase().trim();
            if ("read".equals(a)) {
                result |= READ;
            } else if ("write".equals(a)) {
                result |= WRITE;
            } else if ("*".equals(a)) {
                result = READ | WRITE;
            }
        }
        this.action = result;
    }

    @Override
    public String getActions() {
        switch (action) {
        case READ | WRITE:
            return "read,write";
        case READ:
            return "read";
        case WRITE:
            return "write";
        default:
            return "";
        }
    }

    @Override
    public boolean implies(Permission permission) {
        if (permission instanceof DataStorePermission) {
            DataStorePermission dataServicePermission = (DataStorePermission) permission;
            if ("*".equals(getName()) || getName().equals(permission.getName())) {
                return (dataServicePermission.action & action) == dataServicePermission.action;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == DataStorePermission.class) {
            DataStorePermission otherPermission = (DataStorePermission) obj;
            return getName().equals(otherPermission.getName()) && otherPermission.action == action;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 37 * getName().hashCode() * action;
    }
}
