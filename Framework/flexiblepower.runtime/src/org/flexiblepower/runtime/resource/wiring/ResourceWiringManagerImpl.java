package org.flexiblepower.runtime.resource.wiring;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.flexiblepower.control.ControllerManager;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.ResourceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(immediate = true, provide = ResourceWiringManager.class)
public class ResourceWiringManagerImpl implements ResourceWiringManager {
    private static final Logger logger = LoggerFactory.getLogger(ResourceWiringManagerImpl.class);

    public static final String KEY_APPLIANCE_IDS = "applianceIds";
    public static final String KEY_APPLIANCE_ID = "applianceId";

    final Map<String, ResourceImpl<?>> resources;

    final Map<ControllerManager, Set<String>> controllerManagers;
    final Map<ResourceManager<?>, String> resourceManagers;
    final Map<ResourceDriver<?, ?>, String> resourceDrivers;

    public ResourceWiringManagerImpl() {
        resources = new HashMap<String, ResourceImpl<?>>();
        controllerManagers = new HashMap<ControllerManager, Set<String>>();
        resourceManagers = new HashMap<ResourceManager<?>, String>();
        resourceDrivers = new HashMap<ResourceDriver<?, ?>, String>();
    }

    @Reference(dynamic = true, multiple = true, optional = true)
    public synchronized void addController(ControllerManager controller, Map<String, Object> properties) {
        Set<String> applianceIds = getApplianceIds(properties);

        for (String applianceId : applianceIds) {
            getResource(applianceId).setController(controller);
        }

        controllerManagers.put(controller, applianceIds);
    }

    public synchronized void removeController(ControllerManager controller) {
        for (String resourceId : controllerManagers.remove(controller)) {
            getResource(resourceId).unsetController(controller);
        }
    }

    @Reference(dynamic = true, multiple = true, optional = true)
    public synchronized <RS extends ResourceState> void addResourceManager(ResourceManager<RS> resourceManager,
                                                                           Map<String, Object> properties) {
        String resourceId = getResourceId(properties);
        if (resourceId != null) {
            this.<RS> getResource(resourceId).addManager(resourceManager);
        }

        resourceManagers.put(resourceManager, resourceId);
    }

    public synchronized <RS extends ResourceState> void removeResourceManager(ResourceManager<RS> resourceManager) {
        String resourceId = resourceManagers.remove(resourceManager);
        if (resourceId != null) {
            this.<RS> getResource(resourceId).removeManager(resourceManager);
        }
    }

    @Reference(dynamic = true, multiple = true, optional = true)
    public synchronized <RS extends ResourceState> void addResourceDriver(ResourceDriver<RS, ?> resourceDriver,
                                                                          Map<String, Object> properties) {
        String resourceId = getResourceId(properties);
        if (resourceId != null) {
            this.<RS> getResource(resourceId).addDriver(resourceDriver);
        }

        resourceDrivers.put(resourceDriver, resourceId);
    }

    public synchronized <RS extends ResourceState> void removeResourceDriver(ResourceDriver<RS, ?> resourceDriver) {
        String resourceId = resourceDrivers.remove(resourceDriver);
        if (resourceId != null) {
            this.<RS> getResource(resourceId).removeDriver(resourceDriver);
        }
    }

    @SuppressWarnings("unchecked")
    private <RS extends ResourceState> ResourceImpl<RS> getResource(String resourceId) {
        if (resourceId == null) {
            return null;
        }
        if (!resources.containsKey(resourceId)) {
            resources.put(resourceId, new ResourceImpl<RS>(resourceId));
        }
        return (ResourceImpl<RS>) resources.get(resourceId);
    }

    @SuppressWarnings("unchecked")
    private Set<String> getApplianceIds(Map<String, Object> properties) {
        Object property = properties.get(KEY_APPLIANCE_IDS);
        if (property == null) {
            logger.warn("The applianceIds property has not been set of service " + properties.get("service.id"));
            return Collections.emptySet();
        } else if (property instanceof String[]) {
            return new HashSet<String>(Arrays.asList((String[]) property));
        } else if (property instanceof Collection) {
            return new HashSet<String>((Collection<String>) property);
        } else if (property instanceof String) {
            return new HashSet<String>(Arrays.asList(property.toString()));
        } else {
            logger.warn("The applianceIds property is not of the correct type (String[] or List<String>) of service " + properties.get("service.id"));
            return Collections.emptySet();
        }
    }

    private String getResourceId(Map<String, Object> properties) {
        Object property = properties.get(KEY_APPLIANCE_ID);
        if (property == null) {
            logger.warn("The applianceId property has not been set for service " + properties.get("service.id"));
            return null;
        } else {
            return property.toString();
        }
    }

    @Override
    public Collection<Resource<?>> getResources() {
        return Collections.<Resource<?>> unmodifiableCollection(resources.values());
    }

    @Override
    public int size() {
        return resources.size();
    }
}
