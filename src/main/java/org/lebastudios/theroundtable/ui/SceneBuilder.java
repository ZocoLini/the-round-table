package org.lebastudios.theroundtable.ui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import org.lebastudios.theroundtable.apparience.ThemeLoader;

public class SceneBuilder
{
    private final Parent root;

    public SceneBuilder(Parent root)
    {
        this.root = root;
    }

    public Scene build()
    {
        return ThemeLoader.addActualTheme(new Scene(root));
    }
}
