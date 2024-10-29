package org.lebastudios.theroundtable.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import lombok.Getter;
import org.lebastudios.theroundtable.locale.LangBundleLoader;

import java.io.IOException;
import java.net.URL;

public abstract class Controller<T extends Controller<T>>
{
    @FXML private Node root;
 
    @Getter protected T controller;
    
    private Thread loadingRoot;
    
    public final Node getRoot()
    {
        if (loadingRoot != null)
        {
            try
            {
                loadingRoot.join();
            }
            catch (InterruptedException _) {}
        }
        
        if (root == null) loadFXML();
        
        return this.root;
    }

    public void loadAsync()
    {
        Thread loadingThread = new Thread(() ->
        {
            loadFXML();
            loadingRoot = null;
        });
        loadingThread.start();
        
        loadingRoot = loadingThread;
    }
    
    public final void loadFXML()
    {
        if (root != null) return;
        
        final var fxmlLoader = getFXMLLoader();
        try
        {
            LangBundleLoader.loadLang(fxmlLoader, getBundleClass());
            
            try
            {
                fxmlLoader.setController(this);
            }
            catch (Exception exception) {}
            
            this.root = fxmlLoader.load();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Error loading resource " + getFXML());
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
