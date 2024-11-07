package org.lebastudios.theroundtable.config.data;

import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.config.Settings;

import java.io.File;

public class DatabaseConfigData implements FileRepresentator
{
    public String establishmentDatabaseName = "establishment";
    public String databaseFolder =
            TheRoundTableApplication.getUserDirectory() + File.separator + "databases";
    public boolean enableBackups = false;
    public int numMaxBackups = 5;
    public String backupFolder =
            TheRoundTableApplication.getUserDirectory() + File.separator + "trt-db-backups";

    @Override
    public File getFile()
    {
        return new File(Settings.getGlobalDir() + "/database.json");
    }
}
