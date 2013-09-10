package org.flexiblepower.provisioning;

/**
 * The {@link AppProvisioningException} can be thrown during the provisioning of an {@link App} when anything goes
 * wrong. For example:
 * <ul>
 * <li>Any one of the download URI's could not be reached</li>
 * <li>The downloaded bundles are not signed correctly</li>
 * <li>The downloaded bundles ask for more permissions than the user has agreed to</li>
 * <li>The set of bundles have missing dependencies</li>
 * </ul>
 */
public class AppProvisioningException extends Exception {
    private static final long serialVersionUID = 2701348199303786025L;

    private final String bundleLocation;

    public AppProvisioningException(String message, Throwable innerException, String bundleLocation) {
        super(message, innerException);
        this.bundleLocation = bundleLocation;
    }

    public AppProvisioningException(String message, String bundleLocation) {
        super(message);
        this.bundleLocation = bundleLocation;
    }

    /**
     * @return The location of the bundle on which the error has occurred.
     */
    public String getBundleLocation() {
        return bundleLocation;
    }
}
