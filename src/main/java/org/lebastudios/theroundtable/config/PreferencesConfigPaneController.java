package org.lebastudios.theroundtable.config;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import org.lebastudios.theroundtable.apparience.ThemeLoader;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PreferencesConfigData;

import java.util.Objects;

public class PreferencesConfigPaneController extends SettingsPaneController
{
    @FXML private ChoiceBox<String> themeChoiceBox;
    @FXML private ChoiceBox<String> languageChoiceBox;

    @Override
    public void apply()
    {
        var preferencesManager = new JSONFile<>(PreferencesConfigData.class);

        preferencesManager.get().langauge = languageChoiceBox.getValue();
        preferencesManager.get().theme = themeChoiceBox.getValue();

        preferencesManager.save();

        ThemeLoader.reloadThemes();
    }

    @Override
    public void initialize()
    {
        if (themeChoiceBox.getItems().isEmpty())
        {
            var themesDir = ThemeLoader.getThemesDir();

            for (var theme : Objects.requireNonNull(themesDir.listFiles()))
            {
                if (theme.isFile()) continue;

                themeChoiceBox.getItems().add(theme.getName());
            }
        }

        if (languageChoiceBox.getItems().isEmpty())
        {
            languageChoiceBox.getItems().addAll("es", "en");
        }

        setActualValues();
    }

    private void setActualValues()
    {
        var preferences = new JSONFile<>(PreferencesConfigData.class).get();

        themeChoiceBox.setValue(preferences.theme);
        languageChoiceBox.setValue(preferences.langauge);
    }
}
