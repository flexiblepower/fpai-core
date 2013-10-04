package org.flexiblepower.appstore.common;

import java.util.ArrayList;
import java.util.List;

public class Component {
    private String symbolicName;
    private String version;
    private String jarFile;
    private List<String> permissions;

    public Component() {
    }

    public Component(String symbolicName, String version, String jarFile, List<String> permissions) {
        this.symbolicName = symbolicName;
        this.version = version;
        this.jarFile = jarFile;
        this.permissions = permissions;
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    public String getVersion() {
        return version;
    }

    public String getJarFile() {
        return jarFile;
    }

    public List<String> getPermissions() {
        return new ArrayList<String>(permissions);
    }

    @Override
    public String toString() {
        return "Component [symbolicName=" + symbolicName
               + ", version="
               + version
               + ", jarFile="
               + jarFile
               + ", permissions="
               + permissions
               + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((jarFile == null) ? 0 : jarFile.hashCode());
        result = prime * result + ((permissions == null) ? 0 : permissions.hashCode());
        result = prime * result + ((symbolicName == null) ? 0 : symbolicName.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Component other = (Component) obj;
        if (jarFile == null) {
            if (other.jarFile != null) {
                return false;
            }
        } else if (!jarFile.equals(other.jarFile)) {
            return false;
        }
        if (permissions == null) {
            if (other.permissions != null) {
                return false;
            }
        } else if (!permissions.equals(other.permissions)) {
            return false;
        }
        if (symbolicName == null) {
            if (other.symbolicName != null) {
                return false;
            }
        } else if (!symbolicName.equals(other.symbolicName)) {
            return false;
        }
        if (version == null) {
            if (other.version != null) {
                return false;
            }
        } else if (!version.equals(other.version)) {
            return false;
        }
        return true;
    }
}
