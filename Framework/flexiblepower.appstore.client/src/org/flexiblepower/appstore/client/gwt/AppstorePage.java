package org.flexiblepower.appstore.client.gwt;

import java.util.List;

import org.flexiblepower.appstore.client.shared.AppStoreItem;
import org.flexiblepower.appstore.client.shared.AppStoreService;
import org.flexiblepower.appstore.client.shared.AppStoreServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class AppstorePage extends FlowPanel implements EntryPoint {
    private final AppStoreServiceAsync appStoreService;

    public AppstorePage() {
        appStoreService = GWT.create(AppStoreService.class);
    }
    
    @Override
    public void onModuleLoad() {
    	RootPanel root = RootPanel.get("appstoreContent");
    	root.add(this);
    }

    private void loadApps() {
        // Window.alert("trying to access App Store ...");
        appStoreService.getCatalogue(new AsyncCallback<List<AppStoreItem>>() {
            @Override
            public void onSuccess(List<AppStoreItem> apps) {
                for (AppStoreItem app : apps) {
                    add(new AppPurchaseWidget(app, appStoreService));
                }
            }

            @Override
            public void onFailure(Throwable tr) {
                Window.alert("Could not obtain catalogue from App Store, please try again later.");
            }
        });
    }

    public void refresh() {
        clear();
        loadApps();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        getElement().setId("applist");
        setStyleName("dashboard center");
        loadApps();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
    };
}
