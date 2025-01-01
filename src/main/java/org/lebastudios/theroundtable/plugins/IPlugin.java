package org.lebastudios.theroundtable.plugins;

import com.google.gson.Gson;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.config.SettingsItem;
import org.lebastudios.theroundtable.database.IDatabaseUpdater;
import org.lebastudios.theroundtable.logs.Logs;
import org.lebastudios.theroundtable.plugins.pluginData.PluginData;
import org.lebastudios.theroundtable.ui.LabeledIconButton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public interface IPlugin extends IDatabaseUpdater
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

    default List<LabeledIconButton> getHomeButtons()
    {
        return new ArrayList<>();
    }
    
    default TreeItem<SettingsItem> getSettingsRootTreeItem()
    {
        return null;
    }

    default List<Class<?>> getPluginEntities() { return new ArrayList<>(); }
    
    default File getPluginFolder()
    {
        return new File(TheRoundTableApplication.getUserDirectory(), getPluginData().pluginId);
    }
    
    default PluginData getPluginData()
    {
        InputStream is = this.getClass().getResourceAsStream("pluginData.json");
        
        if (is == null) 
        {
            throw new IllegalStateException("The pluginData.json file is missing");
        }
        
        try (InputStreamReader reader = new InputStreamReader(is))
        {
            return new Gson().fromJson(
                    reader,
                    PluginData.class
            );
        }
        catch (IOException e)
        {
            Logs.getInstance().log("Failed to load plugin data (" + this.getClass().getName() + ")", e);
            return null;
        }
    }

    @Override
    default String getDatabaseIdentifier()
    {
        return getPluginData().pluginId;
    }
}
