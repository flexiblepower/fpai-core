package org.flexiblepower.runtime.ui.resourceinfo;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.runtime.api.Resource;
import org.flexiblepower.runtime.api.ResourceWiringManager;
import org.flexiblepower.ui.Widget;
import org.osgi.framework.BundleContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(properties = { "widget.type=full", "widget.name=applianceinfo" })
public class ResourcePage implements Widget {
    private BundleContext bundleContext;

    @Activate
    public void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    private ResourceWiringManager resourceWiringManager;

    @Reference
    public void setApplianceDataService(ResourceWiringManager resourceWiringManager) {
        this.resourceWiringManager = resourceWiringManager;
    }

    public Map<String, ResourceInfo> getResources() {
        Map<String, ResourceInfo> result = new TreeMap<String, ResourceInfo>();

        for (Resource<?, ?> resource : resourceWiringManager.getResources()) {
            result.put(resource.getId(),
                       new ResourceInfo(resource.getId(),
                                        resourceName(resource),
                                        resource.getControllerManager() != null));
        }

        return result;
    }

    private String resourceName(Resource<?, ?> resource) {
        for (ResourceManager<?, ?, ?> r : resource.getResourceManagers()) {
            r.getControlSpaceType().getSimpleName().replace("ControlSpace", "");
        }
        return "Unknown";
    }

    @Override
    public String getTitle(Locale locale) {
        return "Appliance Info";
    }

}
