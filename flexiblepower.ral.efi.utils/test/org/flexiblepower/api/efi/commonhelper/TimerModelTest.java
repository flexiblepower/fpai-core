package org.flexiblepower.api.efi.commonhelper;

import java.util.Calendar;
import java.util.Date;

import javax.measure.Measure;
import javax.measure.unit.SI;

import junit.framework.TestCase;

import org.flexiblepower.efi.util.Timer;

public class TimerModelTest extends TestCase {

    @Override
    public void setUp() {

    }

    public void testTimerCreation() {
        Timer source = new Timer(0, "minOn", Measure.valueOf(100, SI.SECOND));
        Timer source2 = new Timer(1, "minOff", Measure.valueOf(10, SI.SECOND));
        TimerModel timerOn = new TimerModel(source);
        assertFalse(timerOn.isBlockingAt(new Date()));
        timerOn.updateFinishedAt(null);
        assertFalse(timerOn.isBlockingAt(new Date()));

        Calendar moment = Calendar.getInstance();
        Date now = moment.getTime();
        moment.add(Calendar.SECOND, 30);
        Date inHalfAMinute = moment.getTime();
        moment.add(Calendar.SECOND, 30);
        Date inOneMinute = moment.getTime();
        moment.add(Calendar.MILLISECOND, 1);
        Date inJustOverOneMinute = moment.getTime();
        timerOn.updateFinishedAt(inOneMinute);
        assertTrue(timerOn.isBlockingAt(inHalfAMinute));
        // Timer is not blocking if the time is now.
        assertFalse(timerOn.isBlockingAt(inOneMinute));
        assertFalse(timerOn.isBlockingAt(inJustOverOneMinute));
        timerOn.updateFinishedAt(inOneMinute);
        assertFalse(timerOn.isBlockingAt(null));
    }
}
