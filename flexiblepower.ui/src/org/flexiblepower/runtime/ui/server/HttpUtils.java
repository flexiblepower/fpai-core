package org.flexiblepower.runtime.ui.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static final Map<String, String> contentTypes = new HashMap<String, String>();

    static {
        add("text/html", "html", "htm");
        add("text/css", "css");
        add("text/xml", "xml");
        add("application/javascript", "js");
        add("image/png", "png");
        add("image/jpeg", "jpg", "jpeg");
        add("image/gif", "gif");
        add("image/svg", "svg");
    }

    private HttpUtils() {
    }

    private static void add(String contentType, String... extensions) {
        logger.trace("Entering add, contentType = {}, extensions = {}", contentType, extensions);
        for (String ext : extensions) {
            contentTypes.put(ext, contentType);
        }
        logger.trace("Leaving add");
    }

    public static String getContentType(String filename) {
        logger.trace("Entering getContentType, filename = {}", filename);
        int ix = filename.lastIndexOf('.');
        if (ix > 0) {
            String ext = filename.substring(ix + 1).toLowerCase();
            if (contentTypes.containsKey(ext)) {
                return contentTypes.get(ext);
            }
        }
        String result = URLConnection.guessContentTypeFromName(filename);
        logger.trace("Leaving getContentType, result = {}", result);
        return result;
    }

    public static void setNoCaching(HttpServletResponse resp) {
        logger.trace("Entering setNoCaching, resp = {}", resp.getClass());
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        resp.setHeader("Pragma", "no-cache"); // HTTP 1.0
        resp.setDateHeader("Expires", 0);
        logger.trace("Leaving setNoCaching");
    }

    public static void
            writeFile(URL url, long expirationTime, String name, HttpServletResponse resp, Locale locale) throws IOException {
        logger.trace("Entering writeFile, url = {}, expirationTime = {}, name = {}, locale = {}",
                     url,
                     expirationTime,
                     name,
                     locale);
        if (url == null) {
            resp.sendError(404);
        } else {
            if (expirationTime <= 0) {
                setNoCaching(resp);
            } else {
                resp.setDateHeader("Expires", System.currentTimeMillis() + expirationTime);
            }

            resp.setContentType(getContentType(name));
            if (name.endsWith(".html") || name.endsWith(".css") || name.endsWith(".js")) {
                resp.setCharacterEncoding("UTF-8");
            }
            write(url.openStream(), resp.getOutputStream());
        }
        logger.trace("Leaving writeFile");
    }

    public static void write(InputStream input, OutputStream output) throws IOException {
        logger.trace("Entering write, input = {}, output = {}", input, output);
        byte[] buffer = new byte[4096];
        int read = 0;
        try {
            while ((read = input.read(buffer)) >= 0) {
                output.write(buffer, 0, read);
            }
        } finally {
            input.close();
        }
        logger.trace("Leaving write", input, output);
    }

    public static String readData(InputStream input) throws IOException {
        logger.trace("Entering readData, input = {}", input);
        try {
            Reader reader = new InputStreamReader(input);
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[1024];
            int read = 0;
            while ((read = reader.read(buffer)) >= 0) {
                sb.append(buffer, 0, read);
            }
            logger.trace("Levaing readData, result = (String of {} characters)", sb.length());
            return sb.toString();
        } finally {
            input.close();
        }
    }
}
