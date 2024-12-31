package org.lebastudios.theroundtable.plugins;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.communications.ApiRequests;
import org.lebastudios.theroundtable.controllers.StageController;
import org.lebastudios.theroundtable.plugins.pluginData.PluginData;
import org.lebastudios.theroundtable.ui.IconView;
import org.lebastudios.theroundtable.ui.LazyTab;
import org.lebastudios.theroundtable.ui.LoadingPaneController;
import org.lebastudios.theroundtable.ui.StageBuilder;

public class PluginsStageController extends StageController<PluginsStageController>
{
    @FXML private TabPane tabPane;

    @Override
    public Class<?> getBundleClass()
    {
        return Launcher.class;
    }

    @Override
    public boolean hasFXMLControllerDefined()
    {
        return true;
    }

    @Override
    public String getTitle()
    {
        return "Plugins";
    }

    @Override
    protected void customizeStageBuilder(StageBuilder stageBuilder)
    {
        stageBuilder.setModality(Modality.APPLICATION_MODAL)
                .setResizeable(true);
    }

    @FXML
    @Override
    protected void initialize()
    {
        this.instantiateInstalledPlugins();
        this.instantiateAvailablePlugins();
        
        tabPane.getSelectionModel().selectedItemProperty().addListener((_, _, _) ->
                tabPane.getScene().getWindow().sizeToScene());
    }

    private void instantiateInstalledPlugins()
    {
        LazyTab lazyTab = new LazyTab("Installed", () ->
        {
            ScrollPane content = new ScrollPane();
            content.setPannable(true);
            content.setFitToHeight(true);
            content.setFitToWidth(true);

            VBox list = new VBox();
            list.setSpacing(5);
            list.setPadding(new Insets(15,0, 0, 0));

            for (var plugin : PluginLoader.getInstalledPlugins())
            {
                list.getChildren().add(
                        new PluginLabelController(plugin.getPluginData(), this::showPluginViewer)
                                .getRoot()
                );
            }

            content.setContent(list);

            return content;
        });

        lazyTab.setDropNodeOnDeselect(true);
        
        tabPane.getTabs().add(lazyTab);
    }

    private void instantiateAvailablePlugins()
    {
        LazyTab lazyTab = new LazyTab("Search", () ->
        {
            ScrollPane content = new ScrollPane();
            content.setPannable(true);
            content.setFitToHeight(true);
            content.setFitToWidth(true);
            
            VBox list = new VBox();
            list.setSpacing(5);
            list.setPadding(new Insets(15,0, 0, 0));
            
            var pluginsData = ApiRequests.getPluginsDataAvailable();

            if (pluginsData == null)
            {
                return new IconView("error.png");
            }

            for (var pluginData : pluginsData)
            {
                list.getChildren().add(
                        new PluginLabelController(pluginData, this::showPluginViewer)
                                .getRoot()
                );
            }

            content.setContent(list);
            
            return content;
        });
        
        lazyTab.setDropNodeOnDeselect(true);
        
        tabPane.getTabs().add(lazyTab);
    }

    private void showPluginViewer(PluginData pluginData)
    {
        final var root = (HBox) getRoot();

        if (root.getChildren().size() > 1) root.getChildren().removeLast();

        root.getChildren().add(new PluginViewerPaneController(pluginData).getRoot());
        getStage().sizeToScene();
    }
}
