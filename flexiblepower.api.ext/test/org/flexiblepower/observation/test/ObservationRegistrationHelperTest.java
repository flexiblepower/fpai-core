package org.flexiblepower.observation.test;

import static org.flexiblepower.observation.ext.ObservationProviderRegistrationHelper.KEY_OBSERVATION_OF;
import static org.flexiblepower.observation.ext.ObservationProviderRegistrationHelper.KEY_OBSERVATION_TYPE;
import static org.flexiblepower.observation.ext.ObservationProviderRegistrationHelper.KEY_OBSERVED_BY;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import junit.framework.TestCase;

import org.flexiblepower.observation.ObservationProvider;
import org.flexiblepower.observation.ext.ObservationAttribute;
import org.flexiblepower.observation.ext.ObservationProviderRegistrationHelper;
import org.osgi.framework.BundleContext;

public class ObservationRegistrationHelperTest extends TestCase {
    interface SubType {
        int getInt();

        double getDouble();

        String getString();

        Map<String, Integer> getMap();
    }

    interface ComplexType {
        SubType getSubType();

        int getInt();
    }

    public void testComplexObservationType() {
        BundleContext context = mock(BundleContext.class);
        new ObservationProviderRegistrationHelper(this, context).observationType(ComplexType.class).register();

        Map<String, Object> expectedProperties = new HashMap<String, Object>();
        expectedProperties.put(KEY_OBSERVED_BY, getClass().getName());
        expectedProperties.put(KEY_OBSERVATION_OF, "unknown");
        expectedProperties.put(KEY_OBSERVATION_TYPE, ComplexType.class.getName());
        expectedProperties.put(KEY_OBSERVATION_TYPE + ".sub_type", SubType.class.getName());
        expectedProperties.put(KEY_OBSERVATION_TYPE + ".sub_type.int", "int");
        expectedProperties.put(KEY_OBSERVATION_TYPE + ".sub_type.double", "double");
        expectedProperties.put(KEY_OBSERVATION_TYPE + ".sub_type.string", "string");
        expectedProperties.put(KEY_OBSERVATION_TYPE + ".sub_type.map", Map.class.getName());
        expectedProperties.put(KEY_OBSERVATION_TYPE + ".int", "int");

        verify(context).registerService(new String[] { ObservationProvider.class.getName() },
                                        this,
                                        new Hashtable<String, Object>(expectedProperties));
    }

    interface CyclicType {
        CyclicType getCycle();
    }

    public void testCyclicType() {
        BundleContext context = mock(BundleContext.class);
        try {
            new ObservationProviderRegistrationHelper(this, context).observationType(CyclicType.class).register();
            fail("Expected an IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().startsWith("Circular typing detected"));
        }
    }

    interface UnitType {
        @ObservationAttribute(unit = "Watt", optional = false)
        int getPower();

        @ObservationAttribute(unit = "kWh", optional = true)
        double getTotal();
    }

    public void testUnitType() {
        BundleContext context = mock(BundleContext.class);
        new ObservationProviderRegistrationHelper(this, context).observationType(UnitType.class).register();

        Map<String, Object> expectedProperties = new HashMap<String, Object>();
        expectedProperties.put(KEY_OBSERVED_BY, getClass().getName());
        expectedProperties.put(KEY_OBSERVATION_OF, "unknown");
        expectedProperties.put(KEY_OBSERVATION_TYPE, UnitType.class.getName());
        expectedProperties.put(KEY_OBSERVATION_TYPE + ".power", "int");
        expectedProperties.put(KEY_OBSERVATION_TYPE + ".power.unit", "Watt");
        expectedProperties.put(KEY_OBSERVATION_TYPE + ".power.optional", false);
        expectedProperties.put(KEY_OBSERVATION_TYPE + ".total", "double");
        expectedProperties.put(KEY_OBSERVATION_TYPE + ".total.unit", "kWh");
        expectedProperties.put(KEY_OBSERVATION_TYPE + ".total.optional", true);

        verify(context).registerService(new String[] { ObservationProvider.class.getName() },
                                        this,
                                        new Hashtable<String, Object>(expectedProperties));
    }
}
