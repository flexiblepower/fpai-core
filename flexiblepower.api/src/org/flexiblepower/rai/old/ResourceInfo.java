package org.flexiblepower.rai.old;

import java.util.UUID;

/**
 * This is the super-class of both the {@link ControlSpace} and the {@link Allocation}. It describes the unique id that
 * they should have and the resource identifier.
 * 
 * @author TNO
 */
public abstract class ResourceInfo {
    private final UUID id;
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

    /**
     * @return The unique identifier for a {@link ResourceInfo}.
     */
    public UUID getId() {
        return id;
    }

    /**
     * @return The identifier of the resource to which the {@link ControlSpace} or {@link Allocation} refers.
     */
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
        result = prime * result + resourceId.hashCode();
        result = prime * result + id.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + id + ", resourceId=" + resourceId + "]";
    }
}
