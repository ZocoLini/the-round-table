package org.lebastudios.theroundtable.config.data;

import org.lebastudios.theroundtable.config.Settings;

import java.io.File;
import java.util.Locale;

public class PreferencesConfigData implements FileRepresentator
{
    public String theme = "cupertino-light";
    public String langauge = Locale.getDefault().getLanguage();

    @Override
    public File getFile()
    {
        return new File(Settings.getUserDir() + "/preferences.json");
    }
}
