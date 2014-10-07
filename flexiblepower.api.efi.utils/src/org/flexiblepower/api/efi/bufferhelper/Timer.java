package org.flexiblepower.api.efi.bufferhelper;

import java.util.Date;

public class Timer extends org.flexiblepower.efi.util.Timer {
    private Date finishedAt;

    public Timer(org.flexiblepower.efi.util.Timer base) {
        super(base.getId(), base.getLabel(), base.getDuration());
    }

    public void updateFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public boolean isBlockingAt(Date moment) {
        return (finishedAt != null && finishedAt.after(moment));
    }
}
