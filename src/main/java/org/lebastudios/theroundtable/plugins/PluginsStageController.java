package org.lebastudios.theroundtable.plugins;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.communications.ApiRequests;
import org.lebastudios.theroundtable.controllers.StageController;
import org.lebastudios.theroundtable.ui.StageBuilder;

import java.net.URL;

public class PluginsStageController extends StageController<PluginsStageController>
{
    @FXML private TabPane tabPane;
    @FXML private Tab availablePluginsTab;
    @FXML private VBox availablePlugins;
    @FXML private VBox installedPlugins;
    @FXML private BorderPane pluginViewer; // TODO: Visulize the plugin info here
    
    @Override
    public Class<?> getBundleClass()
    {
        return Launcher.class;
    }

    @Override
    public URL getFXML()
    {
        return PluginsStageController.class.getResource("pluginsStage.fxml");
    }

    @Override
    public String getTitle()
    {
        return "Plugins";
    }

    @Override
    protected void customizeStageBuilder(StageBuilder stageBuilder)
    {
        stageBuilder.setModality(Modality.APPLICATION_MODAL);
    }

    @FXML @Override protected void initialize()
    {
        this.instantiateInstalledPlugins();
        new Thread(this::instantiateAvailablePlugins).start();

        tabPane.getSelectionModel().selectedItemProperty().addListener((_, _, _) ->
                tabPane.getScene().getWindow().sizeToScene());
    }

    private void instantiateInstalledPlugins()
    {
        for (var plugin : PluginLoader.getInstalledPlugins())
        {
            installedPlugins.getChildren().add(new PluginLabelController(plugin.getPluginData()).getRoot());
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
                availablePlugins.getChildren().add(new PluginLabelController(pluginData).getRoot());
            }
        });
    }
}
