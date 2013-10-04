package org.flexiblepower.runtime.ui.applianceinfo;

public class ApplianceInfo {
    private String id;
    private String identification;
    private String type;
    private boolean isManaged;

    public ApplianceInfo() {
    }

    public ApplianceInfo(String id, String identification, String type, boolean isManaged) {
        this.id = id;
        this.identification = identification;
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

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
