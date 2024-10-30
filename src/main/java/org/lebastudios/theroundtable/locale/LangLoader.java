package org.lebastudios.theroundtable.locale;

import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.events.UserEvents;
import org.lebastudios.theroundtable.plugins.PluginLoader;

import java.util.Locale;

public class LangLoader
{
    static {
        UserEvents.OnAccountLogIn.addListener(_ -> reloadLangs());
        UserEvents.OnAccountLogOutAfter.addListener(LangLoader::reloadLangs);
    }
    
    private static void reloadLangs()
    {
        LangLoader.loadLang(Launcher.class, AppLocale.getActualLocale());

        PluginLoader.getLoadedPlugins().forEach(plugin -> LangLoader.loadLang(plugin.getClass(), AppLocale.getActualLocale()));
    }
    
    public static void loadLang(Class<?> langClass, Locale locale)
    {
        Thread langFileThread = new Thread(() -> LangFileLoader.loadLang(locale, langClass));
        Thread langBundleThread = new Thread(() -> LangBundleLoader.loadLang(langClass, locale));
        
        langFileThread.start();
        langBundleThread.start();

        try
        {
            langFileThread.join();
            langBundleThread.join();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}
