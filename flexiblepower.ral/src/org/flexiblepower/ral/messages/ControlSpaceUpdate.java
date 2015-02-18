package org.flexiblepower.ral.messages;

import java.util.Date;

/**
 * This message contains information about the flexibility an appliance has to offer. Just like the
 * {@link ControlSpaceRegistration} message, each of the ControlSpace categories will have its own update class or
 * classes that are derived from this {@link ControlSpaceUpdate} message.
 */
public abstract class ControlSpaceUpdate extends ResourceMessage {
    private final Date validFrom;

    /**
     * Constructs a new {@link ControlSpaceUpdate} message with the specific validFrom
     *
     * @param resourceId
     *            The resource identifier
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     * @param validFrom
     *            This timestamp indicates from which moment on this update is valid.
     */
    public ControlSpaceUpdate(String resourceId, Date timestamp, Date validFrom) {
        super(resourceId, timestamp);
        if (validFrom == null) {
            throw new NullPointerException("validFrom");
        }

        this.validFrom = validFrom;
    }

    /**
     * @return This timestamp indicates from which moment on this update is valid.
     */
    public Date getValidFrom() {
        return validFrom;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + validFrom.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        ControlSpaceUpdate other = (ControlSpaceUpdate) obj;
        return other.validFrom.equals(validFrom);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("validFrom=").append(validFrom).append(", ");
    }
}
