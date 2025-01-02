package org.lebastudios.theroundtable.events;

import lombok.NonNull;
import org.lebastudios.theroundtable.plugins.IPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public final class PluginEvents
{
    private PluginEvents() {}

    public static final Event1<IPlugin> onPluginLoaded = new Event1<>();
    public static final Event1<IPlugin> onPluginUnloaded = new Event1<>();

    // First String is the Package name and second is the event name
    private static final Map<String, Map<String, EventReflexionModel>> events =
            Collections.synchronizedMap(new HashMap<>());

    public static void registerPluginEvent(@NonNull String pluginId, @NonNull String eventName,
            @NonNull Method invokeMethod, @NonNull Method registerMethod, @NonNull Object eventObjectInstance)
    {
        if (pluginId.isEmpty()) throw new IllegalArgumentException("Plugin ID cannot be empty");

        if (eventName.isEmpty()) throw new IllegalArgumentException("Event name cannot be empty");

        synchronized (events)
        {
            events.computeIfAbsent(pluginId, _ -> new ConcurrentHashMap<>())
                    .put(eventName, new EventReflexionModel(invokeMethod, registerMethod, eventObjectInstance));
        }
    }

    private static EventReflexionModel getEventReflexionModel(@NonNull String eventIdentifier)
    {
        String[] eventIdSplit = eventIdentifier.split(":");

        if (eventIdSplit.length != 2)
        {
            throw new IllegalArgumentException(
                    "Invalid event identifier format. Expected 'plugin-id:event-name'"
            );
        }

        String pluginId = eventIdSplit[0];
        String eventName = eventIdSplit[1];

        Map<String, EventReflexionModel> pluginEvents = events.get(pluginId);
        if (pluginEvents == null)
        {
            throw new NoSuchElementException(
                    String.format("Plugin '%s' has no registered events", pluginId)
            );
        }

        EventReflexionModel eventReflexionModel = pluginEvents.get(eventName);
        if (eventReflexionModel == null)
        {
            throw new NoSuchElementException(
                    String.format("Plugin '%s' has no registered event '%s'", pluginId, eventName)
            );
        }

        return eventReflexionModel;
    }

    /**
     * @param eventIdentifier A String representing the event you want to add a listener to plugin-id:event-name
     * @param listener The listener object to add to the event
     */
    public static void addListenerToPluginEvent(String eventIdentifier, Object listener)
    {
        try
        {
            EventReflexionModel model = getEventReflexionModel(eventIdentifier);
            
            model.registerMethod().invoke(model.eventInstanceObject(), listener);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            throw new EventInvocationException("Failed to add listener to event " + eventIdentifier, e);
        }
    }
    
    /**
     * @param eventIdentifier A String representing the event you want to invoke plugin-id:event-name
     */
    public static void invokePluginEvent(String eventIdentifier, Object... args)
    {
        try
        {
            EventReflexionModel methods = getEventReflexionModel(eventIdentifier);
            methods.invokeMethod().invoke(methods.eventInstanceObject(), args);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            throw new EventInvocationException("Failed to invoke event " + eventIdentifier, e);
        }
    }

    public static boolean hasEvent(@NonNull String pluginId, @NonNull String eventName)
    {
        Map<String, EventReflexionModel> pluginEvents = events.get(pluginId);
        return pluginEvents != null && pluginEvents.containsKey(eventName);
    }

    public static class EventInvocationException extends RuntimeException
    {
        public EventInvocationException(String message, Throwable cause)
        {
            super(message, cause);
        }
    }

    private record EventReflexionModel(Method invokeMethod, Method registerMethod, Object eventInstanceObject) {}
}
