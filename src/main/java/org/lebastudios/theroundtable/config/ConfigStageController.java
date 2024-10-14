package org.lebastudios.theroundtable.config;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.ControllableUIObject;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.accounts.AccountManager;
import org.lebastudios.theroundtable.apparience.ThemeLoader;
import org.lebastudios.theroundtable.language.LangBundleLoader;
import org.lebastudios.theroundtable.language.LangFileLoader;
import org.lebastudios.theroundtable.plugins.PluginLoader;
import org.lebastudios.theroundtable.ui.SettingsTreeView;
import org.lebastudios.theroundtable.ui.TreeIconItem;

public class ConfigStageController
{
    public static ControllableUIObject<ConfigStageController> configStageRoot;
    private static Stage configStage;
    @FXML private SettingsTreeView configSectionsTreeView;
    @FXML private ScrollPane mainPane;
    private SettingsPaneController currentPaneController;

    public static void showConfigStage()
    {
        if (configStage != null)
        {
            configStage.show();
            return;
        }

        configStage = new Stage();
        configStage.setTitle(LangFileLoader.getTranslation("title.settingsstage"));
        configStage.setResizable(false);
        configStage.setMaxHeight(600);
        configStage.initModality(Modality.APPLICATION_MODAL);

        configStage.setScene(ThemeLoader.addActualTheme(new Scene((Parent) getAttachedNode().root())));
        configStage.show();
    }

    @SneakyThrows
    private static ControllableUIObject<ConfigStageController> getAttachedNode()
    {
        if (configStageRoot != null) return configStageRoot;

        var loader = new FXMLLoader(ConfigStageController.class.getResource("configStage.fxml"));
        LangBundleLoader.addLangBundle(loader, Launcher.class);
        Node root = loader.load();
        ConfigStageController controller = loader.getController();

        configStageRoot = new ControllableUIObject<>(root, controller);

        return configStageRoot;
    }

    @SneakyThrows
    public void initialize()
    {
        configSectionsTreeView.getRoot().getChildren().add(createGeneralConfigSection());

        configSectionsTreeView.getRoot().getChildren().addAll(PluginLoader.getSettingsTreeViews());

        mainPane.setContent(
                new FXMLLoader(TheRoundTableApplication.class.getResource("defaultCenterPane.fxml")).load()
        );
    }

    private TreeIconItem createGeneralConfigSection()
    {
        var generalConfigSection = new TreeIconItem(LangFileLoader.getTranslation("word.general"),
                "settings.png");
        generalConfigSection.setExpanded(true);
        
        if (AccountManager.getInstance().isAccountAdmin())
        {
            generalConfigSection.getChildren().add(
                    new TreeIconItem(LangFileLoader.getTranslation("word.account"),
                            "user.png",
                            () -> loadFXML("accountConfigPane.fxml"))
            );
            
            generalConfigSection.getChildren().add(
                    new TreeIconItem(LangFileLoader.getTranslation("word.users"),
                            "users.png",
                            () -> loadFXML("usersConfigPane.fxml"))
            );
        }
        
        generalConfigSection.getChildren().add(
                new TreeIconItem(LangFileLoader.getTranslation("word.preferences"),
                        "preferences.png",
                        () -> loadFXML("preferencesConfigPane.fxml"))
        );
        
        if (AccountManager.getInstance().isAccountAdmin()) 
        {
            generalConfigSection.getChildren().add(
                    new TreeIconItem(LangFileLoader.getTranslation("word.establishment"),
                            "establishment.png",
                            () -> loadFXML("establishmentConfigPane.fxml"))
            );
            generalConfigSection.getChildren().add(
                    new TreeIconItem(LangFileLoader.getTranslation("word.printers"),
                            "printer.png",
                            () -> loadFXML("printersConfigPane.fxml"))
            );
            generalConfigSection.getChildren().add(
                    new TreeIconItem(LangFileLoader.getTranslation("word.database"),
                            "database.png",
                            () -> loadFXML("databaseConfigPane.fxml"))
            );
        }
        
        generalConfigSection.getChildren().add(
                new TreeIconItem(LangFileLoader.getTranslation("word.about"),
                        "help.png",
                        () -> loadFXML("aboutConfigPane.fxml"))
        );

        return generalConfigSection;
    }

    private ControllableUIObject<SettingsPaneController> loadFXML(String fxmlPath)
    {
        try
        {
            var loader = new FXMLLoader(ConfigStageController.class.getResource(fxmlPath));
            LangBundleLoader.addLangBundle(loader, Launcher.class);

            Node root = loader.load();

            SettingsPaneController controller = loader.getController();

            return new ControllableUIObject<>(root, controller);
        }
        catch (Exception e)
        {
            System.err.println("Error loading FXML: " + fxmlPath);
            return null;
        }
    }

    public void swapMainPane(ControllableUIObject<SettingsPaneController> newControllableObj)
    {
        mainPane.setContent(newControllableObj.root());
        currentPaneController = newControllableObj.controller();
    }

    @FXML
    private void apply()
    {
        currentPaneController.apply();
    }

    @FXML
    private void cancel()
    {
        currentPaneController.cancel();
    }

    @FXML
    private void accept()
    {
        currentPaneController.acept();
    }
}
