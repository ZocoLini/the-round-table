package org.lebastudios.theroundtable.events;

import lombok.NonNull;
import org.lebastudios.theroundtable.env.Variables;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for the event system. Defines the methods that all AppEvents must have.
 *
 * @param <T> The type of the event method. This is the interface that the event method must implement and
 *         should be created in the AppEvent class.
 */
public abstract class EventHandler<T>
{
    private final List<T> listeners = new ArrayList<>();
    private final List<WeakReference<T>> weakListeners = new ArrayList<>();
    
    public synchronized void addListener(@NonNull T listener)
    {
        if (!hasListener(listener))
        {
            listeners.add(listener);
        }
    }

    public synchronized void addWeakListener(@NonNull T listener)
    {
        if (!hasListener(listener))
        {
            weakListeners.add(new WeakReference<>(listener));
        }
    }

    public synchronized void removeListener(@NonNull T listener)
    {
        if (!listeners.remove(listener))
        {
            weakListeners.removeIf(w -> w.get() == listener);
        }
    }

    public synchronized void removeWeakListener(@NonNull T listener)
    {
        weakListeners.removeIf(w -> w.get() == listener);
    }
    
    public synchronized void clearListeners()
    {
        listeners.clear();
        weakListeners.clear();
    }

    public boolean hasListener(@NonNull T listener)
    {
        return listeners.contains(listener) 
                || weakListeners.stream().anyMatch(w -> w.get() == listener);
    }
    protected List<T> getActiveListeners()
    {
        weakListeners.removeIf(w -> w.get() == null);

        var list = new ArrayList<>(listeners);
        list.addAll(weakListeners.stream().map(WeakReference::get).toList());

        if (Variables.isDev())
        {
            System.out.println("Active Listeners: " + list.size());
        }

        return list;
    }
}
