package org.lebastudios.theroundtable.ui;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.util.Duration;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.controllers.PaneController;

public class LoadingPaneController extends PaneController<LoadingPaneController>
{
    @FXML private IconView loadingIcon;
    
    @Override
    protected void initialize()
    {
        loadingIcon.setIconName("loading.png");
        loadingIcon.setIconSize(75);

        RotateTransition rotate = new RotateTransition(Duration.seconds(3), loadingIcon);
        rotate.setByAngle(360);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.play();
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
}
