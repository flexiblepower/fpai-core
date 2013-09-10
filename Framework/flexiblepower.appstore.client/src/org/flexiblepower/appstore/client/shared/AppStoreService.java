package org.flexiblepower.appstore.client.shared;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("AppStoreService")
public interface AppStoreService extends RemoteService {
    List<AppStoreItem> getCatalogue() throws IOException;

    List<AppStoreQuestion> getDetails(int itemId) throws IOException;

    void purchase(int itemId, Set<String> acceptedPermissions) throws IOException;

    AppStoreStatus getStatus(int itemId);
}
