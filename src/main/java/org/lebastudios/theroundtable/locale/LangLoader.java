package org.lebastudios.theroundtable.locale;

import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.events.UserEvents;
import org.lebastudios.theroundtable.plugins.PluginLoader;

import java.util.Locale;

public class LangLoader
{
    static {
        UserEvents.OnAccountLogIn.addListener(_ -> reloadAllLangs());
        UserEvents.OnAccountLogOutAfter.addListener(LangLoader::reloadAllLangs);
    }
    
    private static void reloadAllLangs()
    {
        System.out.println("Reloading all langs");
        LangLoader.loadLang(Launcher.class, AppLocale.getActualLocale());

        PluginLoader.getLoadedPlugins().forEach(plugin -> LangLoader.loadLang(plugin.getClass(), AppLocale.getActualLocale()));
    }
    
    public static void loadLang(Class<?> langClass, Locale locale)
    {
        LangFileLoader.loadLang(locale, langClass);
        LangBundleLoader.loadLang(langClass, locale);
    }
}
