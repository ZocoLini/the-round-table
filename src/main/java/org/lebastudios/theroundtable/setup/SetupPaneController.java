package org.lebastudios.theroundtable.setup;

import javafx.scene.Node;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.controllers.PaneController;

public abstract class SetupPaneController extends PaneController<SetupPaneController>
{
    public abstract void apply();

    public abstract boolean validateData();
    
    protected Node titleNode;
    
    public SetupPaneController(Node titleNode)
    {
        this.titleNode = titleNode;
    }
    
    @Override
    public final Class<?> getBundleClass()
    {
        return Launcher.class;
    }
}
