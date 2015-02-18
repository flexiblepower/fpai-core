package org.flexiblepower.ral.messages;

import java.util.Date;
import java.util.UUID;

/**
 * The super class for all messages of the Resource Abstraction Interface. This contains:
 *
 * <ul>
 * <li><em>resourceMessageId</em> An identifier that uniquely identifies this message object.
 * <li><em>resourceId</em> An identifier that uniquely identifies the resource that this message contains information
 * about.
 * <li><em>timestamp</em> This timestamp indicates the moment in time this message was constructed.
 * </ul>
 *
 * All types of {@link ResourceMessage}s should be immutable, such that they can safely passed between components and
 * threads.
 */
public abstract class ResourceMessage {
    private final UUID resourceMessageId;
    private final String resourceId;
    private final Date timestamp;

    /**
     * Constructs a new ResourceMessage. This will generate a unique message identier.
     *
     * @param resourceId
     *            The resource identifier
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     */
    public ResourceMessage(String resourceId, Date timestamp) {
        if (resourceId == null) {
            throw new NullPointerException("resourceId");
        } else if (timestamp == null) {
            throw new NullPointerException("timestamp");
        }

        resourceMessageId = UUID.randomUUID();
        this.resourceId = resourceId;
        this.timestamp = timestamp;
    }

    /**
     * @return An identifier that uniquely identifies this message object.
     */
    public UUID getResourceMessageId() {
        return resourceMessageId;
    }

    /**
     * @return An identifier that uniquely identifies the resource that this message contains information about.
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * @return This timestamp indicates the moment in time this message was constructed.
     */
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + resourceId.hashCode();
        result = prime * result + resourceMessageId.hashCode();
        result = prime * result + timestamp.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ResourceMessage other = (ResourceMessage) obj;
        if (!resourceId.equals(other.resourceId)) {
            return false;
        } else if (!resourceMessageId.equals(other.resourceMessageId)) {
            return false;
        } else if (!timestamp.equals(other.timestamp)) {
            return false;
        }
        return true;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append('[');
        toString(sb);
        if (sb.charAt(sb.length() - 1) == ' ' && sb.charAt(sb.length() - 2) == ',') {
            sb.setLength(sb.length() - 2);
        }
        return sb.append(']').toString();
    }

    protected void toString(StringBuilder sb) {
        sb.append("resourceMessageId=").append(resourceMessageId).append(", ");
        sb.append("resourceId=").append(resourceId).append(", ");
        sb.append("timestamp=").append(timestamp).append(", ");
    }
}
