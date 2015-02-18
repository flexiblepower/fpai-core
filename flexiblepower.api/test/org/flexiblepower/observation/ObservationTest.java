package org.flexiblepower.observation;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class ObservationTest extends TestCase {
    static class DeepValue {
        public int getX() {
            return 1;
        }

        public int getY() {
            return 2;
        }

        public String getZ() {
            return "Z";
        }

        @Override
        public boolean equals(Object obj) {
            return getClass() == obj.getClass();
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

    static class ParentValue {
        public boolean isActive() {
            return true;
        }

        public DeepValue getDeep() {
            return new DeepValue();
        }

        public List<Integer> getList() {
            return Arrays.asList(1, 2, 3);
        }

        @Override
        public boolean equals(Object obj) {
            return getClass() == obj.getClass();
        }

        @Override
        public int hashCode() {
            return 2;
        }
    }

    public void testDeepValues() {
        Observation<ParentValue> observation = new Observation<ParentValue>(new Date(), new ParentValue());

        Map<String, Object> map = observation.getValueMap();
        assertEquals(6, map.size());
        assertEquals(true, map.get("active"));
        assertEquals(new DeepValue(), map.get("deep"));
        assertEquals(1, map.get("deep.x"));
        assertEquals(2, map.get("deep.y"));
        assertEquals("Z", map.get("deep.z"));
        assertEquals(Arrays.asList(1, 2, 3), map.get("list"));

        assertEquals(true, observation.getValue("active"));
        assertEquals(new DeepValue(), observation.getValue("deep"));
        assertEquals(1, observation.getValue("deep.x"));
        assertEquals(2, observation.getValue("deep.y"));
        assertEquals("Z", observation.getValue("deep.z"));
        assertEquals(Arrays.asList(1, 2, 3), observation.getValue("list"));
    }
}
