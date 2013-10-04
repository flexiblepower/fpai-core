package org.flexiblepower.runtime.ui.test;

import java.util.Date;
import java.util.Locale;

import org.flexiblepower.ui.Widget;

import aQute.bnd.annotation.component.Component;

@Component(properties={"widget.name=test","widget.type=full"})
public class TestFullWidget implements Widget {
	@Override
	public String getTitle(Locale locale) {
		return "Test Page";
	}
	
	public String update() {
		return "Now = " + new Date();
	}
}
