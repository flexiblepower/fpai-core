package org.flexiblepower.rai;

import java.util.Date;

import org.flexiblepower.rai.values.Duration;
import org.flexiblepower.rai.values.EnergyValue;
import org.flexiblepower.rai.values.PowerConstraintList;
import org.flexiblepower.rai.values.PowerValue;

/**
 * BufferControlSpace is a ControlSpace to expose energetic flexibility of Buffer resource.
 * <p>
 * Buffers can consume/generate more or less energy (within certain operational constraints) according to the needs of
 * the account. They could temporarily consume / generate more energy so that later they need less/more. Examples are :
 * thermic buffers like household heating systems, freezers and refrigerators. Note that micro-CHP systems temporarily
 * generate more.
 * <p>
 * Main parameters are about the buffer state and characteristics: total buffer capacity (Wh), State of Charge (%),
 * Charge speed/curve as PowerConstraintList, self discharge (W), minimal switch on period, minimal switch off period;
 * additional optional parameters specify the target time at which one wants to achieve a target state of charge (%).
 * <p>
 * Note that an appliance is able to operate at different modes, one could have charge values of 20W and 20W, whereas
 * for both modes one could have the same self discharge 10W. Therefore we have charge speed as PowerConstraintList and
 * a self discharge.
 * <p>
 * PMSuite - PM Control Specification - v0.6
 */
public final class BufferControlSpace extends ControlSpace {

    /**
     * total buffer capacity
     */
    private final EnergyValue totalCapacity;

    /**
     * state of charge, percentage expressed as float in [0,1]
     */
    private final float stateOfCharge;

    /**
     * power constraint list to represent charge speed/curve
     */
    private final PowerConstraintList chargeSpeed;

    /**
     * discharge speed/curve value expressed as Power.
     */
    private final PowerValue selfDischarge;

    /**
     * minimal switch on period
     */
    private final Duration minOnPeriod;

    /**
     * minimal switch off period
     */
    private final Duration minOffPeriod;

    /**
     * target state of charge one wants to achieve at the target time, percentage as float in [0,1].
     * <p>
     * Is an optional attribute, null when not specified. When not specified also targetTime is not specified.
     */
    private final Float targetStateOfCharge;

    /**
     * target time at which one wants to achieve the target state of charge. Is an optional attribute, null when not
     * specified. When not specified also targetStateOfCharge is not specified.
     */
    private final Date targetTime;

    /**
     * construct buffer control space, which exposes the energetic flexibility of a buffer resource.
     * 
     * @param resourceManager
     *            creator of the control space, the manager of the buffer resource
     * @param validFrom
     *            is the start time instant of the interval [validFrom,validThru[ for which the control space is valid
     * @param validThru
     *            is the end time instant of the interval [validFrom,validThru[ for which the control space is valid
     * @param expirationTime
     *            time after which the creator will autonomously act when no allocation was received by then. Is
     *            optional, provide null when not specified.
     * @param totalCapacity
     *            total buffer capacity
     * @param stateOfCharge
     *            the current state of charge, percentage expressed as double in [0,1]
     * @param chargeSpeed
     *            is PowerConstraintList to represent charge speed/curve characteristics.
     * @param selfDischarge
     *            is self discharge value expressed as Power
     * @param minOnPeriod
     *            minimal switch on period
     * @param minOffPeriod
     *            minimal switch off period
     * @param targetTime
     *            target time at which one wants to achieve the target state of charge. Optional parameter, provide null
     *            when not specified. When specified then targetStateOfCharge should also be specified, when not
     *            specified then targetStateOfCharge should not be specified.
     * @param targetStateOfCharge
     *            target state of charge one wants to achieve at the target time, percentage as double in [0,1].
     *            Optional parameter, provide null when not specified. When specified then targetTime should also be
     *            specified, when not specified then targetTime should not be specified.
     * 
     * @throws NullPointerException
     *             when totalCapacity is null
     * @throws NullPointerException
     *             when chargeSpeed is null
     * @throws NullPointerException
     *             when selfDischarge is null
     * @throws NullPointerException
     *             when minOnPeriod is null
     * @throws NullPointerException
     *             when minOffPeriod is null
     * @throws NullPointerException
     *             when targetTime is null and targetStateOfCharge is not null
     * @throws NullPointerException
     *             when targetStateOfCharge is null and targetTime is not null
     * @throws IllegalArgumentException
     *             when stateOfCharge is not null but not in [0,1]
     * @throws IllegalArgumentException
     *             when targetStateOfCharge is not null but not in [0,1]
     */
    public BufferControlSpace(String applianceId,
                              Date validFrom,
                              Date validThru,
                              Date expirationTime,
                              EnergyValue totalCapacity,
                              float stateOfCharge,
                              PowerConstraintList chargeSpeed,
                              PowerValue selfDischarge,
                              Duration minOnPeriod,
                              Duration minOffPeriod,
                              Date targetTime,
                              Float targetStateOfCharge) {
        super(applianceId, validFrom, validThru, expirationTime);
        this.totalCapacity = totalCapacity;
        this.stateOfCharge = stateOfCharge;
        this.chargeSpeed = chargeSpeed;
        this.selfDischarge = selfDischarge;
        this.minOnPeriod = minOnPeriod;
        this.minOffPeriod = minOffPeriod;
        this.targetTime = targetTime;
        this.targetStateOfCharge = targetStateOfCharge;
        validate();
    }

    private void validate() {
        if (totalCapacity == null) {
            throw new NullPointerException("totalCapacity is null");
        }
        if (stateOfCharge < 0 || stateOfCharge > 1) {
            throw new IllegalArgumentException("stateOfCharge should be in [0,1] but is " + stateOfCharge);
        }
        if (selfDischarge == null) {
            throw new NullPointerException("selfDischarge is null");
        }
        if (chargeSpeed == null) {
            throw new NullPointerException("chargeSpeed is null");
        }
        if (minOnPeriod == null) {
            throw new NullPointerException("minOnPeriod is null");
        }
        if (minOffPeriod == null) {
            throw new NullPointerException("minOffPeriod is null");
        }
        // TODO -- check validity of chargeSpeed contents
        if (targetTime == null && targetStateOfCharge != null) {
            throw new NullPointerException("targetTime is null, not allowed when targetStateOfCharge is specified");
        }
        if (targetTime != null && targetStateOfCharge == null) {
            throw new NullPointerException("targetStateOfCharge is null, not allowed when targetTime is specified");
        }
        if (targetStateOfCharge != null && (targetStateOfCharge < 0 || targetStateOfCharge > 1)) {
            throw new IllegalArgumentException("targetStateOfCharge should be in [0,1] but is " + targetStateOfCharge);
        }
        // TODO -- check relation with expiration time and target time
    }

    /**
     * @return total capacity
     */
    public EnergyValue getTotalCapacity() {
        return totalCapacity;
    }

    /**
     * 
     * @return current SOC
     */
    public float getStateOfCharge() {
        return stateOfCharge;
    }

    /**
     * @return minimum on period
     */
    public Duration getMinOnPeriod() {
        return minOnPeriod;
    }

    /**
     * @return minimum off period
     */
    public Duration getMinOffPeriod() {
        return minOffPeriod;
    }

    /**
     * 
     * @return charge speed power constraint list
     */
    public PowerConstraintList getChargeSpeed() {
        return chargeSpeed;
    }

    /**
     * 
     * @return self discharge (real power)
     */
    public PowerValue getSelfDischarge() {
        return selfDischarge;
    }

    /**
     * get target SOC
     * 
     * @return target SOC, null when no target (target time and target SOC) specified
     */
    public Float getTargetStateOfCharge() {
        return targetStateOfCharge;
    }

    /**
     * get target time for target SOC
     * 
     * @return target time for target SOC, null when no target (target time and target SOC) specified
     */
    public Date getTargetTime() {
        return targetTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((chargeSpeed == null) ? 0 : chargeSpeed.hashCode());
        result = prime * result + ((minOffPeriod == null) ? 0 : minOffPeriod.hashCode());
        result = prime * result + ((minOnPeriod == null) ? 0 : minOnPeriod.hashCode());
        result = prime * result + ((selfDischarge == null) ? 0 : selfDischarge.hashCode());
        result = prime * result + Float.floatToIntBits(stateOfCharge);
        result = prime * result + ((targetStateOfCharge == null) ? 0 : targetStateOfCharge.hashCode());
        result = prime * result + ((targetTime == null) ? 0 : targetTime.hashCode());
        result = prime * result + ((totalCapacity == null) ? 0 : totalCapacity.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BufferControlSpace other = (BufferControlSpace) obj;
        if (chargeSpeed == null) {
            if (other.chargeSpeed != null) {
                return false;
            }
        } else if (!chargeSpeed.equals(other.chargeSpeed)) {
            return false;
        }
        if (minOffPeriod == null) {
            if (other.minOffPeriod != null) {
                return false;
            }
        } else if (!minOffPeriod.equals(other.minOffPeriod)) {
            return false;
        }
        if (minOnPeriod == null) {
            if (other.minOnPeriod != null) {
                return false;
            }
        } else if (!minOnPeriod.equals(other.minOnPeriod)) {
            return false;
        }
        if (selfDischarge == null) {
            if (other.selfDischarge != null) {
                return false;
            }
        } else if (!selfDischarge.equals(other.selfDischarge)) {
            return false;
        }
        if (Float.floatToIntBits(stateOfCharge) != Float.floatToIntBits(other.stateOfCharge)) {
            return false;
        }
        if (targetStateOfCharge == null) {
            if (other.targetStateOfCharge != null) {
                return false;
            }
        } else if (!targetStateOfCharge.equals(other.targetStateOfCharge)) {
            return false;
        }
        if (targetTime == null) {
            if (other.targetTime != null) {
                return false;
            }
        } else if (!targetTime.equals(other.targetTime)) {
            return false;
        }
        if (totalCapacity == null) {
            if (other.totalCapacity != null) {
                return false;
            }
        } else if (!totalCapacity.equals(other.totalCapacity)) {
            return false;
        }
        return true;
    }

}
