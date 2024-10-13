package org.lebastudios.theroundtable.plugins;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.plugins.pluginData.PluginData;

public class PluginViewerController
{
    private final PluginData pluginData;

    private PluginViewerController(PluginData pluginData)
    {
        this.pluginData = pluginData;
    }

    @SneakyThrows
    public static Node getAttachedNode(PluginData pluginData)
    {
        var loader = new FXMLLoader(PluginViewerController.class.getResource("pluginViewer.fxml"));
        loader.setController(new PluginViewerController(pluginData));

        return loader.load();
    }
}
