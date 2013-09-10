package org.flexiblepower.appstore.client.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AppStoreStatus implements IsSerializable {
    private String currentAction;
    private int status;
    private String errorMessage;

    public AppStoreStatus() {
    }

    public AppStoreStatus(String currentAction, int status, String errorMessage) {
        this.currentAction = currentAction;
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public String getCurrentAction() {
        return currentAction;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
