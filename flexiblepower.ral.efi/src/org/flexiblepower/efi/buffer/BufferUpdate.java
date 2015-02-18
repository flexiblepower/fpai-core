package org.flexiblepower.efi.buffer;

import java.util.Date;

import org.flexiblepower.ral.messages.ControlSpaceUpdate;

/**
 * This class is derived from {@link ControlSpaceUpdate} and is used to bundle all updates from an appliance of the
 * buffer category.
 */
public abstract class BufferUpdate extends ControlSpaceUpdate {
    /**
     * Constructs a new {@link BufferUpdate} message with the specific validFrom
     *
     * @param resourceId
     *            The resource identifier
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     * @param validFrom
     *            This timestamp indicates from which moment on this update is valid.
     */
    public BufferUpdate(String resourceId, Date timestamp, Date validFrom) {
        super(resourceId, timestamp, validFrom);
    }
}
