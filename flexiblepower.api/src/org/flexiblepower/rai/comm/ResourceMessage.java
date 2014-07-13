package org.flexiblepower.rai.comm;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public abstract class ResourceMessage implements Serializable {

    private static final long serialVersionUID = -313146669543611880L;

    private final UUID resourceMessageId;

    private final String resourceId;

    /** Time of creation of the message. */
    private final Date timestamp;

    public ResourceMessage(String resourceId, Date timestamp) {
        super();
        resourceMessageId = UUID.randomUUID();
        this.resourceId = resourceId;
        this.timestamp = timestamp;
    }

    public UUID getResourceMessageId() {
        return resourceMessageId;
    }

    public String getResourceId() {
        return resourceId;
    }

    /**
     * Gets the creation time of this message.
     * 
     * @return Date on which this message was created.
     */
    public Date getTimestamp() {
        return timestamp;
    }

}
