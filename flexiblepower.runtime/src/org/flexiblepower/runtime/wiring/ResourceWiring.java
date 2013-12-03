package org.flexiblepower.runtime.wiring;

import java.util.Collection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.flexiblepower.ral.ResourceControlParameters;
import org.flexiblepower.ral.ResourceState;
import org.flexiblepower.ral.wiring.Resource;
import org.osgi.framework.BundleContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;

@Component(immediate = true, provide = org.flexiblepower.ral.wiring.ResourceWiringManager.class)
public class ResourceWiring implements org.flexiblepower.ral.wiring.ResourceWiringManager {
    private final Map<String, ResourceContainer<?, ?>> resources;

    public ResourceWiring() {
        resources = new HashMap<String, ResourceContainer<?, ?>>();
    }

    private ResourceDriverTracker driverTracker;
    private ResourceManagerTracker managerTracker;
    private ResourceControllerTracker controllerTracker;

    @Activate
    public void activate(BundleContext context) {
        driverTracker = new ResourceDriverTracker(this, context);
        managerTracker = new ResourceManagerTracker(this, context);
        controllerTracker = new ResourceControllerTracker(this, context);

        driverTracker.start();
        managerTracker.start();
        controllerTracker.start();
    }

    @Deactivate
    public void deactivate() {
        driverTracker.close();
        managerTracker.close();
        controllerTracker.close();
    }

    public void cleanUp() {
        for (Iterator<ResourceContainer<?, ?>> it = resources.values().iterator(); it.hasNext();) {
            ResourceContainer<?, ?> resource = it.next();
            if (resource.isEmpty()) {
                it.remove();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <RS extends ResourceState, RCP extends ResourceControlParameters>
            ResourceContainer<RS, RCP>
            getResource(String resourceId) {
        if (resourceId == null) {
            return null;
        }
        if (!resources.containsKey(resourceId)) {
            resources.put(resourceId, new ResourceContainer<RS, RCP>(resourceId));
        }
        return (ResourceContainer<RS, RCP>) resources.get(resourceId);
    }

    @Override
    public Collection<Resource<?, ?>> getResources() {
        return Collections.<Resource<?, ?>> unmodifiableCollection(resources.values());
    }

    @Override
    public int size() {
        return resources.size();
    }
}
