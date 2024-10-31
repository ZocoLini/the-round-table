package org.lebastudios.theroundtable.config;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.lebastudios.theroundtable.controllers.PaneController;

public abstract class SettingsPaneController extends PaneController<SettingsPaneController>
{
    public final void acept()
    {
        getController().apply();
        getController().cancel();
    }

    abstract public void apply();

    public final void cancel()
    {
        getController().initialize();

        getStage().close();
    }

    public final void registerEvents() 
    {
        getStage().setOnCloseRequest(e ->
        {
            e.consume();
            getController().cancel();
        });
    }
}
