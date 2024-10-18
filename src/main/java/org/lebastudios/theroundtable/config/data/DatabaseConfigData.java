package org.lebastudios.theroundtable.config.data;

import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.config.Settings;

import java.io.File;

public class DatabaseConfigData implements FileRepresentator
{
    public String establishmentDatabaseName = "establishment";
    public String databaseFolder =
            TheRoundTableApplication.getUserDirectory() + File.separator + "databases" + File.separator;
    public boolean enableBackups = false;
    public String backupFolder =
            TheRoundTableApplication.getUserDirectory() + File.separator + "trt-db-backups" + File.separator;

    @Override
    public File getFile()
    {
        return new File(Settings.getGlobalDir() + "/databaseData.json");
    }
}
