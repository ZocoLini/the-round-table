package org.lebastudios.theroundtable.locale;

import javafx.fxml.FXMLLoader;

import java.util.*;

public class LangBundleLoader
{
    private static LangBundleLoader instance;
    private final Map<String, ResourceBundle> resourceBundles = new HashMap<>();

    private LangBundleLoader() {}

    public static LangBundleLoader getInstance()
    {
        if (instance == null) instance = new LangBundleLoader();

        return instance;
    }
    
    public static void loadLang(Class<?> pluginClass, Locale locale)
    {
        getInstance().resourceBundles.put(pluginClass.getPackageName() + ".lang",
                ResourceBundle.getBundle(
                        pluginClass.getPackageName() + ".lang", 
                        locale,
                        pluginClass.getClassLoader()
                )
        );
    }

    public static void loadLang(FXMLLoader loader, Class<?> clazz)
    {
        loader.setResources(getInstance().resourceBundles.get(clazz.getPackageName() + ".lang"));
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
