package org.lebastudios.theroundtable.plugins;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.MainStageController;
import org.lebastudios.theroundtable.communications.ApiRequests;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PluginsConfigData;
import org.lebastudios.theroundtable.controllers.PaneController;
import org.lebastudios.theroundtable.plugins.pluginData.PluginData;
import org.lebastudios.theroundtable.ui.IconButton;
import org.lebastudios.theroundtable.ui.IconView;

import java.io.File;
import java.net.URL;

public class PluginLabelController extends PaneController<PluginLabelController>
{
    @FXML private IconView pluginIcon;
    @FXML private Label pluginName;
    @FXML private Label pluginDescription;
    @FXML private Button actionButton;
    @FXML private IconButton unistallButton;
    @FXML private HBox root;
    private PluginData pluginData;

    public PluginLabelController(PluginData pluginData)
    {
        this.pluginData = pluginData;
    }

    @Override
    public Class<?> getBundleClass()
    {
        return Launcher.class;
    }

    @Override
    public URL getFXML()
    {
        return PluginLabelController.class.getResource("pluginLabel.fxml");
    }

    @FXML @Override protected void initialize()
    {
        if (pluginData == null)
        {
            pluginData = new PluginData();
            pluginData.pluginName = "Error";
            pluginData.pluginDescription = "Error";
            pluginData.pluginIcon = "error";

        }

        pluginIcon.setIconName(pluginData.pluginIcon + ".png");
        pluginName.setText(pluginData.pluginName);
        pluginDescription.setText(pluginData.pluginDescription);

        if (PluginLoader.isPluginInstalled(pluginData))
        {
            if (ApiRequests.pluginNeedUpdate(pluginData)) {actionButton.setText("Update");}
            else {root.getChildren().remove(actionButton);}
        }
        else
        {
            root.getChildren().remove(unistallButton);
            actionButton.setOnAction(_ -> onAction());
            actionButton.setText("Install");
        }
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
        Platform.runLater(() -> ApiRequests.updatePlugin(pluginData));
    }

    @FXML
    private void uninstallPlugin()
    {
        var pluginFile =
                new File(new JSONFile<>(PluginsConfigData.class).get().pluginsFolder + pluginData.pluginId + ".jar");

        if (pluginFile.exists()
                && pluginFile.isFile()
                && pluginFile.delete())
        {
            root.getChildren().remove(unistallButton);
            PluginLoader.removePlugin(pluginData);

            Platform.runLater(() -> MainStageController.getInstance().requestRestart());
        }
    }
}
