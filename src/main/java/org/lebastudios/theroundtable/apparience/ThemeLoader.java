package org.lebastudios.theroundtable.apparience;

import javafx.scene.Scene;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PreferencesConfigData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ThemeLoader
{
    private static final List<Scene> scenesInstantiated = new ArrayList<>();

    public static void reloadThemes()
    {
        for (var scene : scenesInstantiated)
        {
            scene.getStylesheets().removeLast();
            addActualTheme(scene);
        }

        new Thread(ThemeLoader::removeRemovedScenes).start();
    }

    @SneakyThrows
    public static Scene addActualTheme(Scene scene)
    {
        removeRemovedScenes();

        if (!scenesInstantiated.contains(scene)) scenesInstantiated.add(scene);


        var actualTheme = new JSONFile<>(PreferencesConfigData.class).get().theme;

        String themeCss = new File(
                TheRoundTableApplication.getAppDirectory() + "/styles/" + actualTheme + "/theme.css")
                .toURI().toURL().toExternalForm();
        scene.getStylesheets().add(themeCss);
        return scene;
    }

    private static void removeRemovedScenes()
    {
        scenesInstantiated.removeIf(scene -> scene.getWindow() == null);
    }

    public static File getThemesDir()
    {
        return new File(TheRoundTableApplication.getAppDirectory() + "/styles");
    }
}
