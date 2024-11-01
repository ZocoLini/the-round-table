package org.lebastudios.theroundtable.plugins.pluginData;

import java.util.Objects;

public class PluginData
{
    public String pluginName;
    public String pluginId;
    public String pluginIcon;
    public String pluginDescription;
    public String pluginVersion;
    public String pluginVendor;
    public String pluginVendorUrl;
    public String pluginRequiredCoreVersion;
    public PluginDependency[] pluginDependencies;

    @Override
    public int hashCode()
    {
        return Objects.hashCode(pluginId);
    }

    @Override
    public final boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof PluginData that)) return false;

        return Objects.equals(pluginId, that.pluginId);
    }
}
