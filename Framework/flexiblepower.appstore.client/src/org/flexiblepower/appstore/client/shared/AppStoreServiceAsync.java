package org.flexiblepower.appstore.client.shared;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AppStoreServiceAsync {

    void getCatalogue(AsyncCallback<List<AppStoreItem>> callback);

    void getDetails(int itemId, AsyncCallback<List<AppStoreQuestion>> callback);

    void getStatus(int itemId, AsyncCallback<AppStoreStatus> callback);

    void purchase(int itemId, Set<String> acceptedPermissions, AsyncCallback<Void> callback);

}
