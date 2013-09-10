package org.flexiblepower.appstore.client.gwt;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

public class UnsortedList extends ComplexPanel {
    public UnsortedList() {
        setElement(DOM.createElement("ul"));
    }

    @Override
    public void add(Widget w) {
        Element li = Document.get().createLIElement().cast();
        getElement().appendChild(li);
        super.add(w, li);
    }
}
