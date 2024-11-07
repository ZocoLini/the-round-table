package org.lebastudios.theroundtable.communications;

import javafx.application.Platform;
import org.lebastudios.theroundtable.AppTask;
import org.lebastudios.theroundtable.config.data.AccountConfigData;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.dialogs.InformationTextDialogController;
import org.lebastudios.theroundtable.dialogs.RequestTextDialogController;
import org.lebastudios.theroundtable.events.AppLifeCicleEvents;
import org.lebastudios.theroundtable.ui.TaskManager;

import java.util.function.Consumer;
import java.util.function.Function;

public class LicenseValidator
{
    private static int tries = 0;

    private Consumer<Boolean> computeResult = new Consumer<>()
    {
        @Override
        public void accept(Boolean validation)
        {
            if (!validation)
            {
                Platform.runLater(() -> new RequestTextDialogController(
                                onLicenseIntroduced,
                                "License", "XXXX-XXXX-XXXX", licenseFormatValidator,
                                "Introduce your license", "Invalid license format", Platform::exit
                        ).instantiate()
                );
            }
            else
            {
                tries = 0;
            }
        }
    };

    public LicenseValidator(Consumer<Boolean> computeResult)
    {
        this.computeResult = computeResult;
    }

    public LicenseValidator() {}

    public AppTask createValidatingLicenseTask()
    {
        return new AppTask()
        {
            @Override
            protected Void call()
            {
                tries++;

                updateMessage("Reading license...");
                var license = new JSONFile<>(AccountConfigData.class).get().license;

                updateMessage("Validating license...");
                var validation = ApiRequests.isLicenseValid(license);

                if (validation != null)
                {
                    computeResult.accept(validation);
                }
                else
                {
                    if (license == null || license.isEmpty())
                    {
                        Platform.runLater(() ->
                        {
                            new InformationTextDialogController(
                                    "An error ocurred while validating your license. " +
                                            "Check your internet connection and try again."
                            ).instantiate(true);

                            AppLifeCicleEvents.OnAppClose.invoke(null);
                            Platform.exit();
                        });

                        return null;
                    }
                }

                return null;
            }
        };
    }

    private final Consumer<String> onLicenseIntroduced = license ->
    {
        if (tries >= 3)
        {
            AppLifeCicleEvents.OnAppClose.invoke(null);
            Platform.exit();
            return;
        }

        var accountData = new JSONFile<>(AccountConfigData.class);
        license = license.replace("-", "");
        accountData.get().license = license;
        accountData.save();

        TaskManager.getInstance().startNewTaskWithProgressBar(createValidatingLicenseTask());
    };

    private final Function<String, Boolean> licenseFormatValidator = license ->
            license.matches("[A-Za-z0-9\\-]{12,28}");
}
