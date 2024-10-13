package org.lebastudios.theroundtable.config;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.lebastudios.theroundtable.TheRoundTableApplication;

public class AboutConfigPaneController extends SettingsPaneController
{
    @FXML private Label supportEmailLabel;
    @FXML private Label appVersionLabel;

    @Override
    public void apply()
    {

    }

    @Override
    public void initialize()
    {
        appVersionLabel.setText(TheRoundTableApplication.getAppVersion());
        supportEmailLabel.setText("support@lebastudios.org");
    }
}
