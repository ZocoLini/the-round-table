package org.lebastudios.theroundtable;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.SneakyThrows;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.action.Action;
import org.lebastudios.theroundtable.accounts.AccountManager;
import org.lebastudios.theroundtable.accounts.AccountStageController;
import org.lebastudios.theroundtable.config.ConfigStageController;
import org.lebastudios.theroundtable.config.data.DatabaseConfigData;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.controllers.Controller;
import org.lebastudios.theroundtable.controllers.PaneController;
import org.lebastudios.theroundtable.database.BackupDB;
import org.lebastudios.theroundtable.locale.LangFileLoader;
import org.lebastudios.theroundtable.plugins.PluginLoader;
import org.lebastudios.theroundtable.plugins.PluginsStageController;
import org.lebastudios.theroundtable.ui.IconButton;
import org.lebastudios.theroundtable.ui.LoadingPaneController;
import org.lebastudios.theroundtable.ui.SceneBuilder;

import java.net.URL;

public class MainStageController extends PaneController<MainStageController>
{
    @Getter private static MainStageController instance;
    @FXML private IconButton pluginsButton;
    @FXML private VBox leftButtons;
    @FXML private VBox rightButtons;

    public MainStageController()
    {
        instance = this;
    }

    @SneakyThrows
    @FXML
    @Override
    protected void initialize()
    {
        if (new JSONFile<>(DatabaseConfigData.class).get().enableBackups) BackupDB.getInstance().initialize();

        pluginsButton.setDisable(!AccountManager.getInstance().isAccountAdmin());
        
        leftButtons.getChildren().addAll(PluginLoader.getLeftButtons());
        rightButtons.getChildren().addAll(PluginLoader.getRightButtons());
        
        if (!PluginLoader.getHomeButtons().isEmpty())
        {
            Button homeButton = new IconButton("home.png");
            homeButton.setOnAction(_ -> swapCentralPaneMainNode(new HomePaneController().getRoot()));

            rightButtons.getChildren().add(homeButton);
        }
    }

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
    public URL getFXML()
    {
        return MainStageController.class.getResource("mainStage.fxml");
    }

    @SneakyThrows
    @FXML
    private void openSettingsStage()
    {
        new ConfigStageController().instantiate();
    }

    @FXML
    private void openPluginsStage()
    {
        new PluginsStageController().instantiate();
    }

    @Deprecated
    public void swapCentralPaneMainNode(Node newNode)
    {
        BorderPane root = (BorderPane) getRoot();
        root.setCenter(newNode);
    }

    @Deprecated
    public Node getCentralPaneMainNode()
    {
        return ((BorderPane) getRoot()).getCenter();
    }

    public void setCentralNode(Controller<?> controller)
    {
        final BorderPane root = (BorderPane) getRoot();
        root.setCenter(new LoadingPaneController().getRoot());
        
        new Thread(() ->
        {
            final Node content = controller.getRoot();
            Platform.runLater(() -> root.setCenter(content));
        }).start();
    }
    
    public void setCentralNode(Node node)
    {
        BorderPane root = (BorderPane) getRoot();
        root.setCenter(node);
    }
    
    public Node getCentralNode()
    {
        return ((BorderPane) getRoot()).getCenter();
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
                .owner(getRoot())
                .action(action)
                .show();
    }

    @SneakyThrows
    @FXML
    private void closeSession(ActionEvent actionEvent)
    {
        // TODO: Enable an option to let the admins decide if the users can close session without closing the cash 
        //  register. Maybe use a checkbox in the User Account to mark this behaviour

        Stage stage = getStage();

        stage.hide();
        AccountManager.getInstance().logOut();

        new AccountStageController().instantiate(true);

        // Loads and set the new instance and then shows it. The instance it calls is not this controller's one.
        stage.setScene(new SceneBuilder(new MainStageController().getParent()).build());
        stage.setTitle("The Round Table - " + AccountManager.getInstance().getCurrentLoggedAccountName());
        stage.show();
    }
}