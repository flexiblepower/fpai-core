package org.flexiblepower.runtime.ui.user;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.flexiblepower.ui.User;

public class SessionManager {
    public static final String SESSION_ID = "org.flexiblepower.session";

    private final Map<String, Session> sessions;

    public SessionManager() {
        sessions = new HashMap<String, Session>();
    }

    public Session createSession(User user) {
        String id = UUID.randomUUID().toString();
        Session session = new Session(id, user);
        sessions.put(id, session);
        return session;
    }

    public void invalidate(Session session) {
        sessions.remove(session.getId());
    }

    public Session getSession(String id) throws IllegalSessionException {
        Session session = sessions.get(id);
        if (session == null) {
            throw new IllegalSessionException();
        } else {
            return session;
        }
    }

    public Session getSession(HttpServletRequest req) throws IllegalSessionException {
        Cookie[] cookies = req.getCookies();
        String sessionId = getCookieValue(cookies, SESSION_ID);
        if (sessionId != null) {
            return getSession(sessionId);
        } else {
            throw new IllegalSessionException();
        }
    }

    private static String getCookieValue(Cookie[] cookies, String cookieName) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return (cookie.getValue());
                }
            }
        }
        return null;
    }
}
