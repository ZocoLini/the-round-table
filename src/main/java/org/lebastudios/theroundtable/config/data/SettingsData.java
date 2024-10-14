package org.lebastudios.theroundtable.config.data;

import org.lebastudios.theroundtable.TheRoundTableApplication;

import java.io.File;

public class SettingsData implements FileRepresentator
{
    public boolean setupComplete = false;
    public ProxyData proxyData = new ProxyData();
    
    @Override
    public File getFile()
    {
        return new File(TheRoundTableApplication.getUserDirectory() + "/settings.json");
    }
    
    public static class ProxyData
    {
        public boolean usingProxy = false;
        public String proxyAddress = "";
        public int proxyPort = 0;
        public String proxyUsername = "";
        public String proxyPassword = "";
    }
}
