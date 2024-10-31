package org.lebastudios.theroundtable.plugins;

import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import org.lebastudios.theroundtable.config.SettingsItem;
import org.lebastudios.theroundtable.plugins.pluginData.PluginData;

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

    PluginData getPluginData();
}
