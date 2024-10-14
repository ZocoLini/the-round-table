package org.lebastudios.theroundtable.config.data;

import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.config.Settings;

import java.io.File;

public class DatabaseConfigData implements FileRepresentator
{
    public String establishmentDatabaseName = "establishment";
    public String databaseFolder = TheRoundTableApplication.getUserDirectory() + "/databases/";
    public boolean enableBackups = false;
    public String backupFolder = TheRoundTableApplication.getUserDirectory() + "/trt-db-backups/";
    
    @Override
    public File getFile()
    {
        return new File(Settings.getGlobalDir() + "/databaseData.json");
    }
}
