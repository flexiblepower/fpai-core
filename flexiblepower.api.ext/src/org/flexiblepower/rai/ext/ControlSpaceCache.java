package org.flexiblepower.rai.ext;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.flexiblepower.rai.old.ControlSpace;
import org.flexiblepower.rai.old.Controller;
import org.flexiblepower.time.TimeService;

/**
 * Class for keeping track of received ControlSpaces and determining which {@link ControlSpace} is active at a certain
 * point in time. This might come in handy while writing a {@link Controller}. Class is thread-safe.
 * 
 * Each {@link ControlSpace} has a validFrom and a validThru field. Together they define the timeframe in which the
 * given {@link ControlSpace} is valid. It is possible that a {@link Controller} receives two ControlSpaces with
 * overlapping timeframes. In that case the most recently received {@link ControlSpace} should be used. This class keeps
 * track of all the received ControlSpaces, determines the active ControlSpace for a given point in time and
 * automatically removes ControlSpaces which cannot become active anymore.
 * 
 * @param <CS>
 *            Type of {@link ControlSpace}
 */
public class ControlSpaceCache<CS extends ControlSpace> {

    /** When the number of ControlSpaces in the cache exceeds this number, cleaning the cache is forced */
    private static final int CLEAN_THRESHOLD = 25;

    /** Queue with ControlSpaces, most recent ControlSpaces are in the front of the queue */
    private final LinkedList<CS> controlSpaceQueue;
    /** TimeService, used to determine when ContorSpaces can are not valid anymore an can be removed */
    private final TimeService timeService;

    /**
     * Create a new ControlSpaceCache.
     * 
     * @param timeService
     *            The TimeService used in the system
     */
    public ControlSpaceCache(TimeService timeService) {
        controlSpaceQueue = new LinkedList<CS>();
        this.timeService = timeService;
    }

    /**
     * Add a {@link ControlSpace} to the cache.
     * 
     * @param controlSpace
     *            {@link ControlSpace} to add to the cache
     */
    public synchronized void addNewControlSpace(CS controlSpace) {
        controlSpaceQueue.addFirst(controlSpace);
        cleanCache(false);
    }

    /**
     * Returns the active {@link ControlSpace} for the given {@link Date}. Returns null if no active
     * {@link ControlSpace} for the given {@link Date} is known.
     * 
     * @param date
     *            The {@link Date} for which to find the active {@link ControlSpace}
     * @return Active ControlSpace for the given {@link Date}, or null if no active {@link ControlSpace} is known.
     */
    public synchronized CS getActiveControlSpace(Date date) {
        Iterator<CS> it = controlSpaceQueue.iterator();
        while (it.hasNext()) {
            CS itCs = it.next();
            if (controlSpaceIsValid(itCs, date)) {
                return itCs;
            }
        }
        // Nothing found
        return null;
    }

    /**
     * Returns the current active {@link ControlSpace}. Returns null if no active {@link ControlSpace} is known.
     * 
     * @return Current active ControlSpace, or null if no active {@link ControlSpace} is known.
     */
    public CS getActiveControlSpace() {
        return getActiveControlSpace(timeService.getTime());
    }

    /**
     * Returns a new List containing all the known {@link ControlSpace}s.
     * 
     * @return New List containing all known {@link ControlSpace}s
     */
    public synchronized List<CS> getAllControlSpaces() {
        cleanCache(true);
        ArrayList<CS> copy = new ArrayList<CS>(controlSpaceQueue.size());
        if (!controlSpaceQueue.isEmpty()) {
            copy.addAll(controlSpaceQueue);
        }
        return copy;
    }

    /**
     * Determines the Date at which the next {@link ControlSpace} becomes active or the current ControSpace becomes
     * invalid.
     * 
     * @return The {@link Date} at which the next {@link ControlSpace} becomes active or null if there is no next
     *         {@link ControlSpace}
     */
    public synchronized Date nextControlSpaceChange() {
        Date now = timeService.getTime();
        long next = Long.MAX_VALUE;
        for (CS cs : controlSpaceQueue) {
            long csStartTime = cs.getValidFrom().getTime();
            if (controlSpaceIsValid(cs, now)) {
                // This is the current ControlSpace
                // Something is going to happen if this one becomes invalid
                long endTime = cs.getValidThru().getTime();
                if (endTime < next) {
                    next = endTime;
                }
                // older ControlSpaces are not relevant
                break;
            }
            if (csStartTime < next && csStartTime > now.getTime()) {
                // This ControlSpace starts sooner than the current one
                next = csStartTime;
            }
        }
        if (next == Long.MAX_VALUE) {
            // Nothing found
            return null;
        } else {
            return new Date(next);
        }
    }

    /**
     * Clean the cache if the number of elements exceeds CLEAN_THRESHOLD or the parameter force is true.
     * 
     * The algorithm removes ControlSpaces which have expired, but also ControlSpaces which haven't expried but are
     * covered by more recent ControlSpaces.
     * 
     * <code>
     *    |-----------|     // last received CS
     *  |----|              // 
     *   |--------|         // can be removed
     * 
     *     -> time ->  
     * </code>
     * 
     * @param force
     *            Indicates if cleaning should be forced
     */
    private void cleanCache(boolean force) {
        if (controlSpaceQueue.size() > CLEAN_THRESHOLD || force) {
            long now = timeService.getCurrentTimeMillis();
            Iterator<CS> it = controlSpaceQueue.iterator();
            // keep track of covered time frame
            long timeFrameStart = -1; // covered time frame start
            long timeFrameEnd = -1; // covered time frame end
            while (it.hasNext()) {
                CS cs = it.next();
                if (cs.getValidThru().getTime() < now) {
                    // already expired, throw away
                    it.remove();
                    continue;
                }
                if (timeFrameStart == -1) {
                    // first, initialize timeFrameStart and timeFrameEnd
                    timeFrameStart = cs.getValidFrom().getTime();
                    timeFrameEnd = cs.getValidThru().getTime();
                    continue;
                }
                // before now is not relevant
                timeFrameStart = Math.max(timeFrameStart, now);
                timeFrameEnd = Math.max(timeFrameEnd, now);
                if (Math.max(now, cs.getValidFrom().getTime()) >= timeFrameStart && cs.getValidThru().getTime() <= timeFrameEnd) {
                    // ControlSpace is already entirely covered by more recent ControlSpaces
                    it.remove();
                } else {
                    // Expand covered area
                    timeFrameStart = Math.min(timeFrameStart, cs.getValidFrom().getTime());
                    timeFrameEnd = Math.max(timeFrameEnd, cs.getValidFrom().getTime());
                }

            }
        }
    }

    /**
     * Check if the given ControlSpace is valid for the given date.
     * 
     * @param controlSpace
     *            {@link ControlSpace} to inspect
     * @param date
     *            {@link Date} for which the {@link ControlSpace} has te be valid
     * @return True if the {@link ControlSpace} is valid for the given {@link Date}
     */
    private boolean controlSpaceIsValid(CS controlSpace, Date date) {
        long t = date.getTime();
        return controlSpace.getValidFrom().getTime() <= t && controlSpace.getValidThru().getTime() >= t;
    }

}