package org.flexiblepower.ui;

import java.util.Locale;

public interface Widget {
    /**
     * @param locale
     *            The local to base the internationalization on.
     * @return The title to show on the dashboard.
     */
    String getTitle(Locale locale);
}
