package org.lebastudios.theroundtable.plugins;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.communications.ApiRequests;
import org.lebastudios.theroundtable.language.LangBundleLoader;

public class PluginsStageController
{
    @FXML private TabPane tabPane;
    @FXML private Tab availablePluginsTab;
    @FXML private VBox availablePlugins;
    @FXML private VBox installedPlugins;
    @FXML private BorderPane pluginViewer;

    @SneakyThrows
    public static void showPluginsStage()
    {
        var loader = new FXMLLoader(PluginsStageController.class.getResource("pluginsStage.fxml"));
        LangBundleLoader.addLangBundle(loader, Launcher.class);
        Parent root = loader.load();

        TheRoundTableApplication.showAndWaitInStage(root, "Plugins");
    }

    public void initialize()
    {
        this.instantiateInstalledPlugins();
        new Thread(this::instantiateAvailablePlugins).start();
        
        tabPane.getSelectionModel().selectedItemProperty().addListener((_, _, _) ->
        {
            tabPane.getScene().getWindow().sizeToScene();
        });
    }

    private void instantiateInstalledPlugins()
    {
        for (var plugin : PluginLoader.getInstalledPlugins())
        {
            installedPlugins.getChildren().add(PluginLabelController.getAttachedNode(plugin.getPluginData()));
        }
    }

    private void instantiateAvailablePlugins()
    {
        var pluginsData = ApiRequests.getPluginsDataAvailable();

        if (pluginsData == null)
        {
            tabPane.getTabs().remove(availablePluginsTab);
            return;
        }

        Platform.runLater(() ->
        {
            for (var pluginData : pluginsData)
            {
                availablePlugins.getChildren().add(PluginLabelController.getAttachedNode(pluginData));
            }
        });
    }
}
