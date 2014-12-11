package org.flexiblepower.ui;

import java.util.Dictionary;

public interface User {
    String getName();

    Dictionary<String, ?> getProperties();

    boolean hasCredential(String key, Object value);
}
