package org.flexiblepower.rai;

import java.util.UUID;

public abstract class ResourceInfo {

    /**
     * Unique identifier for a {@link ResourceInfo}.
     */
    private final UUID id;

    /**
     * Identifier of the appliance.
     */
    private final String applianceId;

    /**
     * Constructs a new {@link ResourceInfo} with a random id.
     * 
     * @param applianceId
     *            Identifier of the appliance
     * 
     * @throws IllegalArgumentException
     *             when applianceId is null or an empty string
     */
    public ResourceInfo(String applianceId) {
        if (applianceId == null || applianceId.trim().length() == 0) {
            throw new IllegalArgumentException("resourceId is empty string");
        }

        id = UUID.randomUUID();
        this.applianceId = applianceId;
    }

    /**
     * The copy constructor.
     * 
     * @param resourceInfo
     *            The base object that should be copied.
     */
    ResourceInfo(ResourceInfo resourceInfo) {
        id = resourceInfo.id;
        applianceId = resourceInfo.applianceId;
    }

    public UUID getId() {
        return id;
    }

    public String getApplianceId() {
        return applianceId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            ResourceInfo other = (ResourceInfo) obj;
            if (!applianceId.equals(other.applianceId)) {
                return false;
            } else if (!id.equals(other.id)) {
                return false;
            }
            return true;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((applianceId == null) ? 0 : applianceId.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + id + ", applianceId=" + applianceId + "]";
    }
}
