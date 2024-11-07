package org.lebastudios.theroundtable.events;

import javafx.stage.WindowEvent;

public final class AppLifeCicleEvents
{
    public static final Event1<WindowEvent> OnAppCloseRequest = new Event1<>();
    public static final Event1<WindowEvent> OnAppClose = new Event1<>();
}
