package org.flexiblepower.runtime.ui.user;

import java.util.Dictionary;

import org.flexiblepower.ui.User;

final class UserImpl implements User {
    private final org.osgi.service.useradmin.User user;

    UserImpl(org.osgi.service.useradmin.User userRef) {
        user = userRef;
    }

    @Override
    public boolean hasCredential(String key, Object value) {
        return user.hasCredential(key, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Dictionary<String, ?> getProperties() {
        return user.getProperties();
    }

    @Override
    public String getName() {
        return user.getName();
    }

    public org.osgi.service.useradmin.User getUser() {
        return user;
    }
}
