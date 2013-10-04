package org.flexiblepower.runtime.ui.user;

import java.util.HashMap;

import org.osgi.service.useradmin.User;

public class Session extends HashMap<String, Object> {
    private static final long serialVersionUID = -1885458683578572091L;

    public final static String KEY_ID = "id";
    public final static String KEY_USER = "user";

    public Session(String id, User user) {
        put(KEY_ID, id);
        put(KEY_USER, user);
    }

    public String getId() {
        return (String) get(KEY_ID);
    }

    public User getUser() {
        return (User) get(KEY_USER);
    }
}
