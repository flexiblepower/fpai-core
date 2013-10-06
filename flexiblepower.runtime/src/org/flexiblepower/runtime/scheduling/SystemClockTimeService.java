package org.flexiblepower.runtime.scheduling;

import java.util.Date;

import org.flexiblepower.time.TimeService;

import aQute.bnd.annotation.component.Component;

@Component(provide = TimeService.class)
public class SystemClockTimeService implements TimeService {
    @Override
    public Date getTime() {
        return new Date();
    }

    @Override
    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
