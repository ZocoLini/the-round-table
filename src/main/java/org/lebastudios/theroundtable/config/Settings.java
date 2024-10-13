package org.lebastudios.theroundtable.config;

import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.accounts.AccountManager;

import java.io.File;

public class Settings
{
    public static File getGlobalDir()
    {
        return new File(get(), "global");
    }

    public static File get()
    {
        return new File(TheRoundTableApplication.getUserDirectory() + "/config");
    }

    public static File getUserDir()
    {
        var accountName = AccountManager.getInstance().getCurrentLoggedAccountName();

        return new File(get(), accountName);
    }
}
