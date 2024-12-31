package org.lebastudios.theroundtable.plugins;

import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.communications.Version;
import org.lebastudios.theroundtable.plugins.pluginData.PluginData;

public class PluginUpdater
{
    public static boolean dependenciesInstalled(PluginData pluginData)
    {
        if (pluginData == null) return false;
        
        if (new Version(pluginData.pluginRequiredCoreVersion)
                .compareTo(new Version(TheRoundTableApplication.getAppVersion())) > 0) 
        {
            System.err.println("INFO: The plugin " + pluginData.pluginName + " requires a newer version of The Round " +
                    "Table.");
            return false;
        }

        for (var pluginDependencyNeeded : pluginData.pluginDependencies)
        {
            var pluginDependencyInstalled =
                    PluginLoader.getInstalledPlugins().stream()
                            .filter(p -> p.getPluginData().pluginId.equals(pluginDependencyNeeded.pluginId))
                            .findFirst().orElse(null);
            
            if (pluginDependencyInstalled == null)
            {
                System.err.println("INFO: The plugin " + pluginData.pluginName + " requires the plugin " +
                        pluginDependencyNeeded.pluginId + " to be installed.");
                return false;
            }
            
            PluginData pluginDependencyInstalledData = pluginDependencyInstalled.getPluginData();
            
            if (new Version(pluginDependencyInstalledData.pluginVersion)
                    .compareTo(new Version(pluginDependencyNeeded.pluginVersion)) < 0)
            {
                System.err.println("INFO: The plugin " + pluginData.pluginName + " requires the plugin " +
                        pluginDependencyNeeded.pluginId + " to be updated.");
                return false;
            } 
        }
        
        return true;
    }
}
