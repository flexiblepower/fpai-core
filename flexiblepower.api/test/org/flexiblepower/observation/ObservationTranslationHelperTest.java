package org.flexiblepower.observation;

import java.lang.reflect.Method;
import java.util.Map;

import junit.framework.TestCase;

public class ObservationTranslationHelperTest extends TestCase {
    interface TestBean {
        boolean isBoolean();

        int getInteger();

        String getComplexNamingType();
    }

    public void testGettingGetters() {
        Map<String, Method> map = ObservationTranslationHelper.getGetterMethods(TestBean.class);
        assertEquals(3, map.size());
        System.out.println(map);
        assertEquals(Boolean.TYPE, map.get("boolean").getReturnType());
        assertEquals(Integer.TYPE, map.get("integer").getReturnType());
        assertEquals(String.class, map.get("complex_naming_type").getReturnType());
    }
}
