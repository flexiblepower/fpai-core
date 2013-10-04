package org.flexiblepower.runtime.ui.appinfo;

import java.util.ArrayList;
import java.util.List;

public class AppInfo {

    private String productId;
    private String appName;
    private String appDescription;
    private boolean isRunning;

    /**
     * changed this to AppInfoComponent and non-final otherwise serialization issues. Also avoided addComponent but give
     * them on construct to be immutable
     */
    private List<String> components;

    public AppInfo() {
    }

    public AppInfo(String productId, String appName, String appDescription, List<String> components, boolean isRunning) {
        this.productId = productId;
        this.appName = appName;
        this.appDescription = appDescription;
        this.components = new ArrayList<String>(components);
        this.isRunning = isRunning;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public String getProductId() {
        return productId;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public List<String> getComponents() {
        return components;
    }

}
