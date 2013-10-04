package org.flexiblepower.appstore.client.gwt;

import com.google.gwt.dom.client.Document;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.SimplePanel;

public class HeaderPanel extends SimplePanel {
    public HeaderPanel(int n, String text) {
        this(n, SafeHtmlUtils.fromString(text));
    }

    public HeaderPanel(int n, SafeHtml text) {
        super(Document.get().createHElement(n));
        getElement().setInnerHTML(text.asString());
    }
}
