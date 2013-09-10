package org.flexiblepower.runtime.ui.applianceinfo;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.flexiblepower.data.appliances.Appliance;
import org.flexiblepower.data.appliances.ApplianceDataStore;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ui.Widget;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(properties = { "widget.type=full", "widget.name=applianceinfo" })
public class AppliancePage implements Widget {
    private BundleContext bundleContext;

    @Activate
    public void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    private ApplianceDataStore applianceDataService;

    @Reference
    public void setApplianceDataService(ApplianceDataStore applianceDataService) {
        this.applianceDataService = applianceDataService;
    }

    public Map<String, ApplianceInfo> getAppliances() {
        Map<String, ApplianceInfo> result = new TreeMap<String, ApplianceInfo>();

        for (String id : applianceDataService.keySet()) {
            Appliance appliance = applianceDataService.get(id);
            result.put(appliance.getId(),
                       new ApplianceInfo(appliance.getId(),
                                         appliance.getIdentification(),
                                         appliance.getApplianceType(),
                                         isManaged(appliance.getId())));
        }

        return result;
    }

    private boolean isManaged(String applianceId) {
        try {
            @SuppressWarnings("rawtypes")
            Collection<ServiceReference<ResourceDriver>> references = bundleContext.getServiceReferences(ResourceDriver.class,
                                                                                                         "(applianceId=" + applianceId
                                                                                                                 + ")");
            return !references.isEmpty();
        } catch (InvalidSyntaxException e) {
            // So the applianceId is not correctly formatted, it will never be managed
            return false;
        }
    }

    @Override
    public String getTitle(Locale locale) {
        return "Appliance Info";
    }
}
