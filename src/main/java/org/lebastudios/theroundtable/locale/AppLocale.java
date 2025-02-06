package org.lebastudios.theroundtable.locale;

import org.lebastudios.theroundtable.accounts.AccountManager;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PreferencesConfigData;

import java.util.Locale;

public class AppLocale
{
    public static Locale getActualLocale()
    {
        if (AccountManager.getInstance().getCurrentLogged() == null) 
        {
            return Locale.getDefault();
        }
        
        return Locale.of(
                new JSONFile<>(PreferencesConfigData.class).get().language,
                System.getProperty("user.country")
        );
    }
}
