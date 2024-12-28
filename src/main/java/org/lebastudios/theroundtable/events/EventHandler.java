package org.lebastudios.theroundtable.events;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is used for the event system. Defines the methods that all AppEvents must have.
 *
 * @param <T> The type of the event method. This is the interface that the event method must implement and
 *         should be created in the AppEvent class.
 */
public abstract class EventHandler<T>
{
    private final List<T> listeners = new CopyOnWriteArrayList<>();

    public synchronized void addListener(T listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        if (!hasListener(listener)) {
            listeners.add(listener);
        }
    }

    public synchronized void removeListener(T listener) {
        if (listener == null) return;

        listeners.remove(listener);
    }

    public synchronized void clearListeners() {
        listeners.clear();
    }

    public boolean hasListener(T listener) {
        if (listener == null) return false;

        return listeners.contains(listener);
    }

    protected List<T> getActiveListeners() {
        return List.copyOf(listeners);
    }
}
