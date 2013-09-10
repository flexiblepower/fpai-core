package org.flexiblepower.runtime.ui.test;

import java.util.Date;
import java.util.Locale;

import org.flexiblepower.ui.Widget;

import aQute.bnd.annotation.component.Component;

@Component
public class TestSmallWidget implements Widget {
	@Override
	public String getTitle(Locale locale) {
		return "Small Test Widget";
	}
	
	public String update() {
		return "Now = " + new Date();
	}
}
