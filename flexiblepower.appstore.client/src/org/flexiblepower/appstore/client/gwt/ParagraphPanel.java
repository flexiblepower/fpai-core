package org.flexiblepower.appstore.client.gwt;

import com.google.gwt.dom.client.Document;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.SimplePanel;

public class ParagraphPanel extends SimplePanel {
    public ParagraphPanel(String text) {
        this(SafeHtmlUtils.fromString(text));
    }

    public ParagraphPanel(SafeHtml text) {
        super(Document.get().createPElement());
        setText(text);
    }

    public void setText(SafeHtml text) {
        getElement().setInnerHTML(text.asString());
    }
}
