package org.lebastudios.theroundtable.config;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.accounts.AccountManager;
import org.lebastudios.theroundtable.apparience.ImageLoader;
import org.lebastudios.theroundtable.controllers.StageController;
import org.lebastudios.theroundtable.events.UserEvents;
import org.lebastudios.theroundtable.locale.LangFileLoader;
import org.lebastudios.theroundtable.plugins.PluginLoader;
import org.lebastudios.theroundtable.ui.StageBuilder;

import java.net.URL;
import java.util.function.Consumer;

public class ConfigStageController extends StageController<ConfigStageController>
{
    public static Stage configStage;

    static
    {
        UserEvents.OnAccountLogOutBefore.addListener(_ -> configStage = null);
    }

    @FXML private TreeView<SettingsItem> configSectionsTreeView;
    @FXML private ScrollPane mainPane;
    private SettingsPaneController currentPaneController;

    @Override
    public void instantiate(Consumer<ConfigStageController> acceptController, boolean shouldWait)
    {
        if (configStage != null)
        {
            configStage.show();
            return;
        }

        super.instantiate(acceptController, shouldWait);

        configStage = getStage();
    }

    @SneakyThrows @FXML @Override
    protected void initialize()
    {
        configSectionsTreeView.setCellFactory(_ -> new TreeCell<>()
        {
            {
                this.setOnMouseClicked(_ ->
                {
                    if (this.getTreeItem() == null) return;

                    if (this.getTreeItem() instanceof TreeItem<SettingsItem> item)
                    {
                        ConfigStageController.this.swapMainPane(item.getValue().settingPane());
                    }
                });
            }

            @Override
            protected void updateItem(SettingsItem item, boolean empty)
            {
                super.updateItem(item, empty);

                if (empty || item == null)
                {
                    setText(null);
                    setGraphic(null);
                }
                else
                {
                    setText(item.value());

                    var imageView = new ImageView(ImageLoader.getIcon(item.iconName()));
                    imageView.setFitHeight(20);
                    imageView.setFitWidth(20);
                    this.setGraphic(imageView);
                }
            }
        });
        configSectionsTreeView.getRoot().getChildren().add(createGeneralConfigSection());

        configSectionsTreeView.getRoot().getChildren().addAll(PluginLoader.getSettingsTreeViews());

        mainPane.setContent(new FXMLLoader(Launcher.class.getResource("defaultCenterPane.fxml")).load());
    }

    private TreeItem<SettingsItem> createGeneralConfigSection()
    {
        var generalConfigSection = new TreeItem<>(new SettingsItem(LangFileLoader.getTranslation("word.general"),
                "settings.png", null));
        generalConfigSection.setExpanded(true);

        if (AccountManager.getInstance().isAccountAdmin())
        {
            // generalConfigSection.getChildren().add(
            //         new TreeItem<>(new SettingsItem(LangFileLoader.getTranslation("word.account"),
            //                 "user.png", new AccountConfigPaneController()))
            // );

            generalConfigSection.getChildren().add(
                    new TreeItem<>(new SettingsItem(LangFileLoader.getTranslation("word.users"),
                            "users.png", new UsersConfigPaneController()))
            );
        }

        generalConfigSection.getChildren().add(
                new TreeItem<>(new SettingsItem(LangFileLoader.getTranslation("word.preferences"),
                        "preferences.png", new PreferencesConfigPaneController()))
        );

        if (AccountManager.getInstance().isAccountAdmin())
        {
            generalConfigSection.getChildren().add(
                    new TreeItem<>(new SettingsItem(LangFileLoader.getTranslation("word.establishment"),
                            "establishment.png", new EstablishmentConfigPaneController()))
            );
            generalConfigSection.getChildren().add(
                    new TreeItem<>(new SettingsItem(LangFileLoader.getTranslation("word.printers"),
                            "printer.png", new PrintersConfigPaneController()))
            );
            generalConfigSection.getChildren().add(
                    new TreeItem<>(new SettingsItem(LangFileLoader.getTranslation("word.database"),
                            "database.png", new DatabaseConfigPaneController()))
            );
        }

        generalConfigSection.getChildren().add(
                new TreeItem<>(new SettingsItem(LangFileLoader.getTranslation("word.about"),
                        "help.png", new AboutConfigPaneController()))
        );

        return generalConfigSection;
    }

    public void swapMainPane(SettingsPaneController controller)
    {
        if (controller == null) return;
        
        final var root = controller.getRoot();
        
        mainPane.setContent(root);
        controller.registerEvents();
        currentPaneController = controller;
    }

    @FXML
    private void apply()
    {
        if (currentPaneController == null)
        {
            return;
        }

        currentPaneController.getController().apply();
    }

    @FXML
    private void cancel()
    {
        if (currentPaneController == null)
        {
            close();
            return;
        }

        currentPaneController.cancel();
    }

    @FXML
    private void accept()
    {
        if (currentPaneController == null)
        {
            cancel();
            return;
        }

        currentPaneController.acept();
    }

    @Override
    public boolean hasFXMLControllerDefined()
    {
        return true;
    }

    @Override
    public String getTitle()
    {
        return LangFileLoader.getTranslation("title.settingsstage");
    }

    @Override
    public Class<?> getBundleClass()
    {
        return Launcher.class;
    }

    @Override
    protected void customizeStageBuilder(StageBuilder stageBuilder)
    {
        stageBuilder.setModality(Modality.APPLICATION_MODAL)
                .setResizeable(true);
    }

    @Override
    public URL getFXML()
    {
        return ConfigStageController.class.getResource("configStage.fxml");
    }
}
