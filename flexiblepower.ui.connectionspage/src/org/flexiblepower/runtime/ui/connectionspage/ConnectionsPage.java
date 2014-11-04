package org.flexiblepower.runtime.ui.connectionspage;

import java.util.Locale;

import org.flexiblepower.ui.Widget;

import aQute.bnd.annotation.component.Component;

@Component(properties = { "widget.type=full", "widget.name=connectionspage" })
public class ConnectionsPage implements Widget {
    @Override
    public String getTitle(Locale locale) {
        return "Connections";
    }
}
