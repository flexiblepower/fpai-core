package org.flexiblepower.security;

import javax.net.ssl.SSLSocketFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public interface SecurityManager {

    /**
     * Checks whether a bundle contains a valid signature, which applies to the given condition.
     * 
     * @param bundle
     *            The bundle that has to be checked.
     * @param expectedSigner
     *            The expected signer. This value should be matched using the
     *            {@link FrameworkUtil#matchDistinguishedNameChain(String, java.util.List)} method.
     * @throws SecurityException
     *             When the signature was not valid.
     */
    public void checkBundleSignature(Bundle bundle, String expectedSigner) throws SecurityException;

    /**
     * Returns a {@link SSLSocketFactory} which can be used to create SSL connections that accept connections from the
     * appstore.
     * 
     * @return The {@link SSLSocketFactory} that should be used to create secure connections with the management center
     *         and the appstore.
     * @throws SecurityException
     */
    public SSLSocketFactory getSSLSocketFactory() throws SecurityException;

}
