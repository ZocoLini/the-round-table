package org.lebastudios.theroundtable.controllers;

import javafx.scene.Parent;
import javafx.stage.Stage;

public abstract class StageController extends Controller
{
    protected StageController(String fxml)
    {
        super(fxml);
    }

    public abstract void instantiate();
    
    public final Parent getParent()
    {
        return (Parent) getRoot();
    }
    
    public final void close()
    {
        ((Stage) getParent().getScene().getWindow()).close();
    }
}
