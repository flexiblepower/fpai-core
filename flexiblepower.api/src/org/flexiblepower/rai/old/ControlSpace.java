package org.flexiblepower.rai.old;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Quantity;

import org.flexiblepower.rai.values.Constraint;
import org.flexiblepower.rai.values.ConstraintList;

/**
 * 
 * 
 * @author TNO
 */
public abstract class ControlSpace extends ResourceInfo {
    protected static final void validateNonNull(Object... objects) {
        for (Object o : objects) {
            if (o == null) {
                throw new NullPointerException();
            }
        }
    }

    protected static final void validateRange(double range, String name) {
        if (range < 0 || range > 1) {
            throw new IllegalArgumentException(name + " should be in [0,1] but is " + range);
        }
    }

    protected static final void validateTarget(Date targetTime, Double targetStateOfCharge) {
        if (targetTime == null) {
            if (targetStateOfCharge != null) {
                throw new IllegalArgumentException("targetTime is null, not allowed when targetStateOfCharge is specified");
            }
        } else {
            if (targetStateOfCharge == null) {
                throw new IllegalArgumentException("targetStateOfCharge is null, not allowed when targetTime is specified");
            } else {
                validateRange(targetStateOfCharge, "targetStateOfCharge");
            }
        }
    }

    protected static final <Q extends Quantity> void validateConstaintList(ConstraintList<Q> list,
                                                                           boolean acceptNegative) {
        boolean foundPositiveValues = false;
        boolean foundNegativeValue = false;
        final Measurable<Q> zero = Measure.zero();

        for (Constraint<Q> c : list) {
            if (c.getUpperBound().compareTo(zero) > 0) {
                foundPositiveValues = true;
            }
            if (c.getLowerBound().compareTo(zero) < 0) {
                foundNegativeValue = true;
            }
        }

        if (foundPositiveValues && foundNegativeValue) {
            throw new IllegalArgumentException("constraintlist should not contain both negative and positive values, see the storage type for this behavior");
        }
        if (!acceptNegative && foundNegativeValue) {
            throw new IllegalArgumentException("constraintlist should not contain negative value");
        }
    }

    private final Date validFrom;
    private final Date validThru;

    /**
     * expiration time is an absolute time point (time instant) at which the creator of the control space will take
     * autonomously action when no allocation is received by then. It is optional, if not provided the validFrom and
     * validThru time instants can serve this purpose.
     */
    private final Date expirationTime;

    /**
     * Creates a new ControlSpace.
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
     *             when resourceId or validFrom is null
     * @throws IllegalArgumentException
     *             when validFrom is not before validThru
     */
    protected ControlSpace(String resourceId, Date validFrom, Date validThru, Date expirationTime) {
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
     * Creates a new ControlSpace, without any expiration time.
     * 
     * @param resourceId
     *            is creator of control space. This is the identifier of the resource that has created the ControlSpace.
     * @param validFrom
     *            is the start time instant of the interval [validFrom,validThru[ for which the control space is valid
     * @param validThru
     *            is the end time instant of the interval [validFrom,validThru[ for which the control space is valid.
     *            This parameter is optional.
     * 
     * @throws NullPointerException
     *             when validFrom or the resourceManager is null
     * @throws IllegalArgumentException
     *             when validFrom is not before validThru
     */
    protected ControlSpace(String resourceId, Date validFrom, Date validThru) {
        this(resourceId, validFrom, validThru, null);
    }

    /**
     * Copy constructor.
     * 
     * @param controlSpace
     *            The {@link ControlSpace} that is to be copied.
     */
    protected ControlSpace(ControlSpace controlSpace) {
        super(controlSpace);
        validFrom = controlSpace.validFrom;
        validThru = controlSpace.validThru;
        expirationTime = controlSpace.expirationTime;
    }

    /**
     * @return The expiration time is an absolute time point (time instant) at which the creator of the control space
     *         will take autonomously action when no allocation is received by then. It is optional, if not provided the
     *         resource will never take autonomous actions.
     */
    public Date getExpirationTime() {
        return expirationTime;
    }

    /**
     * @return The start time instant of the interval [validFrom,validThru[ for which the control space is valid.
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * @return The optional end time instant of the interval [validFrom,validThru[ for which the control space is valid.
     *         If it is null, we have the interval [validFrom, infinite[.
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
            } else if (validThru == null && other.validThru != null) {
                return false;
            } else if (validThru != null && !validThru.equals(other.validThru)) {
                return false;
            } else {
                return validFrom.equals(other.validFrom);
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((expirationTime == null) ? 0 : expirationTime.hashCode());
        result = prime * result + validFrom.hashCode();
        result = prime * result + ((validThru == null) ? 0 : validThru.hashCode());
        return result;
    }
}
