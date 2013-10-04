package org.flexiblepower.appstore.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;

@Component(provide = Servlet.class, properties = "alias=/appstore/download")
public class DownloadServlet extends HttpServlet {
    private static final long serialVersionUID = 7073340410506169301L;

    private static final Logger logger = LoggerFactory.getLogger(DownloadServlet.class);

    private static final int BUFFER_SIZE = 4096;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Downloadrequest for file: " + req.getPathInfo());

        String jarFile = req.getPathInfo();
        InputStream is = openJarFile(jarFile);
        if (is != null) {
            try {
                resp.setContentType("application/octet-stream");
                resp.setHeader("Content-Disposition", "attachment; filename=\"" + jarFile + "\"");

                OutputStream os = resp.getOutputStream();
                byte[] byteBuffer = new byte[BUFFER_SIZE];
                int length = 0;
                while (((length = is.read(byteBuffer)) != -1)) {
                    os.write(byteBuffer, 0, length);
                }
                os.close();
            } finally {
                is.close();
            }
        } else {

        }
    }

    public static InputStream openJarFile(String jarFile) {
        String bundleName = jarFile.substring(0, jarFile.indexOf('-'));

        for (String directory : new String[] { "apps/", "../../Release/" + bundleName, "../../Repository/" + bundleName }) {
            File file = new File(directory, jarFile);

            if (file.exists()) {
                logger.info("Serving from file: " + file.getAbsolutePath());

                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    // shouldn't generally occur unless file was removed between file.exists() and new
                    // FileInputStream(file), in that case, we continue and try opening the jar from the next directory
                }
            }
        }
        logger.warn("File not found: " + jarFile);
        return null;
    }
}
