package org.lebastudios.theroundtable.updates;

import javafx.application.Platform;
import org.lebastudios.theroundtable.communications.ApiRequests;
import org.lebastudios.theroundtable.dialogs.ConfirmationTextDialogController;
import org.lebastudios.theroundtable.language.LangFileLoader;

public class UpdateAppJar
{
    public void update()
    {
        if (ApiRequests.availableUpdate())
        {
            Platform.runLater(() ->
            {
                ConfirmationTextDialogController.loadAttachedNode(
                        LangFileLoader.getTranslation("textblock.confupdate"),
                        response ->
                        {
                            if (!response) return;

                            ApiRequests.getLastAppVersion();
                        }
                );
            });
        }
    }
}
