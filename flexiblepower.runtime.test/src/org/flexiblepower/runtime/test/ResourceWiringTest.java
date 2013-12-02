package org.flexiblepower.runtime.test;

import java.util.Dictionary;
import java.util.Hashtable;

import junit.framework.TestCase;

import org.flexiblepower.control.ControllerManager;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.wiring.ResourceWiringManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceWiringTest extends TestCase {
    private static final Logger logger = LoggerFactory.getLogger(ResourceWiringTest.class);

    private final BundleContext bundleContext;

    private final ServiceReference<ResourceWiringManager> rwmReference;
    private final ResourceWiringManager resourceWiringManager;

    public ResourceWiringTest() {
        bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();

        rwmReference = bundleContext.getServiceReference(ResourceWiringManager.class);
        if (rwmReference == null) {
            fail("Can not find the ResourceWiringManager implementation");
        }
        resourceWiringManager = bundleContext.getService(rwmReference);
    }

    @Override
    protected void tearDown() throws Exception {
        bundleContext.ungetService(rwmReference);
    }

    private <T> ServiceRegistration<T> registerService(Class<T> serviceClass, T service, String resourceId) {
        Dictionary<String, String> props = new Hashtable<String, String>();
        props.put(ResourceWiringManager.RESOURCE_ID, resourceId);
        return bundleContext.registerService(serviceClass, service, props);
    }

    private <T> ServiceRegistration<T> registerService(Class<T> serviceClass, T service, String... resourceIds) {
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put(ResourceWiringManager.RESOURCE_IDS, resourceIds);
        return bundleContext.registerService(serviceClass, service, props);
    }

    @SuppressWarnings("rawtypes")
    public void testWiring() {
        TestControllerManager cm = new TestControllerManager();
        TestResourceManager rm1 = new TestResourceManager();
        TestResourceManager rm2 = new TestResourceManager();
        TestResourceDriver rd1 = new TestResourceDriver();
        TestResourceDriver rd2 = new TestResourceDriver();

        rm1.assertCorrectWiring(null, null);
        rm2.assertCorrectWiring(null, null);
        rd1.assertCorrectWiring(null);
        rd2.assertCorrectWiring(null);

        ServiceRegistration<ControllerManager> srCm = registerService(ControllerManager.class, cm, "type1", "type2");
        ServiceRegistration<ResourceManager> srRm1 = registerService(ResourceManager.class, rm1, "type1");
        ServiceRegistration<ResourceManager> srRm2 = registerService(ResourceManager.class, rm2, "type2");
        ServiceRegistration<ResourceDriver> srRd1 = registerService(ResourceDriver.class, rd1, "type1");
        ServiceRegistration<ResourceDriver> srRd2 = registerService(ResourceDriver.class, rd2, "type2");

        logger.debug("Expecting wiring of {} - {} - {}", cm, rm1, rd1);
        logger.debug("Expecting wiring of {} - {} - {}", cm, rm2, rd2);
        logger.debug("Current state: " + resourceWiringManager.getResources());

        rm1.assertCorrectWiring(cm, rd1);
        rm2.assertCorrectWiring(cm, rd2);
        rd1.assertCorrectWiring(rm1);
        rd2.assertCorrectWiring(rm2);

        srCm.unregister();
        srRm1.unregister();
        srRm2.unregister();
        srRd1.unregister();
        srRd2.unregister();

        rm1.assertCorrectWiring(null, null);
        rm2.assertCorrectWiring(null, null);
        rd1.assertCorrectWiring(null);
        rd2.assertCorrectWiring(null);
    }

    @SuppressWarnings("rawtypes")
    public void testModified() {
        TestControllerManager cm = new TestControllerManager();
        TestResourceManager rm = new TestResourceManager();
        TestResourceDriver rd = new TestResourceDriver();

        rm.assertCorrectWiring(null, null);
        rd.assertCorrectWiring(null);

        ServiceRegistration<ControllerManager> srCm = registerService(ControllerManager.class, cm, "type1", "type2");
        ServiceRegistration<ResourceManager> srRm1 = registerService(ResourceManager.class, rm, "type1");
        ServiceRegistration<ResourceDriver> srRd1 = registerService(ResourceDriver.class, rd, "type1");

        rm.assertCorrectWiring(cm, rd);
        rd.assertCorrectWiring(rm);

        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put(ResourceWiringManager.RESOURCE_IDS, new String[] { "type2" });
        srCm.setProperties(props);

        rm.assertCorrectWiring(null, rd);
        rd.assertCorrectWiring(rm);

        props.put(ResourceWiringManager.RESOURCE_IDS, new String[] { "type1" });
        srCm.setProperties(props);

        rm.assertCorrectWiring(cm, rd);
        rd.assertCorrectWiring(rm);

        srCm.unregister();
        srRd1.unregister();
        srRm1.unregister();

        rm.assertCorrectWiring(null, null);
        rd.assertCorrectWiring(null);
    }
}
