package org.flexiblepower.scheduling;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SchedulerThreadMonitor implements Iterable<Entry<String, AbstractScheduler>> {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerThreadMonitor.class);

    private final Map<String, AbstractScheduler> runningSchedulers = new ConcurrentHashMap<String, AbstractScheduler>();

    void addScheduler(String threadName, AbstractScheduler scheduler) {
        if (runningSchedulers.put(threadName, scheduler) != null) {
            logger.warn("Multiple schedulers with the same threadname active: {}", threadName);
        }
    }

    void removeScheduler(String threadName) {
        runningSchedulers.remove(threadName);
    }

    @Override
    public Iterator<Entry<String, AbstractScheduler>> iterator() {
        return runningSchedulers.entrySet().iterator();
    }
}
