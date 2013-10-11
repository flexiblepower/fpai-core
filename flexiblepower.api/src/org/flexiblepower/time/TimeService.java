package org.flexiblepower.time;

import java.util.Date;

/**
 * The {@link TimeService} is always provided by the runtime environment and should be used to get the current time.
 * When doing this at all times, you make sure that the code is also easily ported to the simlation runtime in which the
 * time can be simulated.
 */
public interface TimeService {
    /**
     * @return The current time as a {@link Date} object. In a normal runtime this would be equal to
     *         <code>new Date()</code>.
     */
    Date getTime();

    /**
     * @see System#currentTimeMillis()
     * @return The current time as milliseconds since epoch. In a normal runtime this would be equal to
     *         <code>System.currentTimeMillis()</code>.
     */
    long getCurrentTimeMillis();
}
