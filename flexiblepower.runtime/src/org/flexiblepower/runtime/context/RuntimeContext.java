package org.flexiblepower.runtime.context;

import java.util.Date;

import org.flexiblepower.context.FlexiblePowerContext;
import org.flexiblepower.context.Scheduler;
import org.flexiblepower.context.Simulation;
import org.flexiblepower.scheduling.AbstractScheduler;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;

@Component(servicefactory = true, provide = FlexiblePowerContext.class)
public class RuntimeContext implements FlexiblePowerContext {
    private static final Logger logger = LoggerFactory.getLogger(RuntimeContext.class);

    private AbstractScheduler scheduler;

    @Activate
    public void activate(ComponentContext context) {
        Bundle bundle = context.getUsingBundle();
        logger.info("Created RuntimeContext for bundle: {}", bundle.getSymbolicName());

        scheduler = new AbstractScheduler(bundle.getBundleId() + "-" + bundle.getSymbolicName()) {
            @Override
            public long currentTimeMillis() {
                return RuntimeContext.this.currentTimeMillis();
            }
        };
        scheduler.start();
    }

    @Deactivate
    public void deactivate() {
        scheduler.stop();
    }

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public Date currentTime() {
        return new Date(currentTimeMillis());
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public boolean isSimulation() {
        return false;
    }

    @Override
    public Simulation getSimulation() {
        return null;
    }
}
