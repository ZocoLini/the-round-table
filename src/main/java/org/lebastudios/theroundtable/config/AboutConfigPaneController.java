package org.lebastudios.theroundtable.config;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.TheRoundTableApplication;

import java.net.URL;

public class AboutConfigPaneController extends SettingsPaneController
{
    @FXML private Label supportEmailLabel;
    @FXML private Label appVersionLabel;

    @Override
    public void apply() {}

    @Override
    @FXML protected void initialize()
    {
        appVersionLabel.setText(TheRoundTableApplication.getAppVersion());
        supportEmailLabel.setText("support@lebastudios.org");
    }

    @Override
    public Class<?> getBundleClass()
    {
        return Launcher.class;
    }

    @Override
    public boolean hasFXMLControllerDefined()
    {
        return true;
    }

    @Override
    public URL getFXML()
    {
        return AboutConfigPaneController.class.getResource("aboutConfigPane.fxml");
    }
}
