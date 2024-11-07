package org.lebastudios.theroundtable.config.data;

import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.config.Settings;

import java.io.File;

public class PluginsConfigData implements FileRepresentator
{
    public String pluginsFolder = TheRoundTableApplication.getUserDirectory() + "/plugins/";

    @Override
    public File getFile()
    {
        return new File(Settings.getGlobalDir() + "/plugins.json");
    }
}
