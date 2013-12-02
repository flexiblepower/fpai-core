package org.flexiblepower.rai;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;

import org.flexiblepower.rai.values.ConstraintList;

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
 * Note that an resource is able to operate at different modes, one could have charge values of 20W and 20W, whereas for
 * both modes one could have the same self discharge 10W. Therefore we have charge speed as PowerConstraintList and a
 * self discharge.
 * <p>
 * PMSuite - PM Control Specification - v0.6
 */
public class BufferControlSpace extends ControlSpace {

    /**
     * total buffer capacity
     */
    private final Measurable<Energy> totalCapacity;

    /**
     * state of charge, percentage expressed as float in [0,1]
     */
    private final double stateOfCharge;

    /**
     * power constraint list to represent charge speed/curve
     */
    private final ConstraintList<Power> chargeSpeed;

    /**
     * discharge speed/curve value expressed as Power.
     */
    private final Measurable<Power> selfDischarge;

    /**
     * minimal switch on period
     */
    private final Measurable<Duration> minOnPeriod;

    /**
     * minimal switch off period
     */
    private final Measurable<Duration> minOffPeriod;

    /**
     * target state of charge one wants to achieve at the target time, percentage as float in [0,1].
     * <p>
     * Is an optional attribute, null when not specified. When not specified also targetTime is not specified.
     */
    private final Double targetStateOfCharge;

    /**
     * target time at which one wants to achieve the target state of charge. Is an optional attribute, null when not
     * specified. When not specified also targetStateOfCharge is not specified.
     */
    private final Date targetTime;

    /**
     * construct buffer control space, which exposes the energetic flexibility of a buffer resource.
     * 
     * @param resourceId
     *            is the identifier of the resource that created this control space.
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
     *             when totalCapacity, chargeSpeed, selfDischarge, minOnPeriod or minOffPeriod is null.
     * @throws IllegalArgumentException
     *             when targetTime is null and targetStateOfCharge is not null.
     * @throws IllegalArgumentException
     *             when targetStateOfCharge is null and targetTime is not null.
     * @throws IllegalArgumentException
     *             when stateOfCharge or targetStateOfCharge is not in the [0,1] range.
     */
    public BufferControlSpace(String resourceId,
                              Date validFrom,
                              Date validThru,
                              Date expirationTime,
                              Measurable<Energy> totalCapacity,
                              double stateOfCharge,
                              ConstraintList<Power> chargeSpeed,
                              Measurable<Power> selfDischarge,
                              Measurable<Duration> minOnPeriod,
                              Measurable<Duration> minOffPeriod,
                              Date targetTime,
                              Double targetStateOfCharge) {
        super(resourceId, validFrom, validThru, expirationTime);
        this.totalCapacity = totalCapacity;
        this.stateOfCharge = stateOfCharge;
        this.chargeSpeed = chargeSpeed;
        this.selfDischarge = selfDischarge;
        this.minOnPeriod = minOnPeriod;
        this.minOffPeriod = minOffPeriod;
        this.targetTime = targetTime;
        this.targetStateOfCharge = targetStateOfCharge;

        validateNonNull(totalCapacity, selfDischarge, chargeSpeed, minOffPeriod, minOnPeriod);
        validateRange(stateOfCharge, "stateOfCharge");
        validateTarget(targetTime, targetStateOfCharge);
        validateConstaintList(chargeSpeed, true);
        // TODO -- check relation with expiration time and target time
    }

    /**
     * @return total capacity
     */
    public Measurable<Energy> getTotalCapacity() {
        return totalCapacity;
    }

    /**
     * 
     * @return current SOC
     */
    public double getStateOfCharge() {
        return stateOfCharge;
    }

    /**
     * @return minimum on period
     */
    public Measurable<Duration> getMinOnPeriod() {
        return minOnPeriod;
    }

    /**
     * @return minimum off period
     */
    public Measurable<Duration> getMinOffPeriod() {
        return minOffPeriod;
    }

    /**
     * 
     * @return charge speed power constraint list
     */
    public ConstraintList<Power> getChargeSpeed() {
        return chargeSpeed;
    }

    /**
     * 
     * @return self discharge (real power)
     */
    public Measurable<Power> getSelfDischarge() {
        return selfDischarge;
    }

    /**
     * @return Target SOC, null when no target (target time and target SOC) specified.
     */
    public Double getTargetStateOfCharge() {
        return targetStateOfCharge;
    }

    /**
     * @return Target time for target SOC, null when no target (target time and target SOC) specified.
     */
    public Date getTargetTime() {
        return targetTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + chargeSpeed.hashCode();
        result = prime * result + minOffPeriod.hashCode();
        result = prime * result + minOnPeriod.hashCode();
        result = prime * result + selfDischarge.hashCode();
        result = (int) (prime * result + Double.doubleToRawLongBits(stateOfCharge));
        result = prime * result + ((targetStateOfCharge == null) ? 0 : targetStateOfCharge.hashCode());
        result = prime * result + ((targetTime == null) ? 0 : targetTime.hashCode());
        result = prime * result + totalCapacity.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        } else {
            BufferControlSpace other = (BufferControlSpace) obj;
            if (!chargeSpeed.equals(other.chargeSpeed) || !selfDischarge.equals(other.selfDischarge)) {
                return false;
            } else if (!minOffPeriod.equals(other.minOffPeriod) || !minOnPeriod.equals(other.minOnPeriod)) {
                return false;
            } else if (!totalCapacity.equals(other.totalCapacity)) {
                return false;
            } else if (Double.doubleToRawLongBits(stateOfCharge) != Double.doubleToRawLongBits(other.stateOfCharge)) {
                return false;
            } else if (targetStateOfCharge == null && other.targetStateOfCharge != null) {
                return false;
            } else if (targetStateOfCharge != null && !targetStateOfCharge.equals(other.targetStateOfCharge)) {
                return false;
            } else if (targetTime == null && other.targetTime != null) {
                return false;
            } else if (targetTime != null && !targetTime.equals(other.targetTime)) {
                return false;
            }
            return true;
        }
    }
}
