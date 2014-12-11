package org.flexiblepower.runtime.ui.user;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Hashtable;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.flexiblepower.ui.User;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.UserAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 6873319359921321938L;

    private static final int SECONDS_PER_YEAR = 60 * 60 * 24 * 365;
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);

    private final String document;
    private final ServiceTracker<UserAdmin, UserAdmin> userAdminTracker;

    private final ServiceRegistration<Servlet> registerService;

    private final SessionManager sessionManager;

    public LoginServlet(BundleContext context, SessionManager sessionManager) throws IOException, NoClassDefFoundError {
        this.sessionManager = sessionManager;

        URL url = context.getBundle().getEntry("login.html");
        Reader reader = new InputStreamReader(url.openStream());

        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1024];
        int read = 0;
        while ((read = reader.read(buffer)) >= 0) {
            sb.append(buffer, 0, read);
        }

        document = sb.toString();
        reader.close();

        userAdminTracker = new ServiceTracker<UserAdmin, UserAdmin>(context, UserAdmin.class, null);
        userAdminTracker.open();
        fillUsers();

        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put("alias", "/login.html");
        properties.put("contextId", "fps");
        registerService = context.registerService(Servlet.class, this, properties);
    }

    public void close() {
        registerService.unregister();
        userAdminTracker.close();
    }

    @SuppressWarnings("unchecked")
    private void fillUsers() {
        new Thread("Creating users") {
            @Override
            public void run() {
                UserAdmin userAdmin = null;
                try {
                    userAdmin = userAdminTracker.waitForService(0);
                } catch (InterruptedException ex) {
                    return;
                }
                while (userAdmin.getRole("root") == null || userAdmin.getRole("user") == null) {
                    logger.info("Adding default groups and roles");
                    Group administrators = (Group) userAdmin.getRole("Administrators");
                    if (administrators == null) {
                        administrators = (Group) userAdmin.createRole("Administrators", Role.GROUP);

                        org.osgi.service.useradmin.User root = (org.osgi.service.useradmin.User) userAdmin.getRole("root");
                        if (root == null) {
                            root = (org.osgi.service.useradmin.User) userAdmin.createRole("root", Role.USER);
                            if (root == null) {
                                logger.error("Could not create user root!");
                            } else {
                                root.getCredentials()
                                    .put("password", "$2a$10$FhfxMQS1BPDEqcdUT8Qo0O4Gg6Xb0gI8udk/EYVJ8urNqSZKcnfra");
                                administrators.addMember(root);
                            }
                        }
                    }

                    Group users = (Group) userAdmin.getRole("Users");
                    if (users == null) {
                        users = (Group) userAdmin.createRole("Users", Role.GROUP);

                        org.osgi.service.useradmin.User user = (org.osgi.service.useradmin.User) userAdmin.getRole("user");
                        if (user == null) {
                            user = (org.osgi.service.useradmin.User) userAdmin.createRole("user", Role.USER);
                            if (user == null) {
                                logger.error("Could not create user user!");
                            } else {
                                user.getCredentials()
                                    .put("password", "$2a$10$FhfxMQS1BPDEqcdUT8Qo0OG8/AsfgzG9eGA/WIbuH3Rb33E.XOGe.");
                                users.addMember(user);
                                users.addMember(userAdmin.getRole("root"));
                            }
                        }
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }.start();
    }

    private void createResponse(HttpServletResponse resp, String errorMessage, String redirect) throws IOException {
        resp.setStatus(200);
        resp.setContentType("text/html");
        resp.setCharacterEncoding(Charset.defaultCharset().name());

        String result = document;
        if (errorMessage == null) {
            result = result.replace("$error$", "");
        } else {
            result = result.replace("$error$", "<p class=\"error\">" + errorMessage + "</p>");
        }
        if (redirect == null) {
            result = result.replace("$redirect$", "");
        } else {
            result = result.replace("$redirect$", redirect);
        }

        ServletOutputStream os = resp.getOutputStream();
        os.write(result.getBytes(Charset.defaultCharset()));
        os.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        createResponse(resp, null, req.getParameter("redirect"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String error = null;

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String redirect = req.getParameter("redirect");

        if (username == null) {
            logger.info("Not logged in yet, serving login form");
        } else if (userAdminTracker == null) {
            logger.warn("The userAdmin is unavailable to the LoginServlet");
            error = "Could not connect to user management service, please try again...";
        } else {
            User user = authenticateUser(username, password);

            if (user != null) {
                logger.info("Authenticated " + username + ", creating session");
                Session session = sessionManager.createSession(user);

                Cookie cookie = new Cookie(SessionManager.SESSION_ID, session.getId());
                cookie.setMaxAge(SECONDS_PER_YEAR);
                resp.addCookie(cookie);
                if (redirect == null) {
                    resp.sendRedirect("/");
                } else {
                    resp.sendRedirect(redirect);
                }
                return;
            } else {
                logger.info("Authentication of " + username + " failed");
                error = "Invalid login. Please try again.";
            }
        }

        createResponse(resp, error, redirect);
    }

    private UserImpl authenticateUser(String username, String password) {
        logger.info("Authenticating with username: " + username);

        final String salt = "$2a$10$FhfxMQS1BPDEqcdUT8Qo0O";
        String hash = BCrypt.hashpw(password, salt);

        UserAdmin userAdmin = null;
        try {
            userAdmin = userAdminTracker.waitForService(1000);
        } catch (InterruptedException e) {
        }
        if (userAdmin == null) {
            logger.warn("No UserAdmin active yet!");
            return null;
        }

        org.osgi.service.useradmin.User user = (org.osgi.service.useradmin.User) userAdmin.getRole(username);

        if (user == null || !(user.hasCredential("password", hash) || user.hasCredential("password", password))) {
            logger.warn("No valid user with username " + username);
            return null;
        } else {
            final org.osgi.service.useradmin.User userRef = user;
            return new UserImpl(userRef);
        }
    }
}
