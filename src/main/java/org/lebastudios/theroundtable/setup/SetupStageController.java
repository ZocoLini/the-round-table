package org.lebastudios.theroundtable.setup;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.SettingsData;
import org.lebastudios.theroundtable.language.LangBundleLoader;
import org.lebastudios.theroundtable.language.LangFileLoader;
import org.lebastudios.theroundtable.ui.TitleBuilder;

public class SetupStageController
{
    private static final SetupPaneController[] setupPanes = {
            AccountSetupPaneController.loadAttachedNode(),
            new SettingsPaneWrapper("establishmentConfigPane.fxml"),
            new SettingsPaneWrapper("printersConfigPane.fxml"),
            new SettingsPaneWrapper("databaseConfigPane.fxml"),
    };
    private static final Node[] setupPaneTitles = {
            new TitleBuilder(LangFileLoader.getTranslation("setup.title.adminconfig"), "admin-user.png").build(),
            new TitleBuilder(LangFileLoader.getTranslation("setup.title.establishmentconfig"), "establishment.png").build(),
            new TitleBuilder(LangFileLoader.getTranslation("setup.title.printersconfig"), "printer.png").build(),
            new TitleBuilder(LangFileLoader.getTranslation("setup.title.databaseconfig"), "database.png").build(),
    };
    private int currentPane = -1;

    public BorderPane root;
    @FXML private Button backButton;
    @FXML private Button nextButton;
    @FXML private ScrollPane mainPane;

    public static boolean checkIfStart()
    {
        return !new JSONFile<>(SettingsData.class).get().setupComplete;
    }

    @SneakyThrows
    public static Parent getParentNode()
    {
        FXMLLoader loader = new FXMLLoader(SetupStageController.class.getResource("setupStage.fxml"));
        LangBundleLoader.addLangBundle(loader, Launcher.class);

        return loader.load();
    }

    public void initialize()
    {
        backButton.setDisable(true);
    }

    @FXML
    private void backButtonAction(ActionEvent actionEvent)
    {
        currentPane--;
        
        onCurrentPaneUpdate();
    }

    @FXML
    private void nextButtonAction(ActionEvent actionEvent)
    {
        if (currentPane >= 0 && !setupPanes[currentPane].validateData()) return;

        currentPane++;

        onCurrentPaneUpdate();
    }
    
    private void onCurrentPaneUpdate()
    {
        backButton.setDisable(currentPane <= 0);

        if (currentPane == setupPanes.length - 1)
        {
            nextButton.setText("Finish");
        }
        else
        {
            nextButton.setText("Next");
        }

        if (currentPane == setupPanes.length)
        {
            for (var pane : setupPanes)
            {
                pane.apply();
            }

            final var settingsData = new JSONFile<>(SettingsData.class);
            settingsData.get().setupComplete = true;
            settingsData.save();

            ((Stage) mainPane.getScene().getWindow()).close();

            return;
        }

        mainPane.setContent(setupPanes[currentPane].getRoot());
        root.setTop(setupPaneTitles[currentPane]);
    }
}
