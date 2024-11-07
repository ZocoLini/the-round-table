package org.lebastudios.theroundtable.setup;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.config.DatabaseConfigPaneController;
import org.lebastudios.theroundtable.config.EstablishmentConfigPaneController;
import org.lebastudios.theroundtable.config.PrintersConfigPaneController;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.SettingsData;
import org.lebastudios.theroundtable.controllers.StageController;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.dialogs.ConfirmationTextDialogController;
import org.lebastudios.theroundtable.events.AppLifeCicleEvents;
import org.lebastudios.theroundtable.locale.LangFileLoader;
import org.lebastudios.theroundtable.ui.LoadingPaneController;
import org.lebastudios.theroundtable.ui.StageBuilder;
import org.lebastudios.theroundtable.ui.TitleBuilder;

import java.net.URL;

public class SetupStageController extends StageController<SetupStageController>
{
    private static final SetupPaneController[] setupPanes = {
            new AccountSetupPaneController(
                    new TitleBuilder(LangFileLoader.getTranslation("setup.title.adminconfig"),
                            "admin-user.png").build()),
            new SettingsPaneWrapperController(new EstablishmentConfigPaneController(),
                    new TitleBuilder(LangFileLoader.getTranslation("setup.title.establishmentconfig"),
                            "establishment.png").build()),
            new SettingsPaneWrapperController(new PrintersConfigPaneController(),
                    new TitleBuilder(LangFileLoader.getTranslation("setup.title.printersconfig"),
                            "printer.png").build()),
            new SettingsPaneWrapperController(new DatabaseConfigPaneController(),
                    new TitleBuilder(LangFileLoader.getTranslation("setup.title.databaseconfig"),
                            "database.png").build()),
    };

    private int currentPane = -1;

    @FXML private Button backButton;
    @FXML private Button nextButton;
    @FXML private ScrollPane mainPane;

    public static boolean checkIfStart()
    {
        return !new JSONFile<>(SettingsData.class).get().setupComplete;
    }

    @FXML
    @Override
    protected void initialize()
    {
        backButton.setDisable(true);
    }

    @Override
    public Class<?> getBundleClass()
    {
        return Launcher.class;
    }

    @Override
    protected void customizeStageBuilder(StageBuilder stageBuilder)
    {
        stageBuilder.setResizeable(true)
                .setStageConsumer(s -> s.setOnCloseRequest(e ->
                {
                    new ConfirmationTextDialogController(
                            LangFileLoader.getTranslation("textblock.closingsetup"),
                            response ->
                            {
                                if (response)
                                {
                                    AppLifeCicleEvents.OnAppClose.invoke(e);
                                    System.exit(0);
                                }
                            }
                    ).instantiate();
                    e.consume();
                }));
    }

    @Override
    public boolean hasFXMLControllerDefined()
    {
        return true;
    }

    @Override
    public URL getFXML()
    {
        return SetupStageController.class.getResource("setupStage.fxml");
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
        if (currentPane > setupPanes.length - 1) return;
        if (currentPane >= 0 && !setupPanes[currentPane].getController().validateData()) return;

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
            ((BorderPane) getRoot()).getBottom().setVisible(false);
            mainPane.setContent(new LoadingPaneController().getRoot());

            new Thread(() ->
            {
                for (var pane : setupPanes)
                {
                    pane.apply();
                }

                final var settingsData = new JSONFile<>(SettingsData.class);
                settingsData.get().setupComplete = true;
                settingsData.save();

                Platform.runLater(this::close);
            }).start();
            
            return;
        }

        mainPane.setContent(setupPanes[currentPane].getRoot());
    }

    @Override
    public String getTitle()
    {
        return "Setup";
    }
}
