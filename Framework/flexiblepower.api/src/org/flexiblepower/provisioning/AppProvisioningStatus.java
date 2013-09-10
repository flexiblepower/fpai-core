package org.flexiblepower.provisioning;

import java.util.Locale;

import org.flexiblepower.data.applications.App;

public interface AppProvisioningStatus {
    /**
     * @return The {@link App} that is being installed.
     */
    App getApp();

    /**
     * @param locale
     *            The {@link Locale} that should be used to give a proper translation of the current action. The default
     *            language when the locale is not understood, should always be English.
     * @return A description of the current step that is being undertaken. This description should be easy enough to
     *         understand such that it can be used in a user interface.
     */
    String getCurrentAction(Locale locale);

    /**
     * @return A number in the range [0,100] (inclusive) to represent the percentage of the progress. When this method
     *         return 100, the provisioning is completed. Whether this completion is successful, depends on the result
     *         of the {@link #getError()} method.
     */
    int getStatus();

    /**
     * @return When the {@link #getStatus()} returns 100 (indicating that the provisioning is completed), this method
     *         should either return the {@link AppProvisioningException} that has been thrown when an error has occurred
     *         or <code>null</code> when the provisioning was successful. When the {@link #getStatus()} returns a number
     *         less that 100, this method always returns <code>null</code>.
     */
    AppProvisioningException getError();
}
