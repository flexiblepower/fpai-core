package org.flexiblepower.time;

import java.util.Date;

public interface TimeService {
    Date getTime();

    long getCurrentTimeMillis();
}
