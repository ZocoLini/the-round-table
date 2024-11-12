package org.lebastudios.theroundtable.database;

import org.hibernate.Session;
import org.lebastudios.theroundtable.database.entities.DatabaseVersion;
import org.lebastudios.theroundtable.plugins.PluginLoader;

class DatabaseUpdater
{
    private static final int DB_VERSION = 1;
    private static final String DB_IDENTIFIER = "desktop-app";
    
    private static DatabaseUpdater instance;
    
    public static DatabaseUpdater getInstance()
    {
        if (instance == null) instance = new DatabaseUpdater();
        
        return instance;
    }
    
    private DatabaseUpdater() {}
    
    public void callToUpdate()
    {
        var plugins = PluginLoader.getLoadedPlugins();
        
        // Update the database version for the core
        updateDatabaseFor(DB_IDENTIFIER, DB_VERSION, this::onDatabaseOpen);
        
        // Update the database version for each plugin
        for (var plugin : plugins)
        {
            updateDatabaseFor(plugin.getPluginData().pluginId, plugin.getDatabaseVersion(), plugin::onDatabaseUpdate);
        }
    }

    private void updateDatabaseFor(String identifier, int newVersion, DatabaseUpdaterCallback callback) 
    {
        Database.getInstance().connectTransaction(session ->
        {
            DatabaseVersion lastVersion = session.find(DatabaseVersion.class, identifier);

            if (lastVersion == null)
            {
                lastVersion = new DatabaseVersion(
                        identifier,
                        0
                );
            }

            if (lastVersion.getVersion() != newVersion)
            {
                callback.updateDatabase(session, lastVersion.getVersion(), newVersion);

                lastVersion.setVersion(newVersion);

                session.persist(lastVersion);
            }
        });
    }
    
    private void onDatabaseOpen(Session session, int oldVersion, int newVersion) 
    {
        // Do nothing (yet)
    }
    
    private interface DatabaseUpdaterCallback
    {
        void updateDatabase(Session session, int oldVersion, int newVersion);
    }
}
