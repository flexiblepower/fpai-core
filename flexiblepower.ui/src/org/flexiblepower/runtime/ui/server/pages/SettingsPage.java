package org.flexiblepower.runtime.ui.server.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServlet;

import org.flexiblepower.runtime.ui.server.widgets.AbstractWidgetManager;
import org.flexiblepower.runtime.ui.server.widgets.WidgetRegistration;
import org.flexiblepower.runtime.ui.server.widgets.WidgetRegistry;
import org.flexiblepower.ui.Widget;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Reference;
import aQute.bnd.annotation.metatype.Configurable;
import aQute.bnd.annotation.metatype.Meta.AD;
import aQute.bnd.annotation.metatype.Meta.OCD;

@Component(designate = SettingsPage.Config.class,
           configurationPolicy = ConfigurationPolicy.optional,
           immediate = true,
           provide = { Widget.class },
           properties = { "widget.type=full", "widget.name=settings", "widget.ranking=1000000" })
public class SettingsPage extends AbstractWidgetManager implements Widget {
    private static final Logger logger = LoggerFactory.getLogger(SettingsPage.class);
    private final List configurationList = new ArrayList();

    @OCD(description = "Configuration of the Settings Servlet", name = "Settings Configuration")
    public interface Config {
        @AD(deflt = "31536000",
            description = "Expiration time of static content (in seconds)",
            name = "Expiration time",
            optionLabels = { "No caching", "A minute", "An hour", "A day", "A year" },
            optionValues = { "0", "60", "3600", "86400", "31536000" },
            required = false)
        long expireTime();
    }

    private long expirationTime = 31536000000L;
    private ConfigurationAdmin configurationAdmin;

    @Activate
    public void activate(Map<String, Object> properties) {
        logger.trace("Entering activate, properties = " + properties);
        Config config = Configurable.createConfigurable(Config.class, properties);
        expirationTime = config.expireTime() * 1000;

        Bundle[] bundles = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundles();

        MetaTypeServiceImpl metaTypeService = new MetaTypeServiceImpl();
        metaTypeService.start(FrameworkUtil.getBundle(this.getClass()).getBundleContext());

        logger.trace("Leaving activate");
    }

    @Override
    @Reference(dynamic = true, multiple = true, optional = true, target = "(!(" + WidgetRegistry.KEY_TYPE
                                                                          + "="
                                                                          + WidgetRegistry.VALUE_TYPE_FULL
                                                                          + "))")
    public synchronized void addWidget(Widget widget, Map<String, Object> properties) {
        super.addWidget(widget, properties);
        notifyAll();
    }

    @Reference
    public void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = configurationAdmin;
    }

    @Override
    public synchronized void removeWidget(Widget widget) {
        super.removeWidget(widget);
        notifyAll();
    }

    @Override
    public String createPath(WidgetRegistration registration) {
        return "/widget/" + registration.getId();
    }

    @Override
    public HttpServlet createServlet(WidgetRegistration registration) {
        return new DashboardWidgetServlet(registration, expirationTime);
    }

    // Full-size widget functions

    @Override
    public String getTitle(Locale locale) {
        return "Settings";
    }

    public synchronized SortedMap<Integer, String> getWidgets(Locale locale, Integer[] currentWidgets) {
        logger.trace("Entering getWidgets, locale = {}, currentWidgets = {}", locale, currentWidgets);
        SortedMap<Integer, String> widgetInfo = getWidgetInfo(locale);

        if (Arrays.equals(widgetInfo.keySet().toArray(new Integer[widgetInfo.size()]), currentWidgets)) {
            logger.trace("No change, waiting...");
            try {
                wait(30000);
            } catch (InterruptedException ex) {
                // Expected
            }

            widgetInfo = getWidgetInfo(locale);
        }

        logger.trace("Leaving getWidgets, result = {}", widgetInfo);
        return widgetInfo;
    }

    private SortedMap<Integer, String> getWidgetInfo(Locale locale) {
        SortedMap<Integer, String> widgetInfo = new TreeMap<Integer, String>();
        for (WidgetRegistration reg : getRegistrations()) {
            widgetInfo.put(reg.getId(), reg.getWidget().getTitle(locale));
        }
        return widgetInfo;
    }

}
