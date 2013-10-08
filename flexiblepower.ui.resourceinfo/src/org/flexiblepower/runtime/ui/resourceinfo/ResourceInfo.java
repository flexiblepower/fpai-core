package org.flexiblepower.runtime.ui.resourceinfo;

/**
 * Data-object containing information on a resource.
 */
public class ResourceInfo {
    private String id;
    private String type;
    private boolean isManaged;

    public ResourceInfo() {
    }

    public ResourceInfo(String id, String type, boolean isManaged) {
        this.id = id;
        this.type = type;
        this.isManaged = isManaged;
    }

    public boolean isManaged() {
        return isManaged;
    }

    public void setManaged(boolean isManaged) {
        this.isManaged = isManaged;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
