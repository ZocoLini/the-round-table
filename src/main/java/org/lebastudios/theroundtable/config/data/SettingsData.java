package org.lebastudios.theroundtable.config.data;

import org.lebastudios.theroundtable.TheRoundTableApplication;

import java.io.File;

public class SettingsData implements FileRepresentator
{
    public boolean setupComplete = false;
    
    @Override
    public File getFile()
    {
        return new File(TheRoundTableApplication.getUserDirectory() + "/settings.json");
    }
}
