package org.lebastudios.theroundtable.config.data;

import org.lebastudios.theroundtable.TheRoundTableApplication;

import java.io.File;

public class CashRegisterStateData implements FileRepresentator
{
    public boolean open = false;
    public String openTime = null;

    @Override
    public File getFile()
    {
        return new File(TheRoundTableApplication.getUserDirectory() + "/cashregister/state.json");
    }
}
