package org.flexiblepower.observation.test;

import static org.flexiblepower.observation.ext.ObservationProviderRegistrationHelper.KEY_OBSERVATION_OF;
import static org.flexiblepower.observation.ext.ObservationProviderRegistrationHelper.KEY_OBSERVATION_TYPE;
import static org.flexiblepower.observation.ext.ObservationProviderRegistrationHelper.KEY_OBSERVED_BY;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import junit.framework.TestCase;

import org.flexiblepower.observation.ObservationProvider;
import org.flexiblepower.observation.ext.ObservationAttribute;
import org.flexiblepower.observation.ext.ObservationProviderRegistrationHelper;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

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

    @SuppressWarnings("unchecked")
    public void testComplexObservationType() {
        final ServiceRegistration<?> reg = mock(ServiceRegistration.class);
        BundleContext context = mock(BundleContext.class);
        when(context.registerService(any(String[].class), eq(this), any(Hashtable.class))).then(new Answer<ServiceRegistration<?>>() {
            @Override
            public ServiceRegistration<?> answer(InvocationOnMock invocation) throws Throwable {
                String[] interfaces = (String[]) invocation.getArguments()[0];
                assertTrue(Arrays.equals(new String[] { ObservationProvider.class.getName() }, interfaces));

                Hashtable<?, ?> table = (Hashtable<?, ?>) invocation.getArguments()[2];
                assertEquals(9, table.size());
                assertEquals(ObservationRegistrationHelperTest.class.getName(), table.get(KEY_OBSERVED_BY));
                assertEquals("unknown", table.get(KEY_OBSERVATION_OF));
                assertTrue(Arrays.equals(new String[] { ComplexType.class.getName() },
                                         (String[]) table.get(KEY_OBSERVATION_TYPE)));
                assertEquals(SubType.class.getName(), table.get(KEY_OBSERVATION_TYPE + ".sub_type"));
                assertEquals("int", table.get(KEY_OBSERVATION_TYPE + ".sub_type.int"));
                assertEquals("double", table.get(KEY_OBSERVATION_TYPE + ".sub_type.double"));
                assertEquals("string", table.get(KEY_OBSERVATION_TYPE + ".sub_type.string"));
                assertEquals(Map.class.getName(), table.get(KEY_OBSERVATION_TYPE + ".sub_type.map"));
                assertEquals("int", table.get(KEY_OBSERVATION_TYPE + ".int"));

                return reg;
            }
        });
        assertEquals(reg, new ObservationProviderRegistrationHelper(this, context).observationType(ComplexType.class)
                                                                                  .register());
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

    @SuppressWarnings({ "unchecked" })
    public void testUnitType() {
        final ServiceRegistration<?> reg = mock(ServiceRegistration.class);
        BundleContext context = mock(BundleContext.class);
        when(context.registerService(any(String[].class), eq(this), any(Hashtable.class))).then(new Answer<ServiceRegistration<?>>() {
            @Override
            public ServiceRegistration<?> answer(InvocationOnMock invocation) throws Throwable {
                String[] interfaces = (String[]) invocation.getArguments()[0];
                assertTrue(Arrays.equals(new String[] { ObservationProvider.class.getName() }, interfaces));

                Hashtable<?, ?> table = (Hashtable<?, ?>) invocation.getArguments()[2];
                assertEquals(9, table.size());
                assertEquals(ObservationRegistrationHelperTest.class.getName(), table.get(KEY_OBSERVED_BY));
                assertEquals("unknown", table.get(KEY_OBSERVATION_OF));
                assertTrue(Arrays.equals(new String[] { UnitType.class.getName() },
                                         (String[]) table.get(KEY_OBSERVATION_TYPE)));
                assertEquals("int", table.get(KEY_OBSERVATION_TYPE + ".power"));
                assertEquals("Watt", table.get(KEY_OBSERVATION_TYPE + ".power.unit"));
                assertEquals(false, table.get(KEY_OBSERVATION_TYPE + ".power.optional"));
                assertEquals("double", table.get(KEY_OBSERVATION_TYPE + ".total"));
                assertEquals("kWh", table.get(KEY_OBSERVATION_TYPE + ".total.unit"));
                assertEquals(true, table.get(KEY_OBSERVATION_TYPE + ".total.optional"));

                return reg;
            }
        });
        assertEquals(reg, new ObservationProviderRegistrationHelper(this, context).observationType(UnitType.class)
                                                                                  .register());
    }
}
