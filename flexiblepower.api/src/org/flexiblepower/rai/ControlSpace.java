package org.flexiblepower.rai;

import java.util.Date;

public abstract class ControlSpace extends ResourceInfo {

    /**
     * start time instant of the interval [validFrom,validThru[ for which the control space is valid
     */
    private final Date validFrom;

    /**
     * optional end time instant of the interval [validFrom,validThru[ for which the control space is valid. If not
     * provided, we have the interval [validFrom, infinite[
     */
    private final Date validThru;

    /**
     * expiration time is an absolute time point (time instant) at which the creator of the control space will take
     * autonomously action when no allocation is received by then. It is optional, if not provided the validFrom and
     * validThru time instants can serve this purpose.
     */
    private final Date expirationTime;

    /**
     * construct control space
     * 
     * @param resourceId
     *            is creator of control space, is the manager of the resource
     * @param validFrom
     *            is the start time instant of the interval [validFrom,validThru[ for which the control space is valid
     * @param validThru
     *            is the end time instant of the interval [validFrom,validThru[ for which the control space is valid.
     *            This parameter is optional.
     * @param expirationTime
     *            is the time instant at which the creator will take autonomously action when no allocation is received
     *            by then. This parameter is optional.
     * 
     * @throws NullPointerException
     *             when resourceManager or validFrom is null
     * @throws IllegalArgumentException
     *             when validFrom is not before validThru
     */
    public ControlSpace(String resourceId, Date validFrom, Date validThru, Date expirationTime) {
        super(resourceId);
        this.validFrom = validFrom;
        this.validThru = validThru;
        this.expirationTime = expirationTime;

        if (validFrom == null) {
            throw new NullPointerException("validFrom is null");
        }
        // validThru is optional
        if (validThru != null && !validFrom.before(validThru)) {
            throw new IllegalArgumentException("validFrom " + validFrom + " is not before validThru " + validThru);
        }
    }

    /**
     * construct control space
     * 
     * @param resourceId
     *            is creator of control space, is the manager of the resource
     * @param validFrom
     *            is the start time instant of the interval [validFrom,validThru[ for which the control space is valid
     * @param validThru
     *            is the end time instant of the interval [validFrom,validThru[ for which the control space is valid.
     *            This parameter is optional.
     * 
     * @throws NullPointerException
     *             when validFrom, validThru or the resourceManager is null
     * @throws IllegalArgumentException
     *             when validFrom is not before validThru
     */
    public ControlSpace(String resourceId, Date validFrom, Date validThru) {
        this(resourceId, validFrom, validThru, null);
    }

    public ControlSpace(ControlSpace controlSpace) {
        super(controlSpace);
        validFrom = controlSpace.validFrom;
        validThru = controlSpace.validThru;
        expirationTime = controlSpace.expirationTime;
    }

    /**
     * The expiration time is optional.
     * 
     * @return expiration time, or null if not provided
     */
    public Date getExpirationTime() {
        return expirationTime;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * The attribute validTru is optional
     * 
     * @return validThru or null if not provided
     */
    public Date getValidThru() {
        return validThru;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        } else {
            ControlSpace other = (ControlSpace) obj;
            if (expirationTime == null && other.expirationTime != null) {
                return false;
            } else if (expirationTime != null && !expirationTime.equals(other.expirationTime)) {
                return false;
            } else if (!validFrom.equals(other.validFrom)) {
                return false;
            } else if (validThru == null && other.validThru != null) {
                return false;
            } else if (validThru != null && !validThru.equals(other.validThru)) {
                return false;
            }
            return true;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((expirationTime == null) ? 0 : expirationTime.hashCode());
        result = prime * result + ((validFrom == null) ? 0 : validFrom.hashCode());
        result = prime * result + ((validThru == null) ? 0 : validThru.hashCode());
        return result;
    }
}
