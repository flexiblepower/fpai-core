package org.flexiblepower.runtime.ui.server.widgets;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.flexiblepower.ui.Widget;
import org.osgi.service.useradmin.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public class WidgetRegistration implements Comparable<WidgetRegistration> {
    private static final Logger logger = LoggerFactory.getLogger(WidgetRegistration.class);
    private static final AtomicInteger widgetCounter = new AtomicInteger();

    private final int id;
    private final int ranking;
    private final String name;
    private final Widget widget;
    private final GsonBuilder gsonBuilder;

    public WidgetRegistration(Widget widget, int ranking, String name) {
        logger.trace("Entering WidgetRegistration constructor, widget = {}, ranking = {}, name = {}",
                     widget,
                     ranking,
                     name);
        id = widgetCounter.incrementAndGet();
        this.widget = widget;
        this.ranking = ranking;
        this.name = name;
        gsonBuilder = new GsonBuilder();
        logger.trace("Leaving WidgetRegistration constructor");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRanking() {
        return ranking;
    }

    public Widget getWidget() {
        return widget;
    }

    public URL getResource(String name, Locale locale) {
        logger.trace("Entering getResource, name = {}, locale = {}", name, locale);
        Class<? extends Widget> clazz = widget.getClass();
        ClassLoader classLoader = clazz.getClassLoader();
        String widgetName = clazz.getSimpleName();
        URL url = classLoader.getResource("widgets/" + widgetName + "/" + locale.getLanguage() + "/" + name);
        if (url == null) {
            url = classLoader.getResource("widgets/" + widgetName + "/" + name);
        }
        logger.trace("Leaving getResource, url = {}", url);
        return url;
    }

    @Override
    public int compareTo(WidgetRegistration o) {
        if (o.ranking != ranking) {
            return o.ranking - ranking;
        } else {
            return id - o.id;
        }
    }

    public String
            executeMethod(String methodName, Locale locale, User user, String jsonInput) throws NoSuchMethodException,
                                                                                        InvocationTargetException {
        logger.trace("Entering executeMethod, methodName = {}, locale = {}, user = {}, jsonInput = {}",
                     methodName,
                     locale,
                     user,
                     jsonInput);
        Method[] methods = widget.getClass().getMethods();
        Method selectedMethod = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                selectedMethod = method;
                break;
            }
        }

        if (selectedMethod == null) {
            throw new NoSuchMethodException(methodName);
        }

        Gson gson = gsonBuilder.create();

        Class<?>[] parameterTypes = selectedMethod.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];
        for (int ix = 0; ix < parameters.length; ix++) {
            Class<?> parameterType = parameterTypes[ix];
            if (parameterType.equals(Locale.class)) {
                parameters[ix] = locale;
            } else if (parameterType.equals(User.class)) {
                parameters[ix] = user;
            } else {
                if (jsonInput.isEmpty() || jsonInput.equals("null")) {
                    parameters[ix] = null;
                } else {
                    try {
                        parameters[ix] = gson.fromJson(jsonInput, parameterType);
                    } catch (JsonParseException ex) {
                        throw new NoSuchMethodException(methodName + ": " + ex.getMessage());
                    }
                }
            }
        }

        try {
            Object result = selectedMethod.invoke(widget, parameters);
            String json = gson.toJson(result);
            if (result instanceof String) {
                json = "{\"text\": " + json + "}";
            }
            logger.trace("Leaving executeMethod, result = {}", json);
            return json;
        } catch (IllegalAccessException e) {
            String msg = "No access to selected method " + selectedMethod;
            logger.warn(msg);
            throw new InvocationTargetException(e, msg);
        } catch (IllegalArgumentException e) {
            String msg = "Illegal argument given for selected method " + selectedMethod;
            logger.warn(msg);
            throw new InvocationTargetException(e, msg);
        } catch (InvocationTargetException e) {
            logger.info("Exception thrown during method execution", e.getTargetException());
            throw e;
        }
    }

    @Override
    public String toString() {
        return "WidgetRegistration [id=" + id + ", ranking=" + ranking + ", name=" + name + ", widget=" + widget + "]";
    }

}
