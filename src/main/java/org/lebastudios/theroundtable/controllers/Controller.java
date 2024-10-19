package org.lebastudios.theroundtable.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import lombok.Getter;

@Getter
public abstract class Controller
{
    @FXML private Node root;
    
    protected final String fxml;
    
    protected Controller(String fxml) 
    {
        this.fxml = fxml;
    }
}
