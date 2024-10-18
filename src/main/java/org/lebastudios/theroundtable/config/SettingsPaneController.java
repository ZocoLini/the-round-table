package org.lebastudios.theroundtable.config;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public abstract class SettingsPaneController
{
    @FXML protected Node root;

    public final void acept()
    {
        apply();
        cancel();
    }

    abstract public void apply();

    public final void cancel()
    {
        initialize();

        ((Stage) root.getScene().getWindow()).close();
    }

    public void initialize() {}

    public final Node getRoot()
    {
        return root;
    }
}
