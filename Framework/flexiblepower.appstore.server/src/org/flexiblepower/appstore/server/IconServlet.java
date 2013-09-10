package org.flexiblepower.appstore.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.flexiblepower.appstore.common.Application;
import org.flexiblepower.appstore.server.model.ApplicationStore;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(provide = Servlet.class, properties = "alias=/appstore/icon")
public class IconServlet extends HttpServlet {
    private static final long serialVersionUID = -4575400026767135780L;

    private ApplicationStore applicationStore;

    @Reference
    public void setApplicationStore(ApplicationStore applicationStore) {
        this.applicationStore = applicationStore;
    }

    @SuppressWarnings("resource")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jarFile = null;
        try {
            int appId = Integer.parseInt(req.getPathInfo().substring(1));
            Application application = applicationStore.get(appId);
            jarFile = application.getComponents().get(0).getJarFile();
        } catch (NumberFormatException ex) {
        }

        if (jarFile == null) {
            jarFile = req.getPathInfo();
        }

        InputStream inputStream = DownloadServlet.openJarFile(jarFile);
        if (inputStream != null) {
            JarInputStream jis = new JarInputStream(inputStream);
            JarEntry entry = null;
            while ((entry = jis.getNextJarEntry()) != null) {
                if ("icon.png".equals(entry.getName())) {
                    write(resp, jis);
                    return;
                }
            }

            jis.close();
            write(resp, getClass().getClassLoader().getResourceAsStream("noicon.png"));
        }
    }

    private void write(HttpServletResponse resp, InputStream is) throws IOException {
        resp.setContentType("image/png");
        ServletOutputStream os = resp.getOutputStream();

        byte[] buffer = new byte[4096];
        int length = 0;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        os.close();
        is.close();
    }
}
