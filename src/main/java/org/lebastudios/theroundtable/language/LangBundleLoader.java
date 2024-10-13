package org.lebastudios.theroundtable.language;

import javafx.fxml.FXMLLoader;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PreferencesConfigData;
import org.lebastudios.theroundtable.plugins.PluginLoader;

import java.util.*;

public class LangBundleLoader
{
    private static LangBundleLoader instance;
    private final Map<String, ResourceBundle> resourceBundles = new HashMap<>();

    private LangBundleLoader(String baseName, Locale locale)
    {
        addLangBundle(baseName, locale);
    }

    private void addLangBundle(String baseName, Locale locale)
    {
        resourceBundles.put(baseName, ResourceBundle.getBundle(baseName, locale));
    }

    public static void addLangBundle(FXMLLoader loader, Class clazz)
    {
        loader.setResources(getInstance().resourceBundles.get(clazz.getPackageName() + ".lang"));
    }

    public static LangBundleLoader getInstance()
    {
        if (instance == null)
        {
            var locale = Locale.of(new JSONFile<>(PreferencesConfigData.class).get().langauge,
                    Locale.getDefault().getCountry());

            instance = new LangBundleLoader(
                    "org.lebastudios.theroundtable.lang",
                    locale);

            for (var plugin : PluginLoader.getLoadedPlugins())
            {
                instance.resourceBundles.put(plugin.getClass().getPackageName() + ".lang",
                        ResourceBundle.getBundle(
                                plugin.getClass().getPackageName() + ".lang", locale,
                                plugin.getClass().getClassLoader())
                );
            }
        }

        return instance;
    }

    public String getString(String key)
    {
        for (var resourceBundle : resourceBundles.values())
        {
            try
            {
                return resourceBundle.getString(key);
            }
            catch (MissingResourceException _) {}
        }

        System.err.println("Key not found: " + key);

        return key;
    }
}
