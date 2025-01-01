package org.lebastudios.theroundtable.plugins;

import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.communications.Version;
import org.lebastudios.theroundtable.config.SettingsItem;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PluginsConfigData;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.locale.AppLocale;
import org.lebastudios.theroundtable.locale.LangLoader;
import org.lebastudios.theroundtable.plugins.pluginData.PluginData;
import org.lebastudios.theroundtable.ui.LabeledIconButton;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class PluginLoader
{
    private static final Map<String, IPlugin> pluginsLoaded = new HashMap<>();
    private static final Map<String, IPlugin> pluginsInstalled = new HashMap<>();
    @Getter private static URLClassLoader pluginsClassLoader = new URLClassLoader(new URL[0]);

    public static void loadPlugins()
    {
        File[] jars = new File(new JSONFile<>(PluginsConfigData.class).get().pluginsFolder)
                .listFiles((_, name) -> name.endsWith(".jar"));
        if (jars == null) return;

        try
        {
            pluginsClassLoader = new URLClassLoader(
                    Arrays.stream(jars).map(File::toURI).map(uri -> {
                        try {return uri.toURL();}
                        catch (Exception e) {return null;}
                    }).filter(Objects::nonNull).toArray(URL[]::new),
            PluginLoader.class.getClassLoader());
            
            ServiceLoader<IPlugin> serviceLoader = ServiceLoader.load(IPlugin.class, pluginsClassLoader);

            for (var plugin : serviceLoader)
            {
                var pluginData = plugin.getPluginData();
                pluginsInstalled.put(pluginData.pluginId, plugin);
            }
        }
        catch (Exception e)
        {
            System.err.println("Error loading plugins: " + e.getMessage());
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
                
                keepTryingToLoad = true;
                loadPlugin(plugin);
            }
        }

        Database.getInstance().reloadDatabase();
    }

    public static void loadPlugin(IPlugin plugin)
    {
        // Add plugin to loaded plugins collection
        pluginsLoaded.put(plugin.getPluginData().pluginId, plugin);
        
        // Load plugin translations
        LangLoader.loadLang(plugin.getClass(), AppLocale.getActualLocale());

        // Initialize the plugin
        plugin.initialize();
    }

    public static void unloadPlugin(IPlugin plugin)
    {

    }
    
    public static boolean canBePluginLoaded(IPlugin plugin)
    {
        Version requiredCoreVersion = new Version(plugin.getPluginData().pluginRequiredCoreVersion);
        Version actualCoreVersion = new Version(TheRoundTableApplication.getAppVersion());
        
        if (actualCoreVersion.isLessThan(requiredCoreVersion)) {return false;}

        if (plugin.getPluginData().pluginDependencies == null) return true;

        for (var dependency : plugin.getPluginData().pluginDependencies)
        {
            if (!pluginsLoaded.containsKey(dependency.pluginId)) return false;
            
            Version requiredPluginVersion = new Version(dependency.pluginVersion);
            Version actualPluginVersion = new Version(pluginsLoaded.get(dependency.pluginId).getPluginData().pluginVersion);
            
            if (actualPluginVersion.isLessThan(requiredPluginVersion)) {return false;}
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

    public static List<LabeledIconButton> getHomeButtons()
    {
        List<LabeledIconButton> buttons = new ArrayList<>();
        for (IPlugin plugin : pluginsLoaded.values())
        {
            buttons.addAll(plugin.getHomeButtons());
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

    public static List<Class<?>> getPluginEntities()
    {
        List<Class<?>> pluginEntities = new ArrayList<>();
        for (IPlugin plugin : pluginsLoaded.values())
        {
            pluginEntities.addAll(plugin.getPluginEntities());
        }
        return pluginEntities;
    }
    
    public static Collection<IPlugin> getLoadedPlugins()
    {
        return pluginsLoaded.values();
    }

    public static Collection<IPlugin> getInstalledPlugins()
    {
        return pluginsInstalled.values();
    }

    public static void uninstallPlugin(PluginData pluginData)
    {
        pluginsInstalled.remove(pluginData.pluginId);
    }

    public static boolean isPluginInstalled(PluginData pluginData)
    {
        return pluginsInstalled.containsKey(pluginData.pluginId);
    }

    public static boolean isPluginLoaded(PluginData pluginData)
    {
        return pluginsLoaded.containsKey(pluginData.pluginId);
    }
}
