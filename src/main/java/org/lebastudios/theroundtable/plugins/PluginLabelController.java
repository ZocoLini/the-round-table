package org.lebastudios.theroundtable.plugins;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.MainStageController;
import org.lebastudios.theroundtable.communications.ApiRequests;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PluginsConfigData;
import org.lebastudios.theroundtable.controllers.PaneController;
import org.lebastudios.theroundtable.plugins.pluginData.PluginData;
import org.lebastudios.theroundtable.ui.IconButton;
import org.lebastudios.theroundtable.ui.IconView;
import org.lebastudios.theroundtable.ui.LoadingPaneController;

import java.io.File;
import java.util.function.Consumer;

public class PluginLabelController extends PaneController<PluginLabelController>
{
    @FXML private IconView pluginIcon;
    @FXML private Label pluginName;
    @FXML private Label pluginDescription;
    @FXML private Button actionButton;
    @FXML private IconButton unistallButton;
    @FXML private HBox root;
    private PluginData pluginData;
    
    private final Consumer<PluginData> onLabelClick;

    public PluginLabelController(PluginData pluginData, Consumer<PluginData> onLabelClick)
    {
        this.pluginData = pluginData;
        this.onLabelClick = onLabelClick;
    }

    @Override
    public Class<?> getBundleClass()
    {
        return Launcher.class;
    }
    
    @FXML @Override protected void initialize()
    {
        if (pluginData == null)
        {
            pluginData = new PluginData();
            pluginData.pluginName = "Error";
            pluginData.pluginDescription = "Error";
            pluginData.pluginIcon = "error";
            return;
        }
        
        root.setOnMouseClicked(_ -> onLabelClick.accept(pluginData));

        pluginIcon.setIconName(pluginData.pluginIcon + ".png");
        pluginName.setText(pluginData.pluginName);
        pluginDescription.setText(pluginData.pluginDescription);

        root.getChildren().remove(actionButton);
        
        if (PluginLoader.isPluginInstalled(pluginData))
        {
            final var loadingNode = new LoadingPaneController().getRoot();
            
            root.getChildren().add(loadingNode);
            
            new Thread(() ->
            {
                if (ApiRequests.pluginNeedUpdate(pluginData)) {
                    
                    PluginData newVersionData = ApiRequests.getServerPluginData(pluginData.pluginId);
                    
                    boolean dependenciesInstalled = PluginUpdater.dependenciesInstalled(newVersionData);
                    
                    Platform.runLater(() ->
                    {
                        root.getChildren().add(actionButton);
                        actionButton.setText("Update");
                        
                        if (!dependenciesInstalled) setNotSatisfiedDependencies();
                    });
                }
                
                Platform.runLater(() -> root.getChildren().remove(loadingNode));
            }).start();
        }
        else
        {

            root.getChildren().remove(unistallButton);

            root.getChildren().add(actionButton);
            actionButton.setOnAction(_ -> onAction());
            actionButton.setText("Install");

            if (!PluginUpdater.dependenciesInstalled(this.pluginData)) setNotSatisfiedDependencies();
        }
    }
    
    private void setNotSatisfiedDependencies()
    {
        final var iconView = new IconView("unavailable.png");
        iconView.setIconSize(24);
        actionButton.setGraphic(iconView);
        actionButton.setDisable(true);

        Tooltip tooltip = new Tooltip("Not all dependencies are satisfied");
        Tooltip.install(root, tooltip);
    }
    
    @FXML
    private void onAction()
    {
        if (pluginData == null) return;

        installPlugin();
    }

    private void installPlugin()
    {
        root.getChildren().remove(actionButton);
        new Thread(() ->
        {
            ApiRequests.updatePlugin(pluginData);
            
            // TODO: Mark the plugin as installed so the install button doesn't appear before the app restart
        }).start();
    }

    @FXML
    private void uninstallPlugin()
    {
        var pluginFile =
                new File(new JSONFile<>(PluginsConfigData.class).get().pluginsFolder + pluginData.pluginId + ".jar");

        if (pluginFile.exists() && pluginFile.isFile() && pluginFile.delete())
        {
            root.getChildren().remove(unistallButton);
            PluginLoader.uninstallPlugin(pluginData);

            Platform.runLater(() -> MainStageController.getInstance().requestRestart());
        }
    }
}
