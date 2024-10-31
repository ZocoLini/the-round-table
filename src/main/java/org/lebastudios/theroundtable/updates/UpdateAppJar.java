package org.lebastudios.theroundtable.updates;

import javafx.application.Platform;
import org.lebastudios.theroundtable.communications.ApiRequests;
import org.lebastudios.theroundtable.dialogs.ConfirmationTextDialogController;
import org.lebastudios.theroundtable.locale.LangFileLoader;

public class UpdateAppJar
{
    public void update()
    {
        if (ApiRequests.availableUpdate())
        {
            Platform.runLater(() -> new ConfirmationTextDialogController(
                    LangFileLoader.getTranslation("textblock.confupdate"),
                    response ->
                    {
                        if (!response) return;

                        ApiRequests.getLastAppVersion();
                    }
            ).instantiate());
        }
    }
}
