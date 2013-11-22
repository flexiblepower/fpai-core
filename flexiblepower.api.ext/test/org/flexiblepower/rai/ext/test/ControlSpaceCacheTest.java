package org.flexiblepower.rai.ext.test;

import java.util.Date;

import junit.framework.TestCase;

import org.flexiblepower.rai.ControlSpace;
import org.flexiblepower.rai.ext.ControlSpaceCache;
import org.flexiblepower.time.TimeService;

public class ControlSpaceCacheTest extends TestCase {

    private static class MockCS extends ControlSpace {
        public MockCS(long validFrom, long validThru) {
            super("resoureId", new Date(validFrom), new Date(validThru));
        }
    }

    /** Current time is always 1000 */
    private TimeService timeService;

    @Override
    protected void setUp() throws Exception {
        timeService = new TimeService() {
            private final Date date = new Date(1000);

            @Override
            public Date getTime() {
                return date;
            }

            @Override
            public long getCurrentTimeMillis() {
                return 1000;
            }
        };
    }

    public void testNoControlSpace() {
        // Create
        ControlSpaceCache<MockCS> c = new ControlSpaceCache<MockCS>(timeService);
        // Test
        assertNull(c.getActiveControlSpace());
        assertNull(c.getActiveControlSpace(new Date(0)));
        assertNull(c.nextControlSpaceChange());
        assertNotNull(c.getAllControlSpaces());
        assertEquals(c.getAllControlSpaces().size(), 0);
    }

    public void testExpiredControlSpaces() {
        // Create
        ControlSpaceCache<MockCS> c = new ControlSpaceCache<MockCS>(timeService);
        // Add
        c.addNewControlSpace(new MockCS(100, 200));
        c.addNewControlSpace(new MockCS(100, 800));
        c.addNewControlSpace(new MockCS(800, 999));
        // Test
        assertNull(c.getActiveControlSpace());
        assertNull(c.getActiveControlSpace(new Date(2000)));
        assertNull(c.nextControlSpaceChange());
        assertNotNull(c.getAllControlSpaces());
        assertEquals(c.getAllControlSpaces().size(), 0);
    }

    public void testNoActiveControlSpace() {
        // Create
        ControlSpaceCache<MockCS> c = new ControlSpaceCache<MockCS>(timeService);
        // Add
        c.addNewControlSpace(new MockCS(100, 800));
        c.addNewControlSpace(new MockCS(1200, 2000));
        // Test
        assertNull(c.getActiveControlSpace());
    }

    public void testActiveControlSpaces() {
        // Create
        ControlSpaceCache<MockCS> c = new ControlSpaceCache<MockCS>(timeService);
        // Add
        MockCS cs1 = new MockCS(900, 1500);
        c.addNewControlSpace(cs1);
        MockCS cs2 = new MockCS(1050, 1450);
        c.addNewControlSpace(cs2);
        MockCS cs3 = new MockCS(1050, 1250);
        c.addNewControlSpace(cs3);
        MockCS cs4 = new MockCS(1150, 1350);
        c.addNewControlSpace(cs4);
        // Test
        assertEquals(cs1, c.getActiveControlSpace(new Date(1000)));
        assertEquals(cs3, c.getActiveControlSpace(new Date(1100)));
        assertEquals(cs4, c.getActiveControlSpace(new Date(1200)));
        assertEquals(cs4, c.getActiveControlSpace(new Date(1300)));
        assertEquals(cs2, c.getActiveControlSpace(new Date(1400)));
        assertNull(c.getActiveControlSpace(new Date(2000)));
    }

    public void testNextControlSpaceCurrentEnds() {
        // Create
        ControlSpaceCache<MockCS> c = new ControlSpaceCache<MockCS>(timeService);
        // Add
        c.addNewControlSpace(new MockCS(800, 1200));
        // Test
        assertEquals(c.nextControlSpaceChange().getTime(), 1200);
    }

    public void testNextControlSpaceNewOverlaps() {
        // Create
        ControlSpaceCache<MockCS> c = new ControlSpaceCache<MockCS>(timeService);
        // Add
        c.addNewControlSpace(new MockCS(1000, 1200));
        c.addNewControlSpace(new MockCS(900, 1100));
        // Test
        assertEquals(c.nextControlSpaceChange().getTime(), 1100);
    }

    public void testNextControlSpaceNewOverlapsAndOneInBetween() {
        // Create
        ControlSpaceCache<MockCS> c = new ControlSpaceCache<MockCS>(timeService);
        // Add
        c.addNewControlSpace(new MockCS(1000, 1200));
        c.addNewControlSpace(new MockCS(1050, 1090));
        c.addNewControlSpace(new MockCS(900, 1100));
        // Test
        assertEquals(c.nextControlSpaceChange().getTime(), 1100);
    }

    public void testNextControlSpaceOldOverlaps() {
        // Create
        ControlSpaceCache<MockCS> c = new ControlSpaceCache<MockCS>(timeService);
        // Add
        c.addNewControlSpace(new MockCS(900, 1100));
        c.addNewControlSpace(new MockCS(1000, 1200));
        // Test
        assertEquals(c.nextControlSpaceChange().getTime(), 1200);
    }

    public void testNextControlSpaceNewStarts() {
        // Create
        ControlSpaceCache<MockCS> c = new ControlSpaceCache<MockCS>(timeService);
        // Add
        c.addNewControlSpace(new MockCS(5000, 6000));
        // Test
        assertEquals(c.nextControlSpaceChange().getTime(), 5000);
    }

    public void testCacheCleanup() {
        // Create
        ControlSpaceCache<MockCS> c = new ControlSpaceCache<MockCS>(timeService);
        // Add
        MockCS controlSpace = new MockCS(1000, 1001);
        c.addNewControlSpace(controlSpace);
        for (int i = 0; i < 500; i++) {
            c.addNewControlSpace(new MockCS(i, i + 1));
        }
        // Test
        assertNotNull(c.getAllControlSpaces());
        assertEquals(1, c.getAllControlSpaces().size());
        assertEquals(controlSpace, c.getAllControlSpaces().get(0));
    }
}
