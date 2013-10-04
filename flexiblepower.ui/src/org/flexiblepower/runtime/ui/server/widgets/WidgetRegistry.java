package org.flexiblepower.runtime.ui.server.widgets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.flexiblepower.ui.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WidgetRegistry implements Iterable<WidgetRegistration> {
    public static final String KEY_TYPE = "widget.type";
    public static final String VALUE_TYPE_FULL = "full";
    public static final String VALUE_TYPE_SMALL = "small";
    public static final String KEY_NAME = "widget.name";
    public static final String KEY_RANKING = "widget.ranking";

    final Logger logger = LoggerFactory.getLogger(WidgetRegistry.class);

    final AtomicInteger widgetCounter;
    private final SortedSet<WidgetRegistration> registrations;
    private final Map<Widget, WidgetRegistration> indexWidget;
    final Map<String, WidgetRegistration> indexId;

    public WidgetRegistry() {
        widgetCounter = new AtomicInteger();
        registrations = new TreeSet<WidgetRegistration>();
        indexWidget = new HashMap<Widget, WidgetRegistration>();
        indexId = new HashMap<String, WidgetRegistration>();
    }

    public synchronized WidgetRegistration registerWidget(Widget widget, Map<String, Object> properties) {
        String name = getName(properties);
        if (indexId.containsKey(name)) {
            throw new IllegalArgumentException("A widget with the name [" + name + "] was already registered");
        }
        WidgetRegistration reg = new WidgetRegistration(widget, getRanking(properties), name);
        registrations.add(reg);
        indexWidget.put(widget, reg);
        indexId.put(name, reg);
        logger.debug("Registered widget " + widget.toString() + " as " + reg.getId());
        return reg;
    }

    private int getRanking(Map<String, Object> properties) {
        int ranking = 0;
        if (properties.containsKey(KEY_RANKING)) {
            Object oRanking = properties.get(KEY_RANKING);
            if (oRanking instanceof Integer) {
                ranking = (Integer) oRanking;
            } else {
                try {
                    ranking = Integer.parseInt(oRanking.toString());
                } catch (NumberFormatException ex) {
                    // Ignore
                }
            }
        }
        return ranking;
    }

    private String getName(Map<String, Object> properties) {
        String name = null;
        if (properties.containsKey(KEY_NAME)) {
            name = properties.get(KEY_NAME).toString();
        }
        if (name == null) {
            name = "widget-" + widgetCounter.incrementAndGet();
        }
        return name;
    }

    public synchronized void unregisterWidget(Widget widget) {
        WidgetRegistration reg = indexWidget.remove(widget);
        if (reg != null) {
            indexId.remove(reg.getName());
            registrations.remove(reg);
            logger.debug("Unregistered widget " + widget.toString() + " as " + reg.getId());
        } else {
            logger.debug("Failed to unregister widget " + widget.toString());
        }
    }

    @Override
    public Iterator<WidgetRegistration> iterator() {
        return Collections.unmodifiableCollection(registrations).iterator();
    }

    public WidgetRegistration get(String name) {
        return indexId.get(name);
    }
}
