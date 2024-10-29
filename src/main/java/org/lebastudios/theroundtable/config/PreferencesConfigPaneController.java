package org.lebastudios.theroundtable.config;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import org.lebastudios.theroundtable.apparience.ThemeLoader;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PreferencesConfigData;
import org.lebastudios.theroundtable.locale.LangLoader;

import java.util.Objects;

public class PreferencesConfigPaneController extends SettingsPaneController
{
    @FXML private ChoiceBox<String> themeChoiceBox;
    @FXML private ChoiceBox<String> languageChoiceBox;

    @Override
    public void initialize()
    {
        if (themeChoiceBox.getItems().isEmpty())
        {
            var themesDir = ThemeLoader.getThemesDir();

            for (var theme : Objects.requireNonNull(themesDir.listFiles()))
            {
                if (theme.isFile()) continue;

                themeChoiceBox.getItems().add(transformThemeToDisplayableText(theme.getName()));
            }
        }

        if (languageChoiceBox.getItems().isEmpty())
        {
            languageChoiceBox.getItems().addAll(
                    transformLanguageToDisplayableText("es"), 
                    transformLanguageToDisplayableText("en")
            );
        }

        setActualValues();
    }

    private void setActualValues()
    {
        var preferences = new JSONFile<>(PreferencesConfigData.class).get();

        themeChoiceBox.setValue(transformThemeToDisplayableText(preferences.theme));
        languageChoiceBox.setValue(transformLanguageToDisplayableText(preferences.langauge));
    }

    @Override
    public void apply()
    {
        var preferencesManager = new JSONFile<>(PreferencesConfigData.class);

        preferencesManager.get().langauge = transformLanguageToInternalText(languageChoiceBox.getValue());
        preferencesManager.get().theme = transformThemeToInternalText(themeChoiceBox.getValue());

        preferencesManager.save();

        ThemeLoader.reloadThemes();
    }
    
    // region: Methods for the customization of the displayed texts
    
    private String transformThemeToDisplayableText(String theme)
    {
        int index = theme.indexOf("-");
        
        if (index == -1) 
        {
            return theme.substring(0, 1).toUpperCase() + theme.substring(1);
        }
        
        return theme.substring(0, 1).toUpperCase() + theme.substring(1, index) + " " 
                + theme.substring(index + 1, index + 2).toUpperCase() + theme.substring(index + 2);
    }
    
    private String transformThemeToInternalText(String theme)
    {
        return theme.toLowerCase().replace(" ", "-");
    }
    
    private String transformLanguageToDisplayableText(String language)
    {
        return switch (language)
        {
            case "es" -> "Español";
            case "en" -> "English";
            default -> language;
        };
    }
    
    private String transformLanguageToInternalText(String language)
    {
        return switch (language)
        {
            case "Español" -> "es";
            case "English" -> "en";
            default -> language;
        };
    }
    
    // endregion
}
