package org.lebastudios.theroundtable.setup;

import javafx.fxml.FXML;
import javafx.scene.Node;

public abstract class SetupPaneController
{
    @FXML protected Node root;
    
    public abstract void apply();
    
    public abstract boolean validateData();
    
    public final Node getRoot()
    {
        return root;
    }
}
