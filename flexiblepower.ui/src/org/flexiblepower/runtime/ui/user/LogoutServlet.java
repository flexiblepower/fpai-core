package org.flexiblepower.runtime.ui.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.Servlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 5622570409132973468L;
    private static final Logger logger = LoggerFactory.getLogger(LogoutServlet.class);

    private final SessionManager sessionManager;
    private final ServiceRegistration<Servlet> serviceRegistration;

    public LogoutServlet(BundleContext context, SessionManager sessionManager) {
        this.sessionManager = sessionManager;

        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put("alias", "/logout.html");
        properties.put("contextId", "fps");
        serviceRegistration = context.registerService(Servlet.class, this, properties);
    }

    public void close() {
        serviceRegistration.unregister();
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean loggedout = false;

        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();

        Cookie[] cookies = req.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if ((cookie.getName().equals(SessionManager.SESSION_ID))) {
                    Session session;
                    try {
                        session = sessionManager.getSession(cookie.getValue());
                    } catch (IllegalSessionException e) {
                        logger.warn("No session with sessionId " + cookie.getValue());
                        return;
                    }

                    String username = session.getUser().getName();
                    String id = session.getId();

                    // remove from HashMap
                    sessionManager.invalidate(session);

                    writer.write("logged out session " + cookie.getValue());

                    // remove cookie
                    cookie.setMaxAge(0);
                    resp.addCookie(cookie);
                    loggedout = true;
                    logger.info("Logged out user " + username + " with sessionId " + id);
                    resp.sendRedirect("/login.html");
                    break;
                }
            }
        }

        if (!loggedout) {
            logger.warn("No session to log out");
            resp.sendRedirect("/login.html");
        }
    }
}
