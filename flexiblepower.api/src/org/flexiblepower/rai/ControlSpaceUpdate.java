package org.flexiblepower.rai;

import java.util.Date;

import org.flexiblepower.time.TimeService;

/**
 * This message contains information about the flexibility an appliance has to offer. Just like the
 * {@link ControlSpaceRegistration} message, each of the ControlSpace categories will have its own update class or
 * classes that are derived from this {@link ControlSpaceUpdate} message.
 */
public abstract class ControlSpaceUpdate extends ResourceMessage {
    private static final long serialVersionUID = -242149664875591012L;

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
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((validFrom == null) ? 0 : validFrom.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        ControlSpaceUpdate other = (ControlSpaceUpdate) obj;
        if (validFrom == null) {
            if (other.validFrom != null) {
                return false;
            }
        } else if (!validFrom.equals(other.validFrom)) {
            return false;
        }
        return true;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("validFrom=").append(validFrom).append(", ");
    }
}
