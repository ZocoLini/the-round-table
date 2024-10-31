package org.lebastudios.theroundtable.setup;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.config.SettingsPaneController;

import java.net.URL;

public class SettingsPaneWrapperController extends SetupPaneController
{
    private final SettingsPaneController settingsPaneController;
    
    @SneakyThrows
    public SettingsPaneWrapperController(SettingsPaneController settingsPaneController, Node titleNode)
    {
        super(titleNode);
        this.settingsPaneController = settingsPaneController.getClass().getConstructor().newInstance();
    }

    @Override
    @FXML
    protected void initialize()
    {
        ((BorderPane) getRoot()).setCenter(settingsPaneController.getRoot());
        ((BorderPane) getRoot()).setTop(titleNode);
    }

    @Override
    public void apply()
    {
        settingsPaneController.getController().apply();
    }

    @Override
    public boolean validateData()
    {
        return true;
    }

    @Override
    public URL getFXML()
    {
        return SettingsPaneWrapperController.class.getResource("settingsPaneWrapper.fxml");
    }
}
