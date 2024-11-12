package org.lebastudios.theroundtable.plugins;

import com.google.gson.Gson;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import org.hibernate.Session;
import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.config.SettingsItem;
import org.lebastudios.theroundtable.plugins.pluginData.PluginData;

import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public interface IPlugin
{
    void initialize();

    default List<Button> getRightButtons()
    {
        return new ArrayList<>();
    }

    default List<Button> getLeftButtons()
    {
        return new ArrayList<>();
    }

    default TreeItem<SettingsItem> getSettingsRootTreeItem()
    {
        return null;
    }

    default List<Class<?>> getPluginEntities() { return new ArrayList<>(); }
    
    default void onDatabaseUpdate(Session session, int oldVersion, int newVersion) 
    {
        
    }
    
    default int getDatabaseVersion()
    {
        return 1;
    }
    
    default File getPluginFolder()
    {
        return new File(TheRoundTableApplication.getUserDirectory(), getPluginData().pluginId);
    }
    
    default PluginData getPluginData()
    {
        return new Gson().fromJson(
                new InputStreamReader(this.getClass().getResourceAsStream("pluginData.json")),
                PluginData.class
        );
    }
}
