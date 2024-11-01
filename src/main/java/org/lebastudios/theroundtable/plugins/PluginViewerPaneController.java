package org.lebastudios.theroundtable.plugins;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.controllers.PaneController;
import org.lebastudios.theroundtable.plugins.pluginData.PluginData;
import org.lebastudios.theroundtable.plugins.pluginData.PluginDependency;
import org.lebastudios.theroundtable.ui.IconView;

public class PluginViewerPaneController extends PaneController<PluginViewerPaneController>
{
    private final PluginData pluginData;
    @FXML private Label pluginVersionLabel;
    @FXML private Label pluginVendorLabel;
    @FXML private Label pluginVendorUrlLabel;
    @FXML private Label pluginRequiredCoreVersionLabel;
    @FXML private VBox dependenciesPane;
    @FXML private HBox iconViewContainer;
    @FXML private Label pluginNameLabel;
    @FXML private Label pluginIdLabel;
    @FXML private Label pluginDescriptionLabel;
    @FXML private TitledPane dependenciesPaneContainer;

    public PluginViewerPaneController(PluginData pluginData)
    {
        this.pluginData = pluginData;
    }

    @Override
    protected void initialize()
    {
        final var iconView = new IconView(pluginData.pluginIcon + ".png");
        iconView.setIconSize(100);
        iconViewContainer.getChildren().add(iconView);
        pluginNameLabel.setText(pluginData.pluginName);
        pluginIdLabel.setText(pluginData.pluginId);
        pluginDescriptionLabel.setText(pluginData.pluginDescription);
        pluginVendorLabel.setText(pluginData.pluginVendor);
        pluginVendorUrlLabel.setText(pluginData.pluginVendorUrl);
        pluginRequiredCoreVersionLabel.setText(pluginData.pluginRequiredCoreVersion);
        pluginVersionLabel.setText(pluginData.pluginVersion);
        
        if (pluginData.pluginDependencies == null || pluginData.pluginDependencies.length == 0)
        {
            ((VBox) getRoot()).getChildren().remove(dependenciesPaneContainer);
        }
        else
        {
            for (var dependency : pluginData.pluginDependencies)
            {
                dependenciesPane.getChildren().add(createDependencyNode(dependency));
            }
        }
    }

    private Node createDependencyNode(PluginDependency dependency)
    {
        final var dependencyLabel = new Label(dependency.pluginId + " " + dependency.pluginVersion);
        dependencyLabel.getStyleClass().add("dependency-label");
        return dependencyLabel;
    }
    
    @Override
    public Class<?> getBundleClass()
    {
        return Launcher.class;
    }
}
