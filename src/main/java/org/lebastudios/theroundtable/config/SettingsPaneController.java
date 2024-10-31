package org.lebastudios.theroundtable.config;

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
