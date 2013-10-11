package org.flexiblepower.rai;

import java.util.Date;
import java.util.UUID;

import org.flexiblepower.rai.values.EnergyProfile;

/**
 * An Allocation is the datatype that will be sent back from the {@link Controller} to the {@link ControllableResource}.
 * 
 * @author TNO
 */
public class Allocation extends ResourceInfo {
    private final UUID controlSpaceId;
    private final Date energyProfileStartTime;
    private final EnergyProfile energyProfile;

    /**
     * Creates a new {@link Allocation} based on a given {@link ControlSpace}. Note that one {@link ControlSpace} map
     * give rise to any number of allocations.
     * 
     * @param controlSpace
     *            is the control space the allocation is based on.
     * @param energyProfileStartTime
     *            is the start time of the energy profile.
     * @param energyProfile
     *            is the profile of the allocation.
     * 
     * @throws NullPointerException
     *             when one of the parameters is null.
     */
    public Allocation(ControlSpace controlSpace, Date energyProfileStartTime, EnergyProfile energyProfile) {
        super(controlSpace.getResourceId());

        controlSpaceId = controlSpace.getId();
        this.energyProfileStartTime = energyProfileStartTime;
        this.energyProfile = energyProfile;

        if (controlSpaceId == null || energyProfileStartTime == null || energyProfile == null) {
            throw new NullPointerException();
        }
    }

    /**
     * Copy constructor, creates a new allocation that is the same as the given one.
     * 
     * @param allocation
     *            The {@link Allocation} that should be copied.
     */
    public Allocation(Allocation allocation) {
        super(allocation);
        controlSpaceId = allocation.controlSpaceId;
        energyProfileStartTime = allocation.energyProfileStartTime;
        energyProfile = allocation.energyProfile;
    }

    /**
     * @return The UUID of the {@link ControlSpace} that has led to this Allocation.
     */
    public UUID getControlSpaceId() {
        return controlSpaceId;
    }

    /**
     * @return The start time of the {@link EnergyProfile}.
     * @see #getEnergyProfile()
     */
    public Date getStartTime() {
        return energyProfileStartTime;
    }

    /**
     * @return The {@link EnergyProfile} that describes the expected energy usage of the resource over the period
     *         starting from the start time.
     */
    public EnergyProfile getEnergyProfile() {
        return energyProfile;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + controlSpaceId.hashCode();
        result = prime * result + energyProfile.hashCode();
        result = prime * result + energyProfileStartTime.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        } else {
            Allocation other = (Allocation) obj;
            if (!controlSpaceId.equals(other.controlSpaceId)) {
                return false;
            } else if (!energyProfile.equals(other.energyProfile)) {
                return false;
            } else if (!energyProfileStartTime.equals(other.energyProfileStartTime)) {
                return false;
            }
            return true;
        }
    }

    @Override
    public String toString() {
        return super.toString() + " energyProfile = " + energyProfileStartTime + " " + energyProfile;
    }
}
