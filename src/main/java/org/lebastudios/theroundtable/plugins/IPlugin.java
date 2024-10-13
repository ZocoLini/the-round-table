package org.lebastudios.theroundtable.plugins;

import javafx.scene.control.Button;
import org.lebastudios.theroundtable.plugins.pluginData.PluginData;
import org.lebastudios.theroundtable.ui.TreeIconItem;

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

    default TreeIconItem getSettingsRootTreeItem()
    {
        return null;
    }

    PluginData getPluginData();
}
