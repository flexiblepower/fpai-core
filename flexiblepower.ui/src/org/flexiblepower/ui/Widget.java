package org.flexiblepower.ui;

import java.util.Locale;

/**
 * This is a marker interface that is used to register widgets in the service repository. This can be used by a UI of
 * the runtime to should small widgets or even full-sized widgets.
 */
public interface Widget {
    /**
     * @param locale
     *            The local to base the internationalization on.
     * @return The title to show on the dashboard.
     */
    String getTitle(Locale locale);
}
