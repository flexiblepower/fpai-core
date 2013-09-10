package org.flexiblepower.runtime.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.flexiblepower.security.SecurityManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.condpermadmin.BundleLocationCondition;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;

@Component
public class SecurityManagerImpl implements SecurityManager {
    private final static Logger logger = LoggerFactory.getLogger(SecurityManagerImpl.class);

    // sslSocketFactory, used to generate secure network connections.
    private SSLSocketFactory sslSocketFactory;

    static {
        // FIXME localhost default accepted for testing.
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
            @Override
            public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
                if (hostname.equals("localhost")) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Constructor, starts the activate method.
     * 
     * @throws SecurityManagerException
     */
    public SecurityManagerImpl() {
        try {
            AccessController.checkPermission(new AllPermission());
        } catch (AccessControlException e) {
            throw new SecurityException("you need 'AllPermission' to start the securitymanager", e);
        }
    }

    /**
     * initialize the keystore and sslsocketfactory
     */
    @Activate
    public void activate() {
        try {
            AccessController.checkPermission(new AllPermission());
        } catch (AccessControlException e) {
            throw new SecurityException("you need 'AllPermission' to activate the securitymanager", e);
        }

        logger.debug("starting SecurityManagerImpl");

        sslSocketFactory = initKeystore();

        // Setting CPadmin
        BundleContext context = FrameworkUtil.getBundle(SecurityManagerImpl.class).getBundleContext();
        ServiceReference<?> serviceReference = context.getServiceReference(ConditionalPermissionAdmin.class.getName());
        if (serviceReference == null) {
            throw new IllegalArgumentException(ConditionalPermissionAdmin.class.getName() + " IS NULL");
        }

        serviceReference = context.getServiceReference(PermissionAdmin.class.getName());
        if (serviceReference == null) {
            throw new IllegalArgumentException(PermissionAdmin.class.getName() + " IS NULL");
        }
    }

    @Override
    public void checkBundleSignature(Bundle bundle, String expectedSigner) {
        logger.debug("Bundle Location condition" + BundleLocationCondition.class.getName());
        logger.debug("Bundle location" + bundle.getLocation());

        Map<X509Certificate, List<X509Certificate>> signers = bundle.getSignerCertificates(Bundle.SIGNERS_ALL);
        logger.debug("Number of signers: " + signers.size());
        boolean match = false;
        for (List<X509Certificate> signerCerts : signers.values()) {
            List<String> dnChain = new ArrayList<String>(signerCerts.size());
            for (X509Certificate signer : signerCerts) {
                dnChain.add(signer.getSubjectDN().getName());
                logger.debug("DN name: " + signer.getSubjectDN().getName());
                logger.debug("expectedSigner: " + expectedSigner);
            }
            if (FrameworkUtil.matchDistinguishedNameChain(expectedSigner, dnChain)) {
                match = true;
                break;
            }
        }

        if (!match) {
            throw new SecurityException("The bundle " + bundle.getLocation() + " is not correctly signed.");
        }
    }

    @Override
    public SSLSocketFactory getSSLSocketFactory() {
        // try {
        // AccessController.checkPermission(new AllPermission());
        // } catch (AccessControlException e) {
        // throw new SecurityException("you need 'AllPermission' to retrieve the SSLSocketFactory", e);
        // }
        if (sslSocketFactory == null) {
            throw new IllegalStateException("SSLSocketfactory not initialized, see logging information at initialization");
        }

        return sslSocketFactory;
    }

    /**
     * initialize the keystore. This keystore can be used for secure connections. (not for signatures on bundles)
     * 
     * @return a sslsocketfactory.
     * @throws SecurityManagerException
     */
    private SSLSocketFactory initKeystore() {
        try {
            System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true"); // FIXME
            KeyStore ks = KeyStore.getInstance("JKS");
            InputStream is = getClass().getClassLoader().getResourceAsStream("keystore");
            // System.out.println("keystore: " + is.toString());
            ks.load(is, "password".toCharArray()); // FIXME
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, "password".toCharArray()); // FIXME password should not be here in plaintext
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // sslContext.init(kmf.getKeyManagers(), getTrustManagers(), new SecureRandom());
            sslContext.init(kmf.getKeyManagers(), getTrustManagers(), new SecureRandom());

            return sslContext.getSocketFactory();
        } catch (KeyStoreException e) {
            logger.error("could not load keystore ");
        } catch (UnrecoverableKeyException e) {
            logger.error("could not load keys ");
        } catch (NoSuchAlgorithmException e) {
            logger.error("No such algorithm exception during construction of sslcontext or secureRandom");
        } catch (CertificateException e) {
            logger.error("certificate exception during loading of keystore");
        } catch (FileNotFoundException e) {
            logger.error("could not find keystore (FileNotFoundException)");
        } catch (IOException e) {
            logger.error("could not load keystore (IOException)");
        } catch (KeyManagementException e) {
            logger.error("could not construct keystore (keymanagementexception)");
        }
        return null;
    }

    private TrustManager[] getTrustManagers() {
        TrustManager[] trustManagers = new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] certs, String signer) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String signer) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

        } };

        return trustManagers;
    }
}
