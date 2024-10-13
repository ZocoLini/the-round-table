package org.lebastudios.theroundtable.config.data;

import org.lebastudios.theroundtable.config.Settings;

import java.io.File;

public class AccountConfigData implements FileRepresentator
{
    public String email = "";
    public String password = "";
    public String license = "";

    @Override
    public File getFile()
    {
        return new File(Settings.getGlobalDir() + "/account.json");
    }
}
