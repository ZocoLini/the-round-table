package org.lebastudios.theroundtable;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.SneakyThrows;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.TaskProgressView;
import org.controlsfx.control.action.Action;
import org.lebastudios.theroundtable.accounts.AccountManager;
import org.lebastudios.theroundtable.config.ConfigStageController;
import org.lebastudios.theroundtable.language.LangBundleLoader;
import org.lebastudios.theroundtable.language.LangFileLoader;
import org.lebastudios.theroundtable.plugins.PluginLoader;
import org.lebastudios.theroundtable.plugins.PluginsStageController;
import org.lebastudios.theroundtable.ui.IconButton;

public class MainStageController
{
    @Getter private static MainStageController instance;
    @FXML private IconButton pluginsButton;
    @FXML private BorderPane root;
    @FXML private TaskProgressView<Task<?>> taskProgressView;
    @FXML private VBox rootTools;
    @FXML private VBox leftButtons;
    @FXML private VBox rightButtons;
    @FXML private SplitPane centralContainer;
    @Getter private Node centralPaneMainNode;

    public MainStageController()
    {
        instance = this;
    }
    
    @SneakyThrows
    public static Parent getParentNode()
    {
        FXMLLoader loader = new FXMLLoader(MainStageController.class.getResource("mainStage.fxml"));
        LangBundleLoader.addLangBundle(loader, MainStageController.class);
        return loader.load();
    }
    
    @SneakyThrows
    public void initialize()
    {
        PluginLoader.loadPlugins();
        
        pluginsButton.setDisable(!AccountManager.getInstance().isAccountAdmin());
        
        leftButtons.getChildren().addAll(PluginLoader.getLeftButtons());
        rightButtons.getChildren().addAll(PluginLoader.getRightButtons());
    }

    @SneakyThrows
    @FXML
    private void openSettingsStage()
    {
        ConfigStageController.showConfigStage();
    }

    @FXML
    private void openPluginsStage()
    {
        PluginsStageController.showPluginsStage();
    }

    public void swapCentralPaneMainNode(Node newNode)
    {
        centralContainer.getItems().set(centralContainer.getItems().size() - 1, newNode);
        centralPaneMainNode = newNode;
    }

    public void requestRestart()
    {
        showNotification(LangFileLoader.getTranslation("textblock.infrestartneeded"),
                new Action(LangFileLoader.getTranslation("word.restart"), _ ->
                        Launcher.restartAplication()));
    }

    public void showNotification(String message, Action action)
    {
        Notifications.create()
                .text(message)
                .owner(root)
                .action(action)
                .show();
    }
}