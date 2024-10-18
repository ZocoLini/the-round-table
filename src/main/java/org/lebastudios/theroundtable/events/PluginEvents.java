package org.lebastudios.theroundtable.events;

import org.lebastudios.theroundtable.plugins.IPlugin;

public class PluginEvents
{
    public static final Event1<IPlugin> onPluginLoaded = new Event1<>();
    public static final Event1<IPlugin> onPluginUnloaded = new Event1<>();
}
