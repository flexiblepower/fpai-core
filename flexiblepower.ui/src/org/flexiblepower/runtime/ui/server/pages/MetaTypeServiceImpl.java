package org.flexiblepower.runtime.ui.server.pages;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;

// http://dz.prosyst.com/pdoc/mBS_SDK_7.2.0/um/runtime/osgi/docs/framework/bundles/osgi/metatype/osgi.html#using

public class MetaTypeServiceImpl implements BundleActivator {
    // Class variables
    ServiceReference metaTypeReference;
    MetaTypeService metaTypeService;

    @Override
    public void start(BundleContext bundleContext)
    {
        metaTypeReference = bundleContext.getServiceReference(MetaTypeService.class.getName());
        metaTypeService = (MetaTypeService) bundleContext.getService(metaTypeReference);

        Bundle[] bundles = bundleContext.getBundles();
        for (Bundle bundle : bundles) {
            MetaTypeInformation metaTypeInformation = metaTypeService.getMetaTypeInformation(bundle);

            // Get the FPIDS and PIDS.
            String[] factoryPIDS = metaTypeInformation.getFactoryPids();
            String[] normalPIDS = metaTypeInformation.getPids();

            // For normal PIDS.
            // Get OCD's and AD's.
            if (normalPIDS != null) {
                for (String element : normalPIDS) {
                    // Get OCD.
                    ObjectClassDefinition ocd = metaTypeInformation.getObjectClassDefinition(element, null);

                    // Get AD's.
                    AttributeDefinition[] ads = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);

                    // Print OCD's and AD's.
                    for (AttributeDefinition ad : ads) {
                        System.out.println("AD= " + ad.getName() + " OCD= " + ocd.getName());
                    }
                }
            }

            // For factory PIDS.
            // Get OCD's and AD's.
            if (factoryPIDS != null) {
                for (String element : factoryPIDS) {
                    // Get OCD.
                    ObjectClassDefinition ocdFactory = metaTypeInformation.getObjectClassDefinition(element, null);

                    // Get AD's.
                    AttributeDefinition[] adsFactory = ocdFactory.getAttributeDefinitions(ObjectClassDefinition.ALL);

                    // Print OCD's and AD's.
                    for (AttributeDefinition element2 : adsFactory) {
                        System.out.println("AD= " + element2.getName() + " OCD= " + ocdFactory.getName());
                    }
                }
            }
        }
    }

    @Override
    public void stop(BundleContext bundleContext)
    {
        bundleContext.ungetService(metaTypeReference);
    }

}
