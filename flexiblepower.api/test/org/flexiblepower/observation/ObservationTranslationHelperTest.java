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
        assertEquals(Boolean.TYPE, map.get("boolean").getReturnType());
        assertEquals(Integer.TYPE, map.get("integer").getReturnType());
        assertEquals(String.class, map.get("complex_naming_type").getReturnType());
    }

    interface NonBean {
        void getNonsense();

        int getMoreNonsense(int x);

        int calculate();
    }

    enum TestEnum {
        X, Y, Z;
    }

    public void testIsJavaBean() {
        assertTrue(ObservationTranslationHelper.isJavaBean(TestBean.class));
        assertFalse(ObservationTranslationHelper.isJavaBean(String.class));
        assertFalse(ObservationTranslationHelper.isJavaBean(int.class));
        assertFalse(ObservationTranslationHelper.isJavaBean(TestBean[].class));
        assertFalse(ObservationTranslationHelper.isJavaBean(NonBean.class));
        assertFalse(ObservationTranslationHelper.isJavaBean(TestEnum.class));
    }
}
