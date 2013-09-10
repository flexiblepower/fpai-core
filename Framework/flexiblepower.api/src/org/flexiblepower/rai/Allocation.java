package org.flexiblepower.rai;

import java.util.Date;
import java.util.UUID;

import org.flexiblepower.rai.values.EnergyProfile;

public class Allocation extends ResourceInfo {
    private final UUID controlSpaceId;
    private final Date startTime;
    private final EnergyProfile energyProfile;

    /**
     * construct an allocation based on a control space. Note that a control space can give rise to zero, one or
     * multiple allocations.
     * 
     * @param controlSpace
     *            is the control space the allocation is based on
     * @param startTime
     *            is the start time of the energy profile
     * @param energyProfile
     *            is the profile of the allocation
     * 
     * @throws NullPointerException
     *             when the controlSpace has a null id
     * @throws NullPointerException
     *             when the startTime is null
     * @throws NullPointerException
     *             when the energyProfile is null
     */
    public Allocation(ControlSpace controlSpace, Date startTime, EnergyProfile energyProfile) {
        super(controlSpace.getApplianceId());
        controlSpaceId = controlSpace.getId();
        this.startTime = startTime;
        this.energyProfile = energyProfile;

        if (controlSpaceId == null) {
            throw new NullPointerException("controlSpaceId is null");
        }
        if (startTime == null) {
            throw new NullPointerException("startTime is null");
        }
        if (energyProfile == null) {
            throw new NullPointerException("energyProfile is null");
        }
    }

    public Allocation(Allocation allocation) {
        super(allocation);
        controlSpaceId = allocation.controlSpaceId;
        startTime = allocation.startTime;
        energyProfile = allocation.energyProfile;
    }

    public UUID getControlSpaceId() {
        return controlSpaceId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public EnergyProfile getEnergyProfile() {
        return energyProfile;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((controlSpaceId == null) ? 0 : controlSpaceId.hashCode());
        result = prime * result + ((energyProfile == null) ? 0 : energyProfile.hashCode());
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
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
        Allocation other = (Allocation) obj;
        if (controlSpaceId == null) {
            if (other.controlSpaceId != null) {
                return false;
            }
        } else if (!controlSpaceId.equals(other.controlSpaceId)) {
            return false;
        }
        if (energyProfile == null) {
            if (other.energyProfile != null) {
                return false;
            }
        } else if (!energyProfile.equals(other.energyProfile)) {
            return false;
        }
        if (startTime == null) {
            if (other.startTime != null) {
                return false;
            }
        } else if (!startTime.equals(other.startTime)) {
            return false;
        }
        return true;
    }
}
