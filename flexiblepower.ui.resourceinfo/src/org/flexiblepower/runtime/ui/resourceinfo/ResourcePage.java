package org.flexiblepower.runtime.ui.resourceinfo;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.flexiblepower.rai.ControlSpace;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.runtime.api.Resource;
import org.flexiblepower.runtime.api.ResourceWiringManager;
import org.flexiblepower.ui.Widget;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(properties = { "widget.type=full", "widget.name=resourceinfo" })
public class ResourcePage implements Widget {

    /** Reference to the ResourceWiringManager */
    private ResourceWiringManager resourceWiringManager;

    @Reference
    public void setApplianceDataService(ResourceWiringManager resourceWiringManager) {
        this.resourceWiringManager = resourceWiringManager;
    }

    /**
     * Create a map of ResourceInfo objects. This method is called by the Widget.
     * 
     * @return
     */
    public Map<String, ResourceInfo> getResources() {
        Map<String, ResourceInfo> result = new TreeMap<String, ResourceInfo>();

        for (Resource<?, ?> resource : resourceWiringManager.getResources()) {
            result.put(resource.getId(),
                       new ResourceInfo(resource.getId(),
                                        resourceType(resource),
                                        resource.getControllerManager() != null));
        }

        return result;
    }

    /**
     * Try to get the the {@link ControlSpace} type as a string. If the type cannot be determined, this method return
     * "Unknown".
     * 
     * @param resource
     * @return
     */
    private String resourceType(Resource<?, ?> resource) {
        for (ResourceManager<?, ?, ?> r : resource.getResourceManagers()) {
            return r.getControlSpaceType().getSimpleName().replace("ControlSpace", "");
        }
        return "Unknown";
    }

    @Override
    public String getTitle(Locale locale) {
        return "Resource Info";
    }

}
