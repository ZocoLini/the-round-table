package org.lebastudios.theroundtable.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import lombok.Getter;
import org.lebastudios.theroundtable.language.LangBundleLoader;

import java.io.IOException;
import java.net.URL;

public abstract class Controller<T extends Controller<T>>
{
    @FXML private Node root;
 
    @Getter protected T controller;
    
    public final Node getRoot()
    {
        if (root == null) loadFXML();
        
        return this.root;
    }

    public final void loadFXML()
    {
        final var fxmlLoader = getFXMLLoader();
        try
        {
            LangBundleLoader.addLangBundle(fxmlLoader, getBundleClass());
            
            try
            {
                fxmlLoader.setController(this);
            }
            catch (Exception exception) {}
            
            this.root = fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Resource " + getFXML() + " not found");
        }

        this.controller = fxmlLoader.getController();
    }
    
    public final Parent getParent()
    {
        return (Parent) getRoot();
    }

    public abstract Class<?> getBundleClass();
    
    public abstract URL getFXML();
    
    public FXMLLoader getFXMLLoader()
    {
        return new FXMLLoader(getFXML());
    }
}
