package org.flexiblepower.rai;

import java.util.UUID;

public abstract class ResourceInfo {

    /**
     * Unique identifier for a {@link ResourceInfo}.
     */
    private final UUID id;

    /**
     * Identifier of the resource.
     */
    private final String resourceId;

    /**
     * Constructs a new {@link ResourceInfo} with a random id.
     * 
     * @param resourceId
     *            Identifier of the resource
     * 
     * @throws IllegalArgumentException
     *             when resourceId is null or an empty string
     */
    public ResourceInfo(String resourceId) {
        if (resourceId == null || resourceId.trim().length() == 0) {
            throw new IllegalArgumentException("resourceId is empty string");
        }

        id = UUID.randomUUID();
        this.resourceId = resourceId;
    }

    /**
     * The copy constructor.
     * 
     * @param resourceInfo
     *            The base object that should be copied.
     */
    ResourceInfo(ResourceInfo resourceInfo) {
        id = resourceInfo.id;
        resourceId = resourceInfo.resourceId;
    }

    public UUID getId() {
        return id;
    }

    public String getResourceId() {
        return resourceId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            ResourceInfo other = (ResourceInfo) obj;
            if (!resourceId.equals(other.resourceId)) {
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
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + id + ", resourceId=" + resourceId + "]";
    }
}
