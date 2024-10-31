package org.lebastudios.theroundtable.plugins;

import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.config.SettingsItem;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PluginsConfigData;
import org.lebastudios.theroundtable.locale.AppLocale;
import org.lebastudios.theroundtable.locale.LangLoader;
import org.lebastudios.theroundtable.plugins.pluginData.PluginData;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class PluginLoader
{
    private static final Map<String, IPlugin> pluginsLoaded = new HashMap<>();
    private static final Map<String, IPlugin> pluginsInstalled = new HashMap<>();

    public static void loadPlugins()
    {
        File[] jars = new File(new JSONFile<>(PluginsConfigData.class).get().pluginsFolder).listFiles(
                (dir, name) -> name.endsWith(".jar"));
        if (jars == null) return;

        for (File jar : jars)
        {
            try
            {
                URL jarUrl = jar.toURI().toURL();
                URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, PluginLoader.class.getClassLoader());

                ServiceLoader<IPlugin> serviceLoader = ServiceLoader.load(IPlugin.class, classLoader);

                for (var plugin : serviceLoader)
                {
                    var pluginData = plugin.getPluginData();
                    pluginsInstalled.put(pluginData.pluginId, plugin);
                }
            }
            catch (Exception e)
            {
                System.err.println("Error loading plugin: " + jar.getName() + " " + e.getMessage());
            }
        }

        // Load all plugins that can be loaded
        boolean keepTryingToLoad = true;

        while (keepTryingToLoad)
        {
            keepTryingToLoad = false;
            for (IPlugin plugin : pluginsInstalled.values())
            {
                var pluginData = plugin.getPluginData();
                
                if (pluginsLoaded.containsKey(pluginData.pluginId)) continue;
                if (!canBePluginLoaded(plugin)) continue;

                pluginsLoaded.put(plugin.getPluginData().pluginId, plugin);
                keepTryingToLoad = true;
                loadPlugin(plugin);
            }
        }
    }

    public static boolean canBePluginLoaded(IPlugin plugin)
    {
        // TODO: Make a better version comparator
        if (plugin.getPluginData().pluginRequiredCoreVersion
                .compareTo(TheRoundTableApplication.getAppVersion()) > 0) {return false;}

        if (plugin.getPluginData().pluginDependencies == null) return true;

        for (var dependency : plugin.getPluginData().pluginDependencies)
        {
            if (!pluginsLoaded.containsKey(dependency.pluginId)) return false;
            if (pluginsLoaded.get(dependency.pluginId).getPluginData().pluginVersion
                    .compareTo(dependency.pluginVersion) < 0) {return false;}
        }

        return true;
    }

    public static List<Button> getLeftButtons()
    {
        List<Button> buttons = new ArrayList<>();
        for (IPlugin plugin : pluginsLoaded.values())
        {
            buttons.addAll(plugin.getLeftButtons());
        }
        return buttons;
    }

    public static List<Button> getRightButtons()
    {
        List<Button> buttons = new ArrayList<>();
        for (IPlugin plugin : pluginsLoaded.values())
        {
            buttons.addAll(plugin.getRightButtons());
        }
        return buttons;
    }

    public static List<TreeItem<SettingsItem>> getSettingsTreeViews()
    {
        List<TreeItem<SettingsItem>> items = new ArrayList<>();

        for (IPlugin plugin : pluginsLoaded.values())
        {
            var rootTreeItem = plugin.getSettingsRootTreeItem();
            if (rootTreeItem == null) continue;

            items.add(rootTreeItem);
        }

        return items;
    }

    public static List<Object> getRessourcesObjects()
    {
        List<Object> classes = new ArrayList<>();

        classes.add(new TheRoundTableApplication());
        classes.addAll(getLoadedPlugins());

        return classes;
    }

    public static void loadPlugin(IPlugin plugin)
    {
        // Load plugin translations
        LangLoader.loadLang(plugin.getClass(), AppLocale.getActualLocale());
        
        // Initialize the plugin
        plugin.initialize();
    }
    
    public static void unloadPlugin(IPlugin plugin)
    {
        
    }
    
    public static Collection<IPlugin> getLoadedPlugins()
    {
        return pluginsLoaded.values();
    }

    public static Collection<IPlugin> getInstalledPlugins()
    {
        return pluginsInstalled.values();
    }

    public static void removePlugin(PluginData pluginData)
    {
        pluginsInstalled.remove(pluginData.pluginName);
    }

    public static boolean isPluginInstalled(PluginData pluginData)
    {
        return pluginsInstalled.containsKey(pluginData.pluginName);
    }

    public static boolean isPluginLoaded(PluginData pluginData)
    {
        return pluginsLoaded.containsKey(pluginData.pluginName);
    }
}
