package org.flexiblepower.ral.ext;

import org.flexiblepower.observation.AbstractObservationProvider;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractResourceDriver<RS extends ResourceState, RCP> extends AbstractObservationProvider<RS> implements
                                                                                                                   ResourceDriver<RS, RCP> {
    protected final Logger logger;

    public AbstractResourceDriver() {
        this.logger = LoggerFactory.getLogger(getClass());
    }
}
