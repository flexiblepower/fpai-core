package org.flexiblepower.appstore.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.flexiblepower.security.SecurityManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private static final int READ_TIMEOUT = 5000;

    public String getResponse(URL url) throws IOException {

        HttpURLConnection connection = getConnection(url);
        if (connection == null) {
            logger.error("no connection to: " + url.toString() + " could be made");
            return null;

        }
        if (logger.isDebugEnabled()) {
            logger.debug("Send HTTP GET request to " + url);
        }

        try {

            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setReadTimeout(READ_TIMEOUT);

            if (logger.isDebugEnabled()) {
                logger.debug("Connecting to " + url + " with read timeout " + READ_TIMEOUT);
            }

            connection.connect();

            if (!(connection.getResponseCode() == HttpURLConnection.HTTP_OK)) {
                logger.warn("Received error response " + connection.getResponseMessage() + " from " + url);
                throw new IOException("Received error response " + connection.getResponseMessage() + " from " + url);
            }

            if ("UTF-8".equalsIgnoreCase(connection.getContentEncoding())) {
                logger.warn("Unsupported encoding received " + connection.getContentEncoding() + " from " + url);
                throw new IOException("Unsupported encoding received " + connection.getContentEncoding()
                                      + " from "
                                      + url);
            }

            InputStream inputStream = connection.getInputStream();
            String payload = convertStreamToString(inputStream);

            if (logger.isDebugEnabled()) {
                logger.debug("Received response " + payload + " from " + url);
            }

            return payload;

        } catch (IOException e) {
            logger.error("A transport error occurred " + e.getMessage() + " with " + url, e);
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String postRequest(URL url, String payload) throws IOException {
        HttpURLConnection connection = getConnection(url);
        if (connection == null) {
            logger.error("no connection to: " + url.toString() + " could be made");
            return null;

        }

        if (logger.isDebugEnabled()) {
            logger.debug("Send HTTP POST request to " + url + " with payload " + payload);
        }

        try {

            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setReadTimeout(READ_TIMEOUT);

            OutputStream outputStream = connection.getOutputStream();
            writeToOutputStream(outputStream, payload);

            if (logger.isDebugEnabled()) {
                logger.debug("Connecting to " + url + " with read timeout " + READ_TIMEOUT);
            }

            connection.connect();

            if (!(connection.getResponseCode() == HttpURLConnection.HTTP_OK)) {
                logger.warn("Received error response " + connection.getResponseMessage() + " from " + url);
                throw new IOException("Received error response " + connection.getResponseMessage() + " from " + url);
            }

            if ("UTF-8".equalsIgnoreCase(connection.getContentEncoding())) {
                logger.warn("Unsupported encoding received " + connection.getContentEncoding() + " from " + url);
                throw new IOException("Unsupported encoding received " + connection.getContentEncoding()
                                      + " from "
                                      + url);
            }

            InputStream inputStream = connection.getInputStream();
            String response = convertStreamToString(inputStream);

            if (logger.isDebugEnabled()) {
                logger.debug("Received response " + response + " from " + url);
            }

            return response;

        } catch (IOException e) {
            logger.error("A transport error occurred " + e.getMessage() + " with " + url, e);
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String convertStreamToString(InputStream inputStream) throws IOException {
        String result = "";

        try {
            result = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            result = "";
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return result;
    }

    private void writeToOutputStream(OutputStream outputStream, String data) throws IOException {
        try {
            byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    private HttpURLConnection getConnection(URL url) throws IOException {
        BundleContext bc = FrameworkUtil.getBundle(getClass()).getBundleContext();
        ServiceReference<SecurityManager> reference = bc.getServiceReference(SecurityManager.class);
        SecurityManager securityManager = bc.getService(reference);
        try {
            SSLSocketFactory sslSocketFactory = securityManager.getSSLSocketFactory();
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(sslSocketFactory);
            return connection;
        } catch (SecurityException e) {
            throw new IOException(e);
        }
    }
}
