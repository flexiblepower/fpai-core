package org.flexiblepower.runtime.ui.resourceinfo;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.flexiblepower.messaging.ConnectionManager;
import org.flexiblepower.ui.Widget;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(properties = { "widget.type=full", "widget.name=resourceinfo" })
public class ResourcePage implements Widget {

    private ConnectionManager connectionManager;

    @Reference
    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Create a map of ResourceInfo objects. This method is called by the Widget.
     *
     * @return
     */
    public Map<String, ResourceInfo> getResources() {
        // TODO: how to get resource info from the drivers?
        // Are there even drivers still? Or are they all endpoints?
        return Collections.emptyMap();
    }

    @Override
    public String getTitle(Locale locale) {
        return "Resource Info";
    }
}
